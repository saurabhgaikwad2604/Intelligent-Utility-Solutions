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
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected int id;

    protected String name;
    @Column(unique = true)
    protected String email;
    protected String password;
    protected String location;
    protected Long contactno;
    protected String role;
    protected String favoriteColor;
    protected String favoriteFruit;
    protected String favoriteAnimal;

    protected String status = "Active"; // Activation status

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", nullable = false, updatable = false)
    protected Date createdAt;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    protected Date updatedAt;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude // Exclude to avoid infinite recursion
    protected List<Booking> bookings = new ArrayList<>(); // List of bookings related to the user

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    @ToString.Exclude // Exclude to avoid infinite recursion
    protected List<Review> reviews = new ArrayList<>(); // List of reviews made by the user

    public enum Role {
        USER,
        SERVICE_PROVIDER,
        ADMIN
    }
}
