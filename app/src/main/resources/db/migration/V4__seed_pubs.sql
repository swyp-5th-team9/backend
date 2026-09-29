-- ============================================================================
-- V4 시연 시드 — 펍 6개 + 자식 6종 (#25 PR ②)
--   배포 분포: 강남구 2 / 마포구 2 / 송파구 2
--   - BBox 테스트 가능한 실제 좌표 분포
--   - 자정 넘김 영업·휴무 케이스 포함 (businessStatus 계산 검증용)
--   - status 다양화: OPEN 5 / TEMP_CLOSED 1
--   - 화이트리스트: V3 (facility 11 / style 6 / theme 5 / food 11)
--   - team_id 매핑 (V1 INSERT 순서):
--       1=LG, 2=두산, 3=KT, 4=SSG, 5=NC, 6=KIA, 7=롯데, 8=삼성, 9=한화, 10=키움
-- ============================================================================

-- ----------------------------------------------------------------------------
-- 1) pubs (pub_id 1~6 명시 INSERT)
-- ----------------------------------------------------------------------------
INSERT INTO pubs (pub_id, name, address, region, latitude, longitude,
                  phone, status, capacity_range, group_seat_max_people,
                  favorite_count, description) VALUES
    (1, '치어스 강남점',          '서울 강남구 강남대로 396',     'GANGNAM',
        37.4979, 127.0276, '02-555-1234', 'OPEN',         'R_50_100', 30,
        24, '잠실 더비 단골 펍. 대형 스크린 + 단체석.'),
    (2, '와우펍 강남역점',         '서울 강남구 테헤란로 132',     'GANGNAM',
        37.4985, 127.0289, '02-555-5678', 'OPEN',         'UNDER_20', NULL,
        7,  '혼자 와서 카운터석에서 관전하기 좋음.'),
    (3, '야구장 펍 홍대점',        '서울 마포구 양화로 156',       'MAPO',
        37.5563, 126.9226, '02-333-2222', 'OPEN',         'R_50_100', 40,
        41, '루프탑 + 대형 스크린, 그룹 단체석 다수.'),
    (4, '응원홀 합정점',           '서울 마포구 양화로 45',        'MAPO',
        37.5495, 126.9134, '02-333-9999', 'TEMP_CLOSED',  'OVER_100', 60,
        12, '리뉴얼 임시 휴업 중 (시연용 TEMP_CLOSED).'),
    (5, '잠실펍',                  '서울 송파구 올림픽로 240',     'SONGPA',
        37.5128, 127.1003, '02-414-7000', 'OPEN',         'OVER_100', 80,
        67, '잠실야구장 도보 5분, 경기 종료 후 새벽 3시까지.'),
    (6, '베이스볼바 잠실새내점',   '서울 송파구 백제고분로 350',   'SONGPA',
        37.5118, 127.0856, '02-414-1212', 'OPEN',         'R_20_50',  NULL,
        15, '롯데팬 모임 단골. 분식 안주 강세.');

-- ----------------------------------------------------------------------------
-- 2) pub_supported_teams
-- ----------------------------------------------------------------------------
INSERT INTO pub_supported_teams (pub_id, team_id) VALUES
    (1, 1), (1, 2),                      -- 치어스 강남: LG, 두산
    (2, 6),                              -- 와우펍 강남: KIA
    (3, 2), (3, 10),                     -- 야구장 펍 홍대: 두산, 키움
    (4, 4), (4, 9),                      -- 응원홀 합정: SSG, 한화
    (5, 1), (5, 2),                      -- 잠실펍: LG, 두산 (잠실 더비)
    (6, 7);                              -- 베이스볼바: 롯데

-- ----------------------------------------------------------------------------
-- 3) pub_facilities (V3 화이트리스트)
-- ----------------------------------------------------------------------------
INSERT INTO pub_facilities (pub_id, facility_code) VALUES
    (1, 'GROUP_SEAT'), (1, 'PARKING'), (1, 'VALET_PARKING'), (1, 'PRIVATE_BOOKING'),
    (2, 'COUNTER_SEAT'), (2, 'SOLO_SEAT'), (2, 'PET_FRIENDLY'),
    (3, 'GROUP_SEAT'), (3, 'SPACIOUS_AREA'), (3, 'ROOFTOP'),
    (4, 'GROUP_SEAT'), (4, 'PRIVATE_BOOKING'), (4, 'WHEELCHAIR_ACCESS'),
    (5, 'GROUP_SEAT'), (5, 'COUNTER_SEAT'), (5, 'PARKING'), (5, 'TERRACE'), (5, 'PRIVATE_BOOKING'),
    (6, 'COUNTER_SEAT'), (6, 'SOLO_SEAT');

-- ----------------------------------------------------------------------------
-- 4) pub_styles (V3, 6종)
-- ----------------------------------------------------------------------------
INSERT INTO pub_styles (pub_id, style_code) VALUES
    (1, 'BIG_SCREEN'), (1, 'OFFICIAL_PUB'),
    (2, 'MULTI_TV'), (2, 'SPECTATOR_MODE'),
    (3, 'BIG_SCREEN'), (3, 'STADIUM_MODE'), (3, 'LOUD_SPEAKER'),
    (4, 'OFFICIAL_PUB'), (4, 'STADIUM_MODE'),
    (5, 'BIG_SCREEN'), (5, 'MULTI_TV'), (5, 'STADIUM_MODE'),
    (6, 'SPECTATOR_MODE');

-- ----------------------------------------------------------------------------
-- 5) pub_themes (V3, 5종)
-- ----------------------------------------------------------------------------
INSERT INTO pub_themes (pub_id, theme_code) VALUES
    (1, 'SPACIOUS_VIEW'),
    (2, 'COMFY_SEAT'),
    (3, 'EXOTIC'), (3, 'SPACIOUS_VIEW'),
    (4, 'SPECIAL_MENU'),
    (5, 'SPACIOUS_VIEW'), (5, 'COMFY_SEAT'),
    (6, 'FRESH');

-- ----------------------------------------------------------------------------
-- 6) pub_food_tags (V3, 11종)
-- ----------------------------------------------------------------------------
INSERT INTO pub_food_tags (pub_id, food_code) VALUES
    (1, 'CHICKEN'), (1, 'BEER'), (1, 'HIGHBALL'),
    (2, 'COCKTAIL'), (2, 'DRY_SNACK'),
    (3, 'PIZZA'), (3, 'TACO'), (3, 'BEER'),
    (4, 'GRILLED'), (4, 'SOJU'), (4, 'BEER'),
    (5, 'CHICKEN'), (5, 'FRIES'), (5, 'BEER'), (5, 'HIGHBALL'),
    (6, 'BUNSIK'), (6, 'BEER');

-- ----------------------------------------------------------------------------
-- 7) pub_business_hours (월~일 7건 × 6펍)
--    - 자정 넘김 케이스: close < open (예: 17:00 → 02:00 = 익일 마감)
--    - 휴무 케이스: is_closed=TRUE, open/close NULL (제약 ck_pub_business_hours_closed)
-- ----------------------------------------------------------------------------
INSERT INTO pub_business_hours (pub_id, day_of_week, open_time, close_time, is_closed) VALUES
    -- pub 1: 치어스 강남점 — 월~토 17:00~02:00(자정 넘김), 일 휴무
    (1, 1, '17:00', '02:00', FALSE),
    (1, 2, '17:00', '02:00', FALSE),
    (1, 3, '17:00', '02:00', FALSE),
    (1, 4, '17:00', '02:00', FALSE),
    (1, 5, '17:00', '02:00', FALSE),
    (1, 6, '17:00', '02:00', FALSE),
    (1, 7, NULL,    NULL,    TRUE),
    -- pub 2: 와우펍 강남역점 — 매일 18:00~01:00 (자정 넘김)
    (2, 1, '18:00', '01:00', FALSE),
    (2, 2, '18:00', '01:00', FALSE),
    (2, 3, '18:00', '01:00', FALSE),
    (2, 4, '18:00', '01:00', FALSE),
    (2, 5, '18:00', '01:00', FALSE),
    (2, 6, '18:00', '01:00', FALSE),
    (2, 7, '18:00', '01:00', FALSE),
    -- pub 3: 야구장 펍 홍대점 — 화~일 16:00~24:00, 월 휴무
    (3, 1, NULL,    NULL,    TRUE),
    (3, 2, '16:00', '23:59', FALSE),
    (3, 3, '16:00', '23:59', FALSE),
    (3, 4, '16:00', '23:59', FALSE),
    (3, 5, '16:00', '23:59', FALSE),
    (3, 6, '16:00', '23:59', FALSE),
    (3, 7, '16:00', '23:59', FALSE),
    -- pub 4: 응원홀 합정점 — 매일 12:00~24:00 (status=TEMP_CLOSED 와 별개 표시용)
    (4, 1, '12:00', '23:59', FALSE),
    (4, 2, '12:00', '23:59', FALSE),
    (4, 3, '12:00', '23:59', FALSE),
    (4, 4, '12:00', '23:59', FALSE),
    (4, 5, '12:00', '23:59', FALSE),
    (4, 6, '12:00', '23:59', FALSE),
    (4, 7, '12:00', '23:59', FALSE),
    -- pub 5: 잠실펍 — 매일 17:00~03:00 (자정 넘김 — 경기 후 새벽)
    (5, 1, '17:00', '03:00', FALSE),
    (5, 2, '17:00', '03:00', FALSE),
    (5, 3, '17:00', '03:00', FALSE),
    (5, 4, '17:00', '03:00', FALSE),
    (5, 5, '17:00', '03:00', FALSE),
    (5, 6, '17:00', '03:00', FALSE),
    (5, 7, '17:00', '03:00', FALSE),
    -- pub 6: 베이스볼바 잠실새내점 — 화~일 17:00~01:00, 월 휴무
    (6, 1, NULL,    NULL,    TRUE),
    (6, 2, '17:00', '01:00', FALSE),
    (6, 3, '17:00', '01:00', FALSE),
    (6, 4, '17:00', '01:00', FALSE),
    (6, 5, '17:00', '01:00', FALSE),
    (6, 6, '17:00', '01:00', FALSE),
    (6, 7, '17:00', '01:00', FALSE);

-- ----------------------------------------------------------------------------
-- 8) menus
-- ----------------------------------------------------------------------------
INSERT INTO menus (pub_id, name, category, price, display_order) VALUES
    (1, '치킨 세트',       'SET',   25000, 0),
    (1, '생맥주 500cc',    'DRINK', 5000,  1),
    (1, '감자튀김',        'FOOD',  8000,  2),
    (2, '클래식 모히토',   'DRINK', 12000, 0),
    (2, '마른안주 세트',   'FOOD',  15000, 1),
    (3, '페퍼로니 피자',   'FOOD',  22000, 0),
    (3, '타코 3종',        'FOOD',  18000, 1),
    (3, '수입맥주',        'DRINK', 9000,  2),
    (4, '모듬구이',        'FOOD',  32000, 0),
    (4, '소주',            'DRINK', 5000,  1),
    (5, '양념치킨',        'FOOD',  23000, 0),
    (5, '트러플 감자',     'FOOD',  11000, 1),
    (5, '생맥주 500cc',    'DRINK', 5500,  2),
    (5, '하이볼',          'DRINK', 9000,  3),
    (6, '떡볶이',          'FOOD',  9000,  0),
    (6, '컵라면',          'FOOD',  6000,  1),
    (6, '생맥주 500cc',    'DRINK', 5000,  2);

-- ----------------------------------------------------------------------------
-- 9) pub_images (placeholder URL — 시연용)
-- ----------------------------------------------------------------------------
INSERT INTO pub_images (pub_id, image_url, display_order) VALUES
    (1, 'https://placehold.co/600x400/png?text=Cheers+Gangnam+1', 0),
    (1, 'https://placehold.co/600x400/png?text=Cheers+Gangnam+2', 1),
    (2, 'https://placehold.co/600x400/png?text=WowPub+Gangnam',   0),
    (3, 'https://placehold.co/600x400/png?text=Stadium+Hongdae+1', 0),
    (3, 'https://placehold.co/600x400/png?text=Stadium+Hongdae+2', 1),
    (3, 'https://placehold.co/600x400/png?text=Stadium+Hongdae+3', 2),
    (4, 'https://placehold.co/600x400/png?text=Cheer+Hall+1', 0),
    (4, 'https://placehold.co/600x400/png?text=Cheer+Hall+2', 1),
    (5, 'https://placehold.co/600x400/png?text=Jamsil+Pub+1', 0),
    (5, 'https://placehold.co/600x400/png?text=Jamsil+Pub+2', 1),
    (5, 'https://placehold.co/600x400/png?text=Jamsil+Pub+3', 2),
    (6, 'https://placehold.co/600x400/png?text=Baseball+Bar', 0);
