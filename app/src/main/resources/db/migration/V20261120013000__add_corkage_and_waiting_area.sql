-- 시설에 콜키지와 대기공간을 추가한다.
-- 이미 적용된 V5 제약은 고치지 않고, 허용 목록만 새로 건다.

ALTER TABLE pub_facilities
    DROP CONSTRAINT ck_pub_facilities_code;

ALTER TABLE pub_facilities
    ADD CONSTRAINT ck_pub_facilities_code
        CHECK (facility_code IN (
            'GROUP_SEAT',
            'COUNTER_SEAT',
            'TERRACE',
            'SOLO_SEAT',
            'SPACIOUS_AREA',
            'ROOFTOP',
            'PARKING',
            'VALET_PARKING',
            'PET_FRIENDLY',
            'PRIVATE_BOOKING',
            'WHEELCHAIR_ACCESS',
            'OUTDOOR_SEAT',
            'RESERVATION',
            'CORKAGE',
            'WAITING_AREA'
        ));
