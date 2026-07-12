-- favorites 테이블에만 쌓이고 pubs.favorite_count 가 갱신되지 않던 기간 데이터 1회 재계산 (#103)

UPDATE pubs p
SET favorite_count = (
    SELECT COUNT(*)
    FROM favorites f
    WHERE f.pub_id = p.pub_id
);
