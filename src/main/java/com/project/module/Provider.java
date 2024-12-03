package com.project.module;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.util.*;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Data
@Entity
@Table(name = "provider")
public class Provider {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected int id;

    protected String name;
    @Column(unique = true)
    protected String email;
    protected String password;
    protected String location;
    protected long contactno;
    protected String service;
    protected String status = "Active";
    protected String role;
    protected double price;
    protected double averageRating;
    protected String favoriteColor;
    protected String favoriteFruit;
    protected String favoriteAnimal;
    protected List<String> comments;
    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", nullable = false, updatable = false)
    protected Date createdAt;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    protected Date updatedAt;

    // One-to-Many relationship with Booking
    @OneToMany(mappedBy = "provider", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude // Exclude to avoid infinite recursion
    protected List<Booking> bookings = new ArrayList<>(); // List of bookings related to the provider

    @OneToMany(mappedBy = "provider", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    @ToString.Exclude // Exclude to avoid infinite recursion
    protected List<Review> reviews = new ArrayList<>(); // List of reviews related to the provider
}
