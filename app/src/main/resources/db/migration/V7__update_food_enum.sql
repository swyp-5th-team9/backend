-- =============================================================================
-- V6: 음식·주류 enum 재편 (이슈 #65)
--   - 삭제: FRIES (감자튀김) — FRY (튀김) 카테고리로 흡수
--   - 추가: FRY (튀김), STEW (찌개/탕)
--   - 워딩 변경: GRILLED 라벨 "구이류" → "구이/볶음" (코드 동일, enum 변경만)
--
-- 처리 순서:
--   1) CHECK 제약 DROP (UPDATE 가 기존 제약에 막히지 않게 먼저 풀어줌)
--   2) 기존 FRIES 데이터 → FRY 로 갈음 (시드 1건: 펍 5 잠실펍)
--   3) CHECK 제약 ADD (FRIES 빠지고 FRY/STEW 추가된 신규 화이트리스트)
-- =============================================================================

ALTER TABLE pub_food_tags
    DROP CONSTRAINT ck_pub_food_tags_code;

UPDATE pub_food_tags
SET food_code = 'FRY'
WHERE food_code = 'FRIES';

ALTER TABLE pub_food_tags
    ADD CONSTRAINT ck_pub_food_tags_code
    CHECK (food_code IN (
        'CHICKEN',        -- 치킨
        'PIZZA',          -- 피자
        'TACO',           -- 타코
        'FRY',            -- 튀김
        'STEW',           -- 찌개/탕
        'GRILLED',        -- 구이/볶음
        'BUNSIK',         -- 분식
        'DRY_SNACK',      -- 마른안주
        'SOJU',           -- 소주
        'BEER',           -- 맥주
        'COCKTAIL',       -- 칵테일
        'HIGHBALL'        -- 하이볼
    ));
