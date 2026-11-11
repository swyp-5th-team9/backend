package com.swift.sportspub.crawler.provider;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.ElementHandle;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.LoadState;
import com.microsoft.playwright.options.WaitUntilState;
import com.swift.sportspub.crawler.dto.GameData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@ConditionalOnProperty(name = "crawler.provider", havingValue = "playwright", matchIfMissing = true)
public class KboPlaywrightProvider implements GameDataProvider {

    private static final String KBO_SCHEDULE_URL =
            "https://www.koreabaseball.com/Schedule/Schedule.aspx";
    private static final int WAIT_SELECTOR_TIMEOUT_MS = 15_000;
    private static final ZoneId KST = ZoneId.of("Asia/Seoul");

    @Override
    public List<GameData> fetchGames(int year, int month) {
        log.info("[Playwright] KBO 크롤링 시작: {}년 {}월 (전 시리즈)", year, month);

        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(
                    new BrowserType.LaunchOptions().setHeadless(true)
            );
            BrowserContext context = browser.newContext(
                    new Browser.NewContextOptions()
                            .setUserAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36")
            );
            Page page = context.newPage();

            page.navigate(KBO_SCHEDULE_URL, new Page.NavigateOptions().setWaitUntil(WaitUntilState.NETWORKIDLE));
            page.waitForSelector("#tblScheduleList",
                    new Page.WaitForSelectorOptions().setTimeout(WAIT_SELECTOR_TIMEOUT_MS));

            selectAndWait(page, "#ddlYear", String.valueOf(year));

            List<String> seriesValues = readSeriesValues(page);
            if (seriesValues.isEmpty()) {
                log.warn("[Playwright] ddlSeries 옵션이 비어 있습니다.");
            }

            Map<String, GameData> merged = new LinkedHashMap<>();
            String monthValue = String.format("%02d", month);

            for (String seriesValue : seriesValues) {
                try {
                    selectAndWait(page, "#ddlSeries", seriesValue);
                    selectAndWait(page, "#ddlMonth", monthValue);

                    List<GameData> games = parseScheduleTable(page, year, month);
                    log.info("[Playwright] series={} → {}건 (요청월 필터 후)", seriesValue, games.size());
                    for (GameData game : games) {
                        merged.putIfAbsent(dedupeKey(game), game);
                    }
                } catch (Exception e) {
                    log.warn("[Playwright] series={} 수집 실패: {}", seriesValue, e.getMessage());
                }
            }

            browser.close();
            log.info("[Playwright] 크롤링 완료: {}년 {}월 - 합계 {}건", year, month, merged.size());
            return new ArrayList<>(merged.values());

        } catch (Exception e) {
            log.error("[Playwright] 크롤링 실패: {}년 {}월 - {}", year, month, e.getMessage(), e);
            return List.of();
        }
    }

    private List<String> readSeriesValues(Page page) {
        List<String> values = new ArrayList<>();
        Locator options = page.locator("#ddlSeries option");
        int count = options.count();
        for (int i = 0; i < count; i++) {
            String value = options.nth(i).getAttribute("value");
            if (value != null && !value.isBlank()) {
                values.add(value);
            }
        }
        return values;
    }

    private void selectAndWait(Page page, String selector, String value) {
        page.selectOption(selector, value);
        page.waitForLoadState(LoadState.NETWORKIDLE);
        page.waitForSelector("#tblScheduleList",
                new Page.WaitForSelectorOptions().setTimeout(WAIT_SELECTOR_TIMEOUT_MS));
        // ASP.NET 부분 갱신 여유
        page.waitForTimeout(500);
    }

    private List<GameData> parseScheduleTable(Page page, int year, int month) {
        List<GameData> games = new ArrayList<>();
        List<ElementHandle> rows = page.querySelectorAll("#tblScheduleList tbody tr");
        LocalDate currentDate = null;
        LocalDate today = LocalDate.now(KST);

        for (ElementHandle row : rows) {
            try {
                ElementHandle dateCell = row.querySelector("td.day");
                if (dateCell != null) {
                    String dateTxt = dateCell.innerText().trim().replaceAll("\\(.*\\)", "").trim();
                    String[] parts = dateTxt.split("\\.");
                    if (parts.length >= 2) {
                        int cellMonth = Integer.parseInt(parts[0].trim());
                        int cellDay = Integer.parseInt(parts[1].trim());
                        currentDate = LocalDate.of(year, cellMonth, cellDay);
                    }
                }

                if (currentDate == null) {
                    continue;
                }
                // 요청 연·월에 속하지 않는 행은 버림 (잘못된 month 스탬프 방지)
                if (currentDate.getYear() != year || currentDate.getMonthValue() != month) {
                    continue;
                }

                ElementHandle playCell = row.querySelector("td.play");
                if (playCell == null) {
                    continue;
                }

                List<ElementHandle> teamSpans = playCell.querySelectorAll(":scope > span");
                if (teamSpans.size() < 2) {
                    continue;
                }
                String awayTeam = teamSpans.get(0).innerText().trim();
                String homeTeam = teamSpans.get(teamSpans.size() - 1).innerText().trim();

                String startTime = "";
                ElementHandle timeEl = row.querySelector("td.time b");
                if (timeEl != null) {
                    startTime = timeEl.innerText().trim();
                }

                String stadium = "";
                String remark = "";
                List<ElementHandle> tds = row.querySelectorAll("td");
                if (tds.size() >= 2) {
                    stadium = tds.get(tds.size() - 2).innerText().trim();
                    remark = tds.get(tds.size() - 1).innerText().replace('\n', ' ').trim();
                }

                ScoreParseResult scoreResult = parseScore(playCell, remark);
                GameData.Status status = scoreResult.status();
                Integer awayScore = scoreResult.awayScore();
                Integer homeScore = scoreResult.homeScore();

                // 미래 날짜에 점수가 붙어 종료로 오는 이상 데이터 방어
                if (status == GameData.Status.FINISHED && currentDate.isAfter(today)) {
                    log.warn("[Playwright] 미래 날짜 FINISHED 무시: {} {} vs {}", currentDate, awayTeam, homeTeam);
                    status = GameData.Status.SCHEDULED;
                    awayScore = null;
                    homeScore = null;
                }

                games.add(GameData.builder()
                        .gameDate(currentDate)
                        .startTime(startTime)
                        .homeTeam(homeTeam)
                        .awayTeam(awayTeam)
                        .stadium(stadium)
                        .homeScore(homeScore)
                        .awayScore(awayScore)
                        .status(status)
                        .build());

            } catch (Exception e) {
                log.warn("[Playwright] 행 파싱 실패, 건너뜀: {}", e.getMessage());
            }
        }

        return games;
    }

    /**
     * 점수/취소 파싱.
     * - 우천·취소는 보통 {@code em}이 아니라 비고(마지막 td)에만 표기된다.
     * - 두 자리 점수(10+)는 win/lose/same span 전체 텍스트를 사용한다.
     */
    private ScoreParseResult parseScore(ElementHandle playCell, String remark) {
        ElementHandle scoreEm = playCell.querySelector("em");
        String emText = scoreEm == null ? "" : scoreEm.innerText().replace('\n', ' ').trim();

        // KBO는 우천취소/그라운드사정 등을 em이 아니라 비고(마지막 td)에 넣는다. 정상 종료 비고는 "-".
        if (isCanceledText(emText) || isCanceledRemark(remark)) {
            return ScoreParseResult.canceled();
        }
        if (scoreEm == null) {
            return ScoreParseResult.scheduled();
        }

        List<ElementHandle> classed = scoreEm.querySelectorAll(
                ":scope > span.win, :scope > span.lose, :scope > span.same"
        );
        if (classed.size() >= 2) {
            Integer away = parseIntOrNull(classed.get(0).innerText());
            Integer home = parseIntOrNull(classed.get(1).innerText());
            if (away != null && home != null) {
                return ScoreParseResult.finished(away, home);
            }
        }

        List<Integer> numericDirect = new ArrayList<>();
        for (ElementHandle span : scoreEm.querySelectorAll(":scope > span")) {
            Integer value = parseIntOrNull(span.innerText());
            if (value != null) {
                numericDirect.add(value);
            }
        }
        if (numericDirect.size() >= 2) {
            return ScoreParseResult.finished(numericDirect.get(0), numericDirect.get(1));
        }

        // 예: "5vs10", "5:10"
        java.util.regex.Matcher matcher = java.util.regex.Pattern
                .compile("(\\d+)\\s*(?:vs|:|：)\\s*(\\d+)", java.util.regex.Pattern.CASE_INSENSITIVE)
                .matcher(emText.replace(" ", ""));
        if (matcher.find()) {
            return ScoreParseResult.finished(
                    Integer.parseInt(matcher.group(1)),
                    Integer.parseInt(matcher.group(2))
            );
        }

        return ScoreParseResult.scheduled();
    }

    private boolean isCanceledText(String text) {
        if (text == null || text.isBlank()) {
            return false;
        }
        return text.contains("취소") || text.contains("우천");
    }

    private boolean isCanceledRemark(String remark) {
        if (remark == null) {
            return false;
        }
        String trimmed = remark.trim();
        if (trimmed.isEmpty() || "-".equals(trimmed)) {
            return false;
        }
        // 비고에 사유 문구가 있으면 취소 (우천취소, 그라운드사정 등)
        return true;
    }

    private Integer parseIntOrNull(String raw) {
        if (raw == null) {
            return null;
        }
        String trimmed = raw.trim();
        if (trimmed.isEmpty()) {
            return null;
        }
        try {
            return Integer.parseInt(trimmed);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String dedupeKey(GameData game) {
        return game.getGameDate() + "|" + game.getAwayTeam() + "|" + game.getHomeTeam();
    }

    private record ScoreParseResult(
            GameData.Status status,
            Integer awayScore,
            Integer homeScore
    ) {
        static ScoreParseResult scheduled() {
            return new ScoreParseResult(GameData.Status.SCHEDULED, null, null);
        }

        static ScoreParseResult canceled() {
            return new ScoreParseResult(GameData.Status.CANCELED, null, null);
        }

        static ScoreParseResult finished(int away, int home) {
            return new ScoreParseResult(GameData.Status.FINISHED, away, home);
        }
    }
}
