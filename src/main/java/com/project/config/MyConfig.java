package com.project.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;

@ComponentScan
@Configuration
public class MyConfig {

    @Bean
    public UserDetailsService userDetailsService() {
        return new CustomUserService(); // Registering your custom user service
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Password encoder for storing hashed passwords
    }

    @Bean
    public DaoAuthenticationProvider daoProvider() {
        DaoAuthenticationProvider dao = new DaoAuthenticationProvider();
        dao.setUserDetailsService(userDetailsService());
        dao.setPasswordEncoder(passwordEncoder());
        return dao;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager(); // Set up authentication manager
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/register/provider").permitAll() // Allow access to the registration endpoint
                        .requestMatchers("/register/user").permitAll() // Allow access to the registration endpoint
                        .requestMatchers("/registration/**").permitAll() // Allow public access to registration pages
                        .requestMatchers("/**").permitAll()
                        .requestMatchers("/login").permitAll() // Allow access to the login page
                        .requestMatchers("/user/**").hasRole("USER") // Protected user pages
                        .requestMatchers("/provider/**").hasRole("PROVIDER") // Protected provider pages
                        .requestMatchers("/admin/**").hasRole("ADMIN") // Protected admin pages
                        .anyRequest().authenticated() // All other requests require authentication
                )
                .formLogin(form -> form
                        .loginPage("/login") // Custom login page
                        .failureHandler((request, response, exception) -> {
                            if ((exception.getMessage()).contains("deactivated")) {
                                response.sendRedirect("/login?userError=deactivated");
                            } else {
                                response.sendRedirect("/login?error=other");
                            }
                        })
                        .successHandler(customAuthenticationSuccessHandler()) // Custom success handler for role-based
                                                                              // redirect
                        .permitAll())
                .logout(logout -> logout
                        .permitAll())
                .csrf(csrf -> csrf.disable()); // Disable CSRF for simplicity (not recommended for production)

        return http.build(); // Return the built SecurityFilterChain
    }

    @Bean
    public AuthenticationSuccessHandler customAuthenticationSuccessHandler() {
        return (HttpServletRequest request, HttpServletResponse response,
                org.springframework.security.core.Authentication authentication) -> {
            String redirectUrl = "/user/dashboard"; // Default redirect for USER role

            // Check if the authenticated user has the role "PROVIDER"
            if (authentication.getAuthorities().stream()
                    .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_PROVIDER"))) {
                redirectUrl = "/provider/dashboard"; // Redirect for PROVIDER role
            } else if (authentication.getAuthorities().stream()
                    .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"))) {
                redirectUrl = "/admin/dashboard"; // Redirect for PROVIDER role
            }

            // Redirect to the appropriate page based on role
            response.sendRedirect(redirectUrl);
        };
    }
}
