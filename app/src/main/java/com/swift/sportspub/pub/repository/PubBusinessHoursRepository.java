package com.swift.sportspub.pub.repository;

import com.swift.sportspub.pub.entity.PubBusinessHours;
import com.swift.sportspub.pub.entity.PubBusinessHoursId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface PubBusinessHoursRepository extends JpaRepository<PubBusinessHours, PubBusinessHoursId> {

    List<PubBusinessHours> findAllByPubIdIn(Collection<Long> pubIds);

    List<PubBusinessHours> findAllByPubIdOrderByDayOfWeekAsc(Long pubId);
}
