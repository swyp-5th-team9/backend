-- Issue #54: 제보 category/status enum을 API 명세에 맞게 갱신

ALTER TABLE reports DROP CONSTRAINT ck_reports_category;
ALTER TABLE reports DROP CONSTRAINT ck_reports_status;

ALTER TABLE reports ADD CONSTRAINT ck_reports_category
    CHECK (category IN ('PUB_INFO', 'APP_ERROR', 'OTHER'));

ALTER TABLE reports ADD CONSTRAINT ck_reports_status
    CHECK (status IN ('PENDING', 'IN_PROGRESS', 'COMPLETED', 'REJECTED'));
