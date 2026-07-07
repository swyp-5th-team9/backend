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
--   Part C. 실 매장 40건 시드 (pub_id 1..40 명시 INSERT, MD 원본 크로스체크)
--           - 자식 테이블: pub_supported_teams / pub_facilities / pub_styles /
--                        pub_themes / pub_food_tags / pub_business_hours
--           - latitude / longitude / phone / capacity_note 는 시드하지 않음
--           - team_id 매핑 (V1 INSERT 순서):
--               1=LG, 2=두산, 3=KT, 4=SSG, 5=NC, 6=KIA, 7=롯데, 8=삼성, 9=한화, 10=키움
--             "모든팀" = 1..10 전부 시드
--           - business_hours: ISO-8601 (1=월 ... 7=일), 자정 넘김 허용,
--             정기휴무는 is_closed=TRUE + open/close NULL
--             24:00 은 Postgres TIME 한계로 23:59 로 저장
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
    (1, '3355펍', '서울 구로구 공원로6나길 40 알파빌딩', 'GURO', NULL, 'OPEN', 'R_50_100', 90, 0,
     '대형 스크린 관전, 대관 가능, 웨이팅 대기 공간 있음');
INSERT INTO pub_supported_teams (pub_id, team_id) VALUES
    (1, 1), (1, 2), (1, 3), (1, 4), (1, 5), (1, 6), (1, 7), (1, 8), (1, 9), (1, 10);
INSERT INTO pub_facilities (pub_id, facility_code) VALUES
    (1, 'GROUP_SEAT'), (1, 'RESERVATION'), (1, 'PRIVATE_BOOKING');
INSERT INTO pub_styles (pub_id, style_code) VALUES
    (1, 'BIG_SCREEN');
INSERT INTO pub_themes (pub_id, theme_code) VALUES
    (1, 'SPECIAL_MENU');
INSERT INTO pub_food_tags (pub_id, food_code) VALUES
    (1, 'CHICKEN'), (1, 'FRY'), (1, 'STEW'), (1, 'GRILLED'), (1, 'BUNSIK'), (1, 'DRY_SNACK'), (1, 'SOJU'), (1, 'BEER'), (1, 'HIGHBALL');
INSERT INTO pub_business_hours (pub_id, day_of_week, open_time, close_time, is_closed) VALUES
    (1, 1, '17:00', '01:00', FALSE),
    (1, 2, '17:00', '01:00', FALSE),
    (1, 3, '17:00', '01:00', FALSE),
    (1, 4, '17:00', '01:00', FALSE),
    (1, 5, '17:00', '02:00', FALSE),
    (1, 6, '15:00', '02:00', FALSE),
    (1, 7, '15:00', '01:00', FALSE);

-- pub_id 2: 곰배곰배 (DONGDAEMUN / —)
INSERT INTO pubs (pub_id, name, address, region, sub_region, status, capacity_range, group_seat_max_people, favorite_count, description) VALUES
    (2, '곰배곰배', '서울 동대문구 장한로5길 75 1층', 'DONGDAEMUN', NULL, 'OPEN', NULL, NULL, 0,
     '논알콜 하이볼 다양, 조용한 술집, 혼술 가능');
INSERT INTO pub_supported_teams (pub_id, team_id) VALUES
    (2, 2);
INSERT INTO pub_facilities (pub_id, facility_code) VALUES
    (2, 'RESERVATION'), (2, 'SOLO_SEAT');
INSERT INTO pub_styles (pub_id, style_code) VALUES
    (2, 'SINGLE_TV');
INSERT INTO pub_themes (pub_id, theme_code) VALUES
    (2, 'SPECIAL_MENU');
INSERT INTO pub_food_tags (pub_id, food_code) VALUES
    (2, 'CHICKEN'), (2, 'FRY'), (2, 'STEW'), (2, 'GRILLED'), (2, 'BUNSIK'), (2, 'SOJU'), (2, 'BEER'), (2, 'HIGHBALL');
INSERT INTO pub_business_hours (pub_id, day_of_week, open_time, close_time, is_closed) VALUES
    (2, 1, NULL, NULL, TRUE),
    (2, 2, '18:00', '22:00', FALSE),
    (2, 3, '18:00', '22:00', FALSE),
    (2, 4, '18:00', '22:00', FALSE),
    (2, 5, '18:00', '22:00', FALSE),
    (2, 6, '16:30', '20:00', FALSE),
    (2, 7, NULL, NULL, TRUE);

-- pub_id 3: 낭만포차 (DONGJAK / —)
INSERT INTO pubs (pub_id, name, address, region, sub_region, status, capacity_range, group_seat_max_people, favorite_count, description) VALUES
    (3, '낭만포차', '서울 동작구 노들로2길 7 드림스퀘어 상가 C동 106호', 'DONGJAK', NULL, 'OPEN', NULL, NULL, 0,
     '대형 TV 야구 관전, 단체·모임 환영, 콜키지 가능, 노량진 야구술집');
INSERT INTO pub_supported_teams (pub_id, team_id) VALUES
    (3, 1);
INSERT INTO pub_facilities (pub_id, facility_code) VALUES
    (3, 'GROUP_SEAT'), (3, 'RESERVATION'), (3, 'PARKING'), (3, 'WHEELCHAIR_ACCESS');
INSERT INTO pub_styles (pub_id, style_code) VALUES
    (3, 'BIG_SCREEN'), (3, 'STADIUM_MODE'), (3, 'LOUD_SPEAKER');
INSERT INTO pub_themes (pub_id, theme_code) VALUES
    (3, 'SPECIAL_MENU');
INSERT INTO pub_food_tags (pub_id, food_code) VALUES
    (3, 'CHICKEN'), (3, 'FRY'), (3, 'STEW'), (3, 'GRILLED'), (3, 'BUNSIK'), (3, 'DRY_SNACK'), (3, 'TACO'), (3, 'SOJU'), (3, 'BEER'), (3, 'HIGHBALL');
INSERT INTO pub_business_hours (pub_id, day_of_week, open_time, close_time, is_closed) VALUES
    (3, 1, '18:00', '02:00', FALSE),
    (3, 2, '18:00', '02:00', FALSE),
    (3, 3, '17:00', '02:00', FALSE),
    (3, 4, '17:00', '02:00', FALSE),
    (3, 5, '17:00', '02:00', FALSE),
    (3, 6, '16:00', '02:00', FALSE),
    (3, 7, '16:00', '02:00', FALSE);

-- pub_id 4: 노가리 (YEONGDEUNGPO / —)
INSERT INTO pubs (pub_id, name, address, region, sub_region, status, capacity_range, group_seat_max_people, favorite_count, description) VALUES
    (4, '노가리', '서울 영등포구 당산로 34 로데오 왘 쇼핑몰', 'YEONGDEUNGPO', NULL, 'OPEN', 'OVER_100', 180, 0,
     '노량진 최대 규모 180석, 대형 스크린 + TV 실시간 중계, 한화 응원 명소, 소모임~기업 회식·대관 가능');
INSERT INTO pub_supported_teams (pub_id, team_id) VALUES
    (4, 9);
INSERT INTO pub_facilities (pub_id, facility_code) VALUES
    (4, 'GROUP_SEAT'), (4, 'RESERVATION'), (4, 'PARKING'), (4, 'PRIVATE_BOOKING');
INSERT INTO pub_styles (pub_id, style_code) VALUES
    (4, 'BIG_SCREEN'), (4, 'MULTI_TV'), (4, 'STADIUM_MODE');
INSERT INTO pub_themes (pub_id, theme_code) VALUES
    (4, 'SPECIAL_MENU');
INSERT INTO pub_food_tags (pub_id, food_code) VALUES
    (4, 'CHICKEN'), (4, 'PIZZA'), (4, 'FRY'), (4, 'STEW'), (4, 'GRILLED'), (4, 'DRY_SNACK'), (4, 'SOJU'), (4, 'BEER'), (4, 'HIGHBALL');
INSERT INTO pub_business_hours (pub_id, day_of_week, open_time, close_time, is_closed) VALUES
    (4, 1, '14:00', '00:50', FALSE),
    (4, 2, '14:00', '00:50', FALSE),
    (4, 3, '14:00', '00:50', FALSE),
    (4, 4, '14:00', '00:50', FALSE),
    (4, 5, '14:00', '00:50', FALSE),
    (4, 6, '14:00', '00:50', FALSE),
    (4, 7, '14:00', '00:50', FALSE);

-- pub_id 5: 당인리극장 (MAPO / HONGDAE_HAPJEONG)
INSERT INTO pubs (pub_id, name, address, region, sub_region, status, capacity_range, group_seat_max_people, favorite_count, description) VALUES
    (5, '당인리극장', '서울 마포구 양화로6길 21 당인리극장 2층', 'MAPO', 'HONGDAE_HAPJEONG', 'OPEN', 'R_50_100', 70, 0,
     '합정역 도보 3분, 대형 스크린 + TV 여러 대로 야구·축구·올림픽 등 모든 스포츠 중계, 최대 100명 단체 대관, 10년 한식주점');
INSERT INTO pub_supported_teams (pub_id, team_id) VALUES
    (5, 9);
INSERT INTO pub_facilities (pub_id, facility_code) VALUES
    (5, 'GROUP_SEAT'), (5, 'COUNTER_SEAT'), (5, 'SOLO_SEAT'), (5, 'SPACIOUS_AREA'), (5, 'PARKING'), (5, 'RESERVATION'), (5, 'PRIVATE_BOOKING'), (5, 'PET_FRIENDLY');
INSERT INTO pub_styles (pub_id, style_code) VALUES
    (5, 'BIG_SCREEN'), (5, 'MULTI_TV'), (5, 'SPECTATOR_MODE');
INSERT INTO pub_themes (pub_id, theme_code) VALUES
    (5, 'SPECIAL_MENU');
INSERT INTO pub_food_tags (pub_id, food_code) VALUES
    (5, 'STEW'), (5, 'BUNSIK'), (5, 'GRILLED'), (5, 'SOJU'), (5, 'BEER'), (5, 'HIGHBALL');
INSERT INTO pub_business_hours (pub_id, day_of_week, open_time, close_time, is_closed) VALUES
    (5, 1, NULL, NULL, TRUE),
    (5, 2, '18:00', '02:00', FALSE),
    (5, 3, '18:00', '02:00', FALSE),
    (5, 4, '18:00', '02:00', FALSE),
    (5, 5, '17:00', '04:00', FALSE),
    (5, 6, '14:00', '04:00', FALSE),
    (5, 7, '14:00', '03:00', FALSE);

-- pub_id 6: 더블플레이치킨 홍대점 (MAPO / HONGDAE_HAPJEONG)
INSERT INTO pubs (pub_id, name, address, region, sub_region, status, capacity_range, group_seat_max_people, favorite_count, description) VALUES
    (6, '더블플레이치킨 홍대점', '서울 마포구 동교로 201 2층 더블플레이치킨', 'MAPO', 'HONGDAE_HAPJEONG', 'OPEN', 'R_50_100', 80, 0,
     '홍대입구역 2번출구 50m, TV 5대로 야구·축구 관전, 미니야구장 컨셉 치킨카페, 대관 가능(수용 80), 웨이팅 대기 공간 있음, 연중무휴');
INSERT INTO pub_supported_teams (pub_id, team_id) VALUES
    (6, 1), (6, 2), (6, 3), (6, 4), (6, 5), (6, 6), (6, 7), (6, 8), (6, 9), (6, 10);
INSERT INTO pub_facilities (pub_id, facility_code) VALUES
    (6, 'GROUP_SEAT'), (6, 'RESERVATION'), (6, 'PRIVATE_BOOKING');
INSERT INTO pub_styles (pub_id, style_code) VALUES
    (6, 'BIG_SCREEN'), (6, 'MULTI_TV'), (6, 'STADIUM_MODE'), (6, 'LOUD_SPEAKER');
INSERT INTO pub_themes (pub_id, theme_code) VALUES
    (6, 'SPECIAL_MENU');
INSERT INTO pub_food_tags (pub_id, food_code) VALUES
    (6, 'CHICKEN'), (6, 'GRILLED'), (6, 'FRY'), (6, 'STEW'), (6, 'BUNSIK'), (6, 'DRY_SNACK'), (6, 'SOJU'), (6, 'BEER');
INSERT INTO pub_business_hours (pub_id, day_of_week, open_time, close_time, is_closed) VALUES
    (6, 1, '17:00', '02:00', FALSE),
    (6, 2, '17:00', '02:00', FALSE),
    (6, 3, '17:00', '02:00', FALSE),
    (6, 4, '17:00', '02:00', FALSE),
    (6, 5, '17:00', '03:00', FALSE),
    (6, 6, '14:00', '03:00', FALSE),
    (6, 7, '14:00', '02:00', FALSE);

-- pub_id 7: 드래프트128 (YEONGDEUNGPO / —)
INSERT INTO pubs (pub_id, name, address, region, sub_region, status, capacity_range, group_seat_max_people, favorite_count, description) VALUES
    (7, '드래프트128', '서울 영등포구 여의대로 128 LG트윈타워 서관 B1 DRAFT 128', 'YEONGDEUNGPO', NULL, 'OPEN', NULL, NULL, 0,
     'LG트윈타워 서관 B1 스포츠 펍, 대형 디스플레이 + 좌석별 TV, 화덕 피자·파스타·스테이크 전문, 여의나루역 근접');
INSERT INTO pub_supported_teams (pub_id, team_id) VALUES
    (7, 1);
INSERT INTO pub_facilities (pub_id, facility_code) VALUES
    (7, 'GROUP_SEAT'), (7, 'RESERVATION');
INSERT INTO pub_styles (pub_id, style_code) VALUES
    (7, 'BIG_SCREEN'), (7, 'MULTI_TV');
INSERT INTO pub_themes (pub_id, theme_code) VALUES
    (7, 'SPECIAL_MENU');
INSERT INTO pub_food_tags (pub_id, food_code) VALUES
    (7, 'PIZZA'), (7, 'GRILLED'), (7, 'FRY'), (7, 'CHICKEN'), (7, 'STEW'), (7, 'DRY_SNACK'), (7, 'BUNSIK'), (7, 'SOJU'), (7, 'BEER'), (7, 'HIGHBALL');
INSERT INTO pub_business_hours (pub_id, day_of_week, open_time, close_time, is_closed) VALUES
    (7, 1, '11:00', '22:00', FALSE),
    (7, 2, '11:00', '22:00', FALSE),
    (7, 3, '11:00', '22:00', FALSE),
    (7, 4, '11:00', '22:00', FALSE),
    (7, 5, '11:00', '22:00', FALSE),
    (7, 6, NULL, NULL, TRUE),
    (7, 7, NULL, NULL, TRUE);

-- pub_id 8: 레코드피자 샤로수길점 (GWANAK / —)
INSERT INTO pubs (pub_id, name, address, region, sub_region, status, capacity_range, group_seat_max_people, favorite_count, description) VALUES
    (8, '레코드피자 샤로수길점', '서울 관악구 남부순환로230길 11 1층', 'GWANAK', NULL, 'OPEN', 'R_20_50', 30, 0,
     '샤로수길 스포츠 펍, 18인치 피자 대표, 대형 TV + 빔스크린으로 축구·야구·모든 스포츠 관전, 최대 30석');
INSERT INTO pub_supported_teams (pub_id, team_id) VALUES
    (8, 1), (8, 2), (8, 3), (8, 4), (8, 5), (8, 6), (8, 7), (8, 8), (8, 9), (8, 10);
INSERT INTO pub_facilities (pub_id, facility_code) VALUES
    (8, 'GROUP_SEAT'), (8, 'RESERVATION');
INSERT INTO pub_styles (pub_id, style_code) VALUES
    (8, 'BIG_SCREEN'), (8, 'SPECTATOR_MODE');
INSERT INTO pub_themes (pub_id, theme_code) VALUES
    (8, 'SPECIAL_MENU');
INSERT INTO pub_food_tags (pub_id, food_code) VALUES
    (8, 'PIZZA'), (8, 'CHICKEN'), (8, 'DRY_SNACK'), (8, 'SOJU'), (8, 'BEER'), (8, 'COCKTAIL');
INSERT INTO pub_business_hours (pub_id, day_of_week, open_time, close_time, is_closed) VALUES
    (8, 1, '16:30', '03:00', FALSE),
    (8, 2, '16:30', '03:00', FALSE),
    (8, 3, '16:30', '03:00', FALSE),
    (8, 4, '16:30', '03:00', FALSE),
    (8, 5, '16:30', '03:00', FALSE),
    (8, 6, '14:00', '04:00', FALSE),
    (8, 7, '14:00', '02:00', FALSE);

-- pub_id 9: 리얼펍 잠실새내점 (SONGPA / JAMSIL)
INSERT INTO pubs (pub_id, name, address, region, sub_region, status, capacity_range, group_seat_max_people, favorite_count, description) VALUES
    (9, '리얼펍 잠실새내점', '서울 송파구 백제고분로7길 24-11 1층', 'SONGPA', 'JAMSIL', 'OPEN', 'R_50_100', 60, 0,
     '잠실새내 대표 펍, 대낮부터 새벽 6시까지 영업, TV 여러 대, 다양한 하이볼·생맥주, 회식·모임·데이트·혼술 환영');
INSERT INTO pub_supported_teams (pub_id, team_id) VALUES
    (9, 2), (9, 1);
INSERT INTO pub_facilities (pub_id, facility_code) VALUES
    (9, 'GROUP_SEAT'), (9, 'TERRACE'), (9, 'RESERVATION'), (9, 'PRIVATE_BOOKING');
INSERT INTO pub_styles (pub_id, style_code) VALUES
    (9, 'BIG_SCREEN'), (9, 'MULTI_TV'), (9, 'SPECTATOR_MODE');
INSERT INTO pub_themes (pub_id, theme_code) VALUES
    (9, 'SPECIAL_MENU');
INSERT INTO pub_food_tags (pub_id, food_code) VALUES
    (9, 'CHICKEN'), (9, 'PIZZA'), (9, 'FRY'), (9, 'STEW'), (9, 'GRILLED'), (9, 'BUNSIK'), (9, 'DRY_SNACK'), (9, 'SOJU'), (9, 'BEER'), (9, 'HIGHBALL');
INSERT INTO pub_business_hours (pub_id, day_of_week, open_time, close_time, is_closed) VALUES
    (9, 1, '15:00', '06:00', FALSE),
    (9, 2, '15:00', '06:00', FALSE),
    (9, 3, '15:00', '06:00', FALSE),
    (9, 4, '15:00', '06:00', FALSE),
    (9, 5, '15:00', '06:00', FALSE),
    (9, 6, '12:00', '06:00', FALSE),
    (9, 7, '12:00', '06:00', FALSE);

-- pub_id 10: 마디그라 (JONGNO / —)
INSERT INTO pubs (pub_id, name, address, region, sub_region, status, capacity_range, group_seat_max_people, favorite_count, description) VALUES
    (10, '마디그라', '서울 종로구 우정국로2길 29 B1F', 'JONGNO', NULL, 'OPEN', 'R_50_100', 80, 0,
     '종각역 4번 출구 50m, American Diner 수제버거·수제맥주, 대형 빔스크린 2 + TV 7대로 야구·축구 관전, 최대 80명 단체·대관');
INSERT INTO pub_supported_teams (pub_id, team_id) VALUES
    (10, 1), (10, 2), (10, 3), (10, 4), (10, 5), (10, 6), (10, 7), (10, 8), (10, 9), (10, 10);
INSERT INTO pub_facilities (pub_id, facility_code) VALUES
    (10, 'GROUP_SEAT'), (10, 'RESERVATION'), (10, 'PRIVATE_BOOKING'), (10, 'WHEELCHAIR_ACCESS');
INSERT INTO pub_styles (pub_id, style_code) VALUES
    (10, 'BIG_SCREEN'), (10, 'MULTI_TV'), (10, 'SPECTATOR_MODE'), (10, 'LOUD_SPEAKER');
INSERT INTO pub_themes (pub_id, theme_code) VALUES
    (10, 'SPECIAL_MENU');
INSERT INTO pub_food_tags (pub_id, food_code) VALUES
    (10, 'GRILLED'), (10, 'PIZZA'), (10, 'CHICKEN'), (10, 'FRY'), (10, 'STEW'), (10, 'BUNSIK'), (10, 'DRY_SNACK'), (10, 'BEER');
INSERT INTO pub_business_hours (pub_id, day_of_week, open_time, close_time, is_closed) VALUES
    (10, 1, '11:00', '23:00', FALSE),
    (10, 2, '11:00', '23:00', FALSE),
    (10, 3, '11:00', '23:00', FALSE),
    (10, 4, '11:00', '23:00', FALSE),
    (10, 5, '11:00', '23:00', FALSE),
    (10, 6, NULL, NULL, TRUE),
    (10, 7, NULL, NULL, TRUE);

-- pub_id 11: 매치볼하우스 (GANGBUK / —)
INSERT INTO pubs (pub_id, name, address, region, sub_region, status, capacity_range, group_seat_max_people, favorite_count, description) VALUES
    (11, '매치볼하우스', '서울 강북구 노해로 38 두온오피스텔 2층 203호', 'GANGBUK', NULL, 'OPEN', NULL, NULL, 0,
     '강북구 두온오피스텔 2층, 축구공 박물관 컨셉 스포츠 펍, 야구·축구·EPL·챔피언스리그 중계 (경기 시간에 따라 오픈·마감 유동)');
INSERT INTO pub_supported_teams (pub_id, team_id) VALUES
    (11, 1), (11, 2), (11, 3), (11, 4), (11, 5), (11, 6), (11, 7), (11, 8), (11, 9), (11, 10);
INSERT INTO pub_facilities (pub_id, facility_code) VALUES
    (11, 'GROUP_SEAT'), (11, 'RESERVATION'), (11, 'PARKING');
INSERT INTO pub_styles (pub_id, style_code) VALUES
    (11, 'BIG_SCREEN'), (11, 'MULTI_TV'), (11, 'STADIUM_MODE'), (11, 'LOUD_SPEAKER');
INSERT INTO pub_themes (pub_id, theme_code) VALUES
    (11, 'SPECIAL_MENU');
INSERT INTO pub_food_tags (pub_id, food_code) VALUES
    (11, 'CHICKEN'), (11, 'PIZZA'), (11, 'FRY'), (11, 'BUNSIK'), (11, 'GRILLED'), (11, 'DRY_SNACK'), (11, 'SOJU'), (11, 'BEER'), (11, 'HIGHBALL');
INSERT INTO pub_business_hours (pub_id, day_of_week, open_time, close_time, is_closed) VALUES
    (11, 1, NULL, NULL, TRUE),
    (11, 2, '17:00', '03:00', FALSE),
    (11, 3, '17:00', '03:00', FALSE),
    (11, 4, '17:00', '03:00', FALSE),
    (11, 5, '17:00', '03:00', FALSE),
    (11, 6, '17:00', '03:00', FALSE),
    (11, 7, '17:00', '03:00', FALSE);

-- pub_id 12: 베이직프라이드치킨 (MAPO / SANGAM_MANGWON)
INSERT INTO pubs (pub_id, name, address, region, sub_region, status, capacity_range, group_seat_max_people, favorite_count, description) VALUES
    (12, '베이직프라이드치킨', '서울 마포구 월드컵로 140 1층', 'MAPO', 'SANGAM_MANGWON', 'OPEN', 'R_50_100', 50, 0,
     '마포구청역 도보 5분(330m), 망원·성산 대표 호프·치킨집, 100% 국내산 냉장 생닭, TV 2대로 야구·축구 관전(평시 무음, 국대 경기 시 소리 ON), 50석 단체·대관 가능');
INSERT INTO pub_supported_teams (pub_id, team_id) VALUES
    (12, 1), (12, 2), (12, 3), (12, 4), (12, 5), (12, 6), (12, 7), (12, 8), (12, 9), (12, 10);
INSERT INTO pub_facilities (pub_id, facility_code) VALUES
    (12, 'GROUP_SEAT'), (12, 'RESERVATION'), (12, 'PRIVATE_BOOKING'), (12, 'PARKING');
INSERT INTO pub_styles (pub_id, style_code) VALUES
    (12, 'MULTI_TV');
INSERT INTO pub_themes (pub_id, theme_code) VALUES
    (12, 'FRESH');
INSERT INTO pub_food_tags (pub_id, food_code) VALUES
    (12, 'CHICKEN'), (12, 'BEER');
INSERT INTO pub_business_hours (pub_id, day_of_week, open_time, close_time, is_closed) VALUES
    (12, 1, '14:00', '02:00', FALSE),
    (12, 2, '14:00', '02:00', FALSE),
    (12, 3, '14:00', '02:00', FALSE),
    (12, 4, '14:00', '02:00', FALSE),
    (12, 5, '14:00', '02:00', FALSE),
    (12, 6, '14:00', '02:00', FALSE),
    (12, 7, NULL, NULL, TRUE);

-- pub_id 13: 삼층맥주집 이수역점 (DONGJAK / —)
INSERT INTO pubs (pub_id, name, address, region, sub_region, status, capacity_range, group_seat_max_people, favorite_count, description) VALUES
    (13, '삼층맥주집 이수역점', '서울 동작구 동작대로27가길 12 3층', 'DONGJAK', NULL, 'OPEN', 'R_50_100', 90, 0,
     '이수역 11번·총신대입구역 13번 출구 도보 근접(설빙 건물 3층), 90석 단체·대관 특화 펍, 대형 스크린 1 + TV 1로 야구·축구 관전(평시 무음), 크리스피 치킨·파스타·맥앤치즈 등 다양 메뉴');
INSERT INTO pub_supported_teams (pub_id, team_id) VALUES
    (13, 1), (13, 2), (13, 3), (13, 4), (13, 5), (13, 6), (13, 7), (13, 8), (13, 9), (13, 10);
INSERT INTO pub_facilities (pub_id, facility_code) VALUES
    (13, 'GROUP_SEAT'), (13, 'RESERVATION'), (13, 'PRIVATE_BOOKING'), (13, 'WHEELCHAIR_ACCESS');
INSERT INTO pub_styles (pub_id, style_code) VALUES
    (13, 'BIG_SCREEN'), (13, 'SINGLE_TV'), (13, 'SPECTATOR_MODE');
INSERT INTO pub_themes (pub_id, theme_code) VALUES
    (13, 'SPECIAL_MENU');
INSERT INTO pub_food_tags (pub_id, food_code) VALUES
    (13, 'CHICKEN'), (13, 'GRILLED'), (13, 'BUNSIK'), (13, 'DRY_SNACK'), (13, 'FRY'), (13, 'SOJU'), (13, 'BEER'), (13, 'HIGHBALL');
INSERT INTO pub_business_hours (pub_id, day_of_week, open_time, close_time, is_closed) VALUES
    (13, 1, '17:00', '01:00', FALSE),
    (13, 2, '17:00', '01:00', FALSE),
    (13, 3, '17:00', '01:00', FALSE),
    (13, 4, '17:00', '01:00', FALSE),
    (13, 5, '17:00', '02:00', FALSE),
    (13, 6, '17:00', '02:00', FALSE),
    (13, 7, '17:00', '23:59', FALSE);

-- pub_id 14: 서울갈매기 (MAPO / HONGDAE_HAPJEONG)
INSERT INTO pubs (pub_id, name, address, region, sub_region, status, capacity_range, group_seat_max_people, favorite_count, description) VALUES
    (14, '서울갈매기', '서울 마포구 연희로 3 2층', 'MAPO', 'HONGDAE_HAPJEONG', 'OPEN', 'R_20_50', NULL, 0,
     '홍대입구역 3번 출구 도보, 수도권 롯데팬 응원 특화 펍, 대형 스크린 + 큰 사운드로 직관 대체, 갈매기 하이볼·수제맥주(갈매기 IPA/라거)·매콤 닭강정 시그니처');
INSERT INTO pub_supported_teams (pub_id, team_id) VALUES
    (14, 7);
INSERT INTO pub_facilities (pub_id, facility_code) VALUES
    (14, 'RESERVATION'), (14, 'SOLO_SEAT'), (14, 'COUNTER_SEAT');
INSERT INTO pub_styles (pub_id, style_code) VALUES
    (14, 'BIG_SCREEN'), (14, 'STADIUM_MODE'), (14, 'LOUD_SPEAKER');
INSERT INTO pub_themes (pub_id, theme_code) VALUES
    (14, 'SPECIAL_MENU');
INSERT INTO pub_food_tags (pub_id, food_code) VALUES
    (14, 'CHICKEN'), (14, 'FRY'), (14, 'DRY_SNACK'), (14, 'BEER'), (14, 'HIGHBALL');
INSERT INTO pub_business_hours (pub_id, day_of_week, open_time, close_time, is_closed) VALUES
    (14, 1, NULL, NULL, TRUE),
    (14, 2, '12:00', '23:59', FALSE),
    (14, 3, '12:00', '23:59', FALSE),
    (14, 4, '12:00', '23:59', FALSE),
    (14, 5, '12:00', '23:59', FALSE),
    (14, 6, '12:00', '23:59', FALSE),
    (14, 7, '12:00', '23:59', FALSE);

-- pub_id 15: 설맥 건대점 (GWANGJIN / —)
INSERT INTO pubs (pub_id, name, address, region, sub_region, status, capacity_range, group_seat_max_people, favorite_count, description) VALUES
    (15, '설맥 건대점', '서울 광진구 능동로13길 15 지하1층', 'GWANGJIN', NULL, 'OPEN', 'R_50_100', 60, 0,
     '건대입구역(2·7호선) 도보 지하 대형 펍, 시그니처 눈꽃맥주(백종원 오피셜)와 치킨 11종·안주 30종+, 대형 스크린 + TV로 야구·해외축구·농구·e스포츠 응원 관전, 60석 단체·회식·대관 특화');
INSERT INTO pub_supported_teams (pub_id, team_id) VALUES
    (15, 1), (15, 2), (15, 3), (15, 4), (15, 5), (15, 6), (15, 7), (15, 8), (15, 9), (15, 10);
INSERT INTO pub_facilities (pub_id, facility_code) VALUES
    (15, 'GROUP_SEAT'), (15, 'RESERVATION'), (15, 'PRIVATE_BOOKING'), (15, 'SOLO_SEAT');
INSERT INTO pub_styles (pub_id, style_code) VALUES
    (15, 'BIG_SCREEN'), (15, 'SINGLE_TV'), (15, 'STADIUM_MODE'), (15, 'LOUD_SPEAKER');
INSERT INTO pub_themes (pub_id, theme_code) VALUES
    (15, 'SPECIAL_MENU');
INSERT INTO pub_food_tags (pub_id, food_code) VALUES
    (15, 'CHICKEN'), (15, 'DRY_SNACK'), (15, 'FRY'), (15, 'GRILLED'), (15, 'PIZZA'), (15, 'STEW'), (15, 'BUNSIK'), (15, 'SOJU'), (15, 'BEER'), (15, 'HIGHBALL');
INSERT INTO pub_business_hours (pub_id, day_of_week, open_time, close_time, is_closed) VALUES
    (15, 1, '17:30', '01:00', FALSE),
    (15, 2, '17:30', '01:00', FALSE),
    (15, 3, '17:30', '01:00', FALSE),
    (15, 4, '17:30', '01:00', FALSE),
    (15, 5, '17:30', '01:00', FALSE),
    (15, 6, '17:30', '01:00', FALSE),
    (15, 7, '17:30', '01:00', FALSE);

-- pub_id 16: 스패로우 상계점 (NOWON / —)
INSERT INTO pubs (pub_id, name, address, region, sub_region, status, capacity_range, group_seat_max_people, favorite_count, description) VALUES
    (16, '스패로우 상계점', '서울 노원구 한글비석로20길 36 2층', 'NOWON', NULL, 'OPEN', 'R_20_50', 12, 0,
     '상계역 1번 출구 도보(먹자골목 솥뚜껑 삼겹살 건물 2층), 스패로우 성신여대 본점 2호점, 파스타·피자 전문 스포츠 펍, 대형 스크린 2 + TV 1로 야구·축구 관전(평시 무음), KBO 10개 구단 시그니처 하이볼, 36석(홀 24 + 룸 12) 단체·대관 가능');
INSERT INTO pub_supported_teams (pub_id, team_id) VALUES
    (16, 1), (16, 2), (16, 3), (16, 4), (16, 5), (16, 6), (16, 7), (16, 8), (16, 9), (16, 10);
INSERT INTO pub_facilities (pub_id, facility_code) VALUES
    (16, 'GROUP_SEAT'), (16, 'RESERVATION'), (16, 'PRIVATE_BOOKING');
INSERT INTO pub_styles (pub_id, style_code) VALUES
    (16, 'BIG_SCREEN'), (16, 'SINGLE_TV');
INSERT INTO pub_themes (pub_id, theme_code) VALUES
    (16, 'SPECIAL_MENU');
INSERT INTO pub_food_tags (pub_id, food_code) VALUES
    (16, 'PIZZA'), (16, 'FRY'), (16, 'STEW'), (16, 'SOJU'), (16, 'BEER'), (16, 'HIGHBALL');
INSERT INTO pub_business_hours (pub_id, day_of_week, open_time, close_time, is_closed) VALUES
    (16, 1, NULL, NULL, TRUE),
    (16, 2, '18:00', '01:00', FALSE),
    (16, 3, '18:00', '01:00', FALSE),
    (16, 4, '18:00', '01:00', FALSE),
    (16, 5, '18:00', '01:00', FALSE),
    (16, 6, '17:00', '23:59', FALSE),
    (16, 7, '16:30', '21:00', FALSE);

-- pub_id 17: 야구는 핑계고 (SONGPA / JAMSIL)
INSERT INTO pubs (pub_id, name, address, region, sub_region, status, capacity_range, group_seat_max_people, favorite_count, description) VALUES
    (17, '야구는 핑계고', '서울 송파구 백제고분로7길 24-14 2층', 'SONGPA', 'JAMSIL', 'OPEN', 'R_50_100', NULL, 0,
     '잠실새내 야구 관전 펍, 모든 KBO 구단 중계, 스크린 2대 + 큰소리로 현장감 응원, 짬뽕탕·치킨·안주 다양, 매일 18:00-03:00 연중무휴');
INSERT INTO pub_supported_teams (pub_id, team_id) VALUES
    (17, 1), (17, 2), (17, 3), (17, 4), (17, 5), (17, 6), (17, 7), (17, 8), (17, 9), (17, 10);
INSERT INTO pub_facilities (pub_id, facility_code) VALUES
    (17, 'GROUP_SEAT'), (17, 'RESERVATION');
INSERT INTO pub_styles (pub_id, style_code) VALUES
    (17, 'BIG_SCREEN'), (17, 'STADIUM_MODE'), (17, 'LOUD_SPEAKER');
INSERT INTO pub_themes (pub_id, theme_code) VALUES
    (17, 'SPECIAL_MENU');
INSERT INTO pub_food_tags (pub_id, food_code) VALUES
    (17, 'STEW'), (17, 'GRILLED'), (17, 'FRY'), (17, 'CHICKEN'), (17, 'BUNSIK'), (17, 'SOJU'), (17, 'BEER'), (17, 'HIGHBALL');
INSERT INTO pub_business_hours (pub_id, day_of_week, open_time, close_time, is_closed) VALUES
    (17, 1, '18:00', '03:00', FALSE),
    (17, 2, '18:00', '03:00', FALSE),
    (17, 3, '18:00', '03:00', FALSE),
    (17, 4, '18:00', '03:00', FALSE),
    (17, 5, '18:00', '03:00', FALSE),
    (17, 6, '18:00', '03:00', FALSE),
    (17, 7, '18:00', '03:00', FALSE);

-- pub_id 18: 야구보러가자 (JUNGNANG / —)
INSERT INTO pubs (pub_id, name, address, region, sub_region, status, capacity_range, group_seat_max_people, favorite_count, description) VALUES
    (18, '야구보러가자', '서울 중랑구 면목로45길 15-3', 'JUNGNANG', NULL, 'OPEN', 'R_20_50', 40, 0,
     '사가정역 인근 야구 마니아 성지, 사장님 수집 야구 애장품(유니폼·사인볼·올드 배트·기념 티켓)으로 꾸민 야구 박물관 콘셉트, TV 4대 + 큰 사운드로 KBO 실시간 응원 중계, 40석 단체·대관 가능, 매일 17:00-01:00');
INSERT INTO pub_supported_teams (pub_id, team_id) VALUES
    (18, 1), (18, 2), (18, 3), (18, 4), (18, 5), (18, 6), (18, 7), (18, 8), (18, 9), (18, 10);
INSERT INTO pub_facilities (pub_id, facility_code) VALUES
    (18, 'GROUP_SEAT'), (18, 'RESERVATION'), (18, 'PRIVATE_BOOKING');
INSERT INTO pub_styles (pub_id, style_code) VALUES
    (18, 'MULTI_TV'), (18, 'STADIUM_MODE'), (18, 'LOUD_SPEAKER');
INSERT INTO pub_themes (pub_id, theme_code) VALUES
    (18, 'SPECIAL_MENU');
INSERT INTO pub_food_tags (pub_id, food_code) VALUES
    (18, 'CHICKEN'), (18, 'FRY'), (18, 'STEW'), (18, 'GRILLED'), (18, 'BUNSIK'), (18, 'DRY_SNACK'), (18, 'SOJU'), (18, 'BEER');
INSERT INTO pub_business_hours (pub_id, day_of_week, open_time, close_time, is_closed) VALUES
    (18, 1, '17:00', '01:00', FALSE),
    (18, 2, '17:00', '01:00', FALSE),
    (18, 3, '17:00', '01:00', FALSE),
    (18, 4, '17:00', '01:00', FALSE),
    (18, 5, '17:00', '01:00', FALSE),
    (18, 6, '17:00', '01:00', FALSE),
    (18, 7, '17:00', '01:00', FALSE);

-- pub_id 19: 엘지포차 (JONGNO / —)
INSERT INTO pubs (pub_id, name, address, region, sub_region, status, capacity_range, group_seat_max_people, favorite_count, description) VALUES
    (19, '엘지포차', '서울 종로구 종로39길 16-1 1층', 'JONGNO', NULL, 'OPEN', 'R_20_50', 40, 0,
     '종로 창신동 인근 LG 팬 응원 포차, TV 1대 + 큰소리 + 현장분위기로 함께 응원, 얀푼찌개·김치수제비·크림새우치킨 등 다양 안주 약 40석. 야구 시즌 주말 13시 오픈, 비시즌 일요일 휴무');
INSERT INTO pub_supported_teams (pub_id, team_id) VALUES
    (19, 1);
INSERT INTO pub_facilities (pub_id, facility_code) VALUES
    (19, 'GROUP_SEAT'), (19, 'RESERVATION');
INSERT INTO pub_styles (pub_id, style_code) VALUES
    (19, 'SINGLE_TV'), (19, 'STADIUM_MODE'), (19, 'LOUD_SPEAKER');
INSERT INTO pub_themes (pub_id, theme_code) VALUES
    (19, 'SPECIAL_MENU');
INSERT INTO pub_food_tags (pub_id, food_code) VALUES
    (19, 'FRY'), (19, 'STEW'), (19, 'BUNSIK'), (19, 'GRILLED'), (19, 'CHICKEN'), (19, 'DRY_SNACK'), (19, 'SOJU'), (19, 'BEER');
INSERT INTO pub_business_hours (pub_id, day_of_week, open_time, close_time, is_closed) VALUES
    (19, 1, '16:00', '23:59', FALSE),
    (19, 2, '16:00', '23:59', FALSE),
    (19, 3, '16:00', '23:59', FALSE),
    (19, 4, '16:00', '23:59', FALSE),
    (19, 5, '16:00', '23:59', FALSE),
    (19, 6, '13:00', '23:59', FALSE),
    (19, 7, '13:00', '23:59', FALSE);

-- pub_id 20: 카페&펍 연무장 던던 동대문점 (JUNG / —)
INSERT INTO pubs (pub_id, name, address, region, sub_region, status, capacity_range, group_seat_max_people, favorite_count, description) VALUES
    (20, '카페&펍 연무장 던던 동대문점', '서울 중구 을지로 264 7층', 'JUNG', NULL, 'OPEN', 'R_50_100', 80, 0,
     '동대문역사문화공원역 11번 출구 지하 직결 7층, 스포츠 펍&카페 콘셉트, 대형 스크린으로 다양 스포츠 관전, 수제 버거·BBQ 플래터·타코·파스타 등 다이닝, 무료 주차(2시간) + 휠체어 접근, 매일 10:30-23:00');
INSERT INTO pub_supported_teams (pub_id, team_id) VALUES
    (20, 1), (20, 2), (20, 3), (20, 4), (20, 5), (20, 6), (20, 7), (20, 8), (20, 9), (20, 10);
INSERT INTO pub_facilities (pub_id, facility_code) VALUES
    (20, 'GROUP_SEAT'), (20, 'RESERVATION'), (20, 'PRIVATE_BOOKING'), (20, 'WHEELCHAIR_ACCESS'), (20, 'PARKING'), (20, 'SOLO_SEAT');
INSERT INTO pub_styles (pub_id, style_code) VALUES
    (20, 'BIG_SCREEN'), (20, 'SPECTATOR_MODE');
INSERT INTO pub_themes (pub_id, theme_code) VALUES
    (20, 'SPECIAL_MENU');
INSERT INTO pub_food_tags (pub_id, food_code) VALUES
    (20, 'GRILLED'), (20, 'CHICKEN'), (20, 'PIZZA'), (20, 'TACO'), (20, 'FRY'), (20, 'BEER'), (20, 'HIGHBALL');
INSERT INTO pub_business_hours (pub_id, day_of_week, open_time, close_time, is_closed) VALUES
    (20, 1, '10:30', '23:00', FALSE),
    (20, 2, '10:30', '23:00', FALSE),
    (20, 3, '10:30', '23:00', FALSE),
    (20, 4, '10:30', '23:00', FALSE),
    (20, 5, '10:30', '23:00', FALSE),
    (20, 6, '10:30', '23:00', FALSE),
    (20, 7, '10:30', '23:00', FALSE);

-- pub_id 21: 연화주점 (MAPO / SANGAM_MANGWON)
INSERT INTO pubs (pub_id, name, address, region, sub_region, status, capacity_range, group_seat_max_people, favorite_count, description) VALUES
    (21, '연화주점', '서울 마포구 월드컵북로44길 50 2층', 'MAPO', 'SANGAM_MANGWON', 'OPEN', 'R_20_50', 14, 0,
     '상암 DMC역 9번 출구 300m, 고급 중식 오너셰프 중화요리주점, 100인치 스크린 + 40인치 TV로 LG 우선 야구 관전(시청 손님 많을 시 소리 ON), 육즙탕수육·고추파유린기·사천팔보채·고량주 연화볼 시그니처, 43석 단체·대관 가능');
INSERT INTO pub_supported_teams (pub_id, team_id) VALUES
    (21, 1);
INSERT INTO pub_facilities (pub_id, facility_code) VALUES
    (21, 'GROUP_SEAT'), (21, 'RESERVATION'), (21, 'PRIVATE_BOOKING'), (21, 'PARKING'), (21, 'SOLO_SEAT'), (21, 'COUNTER_SEAT');
INSERT INTO pub_styles (pub_id, style_code) VALUES
    (21, 'BIG_SCREEN'), (21, 'SINGLE_TV'), (21, 'SPECTATOR_MODE');
INSERT INTO pub_themes (pub_id, theme_code) VALUES
    (21, 'SPECIAL_MENU');
INSERT INTO pub_food_tags (pub_id, food_code) VALUES
    (21, 'CHICKEN'), (21, 'FRY'), (21, 'STEW'), (21, 'GRILLED'), (21, 'DRY_SNACK'), (21, 'SOJU'), (21, 'BEER'), (21, 'HIGHBALL');
INSERT INTO pub_business_hours (pub_id, day_of_week, open_time, close_time, is_closed) VALUES
    (21, 1, '11:30', '01:00', FALSE),
    (21, 2, '11:30', '01:00', FALSE),
    (21, 3, '11:30', '01:00', FALSE),
    (21, 4, '11:30', '01:00', FALSE),
    (21, 5, '11:30', '01:00', FALSE),
    (21, 6, '16:00', '01:00', FALSE),
    (21, 7, '16:00', '01:00', FALSE);

-- pub_id 22: 오하이요 잠실새내점 (SONGPA / JAMSIL)
INSERT INTO pubs (pub_id, name, address, region, sub_region, status, capacity_range, group_seat_max_people, favorite_count, description) VALUES
    (22, '오하이요 잠실새내점', '서울 송파구 백제고분로7길 28-8 1층', 'SONGPA', 'JAMSIL', 'OPEN', 'R_20_50', 30, 0,
     '잠실새내 백제고분로 골목 캐주얼 이자카야, 롯데 팬 응원 공간, TV 1대로 자이언츠 관전, 점보 가라아게·나베·야끼소바·타코야끼 일본식 안주 + 3,800원 하이볼·과실 사와, 야외 테라스 포함 30석, 매일 17:30-02:00');
INSERT INTO pub_supported_teams (pub_id, team_id) VALUES
    (22, 7);
INSERT INTO pub_facilities (pub_id, facility_code) VALUES
    (22, 'GROUP_SEAT'), (22, 'RESERVATION'), (22, 'TERRACE'), (22, 'SOLO_SEAT'), (22, 'COUNTER_SEAT');
INSERT INTO pub_styles (pub_id, style_code) VALUES
    (22, 'SINGLE_TV'), (22, 'SPECTATOR_MODE');
INSERT INTO pub_themes (pub_id, theme_code) VALUES
    (22, 'SPECIAL_MENU');
INSERT INTO pub_food_tags (pub_id, food_code) VALUES
    (22, 'CHICKEN'), (22, 'GRILLED'), (22, 'FRY'), (22, 'STEW'), (22, 'BUNSIK'), (22, 'HIGHBALL'), (22, 'COCKTAIL');
INSERT INTO pub_business_hours (pub_id, day_of_week, open_time, close_time, is_closed) VALUES
    (22, 1, '17:30', '02:00', FALSE),
    (22, 2, '17:30', '02:00', FALSE),
    (22, 3, '17:30', '02:00', FALSE),
    (22, 4, '17:30', '02:00', FALSE),
    (22, 5, '17:30', '02:00', FALSE),
    (22, 6, '17:30', '02:00', FALSE),
    (22, 7, '17:30', '02:00', FALSE);

-- pub_id 23: 외계인피자 은평직영점 (EUNPYEONG / —)
INSERT INTO pubs (pub_id, name, address, region, sub_region, status, capacity_range, group_seat_max_people, favorite_count, description) VALUES
    (23, '외계인피자 은평직영점', '서울 은평구 서오릉로 128 1층', 'EUNPYEONG', NULL, 'OPEN', 'R_20_50', 30, 0,
     '은평구 직영 피자 펍, 100인치 대형 TV 1대로 LG 우선 야구 관전(소리 ON, 단체 예약 시 요청팀 상영), 외계인·지구인 피자 시리즈 20+종·1인피자 라인업, 30석(4인 6 + 2인 3) 규모, 대관·예약 사전 협의');
INSERT INTO pub_supported_teams (pub_id, team_id) VALUES
    (23, 1);
INSERT INTO pub_facilities (pub_id, facility_code) VALUES
    (23, 'GROUP_SEAT'), (23, 'RESERVATION'), (23, 'PRIVATE_BOOKING'), (23, 'COUNTER_SEAT'), (23, 'SOLO_SEAT');
INSERT INTO pub_styles (pub_id, style_code) VALUES
    (23, 'BIG_SCREEN'), (23, 'SINGLE_TV'), (23, 'SPECTATOR_MODE'), (23, 'LOUD_SPEAKER');
INSERT INTO pub_themes (pub_id, theme_code) VALUES
    (23, 'SPECIAL_MENU');
INSERT INTO pub_food_tags (pub_id, food_code) VALUES
    (23, 'PIZZA'), (23, 'CHICKEN'), (23, 'FRY'), (23, 'SOJU'), (23, 'BEER'), (23, 'HIGHBALL');
INSERT INTO pub_business_hours (pub_id, day_of_week, open_time, close_time, is_closed) VALUES
    (23, 1, NULL, NULL, TRUE),
    (23, 2, '17:00', '06:00', FALSE),
    (23, 3, '17:00', '06:00', FALSE),
    (23, 4, '17:00', '06:00', FALSE),
    (23, 5, '17:00', '06:00', FALSE),
    (23, 6, '17:00', '06:00', FALSE),
    (23, 7, '17:00', '23:59', FALSE);

-- pub_id 24: 을지OB베어 (JUNG / —)
INSERT INTO pubs (pub_id, name, address, region, sub_region, status, capacity_range, group_seat_max_people, favorite_count, description) VALUES
    (24, '을지OB베어', '서울 중구 충무로 49-2 1층', 'JUNG', NULL, 'OPEN', 'OVER_100', 200, 0,
     '충무로·을지로 상권 1980년 대한민국 최초 생맥주집, 노맥(노가리+맥주) 원조, 대형 멀티 스크린으로 야구 팀 응원 중계 명소, 노가리·번데기탕·부대찌개·을지로 골뱅이 시그니처, 200석 규모 단체 가능');
INSERT INTO pub_supported_teams (pub_id, team_id) VALUES
    (24, 2);
INSERT INTO pub_facilities (pub_id, facility_code) VALUES
    (24, 'GROUP_SEAT'), (24, 'RESERVATION');
INSERT INTO pub_styles (pub_id, style_code) VALUES
    (24, 'BIG_SCREEN'), (24, 'MULTI_TV'), (24, 'STADIUM_MODE'), (24, 'LOUD_SPEAKER');
INSERT INTO pub_themes (pub_id, theme_code) VALUES
    (24, 'SPECIAL_MENU');
INSERT INTO pub_food_tags (pub_id, food_code) VALUES
    (24, 'DRY_SNACK'), (24, 'GRILLED'), (24, 'CHICKEN'), (24, 'BUNSIK'), (24, 'STEW'), (24, 'FRY'), (24, 'SOJU'), (24, 'BEER');
INSERT INTO pub_business_hours (pub_id, day_of_week, open_time, close_time, is_closed) VALUES
    (24, 1, '15:00', '23:30', FALSE),
    (24, 2, '15:00', '23:30', FALSE),
    (24, 3, '15:00', '23:30', FALSE),
    (24, 4, '15:00', '23:30', FALSE),
    (24, 5, '15:00', '23:30', FALSE),
    (24, 6, '15:00', '23:30', FALSE),
    (24, 7, '15:00', '22:00', FALSE);

-- pub_id 25: 인저리타임 (GANGSEO / —)
INSERT INTO pubs (pub_id, name, address, region, sub_region, status, capacity_range, group_seat_max_people, favorite_count, description) VALUES
    (25, '인저리타임', '서울 강서구 마곡중앙6로 45 A동 1층 107호', 'GANGSEO', NULL, 'OPEN', 'R_20_50', 33, 0,
     '마곡 카페&펍, LG 우선 야구·축구 실시간 중계, 100인치 대형 스크린 1대(실내 손님 많을 시 소리 ON), 토치드 수제잠봉·잠봉루꼴라피자·초코브라우니&하겐다즈 시그니처, 33석(실내 29 + 테라스 4) + 반려동물 동반 · 건물 지하 유료 주차');
INSERT INTO pub_supported_teams (pub_id, team_id) VALUES
    (25, 1);
INSERT INTO pub_facilities (pub_id, facility_code) VALUES
    (25, 'GROUP_SEAT'), (25, 'RESERVATION'), (25, 'PRIVATE_BOOKING'), (25, 'PARKING'), (25, 'TERRACE'), (25, 'COUNTER_SEAT'), (25, 'SOLO_SEAT'), (25, 'PET_FRIENDLY');
INSERT INTO pub_styles (pub_id, style_code) VALUES
    (25, 'BIG_SCREEN'), (25, 'SINGLE_TV'), (25, 'SPECTATOR_MODE');
INSERT INTO pub_themes (pub_id, theme_code) VALUES
    (25, 'SPECIAL_MENU');
INSERT INTO pub_food_tags (pub_id, food_code) VALUES
    (25, 'PIZZA'), (25, 'GRILLED'), (25, 'CHICKEN'), (25, 'BUNSIK'), (25, 'FRY'), (25, 'BEER');
INSERT INTO pub_business_hours (pub_id, day_of_week, open_time, close_time, is_closed) VALUES
    (25, 1, '12:00', '23:00', FALSE),
    (25, 2, '12:00', '23:00', FALSE),
    (25, 3, '12:00', '23:00', FALSE),
    (25, 4, '12:00', '23:00', FALSE),
    (25, 5, '12:00', '23:00', FALSE),
    (25, 6, '14:00', '23:00', FALSE),
    (25, 7, '14:00', '23:00', FALSE);

-- pub_id 26: 잠실새내 맥JOO 퍼블릭하우스 (SONGPA / JAMSIL)
INSERT INTO pubs (pub_id, name, address, region, sub_region, status, capacity_range, group_seat_max_people, favorite_count, description) VALUES
    (26, '잠실새내 맥JOO 퍼블릭하우스', '서울 송파구 백제고분로7길 42-12 2층', 'SONGPA', 'JAMSIL', 'OPEN', 'R_20_50', 30, 0,
     '잠실새내 백제고분로 골목 퍼블릭하우스, 대형 TV 2대(80·50인치)로 야구·해외축구·롤게임 관전(예약 우선), 새우 감바스+빵·맥JOO 플래터·버드와이저생·호가든생 시그니처, 30명 규모 대관 가능, 무료 주차, 해외축구 시 연장 영업');
INSERT INTO pub_supported_teams (pub_id, team_id) VALUES
    (26, 1), (26, 2), (26, 3), (26, 4), (26, 5), (26, 6), (26, 7), (26, 8), (26, 9), (26, 10);
INSERT INTO pub_facilities (pub_id, facility_code) VALUES
    (26, 'GROUP_SEAT'), (26, 'RESERVATION'), (26, 'PRIVATE_BOOKING'), (26, 'PARKING');
INSERT INTO pub_styles (pub_id, style_code) VALUES
    (26, 'BIG_SCREEN'), (26, 'MULTI_TV'), (26, 'SPECTATOR_MODE');
INSERT INTO pub_themes (pub_id, theme_code) VALUES
    (26, 'SPECIAL_MENU');
INSERT INTO pub_food_tags (pub_id, food_code) VALUES
    (26, 'DRY_SNACK'), (26, 'GRILLED'), (26, 'FRY'), (26, 'BEER');
INSERT INTO pub_business_hours (pub_id, day_of_week, open_time, close_time, is_closed) VALUES
    (26, 1, '17:00', '02:00', FALSE),
    (26, 2, '17:00', '02:00', FALSE),
    (26, 3, '17:00', '02:00', FALSE),
    (26, 4, '17:00', '02:00', FALSE),
    (26, 5, '17:00', '03:00', FALSE),
    (26, 6, '17:00', '03:00', FALSE),
    (26, 7, '17:30', '02:00', FALSE);

-- pub_id 27: 제이케이펍 (MAPO / HONGDAE_HAPJEONG)
INSERT INTO pubs (pub_id, name, address, region, sub_region, status, capacity_range, group_seat_max_people, favorite_count, description) VALUES
    (27, '제이케이펍', '서울 마포구 어울마당로5길 46 2층', 'MAPO', 'HONGDAE_HAPJEONG', 'OPEN', 'R_50_100', 80, 0,
     '합정 어울마당로 스포츠펍, 스크린 7대로 야구·해외축구 모든 경기 중계(소리 ON), 8년 인생 감바스·직접 만든 소스 파스타·이베리코 목살 스테이크·엽떡·시그니처 칵테일 5종, 실내 40석 + 야외 테라스·루프탑 60석 총 100석, 반려동물·휠체어·주차 무료, 매일 새벽 6시까지');
INSERT INTO pub_supported_teams (pub_id, team_id) VALUES
    (27, 1), (27, 2), (27, 3), (27, 4), (27, 5), (27, 6), (27, 7), (27, 8), (27, 9), (27, 10);
INSERT INTO pub_facilities (pub_id, facility_code) VALUES
    (27, 'GROUP_SEAT'), (27, 'RESERVATION'), (27, 'PRIVATE_BOOKING'), (27, 'PARKING'), (27, 'PET_FRIENDLY'), (27, 'WHEELCHAIR_ACCESS'), (27, 'TERRACE'), (27, 'OUTDOOR_SEAT');
INSERT INTO pub_styles (pub_id, style_code) VALUES
    (27, 'BIG_SCREEN'), (27, 'MULTI_TV'), (27, 'STADIUM_MODE'), (27, 'LOUD_SPEAKER');
INSERT INTO pub_themes (pub_id, theme_code) VALUES
    (27, 'SPECIAL_MENU');
INSERT INTO pub_food_tags (pub_id, food_code) VALUES
    (27, 'CHICKEN'), (27, 'GRILLED'), (27, 'FRY'), (27, 'BUNSIK'), (27, 'DRY_SNACK'), (27, 'SOJU'), (27, 'BEER'), (27, 'HIGHBALL'), (27, 'COCKTAIL');
INSERT INTO pub_business_hours (pub_id, day_of_week, open_time, close_time, is_closed) VALUES
    (27, 1, '17:00', '06:00', FALSE),
    (27, 2, '17:00', '06:00', FALSE),
    (27, 3, '17:00', '06:00', FALSE),
    (27, 4, '17:00', '06:00', FALSE),
    (27, 5, '17:00', '06:00', FALSE),
    (27, 6, '14:00', '06:00', FALSE),
    (27, 7, '14:00', '06:00', FALSE);

-- pub_id 28: 치어하우스 (MAPO / HONGDAE_HAPJEONG)
INSERT INTO pubs (pub_id, name, address, region, sub_region, status, capacity_range, group_seat_max_people, favorite_count, description) VALUES
    (28, '치어하우스', '서울 마포구 와우산로11길 9-11 1층', 'MAPO', 'HONGDAE_HAPJEONG', 'OPEN', 'R_50_100', 35, 0,
     '홍대 와우산로 스포츠 응원 하우스, 지상·지하 스크린 각 1개 + TV 3대로 예약 경기 우선 중계(소리 ON, 요청 시 다른 경기 음소거 상영 가능), 투쁠9등급 한우 육회·라구감자튀김·나쵸 with 한우 시그니처, 지상 35인 + 지하 12인 규모 50석 대관 가능, 화~일 영업(월 휴무)');
INSERT INTO pub_supported_teams (pub_id, team_id) VALUES
    (28, 1), (28, 2), (28, 3), (28, 4), (28, 5), (28, 6), (28, 7), (28, 8), (28, 9), (28, 10);
INSERT INTO pub_facilities (pub_id, facility_code) VALUES
    (28, 'GROUP_SEAT'), (28, 'RESERVATION'), (28, 'PRIVATE_BOOKING'), (28, 'PET_FRIENDLY'), (28, 'COUNTER_SEAT'), (28, 'SOLO_SEAT');
INSERT INTO pub_styles (pub_id, style_code) VALUES
    (28, 'BIG_SCREEN'), (28, 'MULTI_TV'), (28, 'STADIUM_MODE'), (28, 'LOUD_SPEAKER');
INSERT INTO pub_themes (pub_id, theme_code) VALUES
    (28, 'SPECIAL_MENU');
INSERT INTO pub_food_tags (pub_id, food_code) VALUES
    (28, 'CHICKEN'), (28, 'GRILLED'), (28, 'FRY'), (28, 'STEW'), (28, 'DRY_SNACK'), (28, 'SOJU'), (28, 'BEER'), (28, 'COCKTAIL');
INSERT INTO pub_business_hours (pub_id, day_of_week, open_time, close_time, is_closed) VALUES
    (28, 1, NULL, NULL, TRUE),
    (28, 2, '17:00', '23:59', FALSE),
    (28, 3, '17:00', '23:59', FALSE),
    (28, 4, '17:00', '23:59', FALSE),
    (28, 5, '17:00', '02:00', FALSE),
    (28, 6, '17:00', '02:00', FALSE),
    (28, 7, '17:00', '23:59', FALSE);

-- pub_id 29: 크래프트아일랜드 잠실점 (SONGPA / JAMSIL)
INSERT INTO pubs (pub_id, name, address, region, sub_region, status, capacity_range, group_seat_max_people, favorite_count, description) VALUES
    (29, '크래프트아일랜드 잠실점', '서울 송파구 올림픽로35가길 10 더샵스타파크상가 114·115·119호', 'SONGPA', 'JAMSIL', 'OPEN', 'R_50_100', 40, 0,
     '잠실역 인근 크래프트 브런치&수제맥주 펍, 본관 100인치 빔프로젝트 영상 상영, 33가지 요리(파스타·피자·스테이크·바베큐 플래터) + 7종 수제맥주(진도 필스너·독도 바이젠·한산도 페일에일·백령도 IPA·제주도 스타우트·강화도 1876에일)·과일 와인·24종 와인, 본관 + 별관1호(40석)·별관2호(25석) 대관, 반려동물 동반·휠체어 접근·무료 주차 2시간');
INSERT INTO pub_supported_teams (pub_id, team_id) VALUES
    (29, 1), (29, 2), (29, 3), (29, 4), (29, 5), (29, 6), (29, 7), (29, 8), (29, 9), (29, 10);
INSERT INTO pub_facilities (pub_id, facility_code) VALUES
    (29, 'GROUP_SEAT'), (29, 'RESERVATION'), (29, 'PRIVATE_BOOKING'), (29, 'PARKING'), (29, 'WHEELCHAIR_ACCESS'), (29, 'PET_FRIENDLY');
INSERT INTO pub_styles (pub_id, style_code) VALUES
    (29, 'BIG_SCREEN'), (29, 'SINGLE_TV'), (29, 'SPECTATOR_MODE');
INSERT INTO pub_themes (pub_id, theme_code) VALUES
    (29, 'SPECIAL_MENU'), (29, 'FRESH');
INSERT INTO pub_food_tags (pub_id, food_code) VALUES
    (29, 'CHICKEN'), (29, 'PIZZA'), (29, 'FRY'), (29, 'GRILLED'), (29, 'BUNSIK'), (29, 'DRY_SNACK'), (29, 'SOJU'), (29, 'BEER'), (29, 'HIGHBALL'), (29, 'COCKTAIL');
INSERT INTO pub_business_hours (pub_id, day_of_week, open_time, close_time, is_closed) VALUES
    (29, 1, '11:30', '23:59', FALSE),
    (29, 2, '11:30', '23:59', FALSE),
    (29, 3, '11:30', '23:59', FALSE),
    (29, 4, '11:30', '01:00', FALSE),
    (29, 5, '11:30', '01:00', FALSE),
    (29, 6, '11:30', '01:00', FALSE),
    (29, 7, '11:30', '23:59', FALSE);

-- pub_id 30: 크래프트한스 사당점 (SEOCHO / —)
INSERT INTO pubs (pub_id, name, address, region, sub_region, status, capacity_range, group_seat_max_people, favorite_count, description) VALUES
    (30, '크래프트한스 사당점', '서울 서초구 방배천로 12-4 1층', 'SEOCHO', NULL, 'OPEN', 'R_50_100', 80, 0,
     '사당역 12·13번 출구 방배 골목 수제맥주 스포츠펍, 롯데 우선 야구·해외축구·이스포츠 중계(스크린+TV 총 6대, 3경기 상시 소리 ON), 크리스피 치킨·마늘간장 닭강정·감바스·한스 떡볶이·피쉬 앤 칩스 시그니처, 70~80명 규모, 스포츠 예약 시 연장·조기 오픈');
INSERT INTO pub_supported_teams (pub_id, team_id) VALUES
    (30, 7);
INSERT INTO pub_facilities (pub_id, facility_code) VALUES
    (30, 'GROUP_SEAT'), (30, 'RESERVATION');
INSERT INTO pub_styles (pub_id, style_code) VALUES
    (30, 'BIG_SCREEN'), (30, 'MULTI_TV'), (30, 'STADIUM_MODE'), (30, 'LOUD_SPEAKER');
INSERT INTO pub_themes (pub_id, theme_code) VALUES
    (30, 'SPECIAL_MENU');
INSERT INTO pub_food_tags (pub_id, food_code) VALUES
    (30, 'CHICKEN'), (30, 'FRY'), (30, 'BUNSIK'), (30, 'SOJU'), (30, 'BEER');
INSERT INTO pub_business_hours (pub_id, day_of_week, open_time, close_time, is_closed) VALUES
    (30, 1, '16:00', '23:59', FALSE),
    (30, 2, '16:00', '23:59', FALSE),
    (30, 3, '16:00', '23:59', FALSE),
    (30, 4, '16:00', '23:59', FALSE),
    (30, 5, '16:00', '23:59', FALSE),
    (30, 6, '16:00', '23:59', FALSE),
    (30, 7, '16:00', '23:59', FALSE);

-- pub_id 31: 펍 마이마이 (SONGPA / JAMSIL)
INSERT INTO pubs (pub_id, name, address, region, sub_region, status, capacity_range, group_seat_max_people, favorite_count, description) VALUES
    (31, '펍 마이마이', '서울 송파구 백제고분로7길 24-7', 'SONGPA', 'JAMSIL', 'OPEN', 'OVER_100', 400, 0,
     '잠실새내 백제고분로 5층 대형 스포츠펍, 지하~4층·옥상 루프탑 각 층 대형 스크린으로 프로야구·프리미어리그·챔피언스리그·LCK·프로농구 동시 중계, 파티룸·다트·에어하키·포켓볼·인생네컷·레트로 오락기, MyMy 바베큐피자·명란알리오파스타 시그니처, 400명 규모 + 루프탑·테라스·정원·반려동물 동반, 매일 새벽 5시까지');
INSERT INTO pub_supported_teams (pub_id, team_id) VALUES
    (31, 1), (31, 2), (31, 3), (31, 4), (31, 5), (31, 6), (31, 7), (31, 8), (31, 9), (31, 10);
INSERT INTO pub_facilities (pub_id, facility_code) VALUES
    (31, 'GROUP_SEAT'), (31, 'RESERVATION'), (31, 'PRIVATE_BOOKING'), (31, 'WHEELCHAIR_ACCESS'), (31, 'PET_FRIENDLY'), (31, 'TERRACE'), (31, 'OUTDOOR_SEAT'), (31, 'COUNTER_SEAT'), (31, 'SOLO_SEAT'), (31, 'SPACIOUS_AREA');
INSERT INTO pub_styles (pub_id, style_code) VALUES
    (31, 'BIG_SCREEN'), (31, 'MULTI_TV'), (31, 'STADIUM_MODE'), (31, 'LOUD_SPEAKER');
INSERT INTO pub_themes (pub_id, theme_code) VALUES
    (31, 'SPECIAL_MENU');
INSERT INTO pub_food_tags (pub_id, food_code) VALUES
    (31, 'PIZZA'), (31, 'CHICKEN'), (31, 'FRY'), (31, 'SOJU'), (31, 'BEER'), (31, 'HIGHBALL'), (31, 'COCKTAIL');
INSERT INTO pub_business_hours (pub_id, day_of_week, open_time, close_time, is_closed) VALUES
    (31, 1, '17:00', '05:00', FALSE),
    (31, 2, '17:00', '05:00', FALSE),
    (31, 3, '17:00', '05:00', FALSE),
    (31, 4, '17:00', '05:00', FALSE),
    (31, 5, '17:00', '05:00', FALSE),
    (31, 6, '17:00', '05:00', FALSE),
    (31, 7, '17:00', '05:00', FALSE);

-- pub_id 32: 포차주식시장 (SEOCHO / —)
INSERT INTO pubs (pub_id, name, address, region, sub_region, status, capacity_range, group_seat_max_people, favorite_count, description) VALUES
    (32, '포차주식시장', '서울 서초구 서초대로77길 41 지하1·2층 1호', 'SEOCHO', NULL, 'OPEN', 'OVER_100', 200, 0,
     '강남역 서초대로 지하 1·2층 초대형 포차, 200명 규모 KBO 2시 및 전경기 중계(예약 시 응원팀별 자리 안내), 스크린 1 + 빔 3 + TV 4 총 8매 + 빵빵한 사운드, 통목살 김치찌개·불닭볶음탕·데리똥집·30cm 삼치구이·치즈무뼈닭발 시그니처, 룸 15명·복층 30~35석, 반려동물 동반·휠체어 접근, 매일 새벽 3~5시까지');
INSERT INTO pub_supported_teams (pub_id, team_id) VALUES
    (32, 1), (32, 2), (32, 3), (32, 4), (32, 5), (32, 6), (32, 7), (32, 8), (32, 9), (32, 10);
INSERT INTO pub_facilities (pub_id, facility_code) VALUES
    (32, 'GROUP_SEAT'), (32, 'RESERVATION'), (32, 'PRIVATE_BOOKING'), (32, 'WHEELCHAIR_ACCESS'), (32, 'PET_FRIENDLY'), (32, 'SPACIOUS_AREA');
INSERT INTO pub_styles (pub_id, style_code) VALUES
    (32, 'BIG_SCREEN'), (32, 'MULTI_TV'), (32, 'STADIUM_MODE'), (32, 'LOUD_SPEAKER');
INSERT INTO pub_themes (pub_id, theme_code) VALUES
    (32, 'SPECIAL_MENU');
INSERT INTO pub_food_tags (pub_id, food_code) VALUES
    (32, 'CHICKEN'), (32, 'GRILLED'), (32, 'FRY'), (32, 'STEW'), (32, 'BUNSIK'), (32, 'DRY_SNACK'), (32, 'SOJU'), (32, 'BEER'), (32, 'HIGHBALL');
INSERT INTO pub_business_hours (pub_id, day_of_week, open_time, close_time, is_closed) VALUES
    (32, 1, '15:00', '03:00', FALSE),
    (32, 2, '15:00', '03:00', FALSE),
    (32, 3, '15:00', '03:00', FALSE),
    (32, 4, '15:00', '03:00', FALSE),
    (32, 5, '15:00', '05:00', FALSE),
    (32, 6, '13:30', '05:00', FALSE),
    (32, 7, '13:30', '03:00', FALSE);

-- pub_id 33: 호리도 (GWANAK / —)
INSERT INTO pubs (pub_id, name, address, region, sub_region, status, capacity_range, group_seat_max_people, favorite_count, description) VALUES
    (33, '호리도', '서울 관악구 봉천로 518-4 1층 101호', 'GWANAK', NULL, 'OPEN', 'R_20_50', NULL, 0,
     '서울대입구 샤로수길 수제 맥주 펍, 국내산 정육 닭 직접 염지·주문 즉시 반죽 튀김 후라이드 5종·직접 만든 피자 3종·핸드앤몰트 수제 맥주 10종, 스크린 1(소리 O) + TV 1(소리 X)로 국가대표·챔피언스리그·프리미어리그 축구 관전, 4인 테이블 11개 총 44석(홀 7·야외 4), 인스타 DM 예약제·단체·대관 미운영');
INSERT INTO pub_supported_teams (pub_id, team_id) VALUES
    (33, 1), (33, 2), (33, 3), (33, 4), (33, 5), (33, 6), (33, 7), (33, 8), (33, 9), (33, 10);
INSERT INTO pub_facilities (pub_id, facility_code) VALUES
    (33, 'RESERVATION'), (33, 'TERRACE');
INSERT INTO pub_styles (pub_id, style_code) VALUES
    (33, 'BIG_SCREEN'), (33, 'MULTI_TV'), (33, 'SPECTATOR_MODE'), (33, 'LOUD_SPEAKER');
INSERT INTO pub_themes (pub_id, theme_code) VALUES
    (33, 'SPECIAL_MENU'), (33, 'FRESH');
INSERT INTO pub_food_tags (pub_id, food_code) VALUES
    (33, 'CHICKEN'), (33, 'PIZZA'), (33, 'GRILLED'), (33, 'FRY'), (33, 'BUNSIK'), (33, 'BEER');
INSERT INTO pub_business_hours (pub_id, day_of_week, open_time, close_time, is_closed) VALUES
    (33, 1, '17:00', '04:00', FALSE),
    (33, 2, '17:00', '04:00', FALSE),
    (33, 3, '17:00', '04:00', FALSE),
    (33, 4, '17:00', '04:00', FALSE),
    (33, 5, '17:00', '04:00', FALSE),
    (33, 6, '14:00', '04:00', FALSE),
    (33, 7, '14:00', '04:00', FALSE);

-- pub_id 34: 호멜맥주 3호점 (DONGJAK / —)
INSERT INTO pubs (pub_id, name, address, region, sub_region, status, capacity_range, group_seat_max_people, favorite_count, description) VALUES
    (34, '호멜맥주 3호점', '서울 동작구 노들로2길 7 C동 지하1층 B06·B07호', 'DONGJAK', NULL, 'OPEN', 'OVER_100', 150, 0,
     '노량진 최대 180석 한화이글스 응원 술집, 대형 스크린 + TV로 야구 시즌 전경기 실시간 중계(홈런 함성·직관 분위기), 수비드 바베큐 플래터·4색 치킨 플래터·수비드 장각 스테이크 시그니처, 기린 생맥주·산토리/짐빔 메가하이볼, 단체 10~150명 대응, 드림스퀘어빌딩 무료 주차 3시간 30분');
INSERT INTO pub_supported_teams (pub_id, team_id) VALUES
    (34, 9);
INSERT INTO pub_facilities (pub_id, facility_code) VALUES
    (34, 'GROUP_SEAT'), (34, 'RESERVATION'), (34, 'PARKING'), (34, 'WHEELCHAIR_ACCESS'), (34, 'SPACIOUS_AREA');
INSERT INTO pub_styles (pub_id, style_code) VALUES
    (34, 'BIG_SCREEN'), (34, 'MULTI_TV'), (34, 'STADIUM_MODE'), (34, 'LOUD_SPEAKER');
INSERT INTO pub_themes (pub_id, theme_code) VALUES
    (34, 'SPECIAL_MENU'), (34, 'FRESH');
INSERT INTO pub_food_tags (pub_id, food_code) VALUES
    (34, 'CHICKEN'), (34, 'GRILLED'), (34, 'FRY'), (34, 'STEW'), (34, 'DRY_SNACK'), (34, 'BEER'), (34, 'HIGHBALL'), (34, 'COCKTAIL');
INSERT INTO pub_business_hours (pub_id, day_of_week, open_time, close_time, is_closed) VALUES
    (34, 1, NULL, NULL, TRUE),
    (34, 2, '17:00', '22:00', FALSE),
    (34, 3, '17:00', '22:00', FALSE),
    (34, 4, '17:00', '22:00', FALSE),
    (34, 5, '17:00', '22:00', FALSE),
    (34, 6, '13:00', '22:00', FALSE),
    (34, 7, '13:00', '22:00', FALSE);

-- pub_id 35: 금성슈퍼 광화문본점 (JONGNO / —)
INSERT INTO pubs (pub_id, name, address, region, sub_region, status, capacity_range, group_seat_max_people, favorite_count, description) VALUES
    (35, '금성슈퍼 광화문본점', '서울 종로구 새문안로9길 9 1F', 'JONGNO', NULL, 'OPEN', 'R_20_50', 20, 0,
     '광화문역 7번 출구 새문안로9길 레트로 감성 떡튀순 전문점, 1층 + 지하 스포츠 인테리어 홀 구성, 지하 TV 여러 대·스타디움 분위기로 종합 스포츠 관전, 떡튀순셋트(떡볶이·순대·모듬튀김 10종)·레트로 후라이드·오리엔탈 스파이시·고추마요치킨·소떡소떡·문어핫바 시그니처, 단체 8~20명 지하 수용, 소주·맥주·시원한 생맥주·하이볼');
INSERT INTO pub_supported_teams (pub_id, team_id) VALUES
    (35, 1), (35, 2), (35, 3), (35, 4), (35, 5), (35, 6), (35, 7), (35, 8), (35, 9), (35, 10);
INSERT INTO pub_facilities (pub_id, facility_code) VALUES
    (35, 'GROUP_SEAT'), (35, 'RESERVATION');
INSERT INTO pub_styles (pub_id, style_code) VALUES
    (35, 'MULTI_TV'), (35, 'STADIUM_MODE');
INSERT INTO pub_themes (pub_id, theme_code) VALUES
    (35, 'SPECIAL_MENU');
INSERT INTO pub_food_tags (pub_id, food_code) VALUES
    (35, 'CHICKEN'), (35, 'GRILLED'), (35, 'FRY'), (35, 'BUNSIK'), (35, 'SOJU'), (35, 'BEER'), (35, 'HIGHBALL');
INSERT INTO pub_business_hours (pub_id, day_of_week, open_time, close_time, is_closed) VALUES
    (35, 1, '11:00', '23:30', FALSE),
    (35, 2, '11:00', '23:30', FALSE),
    (35, 3, '11:00', '23:30', FALSE),
    (35, 4, '11:00', '23:30', FALSE),
    (35, 5, '11:00', '23:30', FALSE),
    (35, 6, '13:00', '23:30', FALSE),
    (35, 7, '14:00', '21:00', FALSE);

-- pub_id 36: 워너비대구 강남역본점 (GANGNAM / —)
INSERT INTO pubs (pub_id, name, address, region, sub_region, status, capacity_range, group_seat_max_people, favorite_count, description) VALUES
    (36, '워너비대구 강남역본점', '서울 강남구 테헤란로1길 28-5 2층', 'GANGNAM', NULL, 'OPEN', 'R_50_100', 100, 0,
     '강남역 12번 출구 테헤란로1길 2층 대구 향토 포차, 대구뭉티기(당일도축 육사시미)·대구막창·동인동찜갈비·평화시장 닭똥집·반고개 무침회·양지오드레기·달구벌 라면·북성로우동 대구 향토 시그니처, 참소주·제로투·대구토닉 SET(참소주+토닉워터+레몬) 특화 주류, 삼성라이온즈 응원, 단체 2~100명 + 테라스 + 입식, 매일 새벽 2시까지');
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
     '잠실새내 백제고분로 1층 삼겹삼합 전문점, 국내산 1등급 암퇘지 + 국내산 묵은지 + 국내산 미나리 삼겹삼합·1등급 한우 차돌박이 + 서해안 관자 + 국내산 묵은지 차돌삼합 시그니처, 미나리삼겹 Set·김치삼겹 Set·차돌삼합 Set·한맥 삼겹 삼합 Set·차돌된장술밥·미나리새우전, 스크린 1대 스포츠 관전, 단체 10~60명·테라스·반려동물 동반·매장 앞 무료 주차·콜키지 무료, 소주·맥주·하이볼·전통주(백세주·청하·산사춘)');
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
     '잠실새내 백제고분로15길 1층 한식주점, 시끌벅적함보다 조용한 대화·데이트 지향, 항정살 수비드 수육(저온 수비드 후 그릴링)·채끝 육회·앞치마살 타다끼·단호박 크림 뇨끼·채끝 육회 물회·버섯 만두 전골·묵말랭이 무침·명란계란말이 시그니처, 다양한 전통주 친근하게 준비 + 소주·맥주·하이볼, 야구 팬 야구 중계, 20명 최대·티비 1대·테라스·1인석·반려동물 동반·대관 가능');
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

-- pub_id 39: 908 (DONGJAK / —)
INSERT INTO pubs (pub_id, name, address, region, sub_region, status, capacity_range, group_seat_max_people, favorite_count, description) VALUES
    (39, '908', '서울 동작구 동작대로27가길 26 2층', 'DONGJAK', NULL, 'OPEN', 'R_20_50', 30, 0,
     '이수역 동작대로27가길 2층 30명 규모 심야 술집, 대형 스크린 야구 중계, 908 깐풍·908 또띠아피자·제6볶음·트러플짜파게티·토마토해장파스튜·달달토스트·오다리와 마요소스·버터갈릭가문어·모듬 햄 김치찌개·바지락술찜·스키야끼나베·바지락탕 시그니처, 저렴한 소주·맥주·하이볼, 포근한 분위기 가족·친구·연인 심야 모임, 매일 새벽 3~4시까지, 둘째·넷째 월요일 휴무');
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
     '수유역 도봉로83길 2층 30명 규모 작은 실내포차, 한식·중식·전 다양한 안주(닭볶음탕·모닝글로리·땡초육전·유린기·깐풍기·크림새우·육회·짬뽕탕 3종·해물누릉지탕·아구없는 콩나물찜·잡탕볶음 시그니처), 두산베어스 야구 + 해외축구 중계, 티비 1대, 바테이블·1인석·입식, 소주·병맥주·생맥주(테라·코젤다크·빅웨이브)·하이볼(블루레몬·얼그레이), 새벽 3시까지, 월 정기휴무');
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
    (40, 1, NULL, NULL, TRUE),
    (40, 2, '17:00', '03:00', FALSE),
    (40, 3, '17:00', '03:00', FALSE),
    (40, 4, '17:00', '03:00', FALSE),
    (40, 5, '17:00', '03:00', FALSE),
    (40, 6, '17:00', '03:00', FALSE),
    (40, 7, '17:00', '03:00', FALSE);
