package com.project.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.project.module.Report;

@Repository
public interface ReportJpaRepository extends JpaRepository<Report, Long> {
        @Query("SELECT r FROM Report r WHERE r.status = :status")
        List<Report> findByStatus(@Param("status") String status);

        @Query("SELECT r FROM Report r WHERE r.type = 'User Activity' ORDER BY r.generatedDate DESC LIMIT 1")
        Optional<Report> findLatestUserActivityReport();

        @Query("SELECT r FROM Report r WHERE r.type = 'User Booking Requests' ORDER BY r.generatedDate DESC LIMIT 1")
        Optional<Report> findLatestUserBookingRequestsReport();

        @Query("SELECT r FROM Report r WHERE r.type = 'Provider Activity' ORDER BY r.generatedDate DESC LIMIT 1")
        Optional<Report> findLatestProviderActivityReport();

        @Query("SELECT r FROM Report r WHERE r.type = 'Provider Accepted Requests' ORDER BY r.generatedDate DESC LIMIT 1")
        Optional<Report> findLatestProviderAcceptedRequestsReport();

        @Query("SELECT r FROM Report r WHERE r.type = 'Provider Rejected Requests' ORDER BY r.generatedDate DESC LIMIT 1")
        Optional<Report> findLatestProviderRejectedRequestsReport();

        @Query("SELECT r FROM Report r WHERE r.type = 'User Cancelled Requests' ORDER BY r.generatedDate DESC LIMIT 1")
        Optional<Report> findLatestUserCancelledRequestsReport();

        @Query("SELECT r FROM Report r WHERE r.type = 'Revenue Report' ORDER BY r.generatedDate DESC LIMIT 1")
        Optional<Report> findLatestRevenueReport();

        @Query("SELECT r FROM Report r WHERE r.status = 'Pending' AND r.generatedDate >= :startOfWeek")
        List<Report> findPendingReportsOfCurrentWeek(@Param("startOfWeek") LocalDateTime startOfWeek);

        @Query("SELECT r FROM Report r WHERE (r.type = 'User Activity' OR r.type = 'Provider Activity') " +
                        "AND MONTH(r.generatedDate) = MONTH(CURRENT_DATE) AND YEAR(r.generatedDate) = YEAR(CURRENT_DATE)")
        List<Report> findMonthlyUserAndProviderActivity();

        @Query("SELECT r FROM Report r WHERE r.type IN ('User Booking Requests', 'Provider Accepted Requests', 'User Cancelled Requests', 'Provider Rejected Requests') "
                        +
                        "AND MONTH(r.generatedDate) = MONTH(CURRENT_DATE) AND YEAR(r.generatedDate) = YEAR(CURRENT_DATE)")
        List<Report> findMonthlyRequestCounts();

        @Query("SELECT r FROM Report r WHERE r.type = 'Revenue Report' " +
                        "AND MONTH(r.generatedDate) = MONTH(CURRENT_DATE) AND YEAR(r.generatedDate) = YEAR(CURRENT_DATE)")
        List<Report> findMonthlyRevenueReports();
}
