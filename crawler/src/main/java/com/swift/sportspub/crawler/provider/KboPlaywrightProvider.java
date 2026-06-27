package com.swift.sportspub.crawler.provider;

import com.swift.sportspub.crawler.dto.GameData;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.ElementHandle;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.WaitUntilState;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@ConditionalOnProperty(name = "crawler.provider", havingValue = "playwright", matchIfMissing = true)
public class KboPlaywrightProvider implements GameDataProvider {

    private static final String KBO_SCHEDULE_URL =
            "https://www.koreabaseball.com/Schedule/Schedule.aspx";
    private static final int WAIT_SELECTOR_TIMEOUT_MS = 10_000;
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy.MM.dd");

    @Override
    public List<GameData> fetchGames(int year, int month) {
        log.info("[Playwright] KBO 크롤링 시작: {}년 {}월", year, month);

        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(
                    new BrowserType.LaunchOptions().setHeadless(true)
            );
            BrowserContext context = browser.newContext(
                    new Browser.NewContextOptions()
                            .setUserAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36")
            );
            Page page = context.newPage();

            String url = KBO_SCHEDULE_URL + "?year=" + year + "&month=" + String.format("%02d", month);
            page.navigate(url, new Page.NavigateOptions().setWaitUntil(WaitUntilState.NETWORKIDLE));
            page.waitForSelector("#tblScheduleList",
                    new Page.WaitForSelectorOptions().setTimeout(WAIT_SELECTOR_TIMEOUT_MS));

            List<GameData> games = parseScheduleTable(page, year, month);
            browser.close();

            log.info("[Playwright] 크롤링 완료: {}년 {}월 - {}건", year, month, games.size());
            return games;

        } catch (Exception e) {
            log.error("[Playwright] 크롤링 실패: {}년 {}월 - {}", year, month, e.getMessage(), e);
            return List.of();
        }
    }

    private List<GameData> parseScheduleTable(Page page, int year, int month) {
        List<GameData> games = new ArrayList<>();
        List<ElementHandle> rows = page.querySelectorAll("#tblScheduleList tbody tr");
        String currentDate = null;

        for (ElementHandle row : rows) {
            try {
                ElementHandle dateCell = row.querySelector("td.day");
                if (dateCell != null) {
                    String dateTxt = dateCell.innerText().trim().replaceAll("\\(.*\\)", "").trim();
                    String[] parts = dateTxt.split("\\.");
                    if (parts.length >= 2) {
                        currentDate = year + "." + String.format("%02d", month) + "." + parts[1].trim();
                    }
                }

                if (currentDate == null) continue;

                ElementHandle playCell = row.querySelector("td.play");
                if (playCell == null) continue;

                List<ElementHandle> teamSpans = playCell.querySelectorAll(":scope > span");
                if (teamSpans.size() < 2) continue;
                String awayTeam = teamSpans.get(0).innerText().trim();
                String homeTeam = teamSpans.get(teamSpans.size() - 1).innerText().trim();

                String startTime = "";
                ElementHandle timeEl = row.querySelector("td.time b");
                if (timeEl != null) startTime = timeEl.innerText().trim();

                String stadium = "";
                List<ElementHandle> tds = row.querySelectorAll("td");
                if (tds.size() >= 2) stadium = tds.get(tds.size() - 2).innerText().trim();

                Integer awayScore = null;
                Integer homeScore = null;
                GameData.Status status = GameData.Status.SCHEDULED;

                ElementHandle scoreEm = playCell.querySelector("em");
                if (scoreEm != null) {
                    List<ElementHandle> scoreSpans = scoreEm.querySelectorAll("span");
                    if (scoreSpans.size() >= 3) {
                        try {
                            awayScore = Integer.parseInt(scoreSpans.get(0).innerText().trim());
                            homeScore = Integer.parseInt(scoreSpans.get(2).innerText().trim());
                            status = GameData.Status.FINISHED;
                        } catch (NumberFormatException ignored) {
                            String emText = scoreEm.innerText();
                            if (emText.contains("취소") || emText.contains("우천")) {
                                status = GameData.Status.CANCELED;
                            }
                        }
                    }
                }

                LocalDate gameDate = LocalDate.parse(currentDate, DATE_FORMAT);

                games.add(GameData.builder()
                        .gameDate(gameDate)
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
}
