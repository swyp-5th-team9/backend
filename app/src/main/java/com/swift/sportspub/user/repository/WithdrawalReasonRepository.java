package com.swift.sportspub.user.repository;

import com.swift.sportspub.user.entity.WithdrawalReason;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WithdrawalReasonRepository extends JpaRepository<WithdrawalReason, Long> {
}
