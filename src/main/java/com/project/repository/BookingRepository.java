package com.project.repository;

import java.util.List;
import com.project.module.Booking;
import com.project.module.Provider;
import com.project.module.User;

public interface BookingRepository {
    List<Booking> getAllBookings();
    Booking getBookingById(int id);
    Booking createBooking(Booking booking);
    Booking updateBooking(int id, Booking booking);
    void deleteBooking(int id);
    User getBookingUser(int id);
    Provider getBookingProvider(int id);
    List<Booking> getBookingsForProvider(Provider provider, String status);
}
