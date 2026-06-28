package com.swift.sportspub.pub.repository;

import com.swift.sportspub.pub.entity.PubImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PubImageRepository extends JpaRepository<PubImage, Long> {

    Optional<PubImage> findFirstByPubIdOrderByDisplayOrderAsc(Long pubId);
}
