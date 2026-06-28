package com.swift.sportspub.pub.repository;

import com.swift.sportspub.pub.entity.PubTheme;
import com.swift.sportspub.pub.entity.PubThemeId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface PubThemeRepository extends JpaRepository<PubTheme, PubThemeId> {

    List<PubTheme> findAllByPubIdIn(Collection<Long> pubIds);
}
