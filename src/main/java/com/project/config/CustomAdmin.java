package com.project.config;

import java.util.*;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.project.module.Admin;

public class CustomAdmin implements UserDetails {
    private Admin a;

    public CustomAdmin(Admin a){
        super();
        this.a = a;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        HashSet<SimpleGrantedAuthority> set = new HashSet<SimpleGrantedAuthority>();
        set.add(new SimpleGrantedAuthority(a.getRole()));

        return set;
    }

    @Override
    public String getPassword() {
        return a.getPassword();
    }

    @Override
    public String getUsername() {
        return a.getEmail();
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

    @Override
    public boolean isEnabled() {
        return true; // or implement your logic for active users
    }
}
