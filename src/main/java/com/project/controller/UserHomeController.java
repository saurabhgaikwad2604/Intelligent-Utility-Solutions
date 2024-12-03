package com.project.controller;

import com.project.module.User;
import com.project.repository.BookingJpaRepository;
import com.project.repository.PaymentJpaRepository;
import com.project.repository.ReviewJpaRepository;
import com.project.module.Booking;
import com.project.module.Payment;
import com.project.module.Provider;
import com.project.module.Review;
import com.project.service.ActivityJpaService;
import com.project.service.BookingJpaService;
import com.project.service.PaymentJpaService;
import com.project.service.ProviderJpaService;
import com.project.service.ReportJpaService;
import com.project.service.ReviewJpaService;
import com.project.service.UserJpaService;

import java.security.Principal;
import java.time.LocalDate;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/user")
public class UserHomeController {

    @Autowired
    private UserJpaService userService; // Inject User Service to fetch user data

    @Autowired
    private ProviderJpaService providerService;

    @Autowired
    private ReviewJpaService reviewService;

    @Autowired
    private BookingJpaService bookingService;

    @Autowired
    private BookingJpaRepository bookingRepository;

    @Autowired
    private PaymentJpaRepository paymentRepository;

    @Autowired
    private PaymentJpaService paymentService;

    @Autowired
    private ReviewJpaRepository reviewRepository;

    @Autowired
    private ActivityJpaService activityService;

    @Autowired
    private ReportJpaService reportService;

    @GetMapping("/book")
    public String bookService(@RequestParam String location, @RequestParam String service, Model model) {
        List<Provider> providers = providerService.getProvidersByLocationAndService(location, service);

        // Fetch reviews and ratings for each provider
        for (Provider provider : providers) {
            Double averageRating = Math.round(providerService.getAverageRating(provider.getId()) * 10.0) / 10.0;
            List<String> comments = reviewRepository.findCommentsByProviderId(provider.getId());

            // Ensure averageRating is not null
            provider.setAverageRating(averageRating != null ? averageRating : 0.0); // Default to 0.0 if null
            provider.setComments(comments); // Add comments to the provider object
        }

        model.addAttribute("providers", providers);
        return "user/userBookService"; // Return the modified view with modals
    }

    @GetMapping("/dashboard")
    public String home(Model model, Principal principal) {
        User user = userService.findByEmail(principal.getName());
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }

        // Fetch user's bookings
        List<Booking> bookings = bookingRepository.findByUserOrderByIdDesc(user);
        @SuppressWarnings("unused")
        List<Review> userReviews = reviewRepository.findByUser(user);

        // Add user and bookings to the model
        model.addAttribute("user", user);
        model.addAttribute("bookings", bookings);
        model.addAttribute("userReviews", userReviews);

        return "user/userDashboard";
    }

    @GetMapping("/profile")
    public String viewProfile(Model model, Principal principal) {
        String email = principal.getName(); // Get the logged-in user's email
        User user = userService.findByEmail(email); // Fetch user by email
        model.addAttribute("user", user); // Add user details to the model
        return "user/userUpdateProfile"; // Return the update profile view
    }

    // Process profile update
    @PostMapping("/profile/update")
    public String updateProfile(@ModelAttribute("user") User updatedUser, Principal principal,
            RedirectAttributes redirectAttributes) {
        String email = principal.getName();
        User currentUser = userService.findByEmail(email);

        // Update user using the existing ID
        userService.updateUser(currentUser.getId(), updatedUser);

        redirectAttributes.addFlashAttribute("message", "Profile updated successfully!");
        activityService.logActivity("User updated profile", currentUser.getName());
        return "redirect:/user/profile";
    }

    // New method to handle submitting a review and rating
    @PostMapping("/review/{providerId}")
    public String submitReview(@PathVariable("providerId") int providerId,
            @RequestParam("comment") String comment,
            @RequestParam("rating") int rating,
            Principal principal, RedirectAttributes redirectAttributes) {
        User user = userService.findByEmail(principal.getName());
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }

        Provider provider = providerService.getProviderById(providerId);
        if (provider == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Provider not found");
        }

        // Create and save the review
        Review review = new Review();
        review.setUser(user);
        review.setProvider(provider);
        review.setComment(comment);
        review.setRating(rating);
        reviewService.createReview(review);

        // Optionally, recalculate and update provider's average rating
        Double averageRating = providerService.getAverageRating(providerId);
        provider.setAverageRating(averageRating);
        providerService.updateProvider(provider.getId(), provider);

        redirectAttributes.addFlashAttribute("message", "Review submitted successfully!");
        activityService.logActivity("User given review to " + provider.getName(), user.getName());
        return "redirect:/user/book?location=" + provider.getLocation() + "&service=" + provider.getService();
    }

    // Handle booking submission
    @PostMapping("/book/{providerId}")
    public String confirmBooking(
            @PathVariable("providerId") int providerId,
            @RequestParam("date") String date,
            @RequestParam("timeSlot") String timeSlot,
            @RequestParam("address") String address,
            @RequestParam("numberOfServices") int numberOfServices,
            Principal principal, RedirectAttributes redirectAttributes) {

        // Find user and provider by logged-in email and providerId
        User user = userService.findByEmail(principal.getName());
        Provider provider = providerService.getProviderById(providerId);

        if (user == null || provider == null) {
            redirectAttributes.addFlashAttribute("error", "User or Provider not found.");
            return "redirect:/user/book";
        }

        // Create new booking
        Booking booking = new Booking();
        booking.setUser(user);
        booking.setProvider(provider);
        booking.setServiceName(provider.getService());
        booking.setBookingDate(LocalDate.parse(date)); // Assuming the date is in 'yyyy-MM-dd' format
        booking.setStatus("Pending");
        booking.setPaymentStatus("Pending");
        booking.setTotalPrice(provider.getPrice()); // Total price based on service cost and number
                                                    // of services
        booking.setNumberOfServices(numberOfServices);
        booking.setAddress(address);
        booking.setTimeSlot(timeSlot);

        bookingService.createBooking(booking); // Save the booking

        reportService.updateUserBookingRequestsReport();
        redirectAttributes.addFlashAttribute("message", "Booking confirmed!");
        activityService.logActivity("User booked service", user.getName());
        return "redirect:/user/dashboard";
    }

    @GetMapping("/myBookings")
    public String getMyBookings(Model model, Principal principal) {
        // Fetch the bookings for the logged-in user
        User user = userService.findByEmail(principal.getName());
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }

        // Fetch user's bookings
        List<Booking> bookings = bookingRepository.findByUserOrderByIdDesc(user);

        // Add the list of bookings to the model
        model.addAttribute("bookings", bookings);

        return "user/userMyBookings";
    }

    @GetMapping("/makePayment/{bookingId}")
    public String showMakePaymentPage(@PathVariable int bookingId, Model model) {
        Payment payment = paymentRepository.findByBookingId(bookingId);
        Booking booking = bookingService.getBookingById(bookingId);
        model.addAttribute("payment", payment);
        model.addAttribute("booking", booking);
        return "user/userMakePayment";
    }

    @PostMapping("/confirmPayment")
    public String confirmPayment(@RequestParam int bookingId, Principal principal) {
        User user = userService.findByEmail(principal.getName());
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        paymentService.updatePaymentStatus(bookingId); // Update booking and payment status
        activityService.logActivity("User made payment", user.getName());
        return "redirect:/user/dashboard";
    }

    @GetMapping("/viewReceipt/{bookingId}")
    public String showPaymentReceipt(@PathVariable int bookingId, Model model) {
        Payment payment = paymentRepository.findByBookingId(bookingId);
        Booking booking = bookingService.getBookingById(bookingId);
        model.addAttribute("payment", payment);
        model.addAttribute("booking", booking);
        return "user/userPaymentReceipt";
    }

    @PostMapping("/deleteUser/{userId}")
    public String deleteUser(@PathVariable int userId) {
        User user = userService.getUserById(userId);
        activityService.logActivity("User deleted account", user.getName());
        userService.deleteUser(userId);
        return "redirect:/";
    }

    @PostMapping("/cancelBooking")
    public String cancelBooking(@RequestParam("bookingId") int bookingId, Principal principal) {
        // Determine the new status based on the action
        User user = userService.findByEmail(principal.getName());
        String newStatus = "Cancelled";
        Booking booking = bookingService.getBookingById(bookingId);
        booking.setStatus(newStatus);
        bookingRepository.save(booking);
        activityService.logActivity("User Cancelled Booking", user.getName());
        reportService.updateUserCancelledRequestsReport();

        return "redirect:/user/dashboard"; // Redirect back to the dashboard
    }

    @PostMapping("/feedback/{providerId}")
    public String submitFeedback(@PathVariable("providerId") int providerId,
            @RequestParam("comment") String comment,
            @RequestParam("rating") int rating,
            Principal principal, RedirectAttributes redirectAttributes) {
        User user = userService.findByEmail(principal.getName());
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }

        Provider provider = providerService.getProviderById(providerId);
        if (provider == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Provider not found");
        }

        // Create and save the review
        Review review = new Review();
        review.setUser(user);
        review.setProvider(provider);
        review.setComment(comment);
        review.setRating(rating);
        reviewService.createReview(review);

        // Optionally, recalculate and update provider's average rating
        Double averageRating = providerService.getAverageRating(providerId);
        provider.setAverageRating(averageRating);
        providerService.updateProvider(provider.getId(), provider);

        redirectAttributes.addFlashAttribute("feedbackMessage", "Feedback submitted successfully!");
        activityService.logActivity("User provided feedback to " + provider.getName(), user.getName());
        return "redirect:/user/dashboard";
    }

    @GetMapping("/my-reviews")
    public String getReviewsByProvider(Principal principal, Model model) {
        User user = userService.findByEmail(principal.getName());
        List<Review> reviews = reviewRepository.findByUser(user);
        model.addAttribute("reviews", reviews);
        return "user/userReviews";
    }
}
