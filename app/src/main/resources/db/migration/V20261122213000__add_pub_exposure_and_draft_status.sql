-- 새로 등록한 펍은 공개 전 대기다. 이미 있는 펍은 지금처럼 앱에 보이게 둔다.

ALTER TABLE pubs ADD COLUMN exposure VARCHAR(20);
ALTER TABLE pubs ADD COLUMN draft_status VARCHAR(20);

UPDATE pubs SET exposure = 'PUBLIC' WHERE exposure IS NULL;
UPDATE pubs SET draft_status = 'COMPLETE' WHERE draft_status IS NULL;

ALTER TABLE pubs ALTER COLUMN exposure SET DEFAULT 'PRIVATE';
ALTER TABLE pubs ALTER COLUMN exposure SET NOT NULL;
ALTER TABLE pubs ALTER COLUMN draft_status SET DEFAULT 'DRAFT';
ALTER TABLE pubs ALTER COLUMN draft_status SET NOT NULL;

ALTER TABLE pubs ADD CONSTRAINT ck_pubs_exposure
    CHECK (exposure IN ('PUBLIC', 'PRIVATE'));
ALTER TABLE pubs ADD CONSTRAINT ck_pubs_draft_status
    CHECK (draft_status IN ('DRAFT', 'COMPLETE'));
