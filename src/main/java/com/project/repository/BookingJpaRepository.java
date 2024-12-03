package com.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.project.module.Booking;
import com.project.module.Provider;
import com.project.module.User;

import java.util.List;

@Repository
public interface BookingJpaRepository extends JpaRepository<Booking, Integer> {
    List<Booking> findByUser(User user);
    List<Booking> findByProvider(Provider provider);
    List<Booking> findByProviderAndStatus(Provider provider, String status);
    @Query("SELECT COUNT(*) FROM Booking WHERE status = :status")
    int findCountByStatus(String status);
    List<Booking> findByUserOrderByIdDesc(User user);
    List<Booking> findByProviderOrderByBookingDateDesc(Provider provider);
    List<Booking> findByProviderAndStatusOrderByBookingDateDesc(Provider provider, String status);
}
