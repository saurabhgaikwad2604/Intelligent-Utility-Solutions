package com.project.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.project.module.*;
import com.project.repository.*;

@Service
public class CustomUserService implements UserDetailsService {

    @Autowired
    private UserJpaRepository userRepo;

    @Autowired
    private ProviderJpaRepository providerRepo;

    @Autowired
    private AdminJpaRepository adminRepo;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepo.findByEmail(email);
        if (user != null) {
            if (!user.getStatus().equalsIgnoreCase("Active")) {
                throw new DisabledException("You are currently deactivated by Admin. Please try again later.");
            }
            return new CustomUser(user);
        }

        Provider provider = providerRepo.findByEmail(email);
        if (provider != null) {
            if (!provider.getStatus().equalsIgnoreCase("Active")) {
                throw new DisabledException("You are currently deactivated by Admin. Please try again later.");
            }
            return new CustomProvider(provider);
        }

        Admin admin = adminRepo.findByEmail(email);
        if (admin != null) {
            return new CustomAdmin(admin); // Assuming CustomProvider implements UserDetails
        }

        throw new UsernameNotFoundException("User or provider not found with email: " + email);
    }
}
