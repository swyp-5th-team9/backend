# user 도메인

회원 정보, 역할(FAN/OWNER/ADMIN), 온보딩, 즐겨찾기.

**담당**: 주양 (auth 도메인과 한 묶음)

## 패키지 구조
- `controller/UserController.java`
- `service/UserService.java`, `service/UserHardDeleteService.java`
- `scheduler/UserHardDeleteScheduler.java`
- `repository/UserRepository.java`
- `config/UserWithdrawalProperties.java`
- `dto/` — UserResponse, OnboardingRequest 등
- `entity/` — User, UserRole, WithdrawalReason
- `exception/UserNotFoundException.java`

## User 조회 정책 (Soft Delete)

`User` Entity에는 `@SQLRestriction`을 사용하지 않는다. 탈퇴 회원 제외는 `UserRepository`에서 명시한다.

| 메서드 | 범위 | 용도 |
|---|---|---|
| `findActiveById` | 탈퇴 제외 | JWT 인증 API (`UserService.getUser`) |
| `findByOauthProviderAndOauthIdIncludingDeleted` | 탈퇴 포함 | OAuth 로그인 (`AuthService`) |
| `findAllWithdrawnBefore` | 탈퇴·기간 경과 | Hard Delete 배치 (`UserHardDeleteScheduler`) |

네이밍 규칙:
- `findActiveBy*` — `deleted_at IS NULL`
- `*IncludingDeleted` — 탈퇴 회원 포함
- `deleted_at` 조건이 모호한 `findByOauthProviderAndOauthId` 같은 메서드는 사용하지 않는다.

## 탈퇴·복구·Hard Delete 정책

| 상황 | 동작 | `LoginResponse.restored` |
|---|---|---|
| 활성 회원 로그인 | 기존 처리 | `false` |
| 탈퇴 30일 **이내** OAuth 재로그인 | `restoreForReLogin()` — deletedAt·닉네임·프로필 이미지·온보딩 초기화, 선호 구단 삭제 | `true` |
| 탈퇴 30일 **초과** OAuth 재로그인 | Hard Delete 후 신규 User 생성 (배치 실행 여부 무관) | `false` |
| 회원 없음 | 신규 가입 | `false` |

- 보관 기간: `app.user.withdrawal.retention-days` (기본 30일)
- 미로그인 탈퇴 회원 정리: `UserHardDeleteScheduler` (`app.user.withdrawal.hard-delete-cron`, 기본 매일 03:00)
- Hard Delete 시 `withdrawal_reasons`, `reports` 등 FK CASCADE로 연쇄 삭제 (Flyway V11)
- 탈퇴 후 기존 Access/Refresh Token은 무효 (JWT 필터·탈퇴 시 refresh token 삭제)

## API
- GET `/api/v1/users/me`
- POST `/api/v1/users/me/onboarding` (닉네임 + 응원 구단)
- PATCH `/api/v1/users/me`
- DELETE `/api/v1/users/me` (Soft Delete, `WithdrawRequest` body)
