package com.swift.sportspub.user.repository;

import com.swift.sportspub.user.entity.FailedProfileImageDeletion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FailedProfileImageDeletionRepository extends JpaRepository<FailedProfileImageDeletion, String> {
}
