package com.project.module;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

@Data
@Entity
@Table(name = "review")
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected int id;

    protected String comment; // Review comment
    protected int rating; // Rating out of 5

    @ManyToOne
    @JoinColumn(name = "provider_id")
    @JsonIgnore
    @ToString.Exclude // Exclude to avoid infinite recursion
    protected Provider provider; // Provider that the review belongs to

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    @ToString.Exclude // Exclude to avoid infinite recursion
    protected User user; // User who made the review
}
