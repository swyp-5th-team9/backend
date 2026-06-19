# user 도메인

회원 정보, 역할(FAN/OWNER/ADMIN), 온보딩, 즐겨찾기.

**담당**: 주양 (auth 도메인과 한 묶음)

## 패키지 구조
- `controller/UserController.java`
- `service/UserService.java`
- `repository/UserRepository.java`
- `dto/` — UserResponse, OnboardingRequest 등
- `entity/` — User, UserRole
- `exception/UserNotFoundException.java`

## API
- GET `/api/v1/users/me`
- POST `/api/v1/users/me/onboarding` (닉네임 + 응원 구단)
- PATCH `/api/v1/users/me`
- DELETE `/api/v1/users/me` (Soft Delete)
