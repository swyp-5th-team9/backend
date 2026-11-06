-- 같은 사용자·경기·이동 화면 알림은 스케줄러가 다시 돌아도 한 번만 저장한다.

CREATE UNIQUE INDEX uk_notifications_user_match_deeplink
    ON notifications (user_id, match_id, deep_link_type);
