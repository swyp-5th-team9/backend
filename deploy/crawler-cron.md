# KBO 크롤러 GitHub Actions cron 운영

크롤러는 EC2 상주 대신 **GitHub Actions cron** 으로 매일 1회 실행합니다.
워크플로우 파일: `.github/workflows/crawler.yml`

## 스케줄
- `cron: "0 17 * * *"` (UTC 17시 = **KST 02시**)
- `workflow_dispatch` 로 수동 실행 가능
- 동시 실행 방지: `concurrency: crawler-sync`

## 아키텍처

```
GitHub runner
   ↓  AWS API : SG 22 임시 개방 (runner IP /32)
EC2 (bastion)
   ↓  SSH tunnel  runner:15432 → RDS:5432
RDS (sportspub_db)
```

runner 에서 `./gradlew :crawler:bootRun` 을 `SPRING_PROFILES_ACTIVE=local` 로 실행.
`CrawlerRunner` 가 `@Profile("local")` 이라 부팅 즉시 `syncCurrentAndNextMonth()` 를 호출합니다.

## 실행 스텝 요약
1. Checkout · JDK 21 setup
2. AWS creds 로 SG 22 에 runner IP 등록
3. EC2 로 SSH tunnel 백그라운드 오픈 (`-L 15432:RDS:5432`)
4. `./gradlew :crawler:bootRun` 실행 (Playwright 가 첫 실행 시 chromium 자동 다운로드)
5. 로그에서 `동기화 완료` 문자열이 **2회 감지** (당월 + 익월) 되면 프로세스 SIGTERM
6. tunnel 닫기 · SG rule revoke (**`if: always()`** — 실패해도 무조건 정리)

## 필요한 GitHub Secrets

| Secret | 용도 |
|---|---|
| `AWS_ACCESS_KEY_ID` / `AWS_SECRET_ACCESS_KEY` | SG 조작 (권한: `authorize/revoke-security-group-ingress`) |
| `AWS_SG_ID` | EC2 보안그룹 ID (`sg-...`) |
| `EC2_HOST` / `EC2_USER` / `EC2_SSH_KEY` | bastion SSH 접속 |
| `RDS_HOST` | tunnel 목적지 (RDS 엔드포인트) |
| `DB_USERNAME` / `DB_PASSWORD` | RDS 계정 (권장: 크롤러 전용 계정 분리) |

## 수동 실행

```bash
gh workflow run crawler.yml --ref develop
gh run watch --exit-status
```

## 트러블슈팅

- **`sync did not complete within timeout`**
  - 10분 안에 `동기화 완료` 가 2회 안 나옴. KBO 사이트 셀렉터 변경 또는 Playwright 로드 실패 가능성.
  - Actions 로그에서 `crawler.log` tail 확인 → 필요 시 `KboPlaywrightProvider` 파싱 로직 점검.
- **SSH tunnel 실패 (`nc -z localhost 15432` 실패)**
  - EC2 SG 22 개방이 propagate 되기 전 tunnel 시도. `sleep 2` 로 충분한 편이지만 재발 시 늘리기.
- **SG rule 이 revoke 안 됨**
  - `revoke` 스텝이 `if: always()` 라 워크플로우 강제 취소 시에도 도는 편. 그래도 남아있다면 콘솔에서 수동 제거.
- **DB 이름 오류**
  - 실제 RDS DB 는 **`sportspub_db`**. `moball_db` 아님.

## 왜 EC2 상주가 아닌 GHA cron 인가?

- 하루 1회 실행이라 상주 프로세스가 과설계
- t3.micro (1GB RAM) 에 Chromium + app JVM 이 같이 뜨면 OOM 위험
- runner 에서 실행하면 EC2 는 API 서버 전용으로 유지 가능
- 실패 알림 · 재실행이 GHA UI 로 바로 되어 운영 편의성 ↑

## 관련
- Task #17 (EC2 systemd 배포) 은 이 방식으로 대체됐음
- 로컬 개발 시 즉시 동기화가 필요하면 여전히 `SPRING_PROFILES_ACTIVE=local ./gradlew :crawler:bootRun`
