package com.swift.sportspub.pub.repository;

import com.swift.sportspub.pub.entity.PubFacility;
import com.swift.sportspub.pub.entity.PubFacilityId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface PubFacilityRepository extends JpaRepository<PubFacility, PubFacilityId> {

    List<PubFacility> findAllByPubIdIn(Collection<Long> pubIds);
}
