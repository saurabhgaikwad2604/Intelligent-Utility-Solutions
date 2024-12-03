package com.project.repository;

import java.util.List;
import org.springframework.stereotype.Repository;

import com.project.module.Payment;

@Repository
public interface PaymentRepository {
    List<Payment> getPayments();
    Payment getPaymentById(int id);
    Payment generatePayment(int bookingId, int serviceCount, String description, double amount);
    Payment updatePaymentStatus(int bookingId);
}
