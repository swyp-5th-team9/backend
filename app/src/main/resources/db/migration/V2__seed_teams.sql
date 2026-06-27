-- ============================================================================
-- V2 seed teams — KBO 10팀
-- short_name 은 KBO 공식 일정표 표기와 일치 (크롤러 → team_id 매핑 키)
-- home_stadium 정식 명칭 (크롤러 약식 → 정식 변환은 StadiumNameResolver 담당)
-- 순서: 주양님 #30 PR Swagger example(teamId=1→LG 등) 호환을 위해 LG 부터 INSERT
--
-- ON CONFLICT: V1 baseline 시드(약식 명칭)와 충돌 시 home_stadium 만
--              V2 의 정식 명칭으로 UPDATE. 핫픽스 #41.
-- ============================================================================

INSERT INTO teams (sport_type, name, short_name, home_stadium) VALUES
    ('KBO', 'LG 트윈스',     'LG',  '서울 잠실야구장'),
    ('KBO', '두산 베어스',    '두산', '서울 잠실야구장'),
    ('KBO', 'KT 위즈',       'KT',  '수원 KT위즈파크'),
    ('KBO', 'SSG 랜더스',    'SSG', '인천 SSG랜더스필드'),
    ('KBO', 'NC 다이노스',    'NC',  '창원 NC파크'),
    ('KBO', 'KIA 타이거즈',   'KIA', '광주-기아 챔피언스 필드'),
    ('KBO', '롯데 자이언츠',   '롯데', '부산 사직야구장'),
    ('KBO', '삼성 라이온즈',   '삼성', '대구 삼성라이온즈파크'),
    ('KBO', '한화 이글스',    '한화', '대전 한화생명 볼파크'),
    ('KBO', '키움 히어로즈',   '키움', '고척 스카이돔')
ON CONFLICT (sport_type, name) DO UPDATE
    SET home_stadium = EXCLUDED.home_stadium;
