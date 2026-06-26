# user 도메인

회원 정보, 역할(FAN/OWNER/ADMIN), 온보딩, 즐겨찾기.

**담당**: 주양 (auth 도메인과 한 묶음)

## 패키지 구조
- `controller/UserController.java`
- `service/UserService.java`
- `repository/UserRepository.java`
- `dto/` — UserResponse, OnboardingRequest 등
- `entity/` — User, UserRole, WithdrawalReason
- `exception/UserNotFoundException.java`

## User 조회 정책 (Soft Delete)

`User` Entity에는 `@SQLRestriction`을 사용하지 않는다. 탈퇴 회원 제외는 `UserRepository`에서 명시한다.

| 메서드 | 범위 | 용도 |
|---|---|---|
| `findActiveById` | 탈퇴 제외 | JWT 인증 API (`UserService.getUser`) |
| `findByOauthProviderAndOauthIdIncludingDeleted` | 탈퇴 포함 | OAuth 로그인, 계정 복구 (`AuthService`) |

네이밍 규칙:
- `findActiveBy*` — `deleted_at IS NULL`
- `*IncludingDeleted` — 탈퇴 회원 포함
- `deleted_at` 조건이 모호한 `findByOauthProviderAndOauthId` 같은 메서드는 사용하지 않는다.

계정 복구 (MVP):
- 동일 OAuth 재로그인 시 `restoreForReLogin()`으로 `deletedAt`, 닉네임, 온보딩 상태 초기화
- 선호 구단(`user_favorite_teams`)도 함께 삭제

## API
- GET `/api/v1/users/me`
- POST `/api/v1/users/me/onboarding` (닉네임 + 응원 구단)
- PATCH `/api/v1/users/me`
- DELETE `/api/v1/users/me` (Soft Delete, `WithdrawRequest` body)
