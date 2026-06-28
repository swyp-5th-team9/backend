package com.swift.sportspub.pub.repository;

import com.swift.sportspub.pub.entity.PubBusinessHours;
import com.swift.sportspub.pub.entity.PubBusinessHoursId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PubBusinessHoursRepository extends JpaRepository<PubBusinessHours, PubBusinessHoursId> {
}
