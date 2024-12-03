package com.project.config;

import java.util.*;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.project.module.Provider;

public class CustomProvider implements UserDetails {
    private Provider p;

    public CustomProvider(Provider p){
        super();
        this.p = p;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        HashSet<SimpleGrantedAuthority> set = new HashSet<SimpleGrantedAuthority>();
        set.add(new SimpleGrantedAuthority(p.getRole()));

        return set;
    }

    @Override
    public String getPassword() {
        return p.getPassword();
    }

    @Override
    public String getUsername() {
        return p.getEmail();
    }

    @Override
    public boolean isEnabled() {
        return p.getStatus().equalsIgnoreCase("Active"); // Assuming "Active" is the status for enabled users
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
