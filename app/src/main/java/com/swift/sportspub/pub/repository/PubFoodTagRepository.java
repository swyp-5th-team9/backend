package com.swift.sportspub.pub.repository;

import com.swift.sportspub.pub.entity.PubFoodTag;
import com.swift.sportspub.pub.entity.PubFoodTagId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface PubFoodTagRepository extends JpaRepository<PubFoodTag, PubFoodTagId> {

    List<PubFoodTag> findAllByPubIdIn(Collection<Long> pubIds);
}
