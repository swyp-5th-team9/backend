-- =============================================================================
-- V8: 지역 필터 sub_region 컬럼 추가 (이슈 #70)
--   - 팀 합의 혼합 방식: 대부분 구 단위 + 마포·송파만 상권 세분화
--   - sub 3종: JAMSIL(잠실/잠실새내), HONGDAE_HAPJEONG(홍대/합정), SANGAM_MANGWON(상암/망원)
--
-- 처리 순서:
--   1) sub_region 컬럼 추가 (NULLABLE, 기존 데이터는 sub 없음이 기본)
--   2) 시드 매핑: 마포(pub 3,4 홍대/합정) → HONGDAE_HAPJEONG,
--                송파(pub 5,6 잠실/잠실새내) → JAMSIL
--   3) CHECK 제약: NULL 허용 + 화이트리스트 3개
-- =============================================================================

ALTER TABLE pubs
    ADD COLUMN sub_region VARCHAR(30);

UPDATE pubs SET sub_region = 'HONGDAE_HAPJEONG' WHERE pub_id IN (3, 4);
UPDATE pubs SET sub_region = 'JAMSIL' WHERE pub_id IN (5, 6);

ALTER TABLE pubs
    ADD CONSTRAINT ck_pubs_sub_region
    CHECK (sub_region IS NULL OR sub_region IN (
        'JAMSIL',            -- 잠실/잠실새내 (송파)
        'HONGDAE_HAPJEONG',  -- 홍대/합정 (마포)
        'SANGAM_MANGWON'     -- 상암/망원 (마포)
    ));
