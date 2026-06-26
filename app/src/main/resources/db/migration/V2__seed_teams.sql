-- ============================================================================
-- V2 seed teams — KBO 10팀
-- short_name 은 KBO 공식 일정표 표기와 일치 (크롤러 → team_id 매핑 키)
-- home_stadium 정식 명칭 (크롤러 약식 → 정식 변환은 StadiumNameResolver 담당)
-- ============================================================================

INSERT INTO teams (sport_type, name, short_name, home_stadium) VALUES
    ('KBO', 'KT Wiz',       'KT',  '수원 KT위즈파크'),
    ('KBO', 'NC Dinos',     'NC',  '창원 NC파크'),
    ('KBO', 'LG Twins',     'LG',  '서울 잠실야구장'),
    ('KBO', 'SSG Landers',  'SSG', '인천 SSG랜더스필드'),
    ('KBO', 'KIA Tigers',   'KIA', '광주-기아 챔피언스 필드'),
    ('KBO', '두산 베어스',     '두산', '서울 잠실야구장'),
    ('KBO', '한화 이글스',     '한화', '대전 한화생명 볼파크'),
    ('KBO', '키움 히어로즈',    '키움', '고척 스카이돔'),
    ('KBO', '삼성 라이온즈',    '삼성', '대구 삼성라이온즈파크'),
    ('KBO', '롯데 자이언츠',    '롯데', '부산 사직야구장');
