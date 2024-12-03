package com.project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.module.Booking;
import com.project.module.Payment;
import com.project.module.Provider;
import com.project.module.Review;
import com.project.module.User;
import com.project.repository.BookingJpaRepository;
import com.project.repository.PaymentJpaRepository;
import com.project.repository.ReviewJpaRepository;
import com.project.service.ActivityJpaService;
import com.project.service.BookingJpaService;
import com.project.service.PaymentJpaService;
import com.project.service.ProviderJpaService;
import com.project.service.ReportJpaService;
import com.project.service.ReviewJpaService;
import com.project.service.UserJpaService;

@Controller
@RequestMapping("/provider")
public class ProviderHomeController {

    @Autowired
    private ProviderJpaService providerService;

    @Autowired
    private ReviewJpaService reviewService;

    @Autowired
    private ReviewJpaRepository reviewRepository;

    @Autowired
    private UserJpaService userService;

    @Autowired
    private BookingJpaService bookingService;

    @Autowired
    private BookingJpaRepository bookingRepository;

    @Autowired
    private PaymentJpaService paymentService;

    @Autowired
    private PaymentJpaRepository paymentRepository;

    @Autowired
    private ActivityJpaService activityService;

    @Autowired
    private ReportJpaService reportService;

    @GetMapping("/dashboard")
    public String home(Model model, Principal principal) throws JsonProcessingException {
        String email = principal.getName(); // Get the logged-in provider's email
        Provider provider = providerService.findByEmail(email); // Fetch provider by email

        List<String> comments = providerService.getProviderComments(provider.getId());
        if (comments == null || comments.isEmpty()) {
            model.addAttribute("latestReview", "No reviews yet");
        } else {
            Random random = new Random();
            int randomIndex = random.nextInt(comments.size());
            String randomComment = comments.get(randomIndex);
            model.addAttribute("latestReview", randomComment);
            int userId = reviewService.getUserIdByCommentAndProviderId(randomComment, provider.getId());
            User user = userService.getUserById(userId);
            model.addAttribute("reviewedBy", user.getName());
        }
        model.addAttribute("providerName", provider.getName());

        Double averageRating = Math.round(providerService.getAverageRating(provider.getId()) * 10.0) / 10.0;
        model.addAttribute("averageRating", averageRating);

        // Fetch pending bookings for the logged-in provider
        List<Booking> pendingBookings = bookingRepository.findByProviderAndStatusOrderByBookingDateDesc(provider,
                "Pending");
        model.addAttribute("pendingBookings", pendingBookings);
        List<Booking> completedBookings = bookingRepository.findByProviderAndStatusOrderByBookingDateDesc(provider,
                "Completed");
        model.addAttribute("completedBookings", completedBookings);
        List<Booking> bookings = bookingRepository.findByProviderOrderByBookingDateDesc(provider);
        List<Integer> bookingIds = new ArrayList<>();
        for (Booking booking : bookings) {
            bookingIds.add(booking.getId());
        }
        Map<String, Double> weeklyPayments = paymentService.getWeeklyPayments(bookingIds);
        ObjectMapper mapper = new ObjectMapper();
        String weeklyPaymentsJson = mapper.writeValueAsString(weeklyPayments);
        model.addAttribute("weeklyPaymentsJson", weeklyPaymentsJson);
        int serviceRequestCount = pendingBookings.size();
        model.addAttribute("serviceRequestCount", serviceRequestCount);
        return "provider/providerDashboard";
    }

    @GetMapping("/profile")
    public String viewProfile(Model model, Principal principal) {
        String email = principal.getName(); // Get the logged-in provider's email
        Provider provider = providerService.findByEmail(email); // Fetch provider by email
        model.addAttribute("provider", provider); // Add provider details to the model
        return "provider/providerUpdateProfile"; // Return the update profile view
    }

    // Process profile update
    @PostMapping("/profile/update")
    public String updateProfile(@ModelAttribute("provider") Provider updatedProvider, Principal principal,
            RedirectAttributes redirectAttributes) {
        String email = principal.getName();
        Provider currentProvider = providerService.findByEmail(email);

        // Update provider using the existing ID
        providerService.updateProvider(currentProvider.getId(), updatedProvider);

        redirectAttributes.addFlashAttribute("message", "Profile updated successfully!");
        activityService.logActivity("Provider updated profile", currentProvider.getName());
        return "redirect:/provider/profile";
    }

    @GetMapping("/service-requests")
    public String getPendingBookings(Model model, Principal principal) {
        // Fetch the bookings for the logged-in user
        Provider provider = providerService.findByEmail(principal.getName());
        if (provider == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Provider not found");
        }

        // Fetch user's bookings
        List<Booking> pendingBookings = bookingRepository.findByProviderAndStatusOrderByBookingDateDesc(provider,
                "Pending");

        // Add the list of bookings to the model
        model.addAttribute("pendingBookings", pendingBookings);

        return "provider/providerServiceRequests";
    }

    @GetMapping("/services-provided")
    public String getCompletedBookings(Model model, Principal principal) {
        // Fetch the bookings for the logged-in user
        Provider provider = providerService.findByEmail(principal.getName());
        if (provider == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Provider not found");
        }

        // Fetch user's bookings
        List<Booking> completedBookings = bookingRepository.findByProviderAndStatusOrderByBookingDateDesc(provider,
                "Completed");

        // Add the list of bookings to the model
        model.addAttribute("completedBookings", completedBookings);

        return "provider/providerProvidedService";
    }

    @GetMapping("/service-history")
    public String getMyBookings(Model model, Principal principal) {
        // Fetch the bookings for the logged-in user
        Provider provider = providerService.findByEmail(principal.getName());
        if (provider == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Provider not found");
        }

        // Fetch user's bookings
        List<Booking> myBookings = bookingRepository.findByProviderOrderByBookingDateDesc(provider);

        // Add the list of bookings to the model
        model.addAttribute("myBookings", myBookings);

        return "provider/providerServiceHistory";
    }

    @PostMapping("/serviceRequests/updateBookingStatus")
    public String updateBookingStatus(@RequestParam("bookingId") int bookingId,
            @RequestParam("action") String action, Principal principal) {
        Provider provider = providerService.findByEmail(principal.getName());
        // Determine the new status based on the action
        String newStatus = action.equals("confirm") ? "Confirmed" : "Rejected";
        Booking booking = bookingService.getBookingById(bookingId);
        booking.setStatus(newStatus);
        bookingRepository.save(booking);

        if (newStatus.equals("Confirmed")) {
            activityService.logActivity("Provider approved request", provider.getName());
            reportService.updateProviderAcceptedRequestsReport();
        } else {
            activityService.logActivity("Provider rejected request", provider.getName());
        }

        return "redirect:/provider/service-requests"; // Redirect back to the dashboard
    }

    @PostMapping("/updateBookingStatus")
    public String updateTheBookingStatus(@RequestParam("bookingId") int bookingId,
            @RequestParam("action") String action, Principal principal) {
        // Determine the new status based on the action
        Provider provider = providerService.findByEmail(principal.getName());
        String newStatus = action.equals("confirm") ? "Confirmed" : "Rejected";
        Booking booking = bookingService.getBookingById(bookingId);
        booking.setStatus(newStatus);
        bookingRepository.save(booking);

        if (newStatus.equals("Confirmed")) {
            activityService.logActivity("Provider approved request", provider.getName());
            reportService.updateProviderAcceptedRequestsReport();
        } else {
            activityService.logActivity("Provider rejected request", provider.getName());
            reportService.updateProviderRejectedRequestsReport();
        }

        return "redirect:/provider/dashboard"; // Redirect back to the dashboard
    }

    @PostMapping("/generatePayment")
    public String generateThePayment(@RequestParam int bookingId, @RequestParam int serviceCount,
            @RequestParam String description, @RequestParam double amount, Principal principal) {
        Provider provider = providerService.findByEmail(principal.getName());
        paymentService.generatePayment(bookingId, serviceCount, description, amount);
        activityService.logActivity("Provider Generated payment", provider.getName());
        return "redirect:/provider/service-history";
    }

    @GetMapping("/viewReceipt/{bookingId}")
    public String showPaymentReceipt(@PathVariable int bookingId, Model model) {
        Payment payment = paymentRepository.findByBookingId(bookingId);
        Booking booking = bookingService.getBookingById(bookingId);
        model.addAttribute("payment", payment);
        model.addAttribute("booking", booking);
        return "provider/providerPaymentReceipt";
    }

    @PostMapping("/deleteProvider/{providerId}")
    public String deleteProvider(@PathVariable int providerId) {
        Provider provider = providerService.getProviderById(providerId);
        activityService.logActivity("Provider deleted account", provider.getName());
        providerService.deleteProvider(providerId);
        return "redirect:/";
    }

    @GetMapping("/my-reviews")
    public String getReviewsByProvider(Principal principal, Model model) {
        Provider provider = providerService.findByEmail(principal.getName());
        List<Review> reviews = reviewRepository.findByProvider(provider);
        model.addAttribute("reviews", reviews);
        return "provider/providerReviews";
    }
}
