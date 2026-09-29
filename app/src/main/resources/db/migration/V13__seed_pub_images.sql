-- =============================================================================
-- V13: pub_images 시드
--   - 치어하우스 (pub_id=28): 7장
--   - 호리도    (pub_id=33): 1장
--
-- S3 key 규칙: pubs/pubs:{pub_id}:{n}.jpeg (콜론 구분, URL 인코딩 %3A)
-- display_order = 0 이 대표 이미지
--   (PubImageRepository.findFirstByPubIdOrderByDisplayOrderAsc 참조)
-- =============================================================================

INSERT INTO pub_images (pub_id, image_url, display_order) VALUES
    (28, 'https://moball-s3-team9.s3.ap-northeast-2.amazonaws.com/pubs/pubs%3A28%3A1.jpeg', 0),
    (28, 'https://moball-s3-team9.s3.ap-northeast-2.amazonaws.com/pubs/pubs%3A28%3A2.jpeg', 1),
    (28, 'https://moball-s3-team9.s3.ap-northeast-2.amazonaws.com/pubs/pubs%3A28%3A3.jpeg', 2),
    (28, 'https://moball-s3-team9.s3.ap-northeast-2.amazonaws.com/pubs/pubs%3A28%3A4.jpeg', 3),
    (28, 'https://moball-s3-team9.s3.ap-northeast-2.amazonaws.com/pubs/pubs%3A28%3A5.jpeg', 4),
    (28, 'https://moball-s3-team9.s3.ap-northeast-2.amazonaws.com/pubs/pubs%3A28%3A6.jpeg', 5),
    (28, 'https://moball-s3-team9.s3.ap-northeast-2.amazonaws.com/pubs/pubs%3A28%3A7.jpeg', 6),
    (33, 'https://moball-s3-team9.s3.ap-northeast-2.amazonaws.com/pubs/pubs%3A33%3A1.jpeg', 0);
