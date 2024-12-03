package com.project.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.project.module.Booking;
import com.project.module.Payment;
import com.project.repository.BookingJpaRepository;
import com.project.repository.PaymentJpaRepository;
import com.project.repository.PaymentRepository;

@Service
public class PaymentJpaService implements PaymentRepository {

    @Autowired
    private PaymentJpaRepository paymentRepository;

    @Autowired
    private BookingJpaRepository bookingRepository;

    @Autowired
    private BookingJpaService bookingService;

    @Autowired
    private ReportJpaService reportService;

    @Override
    public List<Payment> getPayments() {
        return paymentRepository.findAll();
    }

    @Override
    public Payment getPaymentById(int id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Payment not found"));
    }

    @Override
    public Payment generatePayment(int bookingId, int serviceCount, String description, double amount) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found"));

        Payment payment = new Payment();
        payment.setBooking(booking);
        payment.setServiceCount(serviceCount);
        payment.setDescription(description);
        payment.setAmount(booking.getTotalPrice() + amount);
        payment.setStatus("Pending");
        booking.setStatus("Payment Generated");
        bookingRepository.save(booking);
        return paymentRepository.save(payment);
    }

    @Override
    public Payment updatePaymentStatus(int bookingId) {
        Payment payment = paymentRepository.findByBookingId(bookingId);
        payment.setStatus("Completed");
        Booking booking = bookingService.getBookingById(bookingId);
        booking.setStatus("Completed");
        booking.setPaymentStatus("Completed");
        bookingRepository.save(booking);
        reportService.updateRevenueReport(payment.getAmount());
        return paymentRepository.save(payment);
    }

    public Map<String, Double> getWeeklyPayments(List<Integer> bookingIds) {
        LocalDate today = LocalDate.now();
        LocalDate startOfWeek = today.with(DayOfWeek.MONDAY);
        LocalDate endOfWeek = today.with(DayOfWeek.SUNDAY);

        List<Payment> payments = paymentRepository.findPaymentsByBookingIdsAndDateRange(bookingIds, startOfWeek,
                endOfWeek);

        // Initialize a map with weekdays as keys
        Map<String, Double> weeklyPayments = new HashMap<>();
        for (DayOfWeek day : DayOfWeek.values()) {
            weeklyPayments.put(day.name(), 0.0);
        }

        // Sum payments per day
        for (Payment payment : payments) {
            DayOfWeek dayOfWeek = payment.getDate().getDayOfWeek();
            Double existingAmount = weeklyPayments.get(dayOfWeek.name());
            if (existingAmount == null) {
                existingAmount = 0.0; // Set default value
            }
            weeklyPayments.put(dayOfWeek.name(), existingAmount + payment.getAmount());
        }

        return weeklyPayments;
    }
}
