package com.swift.sportspub.pub.repository;

import com.swift.sportspub.pub.entity.PubStyle;
import com.swift.sportspub.pub.entity.PubStyleId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface PubStyleRepository extends JpaRepository<PubStyle, PubStyleId> {

    List<PubStyle> findAllByPubIdIn(Collection<Long> pubIds);
}
