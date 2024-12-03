package com.project.repository;

import java.util.List;

import com.project.module.Report;

public interface ReportRepository {
    List<Report> getPendingReports();
    Report generateDetailedReport(Long reportId);
    List<Report> getPendingReportsOfCurrentWeek();
}
