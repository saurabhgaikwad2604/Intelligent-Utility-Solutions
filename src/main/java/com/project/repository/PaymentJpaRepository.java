package com.project.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.project.module.Payment;

@Repository
public interface PaymentJpaRepository extends JpaRepository<Payment, Integer> {
    Payment findByBookingId(int id);
    @Query("SELECT SUM(amount) FROM Payment WHERE status = 'Completed'")
    Double totalRevenue();
    @Query("SELECT p FROM Payment p WHERE p.booking.id IN :bookingIds AND p.date >= :startDate AND p.date <= :endDate")
    List<Payment> findPaymentsByBookingIdsAndDateRange(@Param("bookingIds") List<Integer> bookingIds,
                                                       @Param("startDate") LocalDate startDate,
                                                       @Param("endDate") LocalDate endDate);
}
