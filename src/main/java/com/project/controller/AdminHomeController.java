package com.project.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.module.Activity;
import com.project.module.Admin;
import com.project.module.Booking;
import com.project.module.Contact;
import com.project.module.Payment;
import com.project.module.Provider;
import com.project.module.Report;
import com.project.module.User;
import com.project.repository.AdminJpaRepository;
import com.project.repository.BookingJpaRepository;
import com.project.repository.PaymentJpaRepository;
import com.project.repository.ProviderJpaRepository;
import com.project.repository.UserJpaRepository;
import com.project.service.ActivityJpaService;
import com.project.service.AdminJpaService;
import com.project.service.BookingJpaService;
import com.project.service.ContactJpaService;
import com.project.service.ProviderJpaService;
import com.project.service.ReportJpaService;
import com.project.service.UserJpaService;

import jakarta.servlet.http.HttpSession;



@Controller
@RequestMapping("/admin")
public class AdminHomeController {

    @Autowired
    private AdminJpaService adminJpaService;

    @Autowired
    private UserJpaRepository userRepository;

    @Autowired
    private UserJpaService userService;

    @Autowired
    private ProviderJpaRepository providerRepository;

    @Autowired
    private ProviderJpaService providerService;

    @Autowired
    private BookingJpaRepository bookingRepository;

    @Autowired
    private BookingJpaService bookingService;

    @Autowired
    private PaymentJpaRepository paymentRepository;

    @Autowired
    private ActivityJpaService activityService;

    @Autowired
    private ReportJpaService reportService;

    @Autowired
    private AdminJpaRepository adminRepository;

    @Autowired
    private ContactJpaService contactService;

    private BCryptPasswordEncoder bp = new BCryptPasswordEncoder(12);

    @GetMapping("/dashboard")
    public String home(Model model) {
        long totalUser = userRepository.count();
        long totalProvider = providerRepository.count();
        List<Activity> recentActivities = activityService.getRecentActivities();
        List<Report> pendingReports = reportService.getPendingReportsOfCurrentWeek();
        List<Contact> pendingContacts = contactService.getAllPendingContacts("Pending");
        int pendingContactsCount = pendingContacts.size();

        model.addAttribute("totalUser", totalUser);
        model.addAttribute("totalProvider", totalProvider);
        model.addAttribute("recentActivities", recentActivities);
        model.addAttribute("pendingReports", pendingReports);
        model.addAttribute("pendingContacts", pendingContacts);
        model.addAttribute("pendingContactsCount", pendingContactsCount);

        return "admin/adminDashboard";
    }

    @GetMapping("/users")
    public String getUsers(Model model) {
        List<User> users = userRepository.findAll();

        model.addAttribute("users", users);

        return "admin/adminManageUser";
    }

    @GetMapping("/users/view-details/{id}")
    public String getUserDetails(@PathVariable int id, Model model) {
        User user = userRepository.findById(id).get();
        List<Booking> bookings = bookingRepository.findByUser(user);

        user.setRole("USER");

        model.addAttribute("bookings", bookings);
        model.addAttribute("user", user);

        return "admin/adminViewDetailsUser";
    }

    @PostMapping("/deleteUser/{userId}")
    public String deleteUser(@PathVariable int userId) {
        userService.deleteUser(userId);
        return "redirect:/admin/users";
    }

    @PostMapping("/users/toggleStatus/{userId}")
    public String toggleUserStatus(@PathVariable int userId) {
        User user = userService.getUserById(userId); // Replace with actual user fetch method
        if (user.getStatus().equals("Active")) {
            user.setStatus("Deactivated");
        } else {
            user.setStatus("Active");
        }
        userService.updateUser(userId, user); // Replace with actual save/update method
        return "redirect:/admin/users"; // Redirect back to the manage users page
    }

    @GetMapping("/service-provider")
    public String getProviders(Model model) {
        List<Provider> providers = providerRepository.findAll();

        model.addAttribute("providers", providers);

        return "admin/adminManageProvider";
    }

    @GetMapping("/providers/view-details/{id}")
    public String getProviderDetails(@PathVariable int id, Model model) {
        Provider provider = providerRepository.findById(id).get();
        List<Booking> bookings = bookingRepository.findByProvider(provider);

        provider.setRole("PROVIDER");

        model.addAttribute("bookings", bookings);
        model.addAttribute("provider", provider);

        return "admin/adminViewDetailsProvider";
    }

    @PostMapping("/deleteProvider/{providerId}")
    public String deleteProvider(@PathVariable int providerId) {
        providerService.deleteProvider(providerId);
        return "redirect:/admin/service-provider";
    }

    @PostMapping("/service-provider/toggleStatus/{providerId}")
    public String toggleProviderStatus(@PathVariable int providerId) {
        Provider provider = providerService.getProviderById(providerId); // Replace with actual provider fetch method
        if (provider.getStatus().equals("Active")) {
            provider.setStatus("Deactivated");
        } else {
            provider.setStatus("Active");
        }
        providerService.updateProvider(providerId, provider); // Replace with actual save/update method
        return "redirect:/admin/service-provider"; // Redirect back to the manage providers page
    }

    @GetMapping("/reports")
    public String showReports(Model model) throws JsonProcessingException {
        Map<String, Map<String, Integer>> weeklyCounts = reportService.getWeeklyRegistrationCounts();
        Map<String, Integer> requestCounts = reportService.getRequestCounts();
        Map<String, Double> weeklyRevenue = reportService.getWeeklyRevenue();
        Map<String, Integer> bookingsData = adminJpaService.getTotalBookings();
        ObjectMapper mapper = new ObjectMapper();
        String requestCountsJson = mapper.writeValueAsString(requestCounts);
        String weeklyCountsJson = mapper.writeValueAsString(weeklyCounts);
        String weeklyRevenueJson = mapper.writeValueAsString(weeklyRevenue);
        String bookingsDataJson = mapper.writeValueAsString(bookingsData);
        
        model.addAttribute("bookingsDataJson", bookingsDataJson);
        model.addAttribute("weeklyRevenueJson", weeklyRevenueJson);
        model.addAttribute("requestCountsJson", requestCountsJson);
        model.addAttribute("weeklyCountsJson", weeklyCountsJson);
        model.addAttribute("totalUsersCount", userRepository.count() + providerRepository.count());
        model.addAttribute("registrationCount", adminJpaService.getMonthlyRegistrationCount());
        model.addAttribute("totalRevenue", paymentRepository.totalRevenue());
        model.addAttribute("monthlyRevenue", adminJpaService.getMonthlyRevenue());
        return "admin/adminReports";
    }

    @GetMapping("/bookings")
    public String getBookings(Model model) {
        List<Booking> bookings = bookingRepository.findAll();

        model.addAttribute("bookings", bookings);

        return "admin/adminManageBooking";
    }

    @PostMapping("/deleteBooking/{bookingId}")
    public String deleteBooking(@PathVariable int bookingId) {
        bookingService.deleteBooking(bookingId);
        return "redirect:/admin/bookings";
    }

    @GetMapping("/bookings/view-details/{id}")
    public String getBookingDetails(@PathVariable int id, Model model) {
        Booking booking = bookingService.getBookingById(id);

        model.addAttribute("booking", booking);

        return "admin/adminViewDetailsBooking";
    }

    @GetMapping("/viewReceipt/{bookingId}")
    public String showPaymentReceipt(@PathVariable int bookingId, Model model) {
        Payment payment = paymentRepository.findByBookingId(bookingId);
        Booking booking = bookingService.getBookingById(bookingId);
        model.addAttribute("payment", payment);
        model.addAttribute("booking", booking);
        return "admin/adminPaymentReceipt";
    }

    @PostMapping("/reports/generate/{reportId}")
    public String generateReport(@PathVariable Long reportId, RedirectAttributes redirectAttributes) {
        reportService.generateDetailedReport(reportId);
        redirectAttributes.addFlashAttribute("reportMessage", "Report generated successfully.");
        return "redirect:/admin/dashboard";
    }

    @PostMapping("/create")
    public String createAdmin(@ModelAttribute Admin a, HttpSession session, RedirectAttributes redirectAttributes) {
        a.setPassword(bp.encode(a.getPassword()));
        a.setRole("ROLE_ADMIN");
        adminRepository.save(a);
        redirectAttributes.addFlashAttribute("message", "Admin created successfully!");
        session.setAttribute("message", "Admin Registered Successfully!");
        return "redirect:/admin/dashboard";
    }

    @PostMapping("/complaints/toggleStatus/{complaintId}")
    public String toggleProviderStatus(@PathVariable long complaintId) {
        Contact contact = contactService.getContactById(complaintId); // Replace with actual provider fetch method
        if (contact.getStatus().equals("Pending")) {
            contact.setStatus("Read");
        }
        contactService.saveContact(contact); // Replace with actual save/update method
        return "redirect:/admin/complaints"; // Redirect back to the manage providers page
    }

    @GetMapping("/complaints")
    public String getMethodName(Model model) {
        List<Contact> complaints = contactService.getAllContacts();

        model.addAttribute("complaints", complaints);
        return "admin/adminComplaints";
    }
    
}
