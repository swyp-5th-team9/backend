package com.swift.sportspub.pub.repository;

import com.swift.sportspub.pub.entity.PubStyle;
import com.swift.sportspub.pub.entity.PubStyleId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PubStyleRepository extends JpaRepository<PubStyle, PubStyleId> {
}
