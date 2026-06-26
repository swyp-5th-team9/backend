package com.swift.sportspub.user.dto;

import com.swift.sportspub.user.entity.WithdrawalReasonType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(
        description = "회원 탈퇴 요청",
        example = "{\"reasonCode\":\"NO_USE\"}"
)
public record WithdrawRequest(

        @Schema(
                description = "탈퇴 사유 코드",
                example = "NO_USE",
                allowableValues = {"NO_USE", "INSUFFICIENT_DATA", "INACCURATE_DATA", "APP_ISSUE", "OTHER"}
        )
        @NotNull(message = "reasonCode는 필수입니다.")
        WithdrawalReasonType reasonCode,

        @Schema(
                description = "기타 탈퇴 사유. reasonCode가 OTHER일 때만 필수입니다.",
                example = "원하는 펍 정보가 없어요"
        )
        @Size(max = 500, message = "detail은 최대 500자까지 입력할 수 있습니다.")
        String detail
) {

    @AssertTrue(message = "reasonCode가 OTHER일 때 detail은 필수입니다.")
    private boolean isDetailRequiredForOther() {
        if (reasonCode == WithdrawalReasonType.OTHER) {
            return detail != null && !detail.isBlank();
        }
        return true;
    }

    @AssertTrue(message = "reasonCode가 OTHER가 아닐 때 detail은 입력할 수 없습니다.")
    private boolean isDetailAllowedOnlyForOther() {
        if (reasonCode == WithdrawalReasonType.OTHER) {
            return true;
        }
        return detail == null || detail.isBlank();
    }
}
