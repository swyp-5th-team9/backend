-- S3 프로필 이미지 삭제 실패 URL 재시도 큐 (Hard Delete 배치에서 함께 처리)

CREATE TABLE failed_profile_image_deletions (
    profile_image_url VARCHAR(500) PRIMARY KEY
);
