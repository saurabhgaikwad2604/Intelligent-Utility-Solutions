package com.project.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.project.module.Contact;
import com.project.module.Provider;
import com.project.module.User;
import com.project.repository.ProviderJpaRepository;
import com.project.repository.UserJpaRepository;
import com.project.service.ActivityJpaService;
import com.project.service.ContactJpaService;
import com.project.service.ReportJpaService;

import jakarta.servlet.http.HttpSession;

@Controller
public class UserController {

    @Autowired
    private UserJpaRepository userRepository;

    @Autowired
    private ActivityJpaService activityService;

    @Autowired
    private ReportJpaService reportService;

    @Autowired
    private ProviderJpaRepository providerRepository;

    @Autowired
    private ContactJpaService contactService;

    private BCryptPasswordEncoder bp = new BCryptPasswordEncoder(12);

    @GetMapping("/login")
    public String login(HttpSession session, Model model) {
        String successMessage = (String) session.getAttribute("successMessage");
        if (successMessage != null) {
            model.addAttribute("successMessage", successMessage);
            session.removeAttribute("successMessage"); // Clear message after showing
        }
        return "login"; // Return the login view
    }

    @GetMapping("/")
    public String home() {
        return "home";
    }

    @GetMapping("/contactUs")
    public String contactUs() {
        return "contactUs";
    }

    @GetMapping("/registration/provider")
    public String providerHome(HttpSession session, Model model) {
        String message = (String) session.getAttribute("message");
        if (message != null) {
            model.addAttribute("message", message);
            session.removeAttribute("message"); // Clear the message from the session
        }
        return "provider/providerRegistration";
    }

    @PostMapping("/register/provider")
    public String register(@ModelAttribute Provider p, HttpSession session) {
        p.setPassword(bp.encode(p.getPassword())); // Hash the password
        p.setRole("ROLE_PROVIDER");
        providerRepository.save(p);
        activityService.logActivity("Provider Registered", p.getName());
        reportService.updateProviderActivityReport();
        session.setAttribute("message", "Provider Registered Successfully!");
        return "redirect:/registration/provider";
    }

    @GetMapping("/registration/user")
    public String userHome(HttpSession session, Model model) {
        String message = (String) session.getAttribute("message");
        if (message != null) {
            model.addAttribute("message", message);
            session.removeAttribute("message"); // Clear the message from the session
        }
        return "user/userRegistration";
    }

    @PostMapping("/register/user")
    public String register(@ModelAttribute User u, HttpSession session) {
        u.setPassword(bp.encode(u.getPassword())); // Hash the password
        u.setRole("ROLE_USER");

        userRepository.save(u); // Save user
        activityService.logActivity("User Registered", u.getName());
        reportService.updateUserActivityReport();
        session.setAttribute("message", "User Registered Successfully!");
        return "redirect:/registration/user"; // Redirect to registration page
    }

    @PostMapping("/verifySecurityAnswers")
    public ResponseEntity<?> verifySecurityAnswers(
            @RequestParam String email,
            @RequestParam String role,
            @RequestParam String favoriteColor,
            @RequestParam String favoriteAnimal,
            @RequestParam String favoriteFruit) {

        if (role.equalsIgnoreCase("user")) {
            User user = userRepository.findByEmail(email);
            if (user != null && favoriteColor.equalsIgnoreCase(user.getFavoriteColor())
                    && favoriteFruit.equalsIgnoreCase(user.getFavoriteFruit())
                    && favoriteAnimal.equalsIgnoreCase(user.getFavoriteAnimal())) {
                return ResponseEntity.ok(Map.of("success", true, "userId", user.getId()));
            }
        } else if (role.equalsIgnoreCase("provider")) {
            Provider provider = providerRepository.findByEmail(email);
            if (provider != null && favoriteColor.equalsIgnoreCase(provider.getFavoriteColor())
                    && favoriteFruit.equalsIgnoreCase(provider.getFavoriteFruit())
                    && favoriteAnimal.equalsIgnoreCase(provider.getFavoriteAnimal())) {
                return ResponseEntity.ok(Map.of("success", true, "userId", provider.getId()));
            }
        }

        return ResponseEntity.ok(Map.of("success", false));
    }

    @GetMapping("/reset-password")
    public String showResetPasswordPage(@RequestParam int userId, Model model) {
        model.addAttribute("userId", userId);
        return "resetPassword"; // Name of your reset password HTML file
    }

    @PostMapping("/reset-password")
    public String resetPassword(@RequestParam String role, @RequestParam int userId, @RequestParam String newPassword,
            RedirectAttributes redirectAttributes, HttpSession session) {
        if (role.equalsIgnoreCase("user")) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid user ID"));
            if (!role.equalsIgnoreCase(user.getRole())) {
                redirectAttributes.addFlashAttribute("failedMessage", "Invalide Role Forgot Request Failed!");
                session.setAttribute("failedMessage", "Invalide Role Forgot Request Failed!");
                return "redirect:/login";
            }
            user.setPassword(bp.encode(newPassword));
            userRepository.save(user);
            redirectAttributes.addFlashAttribute("message", "Password changed successfully!");
            session.setAttribute("successMessage", "Password changed successfully!");
        } else if (role.equalsIgnoreCase("provider")) {
            Provider provider = providerRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid prvider ID"));
            if (!role.equalsIgnoreCase(provider.getRole())) {
                redirectAttributes.addFlashAttribute("failedMessage", "Invalide Role Forgot Request Failed!");
                session.setAttribute("failedMessage", "Invalide Role Forgot Request Failed!");
                return "redirect:/login";
            }
            provider.setPassword(bp.encode(newPassword));
            providerRepository.save(provider);
            redirectAttributes.addFlashAttribute("message", "Password changed successfully!");
            session.setAttribute("successMessage", "Password changed successfully!");
        } else {
            redirectAttributes.addFlashAttribute("message", "Invalide Role Forgot Request Failed!");
            session.setAttribute("successMessage", "Invalide Role Forgot Request Failed!");
            return "redirect:/login";
        }
        return "redirect:/login";
    }

    @PostMapping("/contact/submit")
    public String submitContact(Contact contact, RedirectAttributes redirectAttributes, HttpSession session) {
        contactService.saveContact(contact);
        redirectAttributes.addFlashAttribute("complaintSuccessMessage",
                "Your message was submitted successfully, and our team will reach out to you soon!");
        session.setAttribute("complaintSuccessMessage",
                "Your message was submitted successfully, and our team will reach out to you soon!");
        return "redirect:/contactUs"; // Redirect to a home page
    }
}
