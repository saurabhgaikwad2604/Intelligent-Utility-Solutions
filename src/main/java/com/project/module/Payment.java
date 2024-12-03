package com.project.module;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDate;
import java.util.*;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;


@Data
@Entity
@Table(name = "payment")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected int id;
    protected double amount;
    protected String description;
    protected int serviceCount;
    protected String status;
    @CreationTimestamp
    @Temporal(TemporalType.DATE)
    @Column(name = "date", nullable = false, updatable = false)
    protected LocalDate date;
    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    protected Date updatedAt;
    @OneToOne
    @JoinColumn(name = "booking_id", referencedColumnName = "id") // Foreign key in Payment table
    @EqualsAndHashCode.Exclude
    @ToString.Exclude // Exclude from toString
    private Booking booking;  // Owner of the relationship
}
