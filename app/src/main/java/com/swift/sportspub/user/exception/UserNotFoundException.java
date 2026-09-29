package com.swift.sportspub.user.exception;

import com.swift.sportspub.common.exception.BusinessException;
import com.swift.sportspub.common.exception.ErrorCode;

public class UserNotFoundException extends BusinessException {

    public UserNotFoundException() {
        super(ErrorCode.NOT_FOUND, "사용자를 찾을 수 없습니다.");
    }
}
