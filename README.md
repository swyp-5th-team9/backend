# sportspub-backend

> KBO 중계 펍 탐색 서비스 **모여볼** 백엔드 레포
> swyp 5기 9팀

---

## 스택

- **Java 21** (Temurin 권장)
- **Spring Boot 3.5.x**
- **PostgreSQL 16**
- **Gradle 8.x** (멀티 모듈)
- Spring Data JPA · Spring Security · OAuth2 Client · JWT (jjwt 0.12)
- Playwright (크롤러)

---

## 레포 구조

```
backend/
├── app/                       # 메인 백엔드 (REST API, 8080 포트)
│   └── src/main/java/com/swift/sportspub/
│       ├── KboAppApplication.java
│       ├── common/            # ApiResponse, BaseEntity, GlobalExceptionHandler
│       ├── config/            # SecurityConfig
│       ├── auth/              # 소셜 로그인 (주양)
│       ├── user/              # 회원·온보딩 (주양)
│       ├── pub/               # 펍 CRUD
│       ├── lineup/            # 상영 라인업
│       ├── match/             # 경기 일정 (크롤러 DB 공유)
│       ├── favorite/          # 즐겨찾기
│       └── report/            # 제보
│
├── crawler/                   # KBO 크롤러 (스케줄러)
│   └── src/main/java/com/swift/sportspub/crawler/
│
├── docker-compose.yml         # 로컬 Postgres
├── settings.gradle, build.gradle
└── docs/                      # PRD, 스켈레톤, ERD 등 (별도 추가 예정)
```

---

## 빠른 시작

### 1. 사전 준비
- JDK 21 설치 (`brew install --cask temurin@21`)
- Docker Desktop 설치 (Postgres용)

### 2. 시크릿 설정
```bash
cp app/src/main/resources/application-local.yml.example \
   app/src/main/resources/application-local.yml
```
- `KAKAO_CLIENT_ID`, `KAKAO_CLIENT_SECRET`
- `NAVER_CLIENT_ID`, `NAVER_CLIENT_SECRET`
- `JWT_SECRET` (256bit 랜덤 문자열)

→ 환경변수로 주입하거나 `application-local.yml`에 직접 채워도 됨 (이 파일은 `.gitignore`).

### 3. DB 실행
```bash
docker compose up -d db
```

### 4. 메인 백엔드 실행
```bash
./gradlew :app:bootRun
```
- 헬스체크: http://localhost:8080/health
- Swagger UI: http://localhost:8080/swagger-ui.html

### 5. 크롤러 실행 (필요 시)
```bash
./gradlew :crawler:bootRun
```

---

## Git 컨벤션

### 기본 브랜치
- `develop` — 통합 개발 브랜치 (default)
- `main` — 운영 배포 브랜치

### 브랜치 네이밍
```
init/#N-name         # 초기 셋업
feat/#N-name         # 신규 기능
fix/#N-name          # 버그 수정
chore/#N-name        # 잡일 (의존성 업데이트 등)
refactor/#N-name     # 리팩토링
docs/#N-name         # 문서
test/#N-name         # 테스트
setting/#N-name      # 설정 변경
```

이슈 번호(`#N`) 필수.

### 커밋 메시지
```
TYPE : 작업 내용

예) FEAT : 카카오 OAuth2 로그인 구현
    FIX : JWT 만료 시간 계산 오류 수정
```
TYPE: `FEAT` / `FIX` / `DOCS` / `REFACTOR` / `STYLE` / `TEST` / `CHORE`

### PR 규칙
- 하나의 PR = 하나의 목적
- Merge 전 1명 이상 리뷰
- Merge 후 브랜치 삭제

---

## 코드 컨벤션

### 네이밍
| 대상 | 규칙 | 예 |
|---|---|---|
| Class | PascalCase | `UserService` |
| Method / Variable | camelCase | `getUserById` |
| Constant | UPPER_SNAKE_CASE | `MAX_RETRY_COUNT` |
| Boolean | `is~`, `has~` | `isActive`, `hasPermission` |
| Collection | 복수형 | `users`, `pubs` |
| DTO | `XxxRequest` / `XxxResponse` | `UserRequest` |

### Lombok
- 허용: `@Getter` `@Builder` `@NoArgsConstructor` `@RequiredArgsConstructor`
- 지양: `@Setter` `@Data` `@AllArgsConstructor`

### DI
- 생성자 주입 (`@RequiredArgsConstructor` + `private final`)
- `@Autowired` 지양

### Entity
- 수정은 도메인 메서드로: `review.updateContent(...)` (O) / `setContent(...)` (X)
- 사용자 데이터는 **Soft Delete** 우선 (`deleted_at` 컬럼)

### 기타
- 메서드 = 단일 책임
- 중괄호 생략 금지
- 매직 넘버 → `private static final` 상수

---

## API 컨벤션

### URL
- Resource는 **복수형**: `GET /pubs`, `POST /reviews`, `DELETE /favorites/{id}`
- 인증 필요 경로: `/api/v1/...`

### 응답 포맷
```json
// 성공
{ "success": true, "data": { ... } }

// 실패
{ "success": false, "message": "에러 메시지" }
```

배포된 필드 **삭제·이름변경 금지**. 추가만 자유.

### 문서
- Swagger UI: http://localhost:8080/swagger-ui.html
- 스펙 변경 시 PR 본문에 명시

---

## 도메인 담당

| 도메인 | 담당 | 비고 |
|---|---|---|
| auth, user | 주양 | 소셜 로그인 + 회원 |
| pub, lineup, match, favorite, report | 미정 | 기능명세서 확정 후 분배 |
| crawler | 진용 | 기존 KBO 크롤러 모듈 통합 |
