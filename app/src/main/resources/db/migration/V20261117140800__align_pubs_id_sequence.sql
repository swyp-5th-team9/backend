-- V10이 pub_id 1..40을 직접 넣기 전에 시퀀스를 1로 되감았다.
-- 번호를 직접 넣으면 시퀀스는 올라가지 않아, 다음 자동 번호가 기존 행과 겹친다.

SELECT setval(
    pg_get_serial_sequence('pubs', 'pub_id'),
    (SELECT COALESCE(MAX(pub_id), 1) FROM pubs),
    (SELECT MAX(pub_id) IS NOT NULL FROM pubs)
);
