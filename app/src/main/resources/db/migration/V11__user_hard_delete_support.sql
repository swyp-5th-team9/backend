-- =============================================================================
-- V11: 회원 Hard Delete 지원 (탈퇴 보관 기간 경과 후 users row 물리 삭제)
--   - withdrawal_reasons, reports: users 삭제 시 CASCADE
--   - report_images는 reports ON DELETE CASCADE로 연쇄 삭제
--   - favorites, user_favorite_teams, refresh_tokens: 기존 CASCADE 유지
-- =============================================================================

ALTER TABLE withdrawal_reasons
    DROP CONSTRAINT fk_withdrawal_reasons_user;

ALTER TABLE withdrawal_reasons
    ADD CONSTRAINT fk_withdrawal_reasons_user
        FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE;

ALTER TABLE reports
    DROP CONSTRAINT fk_reports_user;

ALTER TABLE reports
    ADD CONSTRAINT fk_reports_user
        FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE;

CREATE INDEX idx_users_deleted_at ON users(deleted_at) WHERE deleted_at IS NOT NULL;
