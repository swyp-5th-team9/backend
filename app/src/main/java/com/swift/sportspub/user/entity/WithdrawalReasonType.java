package com.swift.sportspub.user.entity;

public enum WithdrawalReasonType {

    /** 자주 사용하지 않아요 */
    NO_USE,

    /** 원하는 정보가 부족해요 */
    INSUFFICIENT_DATA,

    /** 정보가 정확하지 않아요 */
    INACCURATE_DATA,

    /** 앱 오류 및 사용이 불편해요 */
    APP_ISSUE,

    /** 기타 — detail 필드에 사용자가 직접 입력한 사유를 저장한다 */
    OTHER
}
