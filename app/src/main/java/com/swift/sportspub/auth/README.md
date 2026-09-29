# auth 도메인

소셜 로그인 (카카오·네이버) + JWT 발급/검증.

**담당**: 주양

## 패키지 구조
- `controller/` — AuthController
- `service/` — AuthService
- `jwt/` — JwtProvider, JwtAuthenticationFilter
- `oauth2/` — OAuth2UserInfo, CustomOAuth2UserService, OAuth2SuccessHandler
- `dto/` — TokenResponse 등

## 참고
- 스켈레톤 문서: `docs/프로젝트_스켈레톤.md`
- application.yml의 `spring.security.oauth2.client.*` 설정 참고
