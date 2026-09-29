-- ============================================================================
-- V3 펍 필터 확장 (#35)
--   - pub_facilities CHECK 화이트리스트 재정의 (디자인팀 필터 화면 기준)
--   - pub_styles / pub_themes / pub_food_tags 신규 (펍 1 : 코드 N)
--   - 지역(region)·영업시간 스키마는 변경 없음
--     · region: VARCHAR(30) 그대로, 서울 19 자치구 코드 + 광역 코드는
--               백엔드 Region enum 메타로 관리
--     · business_hours: 기존 테이블 그대로, 영업상태/요일 필터는 쿼리 로직
-- ============================================================================

-- ----------------------------------------------------------------------------
-- 1) pub_facilities CHECK 교체
--   V1 baseline 의 코드 10종(BIG_SCREEN/PROJECTOR/KIDS_ZONE/NO_SMOKING/
--   LATE_NIGHT/WIFI/RESERVATION 등)은 디자인팀 필터 화면에 없는 항목.
--   "시설" 카테고리 화이트리스트를 디자인 명세대로 재정의.
-- ----------------------------------------------------------------------------
ALTER TABLE pub_facilities
    DROP CONSTRAINT ck_pub_facilities_code;

ALTER TABLE pub_facilities
    ADD CONSTRAINT ck_pub_facilities_code
        CHECK (facility_code IN (
            'GROUP_SEAT',         -- 단체석
            'COUNTER_SEAT',       -- 카운터석
            'TERRACE',            -- 테라스
            'SOLO_SEAT',          -- 1인석
            'SPACIOUS_AREA',      -- 넓은 공간
            'ROOFTOP',            -- 루프탑
            'PARKING',            -- 주차
            'VALET_PARKING',      -- 발렛파킹
            'PET_FRIENDLY',       -- 반려동물 동반
            'PRIVATE_BOOKING',    -- 단체 대관
            'WHEELCHAIR_ACCESS'   -- 휠체어 접근
        ));

-- ----------------------------------------------------------------------------
-- 2) pub_styles  (펍스타일)
-- ----------------------------------------------------------------------------
CREATE TABLE pub_styles (
    pub_id      BIGINT       NOT NULL,
    style_code  VARCHAR(30)  NOT NULL,
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (pub_id, style_code),
    CONSTRAINT fk_pub_styles_pub
        FOREIGN KEY (pub_id) REFERENCES pubs(pub_id) ON DELETE CASCADE,
    CONSTRAINT ck_pub_styles_code
        CHECK (style_code IN (
            'BIG_SCREEN',     -- 대형 스크린
            'MULTI_TV',       -- 다중 TV
            'OFFICIAL_PUB',   -- 공식 펍
            'STADIUM_MODE',   -- 경기장 모드
            'SPECTATOR_MODE', -- 관전 모드
            'LOUD_SPEAKER'    -- 사운드 빵빵
        ))
);

CREATE INDEX idx_pub_styles_code ON pub_styles(style_code);

-- ----------------------------------------------------------------------------
-- 3) pub_themes (테마)
-- ----------------------------------------------------------------------------
CREATE TABLE pub_themes (
    pub_id      BIGINT       NOT NULL,
    theme_code  VARCHAR(30)  NOT NULL,
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (pub_id, theme_code),
    CONSTRAINT fk_pub_themes_pub
        FOREIGN KEY (pub_id) REFERENCES pubs(pub_id) ON DELETE CASCADE,
    CONSTRAINT ck_pub_themes_code
        CHECK (theme_code IN (
            'EXOTIC',         -- 이국적
            'SPACIOUS_VIEW',  -- 넓은 뷰
            'SPECIAL_MENU',   -- 특별 메뉴
            'FRESH',          -- 깔끔/신선
            'COMFY_SEAT'      -- 편안한 좌석
        ))
);

CREATE INDEX idx_pub_themes_code ON pub_themes(theme_code);

-- ----------------------------------------------------------------------------
-- 4) pub_food_tags (음식·주류 태그)
-- ----------------------------------------------------------------------------
CREATE TABLE pub_food_tags (
    pub_id      BIGINT       NOT NULL,
    food_code   VARCHAR(30)  NOT NULL,
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (pub_id, food_code),
    CONSTRAINT fk_pub_food_tags_pub
        FOREIGN KEY (pub_id) REFERENCES pubs(pub_id) ON DELETE CASCADE,
    CONSTRAINT ck_pub_food_tags_code
        CHECK (food_code IN (
            'CHICKEN',        -- 치킨
            'PIZZA',          -- 피자
            'TACO',           -- 타코
            'FRIES',          -- 감자튀김
            'BUNSIK',         -- 분식
            'GRILLED',        -- 구이류
            'DRY_SNACK',      -- 마른안주
            'BEER',           -- 맥주
            'COCKTAIL',       -- 칵테일
            'HIGHBALL',       -- 하이볼
            'SOJU'            -- 소주
        ))
);

CREATE INDEX idx_pub_food_tags_code ON pub_food_tags(food_code);
