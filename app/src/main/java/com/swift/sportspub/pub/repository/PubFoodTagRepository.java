package com.swift.sportspub.pub.repository;

import com.swift.sportspub.pub.entity.PubFoodTag;
import com.swift.sportspub.pub.entity.PubFoodTagId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PubFoodTagRepository extends JpaRepository<PubFoodTag, PubFoodTagId> {
}
