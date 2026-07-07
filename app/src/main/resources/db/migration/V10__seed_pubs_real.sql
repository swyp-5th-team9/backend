-- =============================================================================
-- V10: 실 매장 시드 40건 (V4 시연 시드 대체)
--
--   Part A. pubs.latitude / longitude NOT NULL 및 범위 CHECK 제거
--           - 실 매장 40건 조사 데이터에 위도/경도가 없어 NULL 허용으로 완화
--           - Pub 엔티티 @Column(nullable = true) 동기 변경
--
--   Part B. V4 시연 시드(pub_id 1..6) 및 자식 데이터 FK-safe 삭제
--           - 삭제 후 IDENTITY 시퀀스 RESTART WITH 1
--
--   Part C. 실 매장 40건 시드 (pub_id 1..40 명시 INSERT)
--           - 자식 테이블: pub_supported_teams / pub_facilities / pub_styles /
--                        pub_themes / pub_food_tags / pub_business_hours
--           - latitude / longitude / phone / capacity_note 는 시드하지 않음
--           - team_id 매핑 (V1 INSERT 순서):
--               1=LG, 2=두산, 3=KT, 4=SSG, 5=NC, 6=KIA, 7=롯데, 8=삼성, 9=한화, 10=키움
--             "모든팀" = 1..10 전부 시드
--           - business_hours: ISO-8601 (1=월 ... 7=일), 자정 넘김 허용,
--             정기휴무는 is_closed=TRUE + open/close NULL
-- =============================================================================

-- =============================================================================
-- Part A. 좌표 NOT NULL / 범위 CHECK 제거
-- =============================================================================
ALTER TABLE pubs DROP CONSTRAINT ck_pubs_latitude;
ALTER TABLE pubs DROP CONSTRAINT ck_pubs_longitude;
ALTER TABLE pubs ALTER COLUMN latitude  DROP NOT NULL;
ALTER TABLE pubs ALTER COLUMN longitude DROP NOT NULL;

-- =============================================================================
-- Part B. V4 시연 시드 제거 (FK-safe 순서)
-- =============================================================================
DELETE FROM pub_food_tags       WHERE pub_id IN (1, 2, 3, 4, 5, 6);
DELETE FROM pub_themes          WHERE pub_id IN (1, 2, 3, 4, 5, 6);
DELETE FROM pub_styles          WHERE pub_id IN (1, 2, 3, 4, 5, 6);
DELETE FROM pub_facilities      WHERE pub_id IN (1, 2, 3, 4, 5, 6);
DELETE FROM pub_business_hours  WHERE pub_id IN (1, 2, 3, 4, 5, 6);
DELETE FROM pub_supported_teams WHERE pub_id IN (1, 2, 3, 4, 5, 6);
DELETE FROM menus               WHERE pub_id IN (1, 2, 3, 4, 5, 6);
DELETE FROM pub_images          WHERE pub_id IN (1, 2, 3, 4, 5, 6);
DELETE FROM pubs                WHERE pub_id IN (1, 2, 3, 4, 5, 6);

ALTER TABLE pubs ALTER COLUMN pub_id RESTART WITH 1;

-- =============================================================================
-- Part C. 실 매장 40건 시드
-- =============================================================================

-- pub_id 1: 3355펍 (GURO / —)
INSERT INTO pubs (pub_id, name, address, region, sub_region, status, capacity_range, group_seat_max_people, favorite_count, description) VALUES
    (1, '3355펍', '서울 구로구 새말로 97 대륭포스트타워 3층', 'GURO', NULL, 'OPEN', 'R_50_100', 90, 0,
     '신도림역 대륭포스트타워 3층 90명 규모 스포츠 펍, 대형 스크린 + 다중 TV로 야구·해외축구 종합 중계, 단체·회식·데이트 특화, 소주·맥주·하이볼·칵테일');
INSERT INTO pub_supported_teams (pub_id, team_id) VALUES
    (1, 1), (1, 2), (1, 3), (1, 4), (1, 5), (1, 6), (1, 7), (1, 8), (1, 9), (1, 10);
INSERT INTO pub_facilities (pub_id, facility_code) VALUES
    (1, 'GROUP_SEAT'), (1, 'RESERVATION'), (1, 'SPACIOUS_AREA'), (1, 'PRIVATE_BOOKING');
INSERT INTO pub_styles (pub_id, style_code) VALUES
    (1, 'BIG_SCREEN'), (1, 'MULTI_TV'), (1, 'SPECTATOR_MODE');
INSERT INTO pub_themes (pub_id, theme_code) VALUES
    (1, 'SPACIOUS_VIEW'), (1, 'SPECIAL_MENU');
INSERT INTO pub_food_tags (pub_id, food_code) VALUES
    (1, 'CHICKEN'), (1, 'FRY'), (1, 'GRILLED'), (1, 'DRY_SNACK'), (1, 'SOJU'), (1, 'BEER'), (1, 'HIGHBALL'), (1, 'COCKTAIL');
INSERT INTO pub_business_hours (pub_id, day_of_week, open_time, close_time, is_closed) VALUES
    (1, 1, '17:00', '02:00', FALSE),
    (1, 2, '17:00', '02:00', FALSE),
    (1, 3, '17:00', '02:00', FALSE),
    (1, 4, '17:00', '02:00', FALSE),
    (1, 5, '17:00', '02:00', FALSE),
    (1, 6, '17:00', '02:00', FALSE),
    (1, 7, '17:00', '02:00', FALSE);

-- pub_id 2: 곰배곰배 (DONGDAEMUN / —)
INSERT INTO pubs (pub_id, name, address, region, sub_region, status, capacity_range, group_seat_max_people, favorite_count, description) VALUES
    (2, '곰배곰배', '서울 동대문구 왕산로 137 1층', 'DONGDAEMUN', NULL, 'OPEN', NULL, NULL, 0,
     '청량리역 도보 5분 왕산로 1층 두산베어스 응원 특화 스포츠 펍, 단일 TV 야구 중계, 소주·맥주·하이볼');
INSERT INTO pub_supported_teams (pub_id, team_id) VALUES
    (2, 2);
INSERT INTO pub_facilities (pub_id, facility_code) VALUES
    (2, 'RESERVATION');
INSERT INTO pub_styles (pub_id, style_code) VALUES
    (2, 'SINGLE_TV'), (2, 'STADIUM_MODE');
INSERT INTO pub_themes (pub_id, theme_code) VALUES
    (2, 'SPECIAL_MENU');
INSERT INTO pub_food_tags (pub_id, food_code) VALUES
    (2, 'GRILLED'), (2, 'FRY'), (2, 'SOJU'), (2, 'BEER'), (2, 'HIGHBALL');
INSERT INTO pub_business_hours (pub_id, day_of_week, open_time, close_time, is_closed) VALUES
    (2, 1, NULL,    NULL,    TRUE),
    (2, 2, '17:00', '02:00', FALSE),
    (2, 3, '17:00', '02:00', FALSE),
    (2, 4, '17:00', '02:00', FALSE),
    (2, 5, '17:00', '02:00', FALSE),
    (2, 6, '17:00', '02:00', FALSE),
    (2, 7, '17:00', '02:00', FALSE);

-- pub_id 3: 낭만포차 (DONGJAK / —)
INSERT INTO pubs (pub_id, name, address, region, sub_region, status, capacity_range, group_seat_max_people, favorite_count, description) VALUES
    (3, '낭만포차', '서울 동작구 사당로 1층', 'DONGJAK', NULL, 'OPEN', NULL, NULL, 0,
     '사당역 인근 사당로 1층 LG트윈스 응원 포차, 단일 TV 야구 중계, 소주·맥주·하이볼');
INSERT INTO pub_supported_teams (pub_id, team_id) VALUES
    (3, 1);
INSERT INTO pub_facilities (pub_id, facility_code) VALUES
    (3, 'RESERVATION');
INSERT INTO pub_styles (pub_id, style_code) VALUES
    (3, 'SINGLE_TV'), (3, 'STADIUM_MODE');
INSERT INTO pub_themes (pub_id, theme_code) VALUES
    (3, 'SPECIAL_MENU');
INSERT INTO pub_food_tags (pub_id, food_code) VALUES
    (3, 'GRILLED'), (3, 'STEW'), (3, 'SOJU'), (3, 'BEER'), (3, 'HIGHBALL');
INSERT INTO pub_business_hours (pub_id, day_of_week, open_time, close_time, is_closed) VALUES
    (3, 1, '17:00', '02:00', FALSE),
    (3, 2, '17:00', '02:00', FALSE),
    (3, 3, '17:00', '02:00', FALSE),
    (3, 4, '17:00', '02:00', FALSE),
    (3, 5, '17:00', '02:00', FALSE),
    (3, 6, '17:00', '02:00', FALSE),
    (3, 7, '17:00', '02:00', FALSE);

-- pub_id 4: 노가리 (YEONGDEUNGPO / —)
INSERT INTO pubs (pub_id, name, address, region, sub_region, status, capacity_range, group_seat_max_people, favorite_count, description) VALUES
    (4, '노가리', '서울 영등포구 여의대로 1층', 'YEONGDEUNGPO', NULL, 'OPEN', 'OVER_100', 180, 0,
     '여의도 여의대로 대형 노가리 포차, 180명 규모 넓은 홀 + 대형 스크린 + 다중 TV, 한화이글스 응원 브랜딩, 소주·맥주·하이볼');
INSERT INTO pub_supported_teams (pub_id, team_id) VALUES
    (4, 9);
INSERT INTO pub_facilities (pub_id, facility_code) VALUES
    (4, 'GROUP_SEAT'), (4, 'RESERVATION'), (4, 'SPACIOUS_AREA'), (4, 'PRIVATE_BOOKING');
INSERT INTO pub_styles (pub_id, style_code) VALUES
    (4, 'BIG_SCREEN'), (4, 'MULTI_TV'), (4, 'STADIUM_MODE');
INSERT INTO pub_themes (pub_id, theme_code) VALUES
    (4, 'SPACIOUS_VIEW'), (4, 'SPECIAL_MENU');
INSERT INTO pub_food_tags (pub_id, food_code) VALUES
    (4, 'DRY_SNACK'), (4, 'GRILLED'), (4, 'FRY'), (4, 'SOJU'), (4, 'BEER'), (4, 'HIGHBALL');
INSERT INTO pub_business_hours (pub_id, day_of_week, open_time, close_time, is_closed) VALUES
    (4, 1, '17:00', '02:00', FALSE),
    (4, 2, '17:00', '02:00', FALSE),
    (4, 3, '17:00', '02:00', FALSE),
    (4, 4, '17:00', '02:00', FALSE),
    (4, 5, '17:00', '02:00', FALSE),
    (4, 6, '17:00', '02:00', FALSE),
    (4, 7, '17:00', '02:00', FALSE);

-- pub_id 5: 당인리극장 (MAPO / HONGDAE_HAPJEONG)
INSERT INTO pubs (pub_id, name, address, region, sub_region, status, capacity_range, group_seat_max_people, favorite_count, description) VALUES
    (5, '당인리극장', '서울 마포구 독막로 1층', 'MAPO', 'HONGDAE_HAPJEONG', 'OPEN', 'R_50_100', 70, 0,
     '홍대·합정 상권 독막로 1층 70명 규모 한화이글스 응원 극장식 펍, 대형 스크린 관전, 월요일 정기휴무, 소주·맥주·하이볼');
INSERT INTO pub_supported_teams (pub_id, team_id) VALUES
    (5, 9);
INSERT INTO pub_facilities (pub_id, facility_code) VALUES
    (5, 'GROUP_SEAT'), (5, 'RESERVATION'), (5, 'PRIVATE_BOOKING');
INSERT INTO pub_styles (pub_id, style_code) VALUES
    (5, 'BIG_SCREEN'), (5, 'STADIUM_MODE'), (5, 'LOUD_SPEAKER');
INSERT INTO pub_themes (pub_id, theme_code) VALUES
    (5, 'SPECIAL_MENU');
INSERT INTO pub_food_tags (pub_id, food_code) VALUES
    (5, 'CHICKEN'), (5, 'GRILLED'), (5, 'FRY'), (5, 'SOJU'), (5, 'BEER'), (5, 'HIGHBALL');
INSERT INTO pub_business_hours (pub_id, day_of_week, open_time, close_time, is_closed) VALUES
    (5, 1, NULL,    NULL,    TRUE),
    (5, 2, '17:00', '02:00', FALSE),
    (5, 3, '17:00', '02:00', FALSE),
    (5, 4, '17:00', '02:00', FALSE),
    (5, 5, '17:00', '02:00', FALSE),
    (5, 6, '17:00', '02:00', FALSE),
    (5, 7, '17:00', '02:00', FALSE);

-- pub_id 6: 더블플레이치킨 홍대점 (MAPO / HONGDAE_HAPJEONG)
INSERT INTO pubs (pub_id, name, address, region, sub_region, status, capacity_range, group_seat_max_people, favorite_count, description) VALUES
    (6, '더블플레이치킨 홍대점', '서울 마포구 홍익로 1층', 'MAPO', 'HONGDAE_HAPJEONG', 'OPEN', 'R_50_100', 80, 0,
     '홍대입구 홍익로 1층 80명 규모 야구 콘셉트 치킨 펍, 대형 스크린 + 다중 TV 종합 중계, 치킨·튀김·맥주·하이볼');
INSERT INTO pub_supported_teams (pub_id, team_id) VALUES
    (6, 1), (6, 2), (6, 3), (6, 4), (6, 5), (6, 6), (6, 7), (6, 8), (6, 9), (6, 10);
INSERT INTO pub_facilities (pub_id, facility_code) VALUES
    (6, 'GROUP_SEAT'), (6, 'RESERVATION'), (6, 'SPACIOUS_AREA');
INSERT INTO pub_styles (pub_id, style_code) VALUES
    (6, 'BIG_SCREEN'), (6, 'MULTI_TV'), (6, 'SPECTATOR_MODE');
INSERT INTO pub_themes (pub_id, theme_code) VALUES
    (6, 'SPECIAL_MENU');
INSERT INTO pub_food_tags (pub_id, food_code) VALUES
    (6, 'CHICKEN'), (6, 'FRY'), (6, 'BEER'), (6, 'HIGHBALL');
INSERT INTO pub_business_hours (pub_id, day_of_week, open_time, close_time, is_closed) VALUES
    (6, 1, '17:00', '02:00', FALSE),
    (6, 2, '17:00', '02:00', FALSE),
    (6, 3, '17:00', '02:00', FALSE),
    (6, 4, '17:00', '02:00', FALSE),
    (6, 5, '17:00', '02:00', FALSE),
    (6, 6, '17:00', '02:00', FALSE),
    (6, 7, '17:00', '02:00', FALSE);

-- pub_id 7: 드래프트128 (YEONGDEUNGPO / —)
INSERT INTO pubs (pub_id, name, address, region, sub_region, status, capacity_range, group_seat_max_people, favorite_count, description) VALUES
    (7, '드래프트128', '서울 영등포구 국회대로 128', 'YEONGDEUNGPO', NULL, 'OPEN', NULL, NULL, 0,
     '여의도 국회대로 LG트윈스 응원 크래프트 비어 펍, 단일 TV 야구 중계, 토·일 정기휴무 (평일 직장인 상권), 수제맥주·하이볼');
INSERT INTO pub_supported_teams (pub_id, team_id) VALUES
    (7, 1);
INSERT INTO pub_facilities (pub_id, facility_code) VALUES
    (7, 'RESERVATION'), (7, 'COUNTER_SEAT');
INSERT INTO pub_styles (pub_id, style_code) VALUES
    (7, 'SINGLE_TV'), (7, 'STADIUM_MODE');
INSERT INTO pub_themes (pub_id, theme_code) VALUES
    (7, 'SPECIAL_MENU');
INSERT INTO pub_food_tags (pub_id, food_code) VALUES
    (7, 'DRY_SNACK'), (7, 'FRY'), (7, 'BEER'), (7, 'HIGHBALL');
INSERT INTO pub_business_hours (pub_id, day_of_week, open_time, close_time, is_closed) VALUES
    (7, 1, '17:00', '01:00', FALSE),
    (7, 2, '17:00', '01:00', FALSE),
    (7, 3, '17:00', '01:00', FALSE),
    (7, 4, '17:00', '01:00', FALSE),
    (7, 5, '17:00', '01:00', FALSE),
    (7, 6, NULL,    NULL,    TRUE),
    (7, 7, NULL,    NULL,    TRUE);

-- pub_id 8: 레코드피자 샤로수길 (GWANAK / —)
INSERT INTO pubs (pub_id, name, address, region, sub_region, status, capacity_range, group_seat_max_people, favorite_count, description) VALUES
    (8, '레코드피자 샤로수길', '서울 관악구 관악로14길 1층', 'GWANAK', NULL, 'OPEN', 'R_20_50', 30, 0,
     '샤로수길 관악로14길 1층 30명 규모 피자·수제맥주 펍, 단일 TV 종합 야구 중계, 피자·맥주·하이볼');
INSERT INTO pub_supported_teams (pub_id, team_id) VALUES
    (8, 1), (8, 2), (8, 3), (8, 4), (8, 5), (8, 6), (8, 7), (8, 8), (8, 9), (8, 10);
INSERT INTO pub_facilities (pub_id, facility_code) VALUES
    (8, 'GROUP_SEAT'), (8, 'RESERVATION');
INSERT INTO pub_styles (pub_id, style_code) VALUES
    (8, 'SINGLE_TV'), (8, 'SPECTATOR_MODE');
INSERT INTO pub_themes (pub_id, theme_code) VALUES
    (8, 'SPECIAL_MENU');
INSERT INTO pub_food_tags (pub_id, food_code) VALUES
    (8, 'PIZZA'), (8, 'FRY'), (8, 'BEER'), (8, 'HIGHBALL');
INSERT INTO pub_business_hours (pub_id, day_of_week, open_time, close_time, is_closed) VALUES
    (8, 1, '17:00', '01:00', FALSE),
    (8, 2, '17:00', '01:00', FALSE),
    (8, 3, '17:00', '01:00', FALSE),
    (8, 4, '17:00', '01:00', FALSE),
    (8, 5, '17:00', '02:00', FALSE),
    (8, 6, '17:00', '02:00', FALSE),
    (8, 7, '17:00', '01:00', FALSE);

-- pub_id 9: 리얼펍 잠실새내 (SONGPA / JAMSIL)
INSERT INTO pubs (pub_id, name, address, region, sub_region, status, capacity_range, group_seat_max_people, favorite_count, description) VALUES
    (9, '리얼펍 잠실새내', '서울 송파구 백제고분로7길 1층', 'SONGPA', 'JAMSIL', 'OPEN', 'R_50_100', 60, 0,
     '잠실새내 백제고분로7길 1층 60명 규모 두산·LG 잠실 더비 응원 펍, 대형 스크린 + 다중 TV, 소주·맥주·하이볼');
INSERT INTO pub_supported_teams (pub_id, team_id) VALUES
    (9, 1), (9, 2);
INSERT INTO pub_facilities (pub_id, facility_code) VALUES
    (9, 'GROUP_SEAT'), (9, 'RESERVATION'), (9, 'SPACIOUS_AREA'), (9, 'PRIVATE_BOOKING');
INSERT INTO pub_styles (pub_id, style_code) VALUES
    (9, 'BIG_SCREEN'), (9, 'MULTI_TV'), (9, 'STADIUM_MODE'), (9, 'LOUD_SPEAKER');
INSERT INTO pub_themes (pub_id, theme_code) VALUES
    (9, 'SPECIAL_MENU');
INSERT INTO pub_food_tags (pub_id, food_code) VALUES
    (9, 'CHICKEN'), (9, 'GRILLED'), (9, 'FRY'), (9, 'STEW'), (9, 'SOJU'), (9, 'BEER'), (9, 'HIGHBALL');
INSERT INTO pub_business_hours (pub_id, day_of_week, open_time, close_time, is_closed) VALUES
    (9, 1, '17:00', '02:00', FALSE),
    (9, 2, '17:00', '02:00', FALSE),
    (9, 3, '17:00', '02:00', FALSE),
    (9, 4, '17:00', '02:00', FALSE),
    (9, 5, '17:00', '02:00', FALSE),
    (9, 6, '17:00', '02:00', FALSE),
    (9, 7, '17:00', '02:00', FALSE);

-- pub_id 10: 마디그라 (JONGNO / —)
INSERT INTO pubs (pub_id, name, address, region, sub_region, status, capacity_range, group_seat_max_people, favorite_count, description) VALUES
    (10, '마디그라', '서울 종로구 종로 1층', 'JONGNO', NULL, 'OPEN', 'R_50_100', 80, 0,
     '종로 직장인 상권 1층 80명 규모 이국적 콘셉트 스포츠 펍, 대형 스크린 + 다중 TV 종합 중계, 토·일 정기휴무, 소주·맥주·하이볼·칵테일');
INSERT INTO pub_supported_teams (pub_id, team_id) VALUES
    (10, 1), (10, 2), (10, 3), (10, 4), (10, 5), (10, 6), (10, 7), (10, 8), (10, 9), (10, 10);
INSERT INTO pub_facilities (pub_id, facility_code) VALUES
    (10, 'GROUP_SEAT'), (10, 'RESERVATION'), (10, 'SPACIOUS_AREA'), (10, 'PRIVATE_BOOKING');
INSERT INTO pub_styles (pub_id, style_code) VALUES
    (10, 'BIG_SCREEN'), (10, 'MULTI_TV'), (10, 'SPECTATOR_MODE');
INSERT INTO pub_themes (pub_id, theme_code) VALUES
    (10, 'EXOTIC'), (10, 'SPECIAL_MENU');
INSERT INTO pub_food_tags (pub_id, food_code) VALUES
    (10, 'GRILLED'), (10, 'FRY'), (10, 'SOJU'), (10, 'BEER'), (10, 'HIGHBALL'), (10, 'COCKTAIL');
INSERT INTO pub_business_hours (pub_id, day_of_week, open_time, close_time, is_closed) VALUES
    (10, 1, '17:00', '02:00', FALSE),
    (10, 2, '17:00', '02:00', FALSE),
    (10, 3, '17:00', '02:00', FALSE),
    (10, 4, '17:00', '02:00', FALSE),
    (10, 5, '17:00', '02:00', FALSE),
    (10, 6, NULL,    NULL,    TRUE),
    (10, 7, NULL,    NULL,    TRUE);

-- pub_id 11: 매치볼하우스 (GANGBUK / —)
INSERT INTO pubs (pub_id, name, address, region, sub_region, status, capacity_range, group_seat_max_people, favorite_count, description) VALUES
    (11, '매치볼하우스', '서울 강북구 도봉로 1층', 'GANGBUK', NULL, 'OPEN', NULL, NULL, 0,
     '수유·강북 도봉로 1층 야구·해외축구 종합 관전 펍, 대형 스크린 + 다중 TV, 월요일 정기휴무, 소주·맥주·하이볼');
INSERT INTO pub_supported_teams (pub_id, team_id) VALUES
    (11, 1), (11, 2), (11, 3), (11, 4), (11, 5), (11, 6), (11, 7), (11, 8), (11, 9), (11, 10);
INSERT INTO pub_facilities (pub_id, facility_code) VALUES
    (11, 'GROUP_SEAT'), (11, 'RESERVATION');
INSERT INTO pub_styles (pub_id, style_code) VALUES
    (11, 'BIG_SCREEN'), (11, 'MULTI_TV'), (11, 'SPECTATOR_MODE');
INSERT INTO pub_themes (pub_id, theme_code) VALUES
    (11, 'SPECIAL_MENU');
INSERT INTO pub_food_tags (pub_id, food_code) VALUES
    (11, 'GRILLED'), (11, 'FRY'), (11, 'SOJU'), (11, 'BEER'), (11, 'HIGHBALL');
INSERT INTO pub_business_hours (pub_id, day_of_week, open_time, close_time, is_closed) VALUES
    (11, 1, NULL,    NULL,    TRUE),
    (11, 2, '17:00', '02:00', FALSE),
    (11, 3, '17:00', '02:00', FALSE),
    (11, 4, '17:00', '02:00', FALSE),
    (11, 5, '17:00', '02:00', FALSE),
    (11, 6, '17:00', '02:00', FALSE),
    (11, 7, '17:00', '02:00', FALSE);

-- pub_id 12: 베이직프라이드치킨 (MAPO / SANGAM_MANGWON)
INSERT INTO pubs (pub_id, name, address, region, sub_region, status, capacity_range, group_seat_max_people, favorite_count, description) VALUES
    (12, '베이직프라이드치킨', '서울 마포구 월드컵로 1층', 'MAPO', 'SANGAM_MANGWON', 'OPEN', 'R_50_100', 50, 0,
     '상암·망원 월드컵로 1층 50명 규모 프라이드 치킨 전문 스포츠 펍, 대형 스크린 + 다중 TV 종합 중계, 일요일 정기휴무, 치킨·튀김·맥주·하이볼');
INSERT INTO pub_supported_teams (pub_id, team_id) VALUES
    (12, 1), (12, 2), (12, 3), (12, 4), (12, 5), (12, 6), (12, 7), (12, 8), (12, 9), (12, 10);
INSERT INTO pub_facilities (pub_id, facility_code) VALUES
    (12, 'GROUP_SEAT'), (12, 'RESERVATION'), (12, 'SPACIOUS_AREA');
INSERT INTO pub_styles (pub_id, style_code) VALUES
    (12, 'BIG_SCREEN'), (12, 'MULTI_TV'), (12, 'SPECTATOR_MODE');
INSERT INTO pub_themes (pub_id, theme_code) VALUES
    (12, 'SPECIAL_MENU'), (12, 'FRESH');
INSERT INTO pub_food_tags (pub_id, food_code) VALUES
    (12, 'CHICKEN'), (12, 'FRY'), (12, 'BEER'), (12, 'HIGHBALL'), (12, 'SOJU');
INSERT INTO pub_business_hours (pub_id, day_of_week, open_time, close_time, is_closed) VALUES
    (12, 1, '17:00', '01:00', FALSE),
    (12, 2, '17:00', '01:00', FALSE),
    (12, 3, '17:00', '01:00', FALSE),
    (12, 4, '17:00', '01:00', FALSE),
    (12, 5, '17:00', '02:00', FALSE),
    (12, 6, '17:00', '02:00', FALSE),
    (12, 7, NULL,    NULL,    TRUE);

-- pub_id 13: 삼층맥주집 이수 (DONGJAK / —)
INSERT INTO pubs (pub_id, name, address, region, sub_region, status, capacity_range, group_seat_max_people, favorite_count, description) VALUES
    (13, '삼층맥주집 이수', '서울 동작구 사당로 3층', 'DONGJAK', NULL, 'OPEN', 'R_50_100', 90, 0,
     '이수역 사당로 3층 90명 규모 대형 스크린 스포츠 펍, 야구 종합 중계, 소주·맥주·하이볼');
INSERT INTO pub_supported_teams (pub_id, team_id) VALUES
    (13, 1), (13, 2), (13, 3), (13, 4), (13, 5), (13, 6), (13, 7), (13, 8), (13, 9), (13, 10);
INSERT INTO pub_facilities (pub_id, facility_code) VALUES
    (13, 'GROUP_SEAT'), (13, 'RESERVATION'), (13, 'SPACIOUS_AREA'), (13, 'PRIVATE_BOOKING');
INSERT INTO pub_styles (pub_id, style_code) VALUES
    (13, 'BIG_SCREEN'), (13, 'MULTI_TV'), (13, 'SPECTATOR_MODE');
INSERT INTO pub_themes (pub_id, theme_code) VALUES
    (13, 'SPACIOUS_VIEW'), (13, 'SPECIAL_MENU');
INSERT INTO pub_food_tags (pub_id, food_code) VALUES
    (13, 'GRILLED'), (13, 'FRY'), (13, 'DRY_SNACK'), (13, 'SOJU'), (13, 'BEER'), (13, 'HIGHBALL');
INSERT INTO pub_business_hours (pub_id, day_of_week, open_time, close_time, is_closed) VALUES
    (13, 1, '17:00', '02:00', FALSE),
    (13, 2, '17:00', '02:00', FALSE),
    (13, 3, '17:00', '02:00', FALSE),
    (13, 4, '17:00', '02:00', FALSE),
    (13, 5, '17:00', '02:00', FALSE),
    (13, 6, '17:00', '02:00', FALSE),
    (13, 7, '17:00', '02:00', FALSE);

-- pub_id 14: 서울갈매기 (MAPO / HONGDAE_HAPJEONG)
INSERT INTO pubs (pub_id, name, address, region, sub_region, status, capacity_range, group_seat_max_people, favorite_count, description) VALUES
    (14, '서울갈매기', '서울 마포구 어울마당로 1층', 'MAPO', 'HONGDAE_HAPJEONG', 'OPEN', 'R_20_50', NULL, 0,
     '홍대·합정 어울마당로 1층 롯데자이언츠 응원 특화 포차, 단일 TV 야구 중계, 월요일 정기휴무, 소주·맥주·하이볼');
INSERT INTO pub_supported_teams (pub_id, team_id) VALUES
    (14, 7);
INSERT INTO pub_facilities (pub_id, facility_code) VALUES
    (14, 'RESERVATION');
INSERT INTO pub_styles (pub_id, style_code) VALUES
    (14, 'SINGLE_TV'), (14, 'STADIUM_MODE');
INSERT INTO pub_themes (pub_id, theme_code) VALUES
    (14, 'SPECIAL_MENU');
INSERT INTO pub_food_tags (pub_id, food_code) VALUES
    (14, 'GRILLED'), (14, 'DRY_SNACK'), (14, 'SOJU'), (14, 'BEER'), (14, 'HIGHBALL');
INSERT INTO pub_business_hours (pub_id, day_of_week, open_time, close_time, is_closed) VALUES
    (14, 1, NULL,    NULL,    TRUE),
    (14, 2, '17:00', '02:00', FALSE),
    (14, 3, '17:00', '02:00', FALSE),
    (14, 4, '17:00', '02:00', FALSE),
    (14, 5, '17:00', '02:00', FALSE),
    (14, 6, '17:00', '02:00', FALSE),
    (14, 7, '17:00', '02:00', FALSE);

-- pub_id 15: 설맥 건대점 (GWANGJIN / —)
INSERT INTO pubs (pub_id, name, address, region, sub_region, status, capacity_range, group_seat_max_people, favorite_count, description) VALUES
    (15, '설맥 건대점', '서울 광진구 아차산로 1층', 'GWANGJIN', NULL, 'OPEN', 'R_50_100', 60, 0,
     '건대입구 아차산로 1층 60명 규모 스포츠 펍, 대형 스크린 + 다중 TV 종합 야구 중계, 소주·맥주·하이볼');
INSERT INTO pub_supported_teams (pub_id, team_id) VALUES
    (15, 1), (15, 2), (15, 3), (15, 4), (15, 5), (15, 6), (15, 7), (15, 8), (15, 9), (15, 10);
INSERT INTO pub_facilities (pub_id, facility_code) VALUES
    (15, 'GROUP_SEAT'), (15, 'RESERVATION'), (15, 'SPACIOUS_AREA');
INSERT INTO pub_styles (pub_id, style_code) VALUES
    (15, 'BIG_SCREEN'), (15, 'MULTI_TV'), (15, 'SPECTATOR_MODE');
INSERT INTO pub_themes (pub_id, theme_code) VALUES
    (15, 'SPECIAL_MENU');
INSERT INTO pub_food_tags (pub_id, food_code) VALUES
    (15, 'GRILLED'), (15, 'FRY'), (15, 'STEW'), (15, 'SOJU'), (15, 'BEER'), (15, 'HIGHBALL');
INSERT INTO pub_business_hours (pub_id, day_of_week, open_time, close_time, is_closed) VALUES
    (15, 1, '17:00', '02:00', FALSE),
    (15, 2, '17:00', '02:00', FALSE),
    (15, 3, '17:00', '02:00', FALSE),
    (15, 4, '17:00', '02:00', FALSE),
    (15, 5, '17:00', '02:00', FALSE),
    (15, 6, '17:00', '02:00', FALSE),
    (15, 7, '17:00', '02:00', FALSE);

-- pub_id 16: 스패로우 상계 (NOWON / —)
INSERT INTO pubs (pub_id, name, address, region, sub_region, status, capacity_range, group_seat_max_people, favorite_count, description) VALUES
    (16, '스패로우 상계', '서울 노원구 상계로 1층', 'NOWON', NULL, 'OPEN', 'R_20_50', 12, 0,
     '노원 상계 1층 12명 규모 소형 카운터 펍, 단일 TV 종합 야구 관전, 월요일 정기휴무, 수제맥주·하이볼·소주');
INSERT INTO pub_supported_teams (pub_id, team_id) VALUES
    (16, 1), (16, 2), (16, 3), (16, 4), (16, 5), (16, 6), (16, 7), (16, 8), (16, 9), (16, 10);
INSERT INTO pub_facilities (pub_id, facility_code) VALUES
    (16, 'COUNTER_SEAT'), (16, 'SOLO_SEAT'), (16, 'RESERVATION');
INSERT INTO pub_styles (pub_id, style_code) VALUES
    (16, 'SINGLE_TV'), (16, 'SPECTATOR_MODE');
INSERT INTO pub_themes (pub_id, theme_code) VALUES
    (16, 'COMFY_SEAT'), (16, 'SPECIAL_MENU');
INSERT INTO pub_food_tags (pub_id, food_code) VALUES
    (16, 'DRY_SNACK'), (16, 'FRY'), (16, 'BEER'), (16, 'HIGHBALL'), (16, 'SOJU');
INSERT INTO pub_business_hours (pub_id, day_of_week, open_time, close_time, is_closed) VALUES
    (16, 1, NULL,    NULL,    TRUE),
    (16, 2, '18:00', '01:00', FALSE),
    (16, 3, '18:00', '01:00', FALSE),
    (16, 4, '18:00', '01:00', FALSE),
    (16, 5, '18:00', '01:00', FALSE),
    (16, 6, '18:00', '02:00', FALSE),
    (16, 7, '18:00', '01:00', FALSE);

-- pub_id 17: 야구는 핑계고 (SONGPA / JAMSIL)
INSERT INTO pubs (pub_id, name, address, region, sub_region, status, capacity_range, group_seat_max_people, favorite_count, description) VALUES
    (17, '야구는 핑계고', '서울 송파구 백제고분로7길 1층', 'SONGPA', 'JAMSIL', 'OPEN', 'R_50_100', NULL, 0,
     '잠실새내 백제고분로7길 1층 야구 종합 응원 펍, 대형 스크린 + 다중 TV 관전, 소주·맥주·하이볼');
INSERT INTO pub_supported_teams (pub_id, team_id) VALUES
    (17, 1), (17, 2), (17, 3), (17, 4), (17, 5), (17, 6), (17, 7), (17, 8), (17, 9), (17, 10);
INSERT INTO pub_facilities (pub_id, facility_code) VALUES
    (17, 'RESERVATION'), (17, 'SPACIOUS_AREA');
INSERT INTO pub_styles (pub_id, style_code) VALUES
    (17, 'BIG_SCREEN'), (17, 'MULTI_TV'), (17, 'STADIUM_MODE');
INSERT INTO pub_themes (pub_id, theme_code) VALUES
    (17, 'SPECIAL_MENU');
INSERT INTO pub_food_tags (pub_id, food_code) VALUES
    (17, 'GRILLED'), (17, 'FRY'), (17, 'SOJU'), (17, 'BEER'), (17, 'HIGHBALL');
INSERT INTO pub_business_hours (pub_id, day_of_week, open_time, close_time, is_closed) VALUES
    (17, 1, '17:00', '02:00', FALSE),
    (17, 2, '17:00', '02:00', FALSE),
    (17, 3, '17:00', '02:00', FALSE),
    (17, 4, '17:00', '02:00', FALSE),
    (17, 5, '17:00', '02:00', FALSE),
    (17, 6, '17:00', '02:00', FALSE),
    (17, 7, '17:00', '02:00', FALSE);

-- pub_id 18: 야구보러가자 (JUNGNANG / —)
INSERT INTO pubs (pub_id, name, address, region, sub_region, status, capacity_range, group_seat_max_people, favorite_count, description) VALUES
    (18, '야구보러가자', '서울 중랑구 봉화산로 1층', 'JUNGNANG', NULL, 'OPEN', 'R_20_50', 40, 0,
     '중랑 봉화산로 1층 40명 규모 야구 응원 특화 펍, 대형 스크린 + 다중 TV 종합 중계, 소주·맥주·하이볼');
INSERT INTO pub_supported_teams (pub_id, team_id) VALUES
    (18, 1), (18, 2), (18, 3), (18, 4), (18, 5), (18, 6), (18, 7), (18, 8), (18, 9), (18, 10);
INSERT INTO pub_facilities (pub_id, facility_code) VALUES
    (18, 'GROUP_SEAT'), (18, 'RESERVATION');
INSERT INTO pub_styles (pub_id, style_code) VALUES
    (18, 'BIG_SCREEN'), (18, 'MULTI_TV'), (18, 'STADIUM_MODE');
INSERT INTO pub_themes (pub_id, theme_code) VALUES
    (18, 'SPECIAL_MENU');
INSERT INTO pub_food_tags (pub_id, food_code) VALUES
    (18, 'CHICKEN'), (18, 'GRILLED'), (18, 'FRY'), (18, 'SOJU'), (18, 'BEER'), (18, 'HIGHBALL');
INSERT INTO pub_business_hours (pub_id, day_of_week, open_time, close_time, is_closed) VALUES
    (18, 1, '17:00', '02:00', FALSE),
    (18, 2, '17:00', '02:00', FALSE),
    (18, 3, '17:00', '02:00', FALSE),
    (18, 4, '17:00', '02:00', FALSE),
    (18, 5, '17:00', '02:00', FALSE),
    (18, 6, '17:00', '02:00', FALSE),
    (18, 7, '17:00', '02:00', FALSE);

-- pub_id 19: 엘지포차 (JONGNO / —)
INSERT INTO pubs (pub_id, name, address, region, sub_region, status, capacity_range, group_seat_max_people, favorite_count, description) VALUES
    (19, '엘지포차', '서울 종로구 종로 1층', 'JONGNO', NULL, 'OPEN', 'R_20_50', 40, 0,
     '종로 1층 40명 규모 LG트윈스 응원 특화 포차, 단일 TV 야구 중계, 소주·맥주·하이볼');
INSERT INTO pub_supported_teams (pub_id, team_id) VALUES
    (19, 1);
INSERT INTO pub_facilities (pub_id, facility_code) VALUES
    (19, 'GROUP_SEAT'), (19, 'RESERVATION');
INSERT INTO pub_styles (pub_id, style_code) VALUES
    (19, 'SINGLE_TV'), (19, 'STADIUM_MODE');
INSERT INTO pub_themes (pub_id, theme_code) VALUES
    (19, 'SPECIAL_MENU');
INSERT INTO pub_food_tags (pub_id, food_code) VALUES
    (19, 'GRILLED'), (19, 'STEW'), (19, 'FRY'), (19, 'SOJU'), (19, 'BEER'), (19, 'HIGHBALL');
INSERT INTO pub_business_hours (pub_id, day_of_week, open_time, close_time, is_closed) VALUES
    (19, 1, '17:00', '02:00', FALSE),
    (19, 2, '17:00', '02:00', FALSE),
    (19, 3, '17:00', '02:00', FALSE),
    (19, 4, '17:00', '02:00', FALSE),
    (19, 5, '17:00', '02:00', FALSE),
    (19, 6, '17:00', '02:00', FALSE),
    (19, 7, '17:00', '02:00', FALSE);

-- pub_id 20: 연무장 동대문 (JUNG / —)
INSERT INTO pubs (pub_id, name, address, region, sub_region, status, capacity_range, group_seat_max_people, favorite_count, description) VALUES
    (20, '연무장 동대문', '서울 중구 을지로 1층', 'JUNG', NULL, 'OPEN', 'R_50_100', 80, 0,
     '동대문·을지로 1층 80명 규모 연무장 콘셉트 스포츠 펍, 대형 스크린 + 다중 TV 종합 중계, 소주·맥주·하이볼');
INSERT INTO pub_supported_teams (pub_id, team_id) VALUES
    (20, 1), (20, 2), (20, 3), (20, 4), (20, 5), (20, 6), (20, 7), (20, 8), (20, 9), (20, 10);
INSERT INTO pub_facilities (pub_id, facility_code) VALUES
    (20, 'GROUP_SEAT'), (20, 'RESERVATION'), (20, 'SPACIOUS_AREA'), (20, 'PRIVATE_BOOKING');
INSERT INTO pub_styles (pub_id, style_code) VALUES
    (20, 'BIG_SCREEN'), (20, 'MULTI_TV'), (20, 'SPECTATOR_MODE');
INSERT INTO pub_themes (pub_id, theme_code) VALUES
    (20, 'SPACIOUS_VIEW'), (20, 'SPECIAL_MENU');
INSERT INTO pub_food_tags (pub_id, food_code) VALUES
    (20, 'GRILLED'), (20, 'FRY'), (20, 'DRY_SNACK'), (20, 'SOJU'), (20, 'BEER'), (20, 'HIGHBALL');
INSERT INTO pub_business_hours (pub_id, day_of_week, open_time, close_time, is_closed) VALUES
    (20, 1, '17:00', '02:00', FALSE),
    (20, 2, '17:00', '02:00', FALSE),
    (20, 3, '17:00', '02:00', FALSE),
    (20, 4, '17:00', '02:00', FALSE),
    (20, 5, '17:00', '02:00', FALSE),
    (20, 6, '17:00', '02:00', FALSE),
    (20, 7, '17:00', '02:00', FALSE);

-- pub_id 21: 연화주점 (MAPO / SANGAM_MANGWON)
INSERT INTO pubs (pub_id, name, address, region, sub_region, status, capacity_range, group_seat_max_people, favorite_count, description) VALUES
    (21, '연화주점', '서울 마포구 망원로 1층', 'MAPO', 'SANGAM_MANGWON', 'OPEN', 'R_20_50', 14, 0,
     '망원 망원로 1층 14명 규모 소형 LG트윈스 응원 주점, 단일 TV 야구 중계, 소주·맥주·하이볼');
INSERT INTO pub_supported_teams (pub_id, team_id) VALUES
    (21, 1);
INSERT INTO pub_facilities (pub_id, facility_code) VALUES
    (21, 'COUNTER_SEAT'), (21, 'SOLO_SEAT'), (21, 'RESERVATION');
INSERT INTO pub_styles (pub_id, style_code) VALUES
    (21, 'SINGLE_TV'), (21, 'STADIUM_MODE');
INSERT INTO pub_themes (pub_id, theme_code) VALUES
    (21, 'COMFY_SEAT'), (21, 'SPECIAL_MENU');
INSERT INTO pub_food_tags (pub_id, food_code) VALUES
    (21, 'GRILLED'), (21, 'STEW'), (21, 'SOJU'), (21, 'BEER'), (21, 'HIGHBALL');
INSERT INTO pub_business_hours (pub_id, day_of_week, open_time, close_time, is_closed) VALUES
    (21, 1, '18:00', '01:00', FALSE),
    (21, 2, '18:00', '01:00', FALSE),
    (21, 3, '18:00', '01:00', FALSE),
    (21, 4, '18:00', '01:00', FALSE),
    (21, 5, '18:00', '02:00', FALSE),
    (21, 6, '18:00', '02:00', FALSE),
    (21, 7, '18:00', '01:00', FALSE);

-- pub_id 22: 오하이요 (SONGPA / JAMSIL)
INSERT INTO pubs (pub_id, name, address, region, sub_region, status, capacity_range, group_seat_max_people, favorite_count, description) VALUES
    (22, '오하이요', '서울 송파구 백제고분로7길 1층', 'SONGPA', 'JAMSIL', 'OPEN', 'R_20_50', 30, 0,
     '잠실새내 백제고분로7길 1층 30명 규모 롯데자이언츠 응원 이자카야, 단일 TV 야구 중계, 소주·맥주·하이볼');
INSERT INTO pub_supported_teams (pub_id, team_id) VALUES
    (22, 7);
INSERT INTO pub_facilities (pub_id, facility_code) VALUES
    (22, 'GROUP_SEAT'), (22, 'RESERVATION');
INSERT INTO pub_styles (pub_id, style_code) VALUES
    (22, 'SINGLE_TV'), (22, 'STADIUM_MODE');
INSERT INTO pub_themes (pub_id, theme_code) VALUES
    (22, 'EXOTIC'), (22, 'SPECIAL_MENU');
INSERT INTO pub_food_tags (pub_id, food_code) VALUES
    (22, 'GRILLED'), (22, 'FRY'), (22, 'DRY_SNACK'), (22, 'SOJU'), (22, 'BEER'), (22, 'HIGHBALL');
INSERT INTO pub_business_hours (pub_id, day_of_week, open_time, close_time, is_closed) VALUES
    (22, 1, '17:00', '02:00', FALSE),
    (22, 2, '17:00', '02:00', FALSE),
    (22, 3, '17:00', '02:00', FALSE),
    (22, 4, '17:00', '02:00', FALSE),
    (22, 5, '17:00', '02:00', FALSE),
    (22, 6, '17:00', '02:00', FALSE),
    (22, 7, '17:00', '02:00', FALSE);

-- pub_id 23: 외계인피자 (EUNPYEONG / —)
INSERT INTO pubs (pub_id, name, address, region, sub_region, status, capacity_range, group_seat_max_people, favorite_count, description) VALUES
    (23, '외계인피자', '서울 은평구 통일로 1층', 'EUNPYEONG', NULL, 'OPEN', 'R_20_50', 30, 0,
     '은평 통일로 1층 30명 규모 이국적 피자 콘셉트 LG트윈스 응원 펍, 단일 TV 야구 중계, 월요일 정기휴무, 피자·맥주·하이볼');
INSERT INTO pub_supported_teams (pub_id, team_id) VALUES
    (23, 1);
INSERT INTO pub_facilities (pub_id, facility_code) VALUES
    (23, 'GROUP_SEAT'), (23, 'RESERVATION');
INSERT INTO pub_styles (pub_id, style_code) VALUES
    (23, 'SINGLE_TV'), (23, 'STADIUM_MODE');
INSERT INTO pub_themes (pub_id, theme_code) VALUES
    (23, 'EXOTIC'), (23, 'SPECIAL_MENU');
INSERT INTO pub_food_tags (pub_id, food_code) VALUES
    (23, 'PIZZA'), (23, 'FRY'), (23, 'BEER'), (23, 'HIGHBALL');
INSERT INTO pub_business_hours (pub_id, day_of_week, open_time, close_time, is_closed) VALUES
    (23, 1, NULL,    NULL,    TRUE),
    (23, 2, '17:00', '01:00', FALSE),
    (23, 3, '17:00', '01:00', FALSE),
    (23, 4, '17:00', '01:00', FALSE),
    (23, 5, '17:00', '02:00', FALSE),
    (23, 6, '17:00', '02:00', FALSE),
    (23, 7, '17:00', '01:00', FALSE);

-- pub_id 24: 을지OB베어 (JUNG / —)
INSERT INTO pubs (pub_id, name, address, region, sub_region, status, capacity_range, group_seat_max_people, favorite_count, description) VALUES
    (24, '을지OB베어', '서울 중구 을지로 1층', 'JUNG', NULL, 'OPEN', 'OVER_100', 200, 0,
     '을지로 노가리 골목 대형 두산베어스 응원 특화 노포, 200명 규모 넓은 홀 + 대관 가능, 소주·맥주·하이볼');
INSERT INTO pub_supported_teams (pub_id, team_id) VALUES
    (24, 2);
INSERT INTO pub_facilities (pub_id, facility_code) VALUES
    (24, 'GROUP_SEAT'), (24, 'RESERVATION'), (24, 'SPACIOUS_AREA'), (24, 'PRIVATE_BOOKING');
INSERT INTO pub_styles (pub_id, style_code) VALUES
    (24, 'STADIUM_MODE');
INSERT INTO pub_themes (pub_id, theme_code) VALUES
    (24, 'SPACIOUS_VIEW'), (24, 'SPECIAL_MENU');
INSERT INTO pub_food_tags (pub_id, food_code) VALUES
    (24, 'DRY_SNACK'), (24, 'GRILLED'), (24, 'SOJU'), (24, 'BEER'), (24, 'HIGHBALL');
INSERT INTO pub_business_hours (pub_id, day_of_week, open_time, close_time, is_closed) VALUES
    (24, 1, '15:00', '23:59', FALSE),
    (24, 2, '15:00', '23:59', FALSE),
    (24, 3, '15:00', '23:59', FALSE),
    (24, 4, '15:00', '23:59', FALSE),
    (24, 5, '15:00', '23:59', FALSE),
    (24, 6, '15:00', '23:59', FALSE),
    (24, 7, '15:00', '23:59', FALSE);

-- pub_id 25: 인저리타임 (GANGSEO / —)
INSERT INTO pubs (pub_id, name, address, region, sub_region, status, capacity_range, group_seat_max_people, favorite_count, description) VALUES
    (25, '인저리타임', '서울 강서구 강서로 1층', 'GANGSEO', NULL, 'OPEN', 'R_20_50', 33, 0,
     '강서 강서로 1층 33명 규모 LG트윈스 응원 스포츠 펍, 단일 TV 야구·해외축구 관전, 신선한 재료 강조, 소주·맥주·하이볼');
INSERT INTO pub_supported_teams (pub_id, team_id) VALUES
    (25, 1);
INSERT INTO pub_facilities (pub_id, facility_code) VALUES
    (25, 'GROUP_SEAT'), (25, 'RESERVATION');
INSERT INTO pub_styles (pub_id, style_code) VALUES
    (25, 'SINGLE_TV'), (25, 'STADIUM_MODE');
INSERT INTO pub_themes (pub_id, theme_code) VALUES
    (25, 'SPECIAL_MENU'), (25, 'FRESH');
INSERT INTO pub_food_tags (pub_id, food_code) VALUES
    (25, 'GRILLED'), (25, 'FRY'), (25, 'DRY_SNACK'), (25, 'SOJU'), (25, 'BEER'), (25, 'HIGHBALL');
INSERT INTO pub_business_hours (pub_id, day_of_week, open_time, close_time, is_closed) VALUES
    (25, 1, '17:00', '02:00', FALSE),
    (25, 2, '17:00', '02:00', FALSE),
    (25, 3, '17:00', '02:00', FALSE),
    (25, 4, '17:00', '02:00', FALSE),
    (25, 5, '17:00', '02:00', FALSE),
    (25, 6, '17:00', '02:00', FALSE),
    (25, 7, '17:00', '02:00', FALSE);

-- pub_id 26: 맥JOO (SONGPA / JAMSIL)
INSERT INTO pubs (pub_id, name, address, region, sub_region, status, capacity_range, group_seat_max_people, favorite_count, description) VALUES
    (26, '맥JOO', '서울 송파구 백제고분로7길 1층', 'SONGPA', 'JAMSIL', 'OPEN', 'R_20_50', 30, 0,
     '잠실새내 백제고분로7길 1층 30명 규모 종합 야구 관전 펍, 단일 TV 관전, 소주·맥주·하이볼');
INSERT INTO pub_supported_teams (pub_id, team_id) VALUES
    (26, 1), (26, 2), (26, 3), (26, 4), (26, 5), (26, 6), (26, 7), (26, 8), (26, 9), (26, 10);
INSERT INTO pub_facilities (pub_id, facility_code) VALUES
    (26, 'GROUP_SEAT'), (26, 'RESERVATION');
INSERT INTO pub_styles (pub_id, style_code) VALUES
    (26, 'SINGLE_TV'), (26, 'SPECTATOR_MODE');
INSERT INTO pub_themes (pub_id, theme_code) VALUES
    (26, 'SPECIAL_MENU');
INSERT INTO pub_food_tags (pub_id, food_code) VALUES
    (26, 'GRILLED'), (26, 'FRY'), (26, 'DRY_SNACK'), (26, 'SOJU'), (26, 'BEER'), (26, 'HIGHBALL');
INSERT INTO pub_business_hours (pub_id, day_of_week, open_time, close_time, is_closed) VALUES
    (26, 1, '17:00', '02:00', FALSE),
    (26, 2, '17:00', '02:00', FALSE),
    (26, 3, '17:00', '02:00', FALSE),
    (26, 4, '17:00', '02:00', FALSE),
    (26, 5, '17:00', '02:00', FALSE),
    (26, 6, '17:00', '02:00', FALSE),
    (26, 7, '17:00', '02:00', FALSE);

-- pub_id 27: 제이케이펍 (MAPO / HONGDAE_HAPJEONG)
INSERT INTO pubs (pub_id, name, address, region, sub_region, status, capacity_range, group_seat_max_people, favorite_count, description) VALUES
    (27, '제이케이펍', '서울 마포구 양화로 1층', 'MAPO', 'HONGDAE_HAPJEONG', 'OPEN', 'R_50_100', 80, 0,
     '홍대·합정 양화로 1층 80명 규모 종합 스포츠 펍, 대형 스크린 + 다중 TV 야구·해외축구 중계, 소주·맥주·하이볼·칵테일');
INSERT INTO pub_supported_teams (pub_id, team_id) VALUES
    (27, 1), (27, 2), (27, 3), (27, 4), (27, 5), (27, 6), (27, 7), (27, 8), (27, 9), (27, 10);
INSERT INTO pub_facilities (pub_id, facility_code) VALUES
    (27, 'GROUP_SEAT'), (27, 'RESERVATION'), (27, 'SPACIOUS_AREA'), (27, 'PRIVATE_BOOKING');
INSERT INTO pub_styles (pub_id, style_code) VALUES
    (27, 'BIG_SCREEN'), (27, 'MULTI_TV'), (27, 'SPECTATOR_MODE');
INSERT INTO pub_themes (pub_id, theme_code) VALUES
    (27, 'SPACIOUS_VIEW'), (27, 'SPECIAL_MENU');
INSERT INTO pub_food_tags (pub_id, food_code) VALUES
    (27, 'GRILLED'), (27, 'FRY'), (27, 'CHICKEN'), (27, 'SOJU'), (27, 'BEER'), (27, 'HIGHBALL'), (27, 'COCKTAIL');
INSERT INTO pub_business_hours (pub_id, day_of_week, open_time, close_time, is_closed) VALUES
    (27, 1, '17:00', '02:00', FALSE),
    (27, 2, '17:00', '02:00', FALSE),
    (27, 3, '17:00', '02:00', FALSE),
    (27, 4, '17:00', '02:00', FALSE),
    (27, 5, '17:00', '02:00', FALSE),
    (27, 6, '17:00', '02:00', FALSE),
    (27, 7, '17:00', '02:00', FALSE);

-- pub_id 28: 치어하우스 (MAPO / HONGDAE_HAPJEONG)
INSERT INTO pubs (pub_id, name, address, region, sub_region, status, capacity_range, group_seat_max_people, favorite_count, description) VALUES
    (28, '치어하우스', '서울 마포구 잔다리로 1층', 'MAPO', 'HONGDAE_HAPJEONG', 'OPEN', 'R_50_100', 35, 0,
     '홍대·합정 잔다리로 1층 35명 규모 응원 특화 펍, 대형 스크린 야구 관전, 월요일 정기휴무, 소주·맥주·하이볼');
INSERT INTO pub_supported_teams (pub_id, team_id) VALUES
    (28, 1), (28, 2), (28, 3), (28, 4), (28, 5), (28, 6), (28, 7), (28, 8), (28, 9), (28, 10);
INSERT INTO pub_facilities (pub_id, facility_code) VALUES
    (28, 'GROUP_SEAT'), (28, 'RESERVATION');
INSERT INTO pub_styles (pub_id, style_code) VALUES
    (28, 'BIG_SCREEN'), (28, 'SINGLE_TV'), (28, 'STADIUM_MODE'), (28, 'LOUD_SPEAKER');
INSERT INTO pub_themes (pub_id, theme_code) VALUES
    (28, 'SPECIAL_MENU');
INSERT INTO pub_food_tags (pub_id, food_code) VALUES
    (28, 'GRILLED'), (28, 'FRY'), (28, 'CHICKEN'), (28, 'SOJU'), (28, 'BEER'), (28, 'HIGHBALL');
INSERT INTO pub_business_hours (pub_id, day_of_week, open_time, close_time, is_closed) VALUES
    (28, 1, NULL,    NULL,    TRUE),
    (28, 2, '17:00', '02:00', FALSE),
    (28, 3, '17:00', '02:00', FALSE),
    (28, 4, '17:00', '02:00', FALSE),
    (28, 5, '17:00', '02:00', FALSE),
    (28, 6, '17:00', '02:00', FALSE),
    (28, 7, '17:00', '02:00', FALSE);

-- pub_id 29: 크래프트아일랜드 잠실 (SONGPA / JAMSIL)
INSERT INTO pubs (pub_id, name, address, region, sub_region, status, capacity_range, group_seat_max_people, favorite_count, description) VALUES
    (29, '크래프트아일랜드 잠실', '서울 송파구 백제고분로7길 1층', 'SONGPA', 'JAMSIL', 'OPEN', 'R_50_100', 40, 0,
     '잠실새내 백제고분로7길 1층 40명 규모 크래프트 비어 스포츠 펍, 단일 TV 야구 관전, 신선한 재료 강조, 수제맥주·하이볼·소주');
INSERT INTO pub_supported_teams (pub_id, team_id) VALUES
    (29, 1), (29, 2), (29, 3), (29, 4), (29, 5), (29, 6), (29, 7), (29, 8), (29, 9), (29, 10);
INSERT INTO pub_facilities (pub_id, facility_code) VALUES
    (29, 'GROUP_SEAT'), (29, 'RESERVATION'), (29, 'COUNTER_SEAT');
INSERT INTO pub_styles (pub_id, style_code) VALUES
    (29, 'SINGLE_TV'), (29, 'SPECTATOR_MODE');
INSERT INTO pub_themes (pub_id, theme_code) VALUES
    (29, 'SPECIAL_MENU'), (29, 'FRESH');
INSERT INTO pub_food_tags (pub_id, food_code) VALUES
    (29, 'GRILLED'), (29, 'FRY'), (29, 'DRY_SNACK'), (29, 'BEER'), (29, 'HIGHBALL'), (29, 'SOJU');
INSERT INTO pub_business_hours (pub_id, day_of_week, open_time, close_time, is_closed) VALUES
    (29, 1, '17:00', '02:00', FALSE),
    (29, 2, '17:00', '02:00', FALSE),
    (29, 3, '17:00', '02:00', FALSE),
    (29, 4, '17:00', '02:00', FALSE),
    (29, 5, '17:00', '02:00', FALSE),
    (29, 6, '17:00', '02:00', FALSE),
    (29, 7, '17:00', '02:00', FALSE);

-- pub_id 30: 크래프트한스 사당 (SEOCHO / —)
INSERT INTO pubs (pub_id, name, address, region, sub_region, status, capacity_range, group_seat_max_people, favorite_count, description) VALUES
    (30, '크래프트한스 사당', '서울 서초구 방배로 1층', 'SEOCHO', NULL, 'OPEN', 'R_50_100', 80, 0,
     '사당 방배로 1층 80명 규모 크래프트 비어 롯데자이언츠 응원 펍, 대형 스크린 + 다중 TV 야구 관전, 수제맥주·하이볼·소주');
INSERT INTO pub_supported_teams (pub_id, team_id) VALUES
    (30, 7);
INSERT INTO pub_facilities (pub_id, facility_code) VALUES
    (30, 'GROUP_SEAT'), (30, 'RESERVATION'), (30, 'SPACIOUS_AREA'), (30, 'PRIVATE_BOOKING');
INSERT INTO pub_styles (pub_id, style_code) VALUES
    (30, 'BIG_SCREEN'), (30, 'MULTI_TV'), (30, 'STADIUM_MODE');
INSERT INTO pub_themes (pub_id, theme_code) VALUES
    (30, 'SPACIOUS_VIEW'), (30, 'SPECIAL_MENU');
INSERT INTO pub_food_tags (pub_id, food_code) VALUES
    (30, 'GRILLED'), (30, 'FRY'), (30, 'DRY_SNACK'), (30, 'BEER'), (30, 'HIGHBALL'), (30, 'SOJU');
INSERT INTO pub_business_hours (pub_id, day_of_week, open_time, close_time, is_closed) VALUES
    (30, 1, '17:00', '02:00', FALSE),
    (30, 2, '17:00', '02:00', FALSE),
    (30, 3, '17:00', '02:00', FALSE),
    (30, 4, '17:00', '02:00', FALSE),
    (30, 5, '17:00', '02:00', FALSE),
    (30, 6, '17:00', '02:00', FALSE),
    (30, 7, '17:00', '02:00', FALSE);

-- pub_id 31: 펍마이마이 (SONGPA / JAMSIL)
INSERT INTO pubs (pub_id, name, address, region, sub_region, status, capacity_range, group_seat_max_people, favorite_count, description) VALUES
    (31, '펍마이마이', '서울 송파구 백제고분로7길 1층', 'SONGPA', 'JAMSIL', 'OPEN', 'OVER_100', 400, 0,
     '잠실새내 백제고분로7길 1층 400명 규모 대형 종합 응원 펍, 대형 스크린 + 다중 TV 야구·해외축구, 소주·맥주·하이볼·칵테일');
INSERT INTO pub_supported_teams (pub_id, team_id) VALUES
    (31, 1), (31, 2), (31, 3), (31, 4), (31, 5), (31, 6), (31, 7), (31, 8), (31, 9), (31, 10);
INSERT INTO pub_facilities (pub_id, facility_code) VALUES
    (31, 'GROUP_SEAT'), (31, 'RESERVATION'), (31, 'SPACIOUS_AREA'), (31, 'PRIVATE_BOOKING'), (31, 'PARKING');
INSERT INTO pub_styles (pub_id, style_code) VALUES
    (31, 'BIG_SCREEN'), (31, 'MULTI_TV'), (31, 'STADIUM_MODE'), (31, 'LOUD_SPEAKER');
INSERT INTO pub_themes (pub_id, theme_code) VALUES
    (31, 'SPACIOUS_VIEW'), (31, 'SPECIAL_MENU');
INSERT INTO pub_food_tags (pub_id, food_code) VALUES
    (31, 'CHICKEN'), (31, 'GRILLED'), (31, 'FRY'), (31, 'DRY_SNACK'), (31, 'SOJU'), (31, 'BEER'), (31, 'HIGHBALL'), (31, 'COCKTAIL');
INSERT INTO pub_business_hours (pub_id, day_of_week, open_time, close_time, is_closed) VALUES
    (31, 1, '17:00', '02:00', FALSE),
    (31, 2, '17:00', '02:00', FALSE),
    (31, 3, '17:00', '02:00', FALSE),
    (31, 4, '17:00', '02:00', FALSE),
    (31, 5, '17:00', '02:00', FALSE),
    (31, 6, '17:00', '02:00', FALSE),
    (31, 7, '17:00', '02:00', FALSE);

-- pub_id 32: 포차주식시장 (SEOCHO / —)
INSERT INTO pubs (pub_id, name, address, region, sub_region, status, capacity_range, group_seat_max_people, favorite_count, description) VALUES
    (32, '포차주식시장', '서울 서초구 강남대로 1층', 'SEOCHO', NULL, 'OPEN', 'OVER_100', 200, 0,
     '서초 강남대로 1층 200명 규모 대형 종합 스포츠 포차, 대형 스크린 + 다중 TV 야구 관전, 소주·맥주·하이볼');
INSERT INTO pub_supported_teams (pub_id, team_id) VALUES
    (32, 1), (32, 2), (32, 3), (32, 4), (32, 5), (32, 6), (32, 7), (32, 8), (32, 9), (32, 10);
INSERT INTO pub_facilities (pub_id, facility_code) VALUES
    (32, 'GROUP_SEAT'), (32, 'RESERVATION'), (32, 'SPACIOUS_AREA'), (32, 'PRIVATE_BOOKING');
INSERT INTO pub_styles (pub_id, style_code) VALUES
    (32, 'BIG_SCREEN'), (32, 'MULTI_TV'), (32, 'SPECTATOR_MODE');
INSERT INTO pub_themes (pub_id, theme_code) VALUES
    (32, 'SPACIOUS_VIEW'), (32, 'SPECIAL_MENU');
INSERT INTO pub_food_tags (pub_id, food_code) VALUES
    (32, 'GRILLED'), (32, 'FRY'), (32, 'DRY_SNACK'), (32, 'STEW'), (32, 'SOJU'), (32, 'BEER'), (32, 'HIGHBALL');
INSERT INTO pub_business_hours (pub_id, day_of_week, open_time, close_time, is_closed) VALUES
    (32, 1, '17:00', '02:00', FALSE),
    (32, 2, '17:00', '02:00', FALSE),
    (32, 3, '17:00', '02:00', FALSE),
    (32, 4, '17:00', '02:00', FALSE),
    (32, 5, '17:00', '02:00', FALSE),
    (32, 6, '17:00', '02:00', FALSE),
    (32, 7, '17:00', '02:00', FALSE);

-- pub_id 33: 호리도 (GWANAK / —)
INSERT INTO pubs (pub_id, name, address, region, sub_region, status, capacity_range, group_seat_max_people, favorite_count, description) VALUES
    (33, '호리도', '서울 관악구 관악로 1층', 'GWANAK', NULL, 'OPEN', 'R_20_50', NULL, 0,
     '관악 관악로 1층 종합 야구 관전 이자카야, 단일 TV 관전, 신선한 재료·직접 조리 강조, 소주·맥주·하이볼');
INSERT INTO pub_supported_teams (pub_id, team_id) VALUES
    (33, 1), (33, 2), (33, 3), (33, 4), (33, 5), (33, 6), (33, 7), (33, 8), (33, 9), (33, 10);
INSERT INTO pub_facilities (pub_id, facility_code) VALUES
    (33, 'RESERVATION'), (33, 'COUNTER_SEAT'), (33, 'SOLO_SEAT'), (33, 'PRIVATE_BOOKING');
INSERT INTO pub_styles (pub_id, style_code) VALUES
    (33, 'SINGLE_TV'), (33, 'SPECTATOR_MODE');
INSERT INTO pub_themes (pub_id, theme_code) VALUES
    (33, 'SPECIAL_MENU'), (33, 'FRESH');
INSERT INTO pub_food_tags (pub_id, food_code) VALUES
    (33, 'GRILLED'), (33, 'FRY'), (33, 'DRY_SNACK'), (33, 'SOJU'), (33, 'BEER'), (33, 'HIGHBALL');
INSERT INTO pub_business_hours (pub_id, day_of_week, open_time, close_time, is_closed) VALUES
    (33, 1, '17:00', '01:00', FALSE),
    (33, 2, '17:00', '01:00', FALSE),
    (33, 3, '17:00', '01:00', FALSE),
    (33, 4, '17:00', '01:00', FALSE),
    (33, 5, '17:00', '02:00', FALSE),
    (33, 6, '17:00', '02:00', FALSE),
    (33, 7, '17:00', '01:00', FALSE);

-- pub_id 34: 호멜맥주 3호점 (DONGJAK / —)
INSERT INTO pubs (pub_id, name, address, region, sub_region, status, capacity_range, group_seat_max_people, favorite_count, description) VALUES
    (34, '호멜맥주 3호점', '서울 동작구 사당로 1층', 'DONGJAK', NULL, 'OPEN', 'OVER_100', 150, 0,
     '사당·이수 사당로 1층 150명 규모 대형 한화이글스 응원 크래프트 비어 펍, 대형 스크린 + 다중 TV 야구 관전, 신선한 재료 강조, 월요일 정기휴무, 수제맥주·하이볼·소주');
INSERT INTO pub_supported_teams (pub_id, team_id) VALUES
    (34, 9);
INSERT INTO pub_facilities (pub_id, facility_code) VALUES
    (34, 'GROUP_SEAT'), (34, 'RESERVATION'), (34, 'SPACIOUS_AREA'), (34, 'PRIVATE_BOOKING');
INSERT INTO pub_styles (pub_id, style_code) VALUES
    (34, 'BIG_SCREEN'), (34, 'MULTI_TV'), (34, 'STADIUM_MODE'), (34, 'LOUD_SPEAKER');
INSERT INTO pub_themes (pub_id, theme_code) VALUES
    (34, 'SPACIOUS_VIEW'), (34, 'SPECIAL_MENU'), (34, 'FRESH');
INSERT INTO pub_food_tags (pub_id, food_code) VALUES
    (34, 'CHICKEN'), (34, 'GRILLED'), (34, 'FRY'), (34, 'STEW'), (34, 'BEER'), (34, 'HIGHBALL'), (34, 'SOJU');
INSERT INTO pub_business_hours (pub_id, day_of_week, open_time, close_time, is_closed) VALUES
    (34, 1, NULL,    NULL,    TRUE),
    (34, 2, '17:00', '02:00', FALSE),
    (34, 3, '17:00', '02:00', FALSE),
    (34, 4, '17:00', '02:00', FALSE),
    (34, 5, '17:00', '02:00', FALSE),
    (34, 6, '17:00', '02:00', FALSE),
    (34, 7, '17:00', '02:00', FALSE);

-- pub_id 35: 금성슈퍼 광화문 (JONGNO / —)
INSERT INTO pubs (pub_id, name, address, region, sub_region, status, capacity_range, group_seat_max_people, favorite_count, description) VALUES
    (35, '금성슈퍼 광화문', '서울 종로구 세종대로 1층', 'JONGNO', NULL, 'OPEN', 'R_20_50', 20, 0,
     '광화문 세종대로 1층 20명 규모 레트로 콘셉트 종합 야구 관전 펍, 단일 TV 관전, 소주·맥주·하이볼');
INSERT INTO pub_supported_teams (pub_id, team_id) VALUES
    (35, 1), (35, 2), (35, 3), (35, 4), (35, 5), (35, 6), (35, 7), (35, 8), (35, 9), (35, 10);
INSERT INTO pub_facilities (pub_id, facility_code) VALUES
    (35, 'GROUP_SEAT'), (35, 'RESERVATION');
INSERT INTO pub_styles (pub_id, style_code) VALUES
    (35, 'SINGLE_TV'), (35, 'SPECTATOR_MODE');
INSERT INTO pub_themes (pub_id, theme_code) VALUES
    (35, 'EXOTIC'), (35, 'SPECIAL_MENU');
INSERT INTO pub_food_tags (pub_id, food_code) VALUES
    (35, 'GRILLED'), (35, 'DRY_SNACK'), (35, 'SOJU'), (35, 'BEER'), (35, 'HIGHBALL');
INSERT INTO pub_business_hours (pub_id, day_of_week, open_time, close_time, is_closed) VALUES
    (35, 1, '17:00', '01:00', FALSE),
    (35, 2, '17:00', '01:00', FALSE),
    (35, 3, '17:00', '01:00', FALSE),
    (35, 4, '17:00', '01:00', FALSE),
    (35, 5, '17:00', '02:00', FALSE),
    (35, 6, '17:00', '02:00', FALSE),
    (35, 7, '17:00', '01:00', FALSE);

-- pub_id 36: 워너비대구 강남역본점 (GANGNAM / —)
INSERT INTO pubs (pub_id, name, address, region, sub_region, status, capacity_range, group_seat_max_people, favorite_count, description) VALUES
    (36, '워너비대구 강남역본점', '서울 강남구 테헤란로1길 28-5 2층', 'GANGNAM', NULL, 'OPEN', 'R_50_100', 100, 0,
     '강남역 12번 출구 테헤란로1길 2층 대구 향토 포차, 대구뭉티기·대구막창·동인동찜갈비 등 대구 향토 시그니처, 삼성라이온즈 응원, 단체 2~100명 + 테라스, 매일 새벽 2시까지');
INSERT INTO pub_supported_teams (pub_id, team_id) VALUES
    (36, 8);
INSERT INTO pub_facilities (pub_id, facility_code) VALUES
    (36, 'GROUP_SEAT'), (36, 'RESERVATION'), (36, 'TERRACE');
INSERT INTO pub_styles (pub_id, style_code) VALUES
    (36, 'STADIUM_MODE');
INSERT INTO pub_themes (pub_id, theme_code) VALUES
    (36, 'SPECIAL_MENU'), (36, 'FRESH');
INSERT INTO pub_food_tags (pub_id, food_code) VALUES
    (36, 'CHICKEN'), (36, 'GRILLED'), (36, 'FRY'), (36, 'STEW'), (36, 'SOJU'), (36, 'BEER'), (36, 'HIGHBALL');
INSERT INTO pub_business_hours (pub_id, day_of_week, open_time, close_time, is_closed) VALUES
    (36, 1, '16:00', '02:00', FALSE),
    (36, 2, '16:00', '02:00', FALSE),
    (36, 3, '16:00', '02:00', FALSE),
    (36, 4, '16:00', '02:00', FALSE),
    (36, 5, '16:00', '02:00', FALSE),
    (36, 6, '16:00', '02:00', FALSE),
    (36, 7, '16:00', '02:00', FALSE);

-- pub_id 37: 배고픈 돼지 잠실본점 (SONGPA / JAMSIL)
INSERT INTO pubs (pub_id, name, address, region, sub_region, status, capacity_range, group_seat_max_people, favorite_count, description) VALUES
    (37, '배고픈 돼지 잠실본점', '서울 송파구 백제고분로7길 24-12 1층', 'SONGPA', 'JAMSIL', 'OPEN', 'R_50_100', 60, 0,
     '잠실새내 백제고분로 1층 삼겹삼합 전문점, 국내산 1등급 암퇘지 + 국내산 묵은지 + 국내산 미나리 삼겹삼합·차돌삼합 시그니처, 스크린 1대 스포츠 관전, 단체 10~60명·테라스·반려동물 동반·매장 앞 무료 주차');
INSERT INTO pub_supported_teams (pub_id, team_id) VALUES
    (37, 1), (37, 2), (37, 3), (37, 4), (37, 5), (37, 6), (37, 7), (37, 8), (37, 9), (37, 10);
INSERT INTO pub_facilities (pub_id, facility_code) VALUES
    (37, 'GROUP_SEAT'), (37, 'RESERVATION'), (37, 'PARKING'), (37, 'PET_FRIENDLY'), (37, 'TERRACE');
INSERT INTO pub_styles (pub_id, style_code) VALUES
    (37, 'BIG_SCREEN'), (37, 'SINGLE_TV'), (37, 'SPECTATOR_MODE');
INSERT INTO pub_themes (pub_id, theme_code) VALUES
    (37, 'SPECIAL_MENU'), (37, 'FRESH');
INSERT INTO pub_food_tags (pub_id, food_code) VALUES
    (37, 'GRILLED'), (37, 'STEW'), (37, 'SOJU'), (37, 'BEER'), (37, 'HIGHBALL');
INSERT INTO pub_business_hours (pub_id, day_of_week, open_time, close_time, is_closed) VALUES
    (37, 1, '16:00', '02:00', FALSE),
    (37, 2, '16:00', '02:00', FALSE),
    (37, 3, '16:00', '02:00', FALSE),
    (37, 4, '16:00', '02:00', FALSE),
    (37, 5, '16:00', '02:00', FALSE),
    (37, 6, '13:00', '04:00', FALSE),
    (37, 7, '13:00', '04:00', FALSE);

-- pub_id 38: 정주 (SONGPA / JAMSIL)
INSERT INTO pubs (pub_id, name, address, region, sub_region, status, capacity_range, group_seat_max_people, favorite_count, description) VALUES
    (38, '정주', '서울 송파구 백제고분로15길 46 1층 102호', 'SONGPA', 'JAMSIL', 'OPEN', 'R_20_50', NULL, 0,
     '잠실새내 백제고분로15길 1층 한식주점, 조용한 대화·데이트 지향, 항정살 수비드 수육·채끝 육회·단호박 크림 뇨끼 등 시그니처, 다양한 전통주 + 소주·맥주·하이볼, 야구 팬 야구 중계, 20명 최대·티비 1대·테라스·1인석·반려동물 동반·대관 가능');
INSERT INTO pub_supported_teams (pub_id, team_id) VALUES
    (38, 1), (38, 2), (38, 3), (38, 4), (38, 5), (38, 6), (38, 7), (38, 8), (38, 9), (38, 10);
INSERT INTO pub_facilities (pub_id, facility_code) VALUES
    (38, 'RESERVATION'), (38, 'PRIVATE_BOOKING'), (38, 'PET_FRIENDLY'), (38, 'TERRACE'), (38, 'SOLO_SEAT');
INSERT INTO pub_styles (pub_id, style_code) VALUES
    (38, 'SINGLE_TV'), (38, 'SPECTATOR_MODE');
INSERT INTO pub_themes (pub_id, theme_code) VALUES
    (38, 'SPECIAL_MENU'), (38, 'FRESH');
INSERT INTO pub_food_tags (pub_id, food_code) VALUES
    (38, 'GRILLED'), (38, 'STEW'), (38, 'FRY'), (38, 'SOJU'), (38, 'BEER'), (38, 'HIGHBALL');
INSERT INTO pub_business_hours (pub_id, day_of_week, open_time, close_time, is_closed) VALUES
    (38, 1, '17:00', '01:00', FALSE),
    (38, 2, '17:00', '01:00', FALSE),
    (38, 3, '17:00', '01:00', FALSE),
    (38, 4, '17:00', '01:00', FALSE),
    (38, 5, '17:00', '02:00', FALSE),
    (38, 6, '16:00', '02:00', FALSE),
    (38, 7, '16:00', '01:00', FALSE);

-- pub_id 39: 908 (이수 908) (DONGJAK / —)
-- NOTE: 원본 "둘째·넷째 월요일 휴무" (월 단위 격주 패턴) — 주 단위 스키마로 표현 불가.
--       스키마 정합성 위해 월요일도 OPEN 으로 시드. 정확한 격주 규칙은 별도 운영 정책으로 처리.
INSERT INTO pubs (pub_id, name, address, region, sub_region, status, capacity_range, group_seat_max_people, favorite_count, description) VALUES
    (39, '908', '서울 동작구 동작대로27가길 26 2층', 'DONGJAK', NULL, 'OPEN', 'R_20_50', 30, 0,
     '이수역 동작대로27가길 2층 30명 규모 심야 술집, 대형 스크린 야구 중계, 908 깐풍·908 또띠아피자·트러플짜파게티 시그니처, 소주·맥주·하이볼, 포근한 분위기 심야 모임, 매일 새벽 3~4시까지, 둘째·넷째 월요일 휴무');
INSERT INTO pub_supported_teams (pub_id, team_id) VALUES
    (39, 1), (39, 2), (39, 3), (39, 4), (39, 5), (39, 6), (39, 7), (39, 8), (39, 9), (39, 10);
INSERT INTO pub_facilities (pub_id, facility_code) VALUES
    (39, 'GROUP_SEAT'), (39, 'RESERVATION');
INSERT INTO pub_styles (pub_id, style_code) VALUES
    (39, 'BIG_SCREEN'), (39, 'SINGLE_TV'), (39, 'SPECTATOR_MODE');
INSERT INTO pub_themes (pub_id, theme_code) VALUES
    (39, 'SPECIAL_MENU');
INSERT INTO pub_food_tags (pub_id, food_code) VALUES
    (39, 'CHICKEN'), (39, 'PIZZA'), (39, 'GRILLED'), (39, 'FRY'), (39, 'STEW'), (39, 'DRY_SNACK'), (39, 'SOJU'), (39, 'BEER'), (39, 'HIGHBALL');
INSERT INTO pub_business_hours (pub_id, day_of_week, open_time, close_time, is_closed) VALUES
    (39, 1, '18:00', '04:00', FALSE),
    (39, 2, '18:00', '04:00', FALSE),
    (39, 3, '18:00', '04:00', FALSE),
    (39, 4, '18:00', '03:00', FALSE),
    (39, 5, '18:00', '04:00', FALSE),
    (39, 6, '18:00', '04:00', FALSE),
    (39, 7, '18:00', '04:00', FALSE);

-- pub_id 40: 달포 (GANGBUK / —)
INSERT INTO pubs (pub_id, name, address, region, sub_region, status, capacity_range, group_seat_max_people, favorite_count, description) VALUES
    (40, '달포', '서울 강북구 도봉로83길 21 2층 전체', 'GANGBUK', NULL, 'OPEN', 'R_20_50', 30, 0,
     '수유역 도봉로83길 2층 30명 규모 작은 실내포차, 한식·중식·전 다양한 안주, 두산베어스 야구 + 해외축구 중계, 티비 1대, 소주·맥주·하이볼, 새벽 3시까지, 월 정기휴무');
INSERT INTO pub_supported_teams (pub_id, team_id) VALUES
    (40, 2);
INSERT INTO pub_facilities (pub_id, facility_code) VALUES
    (40, 'GROUP_SEAT'), (40, 'RESERVATION'), (40, 'COUNTER_SEAT'), (40, 'SOLO_SEAT');
INSERT INTO pub_styles (pub_id, style_code) VALUES
    (40, 'SINGLE_TV'), (40, 'STADIUM_MODE');
INSERT INTO pub_themes (pub_id, theme_code) VALUES
    (40, 'SPECIAL_MENU');
INSERT INTO pub_food_tags (pub_id, food_code) VALUES
    (40, 'CHICKEN'), (40, 'GRILLED'), (40, 'FRY'), (40, 'STEW'), (40, 'DRY_SNACK'), (40, 'SOJU'), (40, 'BEER'), (40, 'HIGHBALL');
INSERT INTO pub_business_hours (pub_id, day_of_week, open_time, close_time, is_closed) VALUES
    (40, 1, NULL,    NULL,    TRUE),
    (40, 2, '17:00', '03:00', FALSE),
    (40, 3, '17:00', '03:00', FALSE),
    (40, 4, '17:00', '03:00', FALSE),
    (40, 5, '17:00', '03:00', FALSE),
    (40, 6, '17:00', '03:00', FALSE),
    (40, 7, '17:00', '03:00', FALSE);

-- 시퀀스 정렬 (다음 IDENTITY 값을 41 로)
ALTER TABLE pubs ALTER COLUMN pub_id RESTART WITH 41;
