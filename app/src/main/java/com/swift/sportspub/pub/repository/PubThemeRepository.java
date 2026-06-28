package com.swift.sportspub.pub.repository;

import com.swift.sportspub.pub.entity.PubTheme;
import com.swift.sportspub.pub.entity.PubThemeId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PubThemeRepository extends JpaRepository<PubTheme, PubThemeId> {
}
