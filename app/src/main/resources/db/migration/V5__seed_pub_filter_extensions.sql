-- ============================================================================
-- V5 — 필터 확장 (#25 후속)
--   - 디자인팀 필터 화면 보강: 야외좌석 / 예약가능 / 단일 TV
--   - V3 CHECK 화이트리스트 갱신 후 V4 펍에 신규 코드 시드 분포
-- ============================================================================

-- ----------------------------------------------------------------------------
-- 1) pub_facilities CHECK 확장 — OUTDOOR_SEAT, RESERVATION 추가
-- ----------------------------------------------------------------------------
ALTER TABLE pub_facilities
    DROP CONSTRAINT ck_pub_facilities_code;

ALTER TABLE pub_facilities
    ADD CONSTRAINT ck_pub_facilities_code
        CHECK (facility_code IN (
            'GROUP_SEAT',
            'COUNTER_SEAT',
            'TERRACE',
            'SOLO_SEAT',
            'SPACIOUS_AREA',
            'ROOFTOP',
            'PARKING',
            'VALET_PARKING',
            'PET_FRIENDLY',
            'PRIVATE_BOOKING',
            'WHEELCHAIR_ACCESS',
            'OUTDOOR_SEAT',       -- 야외좌석
            'RESERVATION'         -- 예약가능
        ));

-- ----------------------------------------------------------------------------
-- 2) pub_styles CHECK 확장 — SINGLE_TV 추가
-- ----------------------------------------------------------------------------
ALTER TABLE pub_styles
    DROP CONSTRAINT ck_pub_styles_code;

ALTER TABLE pub_styles
    ADD CONSTRAINT ck_pub_styles_code
        CHECK (style_code IN (
            'BIG_SCREEN',
            'SINGLE_TV',          -- 단일 TV
            'MULTI_TV',
            'OFFICIAL_PUB',
            'STADIUM_MODE',
            'SPECTATOR_MODE',
            'LOUD_SPEAKER'
        ));

-- ----------------------------------------------------------------------------
-- 3) 신규 코드 시드 분포 (V4 펍 1~6 기준)
-- ----------------------------------------------------------------------------
INSERT INTO pub_facilities (pub_id, facility_code) VALUES
    (1, 'OUTDOOR_SEAT'),
    (1, 'RESERVATION'),
    (2, 'RESERVATION'),
    (5, 'OUTDOOR_SEAT'),
    (6, 'RESERVATION');

INSERT INTO pub_styles (pub_id, style_code) VALUES
    (2, 'SINGLE_TV'),
    (3, 'SINGLE_TV');
