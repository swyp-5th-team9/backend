package com.swift.sportspub.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "withdrawal_reasons")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@EqualsAndHashCode(of = "withdrawalId", callSuper = false)
public class WithdrawalReason {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "withdrawal_id")
    private Long withdrawalId;

    /*
     * 탈퇴 사유는 회원 탈퇴 시점의 선택을 기록하는 append-only 이력 테이블이다.
     *
     * User는 계정 상태(프로필, Soft Delete)를 담당하고, WithdrawalReason은
     * "왜 탈퇴했는가"를 별도로 보관한다. 계정 복구 후 재탈퇴가 가능하므로
     * 한 User에 여러 WithdrawalReason 이력이 쌓일 수 있다.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "reason_code", nullable = false, length = 30)
    private WithdrawalReasonType reasonCode;

    /*
     * reasonCode가 OTHER일 때 사용자가 직접 입력한 탈퇴 사유.
     * 사전 정의된 사유를 선택한 경우에는 null로 저장한다.
     */
    @Column(name = "detail", columnDefinition = "TEXT")
    private String detail;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    private WithdrawalReason(User user, WithdrawalReasonType reasonCode, String detail) {
        this.user = user;
        this.reasonCode = reasonCode;
        this.detail = detail;
    }

    /*
     * 회원 탈퇴 API에서 사유 이력을 저장할 때 사용한다.
     * Builder를 외부에 노출하지 않고, 탈퇴 사유 생성 규칙을 이 메서드에 모은다.
     */
    public static WithdrawalReason create(User user, WithdrawalReasonType reasonCode, String detail) {
        return WithdrawalReason.builder()
                .user(user)
                .reasonCode(reasonCode)
                .detail(reasonCode == WithdrawalReasonType.OTHER ? detail : null)
                .build();
    }
}
