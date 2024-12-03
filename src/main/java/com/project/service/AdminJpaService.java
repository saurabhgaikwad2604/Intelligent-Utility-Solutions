package com.project.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.project.module.Admin;
import com.project.module.Provider;
import com.project.module.Report;
import com.project.module.User;
import com.project.repository.AdminJpaRepository;
import com.project.repository.AdminRepository;
import com.project.repository.BookingJpaRepository;
import com.project.repository.ProviderJpaRepository;
import com.project.repository.ReportJpaRepository;
import com.project.repository.UserJpaRepository;

@Service
public class AdminJpaService implements AdminRepository {

    @Autowired
    private AdminJpaRepository adminRepository;

    @Autowired
    private UserJpaRepository userRepository;

    @Autowired
    private ProviderJpaRepository providerRepository;

    @Autowired
    private ReportJpaRepository reportRepository;

    @Autowired
    private BookingJpaRepository bookingRepository;

    @Override
    public List<Admin> getAllAdmins() {
        return adminRepository.findAll();
    }

    @Override
    public Admin getAdminById(int id) {
        return adminRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Admin not found"));
    }

    @Override
    public Admin createAdmin(Admin admin) {
        return adminRepository.save(admin);
    }

    @Override
    public Admin updateAdmin(int id, Admin admin) {
        Admin existingAdmin = adminRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Admin not found"));

        if(admin.getName() !=  null){
            existingAdmin.setName(admin.getName());
        }
        if(admin.getEmail() != null){
            existingAdmin.setEmail(admin.getEmail());
        }
        if(admin.getContactno() != null){
            existingAdmin.setContactno(admin.getContactno());
        }

        return adminRepository.save(existingAdmin);
    }

    @Override
    public void deleteAdmin(int id) {
        Admin existingAdmin = adminRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Admin not found"));

        adminRepository.delete(existingAdmin);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public List<Provider> getAllProviders() {
        return providerRepository.findAll();
    }

    public int getMonthlyRegistrationCount(){
        List<Report> reports = reportRepository.findMonthlyUserAndProviderActivity();
        int count = 0;
        for(Report report: reports){
            count += Integer.parseInt(report.getDetails().split(" ")[0]);
        }
        return count;
    }

    public double getMonthlyRevenue(){
        List<Report> reports = reportRepository.findMonthlyRevenueReports();
        double revenue = 0;
        for(Report report: reports){
            revenue += Double.parseDouble(report.getDetails().split(" ")[report.getDetails().split(" ").length - 1]);
        }
        return revenue;
    }

    public Map<String, Integer> getTotalBookings(){
        int completedCount = bookingRepository.findCountByStatus("Completed");
        int pendingCount = bookingRepository.findCountByStatus("Pending");
        Map<String, Integer> bookingsData = new HashMap<>();
        bookingsData.put("Pending Bookings", pendingCount);
        bookingsData.put("Completed Bookings", completedCount);
        
        return bookingsData;
    }
}
