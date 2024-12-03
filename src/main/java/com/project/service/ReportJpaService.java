package com.project.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.module.Report;
import com.project.repository.ReportJpaRepository;
import com.project.repository.ReportRepository;

import jakarta.transaction.Transactional;

@Service
public class ReportJpaService implements ReportRepository {

    @Autowired
    private ReportJpaRepository reportRepository;

    @Override
    public List<Report> getPendingReports() {
        return reportRepository.findByStatus("Pending");
    }

    @Override
    public Report generateDetailedReport(Long reportId) {
        Optional<Report> report = reportRepository.findById(reportId);
        if (report.isPresent()) {
            report.get().setStatus("Completed");
            return reportRepository.save(report.get());
        }
        return null;
    }

    @Transactional
    public void updateUserActivityReport() {
        // Fetch the most recent "User Activity" report
        Optional<Report> latestReportOpt = reportRepository.findLatestUserActivityReport();
        LocalDateTime todayNow = LocalDateTime.now();
        LocalDate now = LocalDate.now();

        if (latestReportOpt.isPresent()) {
            Report latestReport = latestReportOpt.get();
            LocalDate reportDate = latestReport.getGeneratedDate().toLocalDate(); // Convert to LocalDate
            boolean isNewWeek = isStartOfWeek(now) || !isSameWeek(reportDate, now);

            if (isNewWeek) {
                Report newReport = new Report();
                newReport.setType("User Activity");
                newReport.setStatus("Pending");
                newReport.setGeneratedDate(todayNow);
                newReport.setDetails("1 new user registered this week.");
                reportRepository.save(newReport);

            } else {
                // If less than 7 days, update the existing report count
                String currentDetails = latestReport.getDetails();
                int currentCount = Integer.parseInt(currentDetails.split(" ")[0]);
                currentCount++; // Increment user count

                latestReport.setDetails(currentCount + " new users registered this week.");
                latestReport.setStatus("Pending");
                reportRepository.save(latestReport);
            }
        } else {
            // If no "User Activity" report exists, create a new one
            Report newReport = new Report();
            newReport.setType("User Activity");
            newReport.setStatus("Pending");
            newReport.setGeneratedDate(todayNow);
            newReport.setDetails("1 new user registered this week.");
            reportRepository.save(newReport);
        }
    }

    @Transactional
    public void updateUserBookingRequestsReport() {
        // Fetch the most recent "User BookingRequests" report
        Optional<Report> latestReportOpt = reportRepository.findLatestUserBookingRequestsReport();
        LocalDateTime todayNow = LocalDateTime.now();
        LocalDate now = LocalDate.now();

        if (latestReportOpt.isPresent()) {
            Report latestReport = latestReportOpt.get();
            LocalDate reportDate = latestReport.getGeneratedDate().toLocalDate(); // Convert to LocalDate
            boolean isNewWeek = isStartOfWeek(now) || !isSameWeek(reportDate, now);

            if (isNewWeek) {
                // If 7 days have passed, create a new report entry
                Report newReport = new Report();
                newReport.setType("User Booking Requests");
                newReport.setStatus("Pending");
                newReport.setGeneratedDate(todayNow);
                newReport.setDetails("1 request for booking this week.");
                reportRepository.save(newReport);

            } else {
                // If less than 7 days, update the existing report count
                String currentDetails = latestReport.getDetails();
                int currentCount = Integer.parseInt(currentDetails.split(" ")[0]);
                currentCount++; // Increment user count

                latestReport.setDetails(currentCount + " requests for booking this week.");
                latestReport.setStatus("Pending");
                reportRepository.save(latestReport);
            }
        } else {
            // If no "User Booking Requests" report exists, create a new one
            Report newReport = new Report();
            newReport.setType("User Booking Requests");
            newReport.setStatus("Pending");
            newReport.setGeneratedDate(todayNow);
            newReport.setDetails("1 request for booking this week.");
            reportRepository.save(newReport);
        }
    }

    @Transactional
    public void updateProviderActivityReport() {
        // Fetch the most recent "Provider Activity" report
        Optional<Report> latestReportOpt = reportRepository.findLatestProviderActivityReport();
        LocalDateTime todayNow = LocalDateTime.now();
        LocalDate now = LocalDate.now();

        if (latestReportOpt.isPresent()) {
            Report latestReport = latestReportOpt.get();
            LocalDate reportDate = latestReport.getGeneratedDate().toLocalDate(); // Convert to LocalDate
            boolean isNewWeek = isStartOfWeek(now) || !isSameWeek(reportDate, now);

            if (isNewWeek) {
                // If 7 days have passed, create a new report entry
                Report newReport = new Report();
                newReport.setType("Provider Activity");
                newReport.setStatus("Pending");
                newReport.setGeneratedDate(todayNow);
                newReport.setDetails("1 new provider registered this week.");
                reportRepository.save(newReport);

            } else {
                // If less than 7 days, update the existing report count
                String currentDetails = latestReport.getDetails();
                int currentCount = Integer.parseInt(currentDetails.split(" ")[0]);
                currentCount++; // Increment provider count

                latestReport.setDetails(currentCount + " new providers registered this week.");
                latestReport.setStatus("Pending");
                reportRepository.save(latestReport);
            }
        } else {
            // If no "Provider Activity" report exists, create a new one
            Report newReport = new Report();
            newReport.setType("Provider Activity");
            newReport.setStatus("Pending");
            newReport.setGeneratedDate(todayNow);
            newReport.setDetails("1 new provider registered this week.");
            reportRepository.save(newReport);
        }
    }

    @Transactional
    public void updateProviderAcceptedRequestsReport() {
        // Fetch the most recent "Provider Accepted Requests" report
        Optional<Report> latestReportOpt = reportRepository.findLatestProviderAcceptedRequestsReport();
        LocalDateTime todayNow = LocalDateTime.now();
        LocalDate now = LocalDate.now();

        if (latestReportOpt.isPresent()) {
            Report latestReport = latestReportOpt.get();
            LocalDate reportDate = latestReport.getGeneratedDate().toLocalDate(); // Convert to LocalDate
            boolean isNewWeek = isStartOfWeek(now) || !isSameWeek(reportDate, now);

            if (isNewWeek) {
                // If 7 days have passed, create a new report entry
                Report newReport = new Report();
                newReport.setType("Provider Accepted Requests");
                newReport.setStatus("Pending");
                newReport.setGeneratedDate(todayNow);
                newReport.setDetails("1 request approved this week.");
                reportRepository.save(newReport);

            } else {
                // If less than 7 days, update the existing report count
                String currentDetails = latestReport.getDetails();
                int currentCount = Integer.parseInt(currentDetails.split(" ")[0]);
                currentCount++; // Increment provider count

                latestReport.setDetails(currentCount + " requests approved this week.");
                latestReport.setStatus("Pending");
                reportRepository.save(latestReport);
            }
        } else {
            // If no "Provider Accepted Requests" report exists, create a new one
            Report newReport = new Report();
            newReport.setType("Provider Accepted Requests");
            newReport.setStatus("Pending");
            newReport.setGeneratedDate(todayNow);
            newReport.setDetails("1 request approved this week.");
            reportRepository.save(newReport);
        }
    }

    @Transactional
    public void updateProviderRejectedRequestsReport() {
        // Fetch the most recent "Provider Rejected Requests" report
        Optional<Report> latestReportOpt = reportRepository.findLatestProviderRejectedRequestsReport();
        LocalDateTime todayNow = LocalDateTime.now();
        LocalDate now = LocalDate.now();

        if (latestReportOpt.isPresent()) {
            Report latestReport = latestReportOpt.get();
            LocalDate reportDate = latestReport.getGeneratedDate().toLocalDate(); // Convert to LocalDate
            boolean isNewWeek = isStartOfWeek(now) || !isSameWeek(reportDate, now);

            if (isNewWeek) {
                // If 7 days have passed, create a new report entry
                Report newReport = new Report();
                newReport.setType("Provider Rejected Requests");
                newReport.setStatus("Pending");
                newReport.setGeneratedDate(todayNow);
                newReport.setDetails("1 request rejected this week.");
                reportRepository.save(newReport);

            } else {
                // If less than 7 days, update the existing report count
                String currentDetails = latestReport.getDetails();
                int currentCount = Integer.parseInt(currentDetails.split(" ")[0]);
                currentCount++; // Increment provider count

                latestReport.setDetails(currentCount + " requests rejected this week.");
                latestReport.setStatus("Pending");
                reportRepository.save(latestReport);
            }
        } else {
            // If no "Provider Rejected Requests" report exists, create a new one
            Report newReport = new Report();
            newReport.setType("Provider Rejected Requests");
            newReport.setStatus("Pending");
            newReport.setGeneratedDate(todayNow);
            newReport.setDetails("1 request rejected this week.");
            reportRepository.save(newReport);
        }
    }

    @Transactional
    public void updateUserCancelledRequestsReport() {
        // Fetch the most recent "User Cancelled Requests" report
        Optional<Report> latestReportOpt = reportRepository.findLatestUserCancelledRequestsReport();
        LocalDateTime todayNow = LocalDateTime.now();
        LocalDate now = LocalDate.now();

        if (latestReportOpt.isPresent()) {
            Report latestReport = latestReportOpt.get();
            LocalDate reportDate = latestReport.getGeneratedDate().toLocalDate(); // Convert to LocalDate
            boolean isNewWeek = isStartOfWeek(now) || !isSameWeek(reportDate, now);

            if (isNewWeek) {
                // If 7 days have passed, create a new report entry
                Report newReport = new Report();
                newReport.setType("User Cancelled Requests");
                newReport.setStatus("Pending");
                newReport.setGeneratedDate(todayNow);
                newReport.setDetails("1 request cancelled this week.");
                reportRepository.save(newReport);

            } else {
                // If less than 7 days, update the existing report count
                String currentDetails = latestReport.getDetails();
                int currentCount = Integer.parseInt(currentDetails.split(" ")[0]);
                currentCount++; // Increment user count

                latestReport.setDetails(currentCount + " requests cancelled this week.");
                latestReport.setStatus("Pending");
                reportRepository.save(latestReport);
            }
        } else {
            // If no "User Cancelled Requests" report exists, create a new one
            Report newReport = new Report();
            newReport.setType("User Cancelled Requests");
            newReport.setStatus("Pending");
            newReport.setGeneratedDate(todayNow);
            newReport.setDetails("1 request cancelled this week.");
            reportRepository.save(newReport);
        }
    }

    @Transactional
    public void updateRevenueReport(double amount) {
        // Fetch the most recent "Revenue Report" report
        Optional<Report> latestReportOpt = reportRepository.findLatestRevenueReport();
        LocalDateTime todayNow = LocalDateTime.now();
        LocalDate now = LocalDate.now();

        if (latestReportOpt.isPresent()) {
            Report latestReport = latestReportOpt.get();
            LocalDate reportDate = latestReport.getGeneratedDate().toLocalDate(); // Convert to LocalDate
            boolean isNewWeek = isStartOfWeek(now) || !isSameWeek(reportDate, now);

            if (isNewWeek) {
                // If 7 days have passed, create a new report entry
                Report newReport = new Report();
                newReport.setType("Revenue Report");
                newReport.setStatus("Pending");
                newReport.setGeneratedDate(todayNow);
                newReport.setDetails("Total revenue for this week: " + amount);
                reportRepository.save(newReport);

            } else {
                // If less than 7 days, update the existing report revenue
                String[] currentDetails = latestReport.getDetails().split(" ");
                double currentRevenue = Double.parseDouble(currentDetails[currentDetails.length - 1]);
                currentRevenue += amount; // Increment revenue

                latestReport.setDetails("Total revenue for this week: " + currentRevenue);
                latestReport.setStatus("Pending");
                reportRepository.save(latestReport);
            }
        } else {
            // If no "Revenue Report" report exists, create a new one
            Report newReport = new Report();
            newReport.setType("Revenue Report");
            newReport.setStatus("Pending");
            newReport.setGeneratedDate(todayNow);
            newReport.setDetails("Total revenue for this week: " + amount);
            reportRepository.save(newReport);
        }
    }

    // Helper method to check if two dates fall in the same week
    private boolean isSameWeek(LocalDate date1, LocalDate date2) {
        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        return date1.get(weekFields.weekOfWeekBasedYear()) == date2.get(weekFields.weekOfWeekBasedYear());
    }

    // Helper method to check if the given date is the start of the week
    private boolean isStartOfWeek(LocalDate date) {
        DayOfWeek firstDayOfWeek = WeekFields.of(Locale.getDefault()).getFirstDayOfWeek();
        return date.getDayOfWeek() == firstDayOfWeek;
    }

    @Override
    public List<Report> getPendingReportsOfCurrentWeek() {
        LocalDateTime startOfWeek = LocalDate.now()
                .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                .atStartOfDay();
        return reportRepository.findPendingReportsOfCurrentWeek(startOfWeek);
    }

    public Map<String, Map<String, Integer>> getWeeklyRegistrationCounts() {
        List<Report> reports = reportRepository.findMonthlyUserAndProviderActivity();

        Map<String, Map<String, Integer>> weeklyCounts = new HashMap<>();

        for (Report report : reports) {
            String type = report.getType();
            int week = report.getGeneratedDate().toLocalDate().get(WeekFields.of(Locale.getDefault()).weekOfMonth());
            String weekLabel = "Week " + week;

            int count = Integer.parseInt(report.getDetails().split(" ")[0].trim());

            weeklyCounts.putIfAbsent(weekLabel, new HashMap<>());
            weeklyCounts.get(weekLabel).merge(type, count, Integer::sum);
        }

        return weeklyCounts;
    }

    public Map<String, Integer> getRequestCounts() {
        List<Report> reports = reportRepository.findMonthlyRequestCounts();

        Map<String, Integer> requestCounts = new HashMap<>();
        requestCounts.put("User Booking Requests", 0);
        requestCounts.put("Provider Accepted Requests", 0);
        requestCounts.put("User Cancelled Requests", 0);
        requestCounts.put("Provider Rejected Requests", 0);

        for (Report report : reports) {
            String type = report.getType();
            int count = Integer.parseInt(report.getDetails().split(" ")[0].trim());
            requestCounts.put(type, requestCounts.get(type) + count);
        }

        return requestCounts;
    }

    public Map<String, Double> getWeeklyRevenue() {
        List<Report> reports = reportRepository.findMonthlyRevenueReports();
        Map<String, Double> weeklyRevenue = new HashMap<>();
        for (int i = 1; i <= 5; i++) {
            weeklyRevenue.put("Week " + i, 0.0);
        }

        for (Report report : reports) {
            int weekOfMonth = report.getGeneratedDate().get(WeekFields.of(Locale.getDefault()).weekOfMonth());
            String weekLabel = "Week " + weekOfMonth;

            double revenue = Double
                    .parseDouble(report.getDetails().split(" ")[report.getDetails().split(" ").length - 1]);

            weeklyRevenue.put(weekLabel, revenue);
        }

        return weeklyRevenue;
    }
}
