package com.swift.sportspub.report.repository;

import com.swift.sportspub.report.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, Long> {
}
