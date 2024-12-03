package com.project.config;

import java.util.*;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.project.module.User;

public class CustomUser implements UserDetails {
    private User u;

    public CustomUser(User u) {
        super();
        this.u = u;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        HashSet<SimpleGrantedAuthority> set = new HashSet<SimpleGrantedAuthority>();
        set.add(new SimpleGrantedAuthority(u.getRole()));

        return set;
    }

    @Override
    public String getPassword() {
        return u.getPassword();
    }

    @Override
    public String getUsername() {
        return u.getEmail();
    }

    @Override
    public boolean isEnabled() {
        return u.getStatus().equalsIgnoreCase("Active"); // Assuming "Active" is the status for enabled users
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // or add your logic
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // or add your logic
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // or add your logic
    }

}
