package com.project.module;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "booking")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected int id;

    @OneToOne(mappedBy = "booking", cascade = CascadeType.ALL)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude // Exclude from toString
    private Payment payment;  // Reference to Payment

    @ManyToOne
    @JoinColumn(name = "user_id") // Foreign key to reference the user
    @JsonIgnore
    @EqualsAndHashCode.Exclude
    @ToString.Exclude // Exclude from toString
    protected User user;

    @ManyToOne
    @JoinColumn(name = "provider_id") // Foreign key to reference the provider
    @JsonIgnore
    @EqualsAndHashCode.Exclude
    @ToString.Exclude // Exclude from toString
    protected Provider provider;

    protected String serviceName;
    protected LocalDate bookingDate;
    protected String status;
    protected String paymentStatus;
    protected double totalPrice;
    protected int numberOfServices;
    protected String address;
    protected String timeSlot;

    public String getCapitalizedServiceName() {
        if (serviceName != null && serviceName.length() > 0) {
            return serviceName.substring(0, 1).toUpperCase() + serviceName.substring(1).toLowerCase();
        }
        return serviceName;
    }
    
}
