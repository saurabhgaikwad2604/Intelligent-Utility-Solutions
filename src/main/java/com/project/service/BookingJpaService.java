package com.project.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.project.module.Booking;
import com.project.module.Provider;
import com.project.module.User;
import com.project.repository.BookingJpaRepository;
import com.project.repository.BookingRepository;
import com.project.repository.ProviderJpaRepository;
import com.project.repository.UserJpaRepository;

@Service
public class BookingJpaService implements BookingRepository {

    @Autowired
    private BookingJpaRepository bookingRepository;

    @Autowired
    private UserJpaRepository userRepository;

    @Autowired
    private ProviderJpaRepository providerRepository;

    @Override
    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    @Override
    public Booking getBookingById(int id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found"));
    }

    @Override
    public Booking createBooking(Booking booking) {
        Provider provider = providerRepository.findById(booking.getProvider().getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Provider not found"));

        // Calculate total price based on provider's price and number of services
        double totalPrice = provider.getPrice();
        booking.setTotalPrice(totalPrice);

        return bookingRepository.save(booking);
    }

    @Override
    public Booking updateBooking(int id, Booking booking) {
        Booking existingBooking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found"));

        if (booking.getServiceName() != null) {
            existingBooking.setServiceName(booking.getServiceName());
        }
        if (booking.getBookingDate() != null) {
            existingBooking.setBookingDate(booking.getBookingDate());
        }
        if (booking.getStatus() != null) {
            existingBooking.setStatus(booking.getStatus());
        }
        if (booking.getPaymentStatus() != null) {
            existingBooking.setPaymentStatus(booking.getPaymentStatus());
        }
        if (booking.getNumberOfServices() != 0) {
            existingBooking.setNumberOfServices(booking.getNumberOfServices());
        }
        if (booking.getAddress() != null) {
            existingBooking.setAddress(booking.getAddress());
        }
        if (booking.getTimeSlot() != null) {
            existingBooking.setTimeSlot(booking.getTimeSlot());
        }
        if (booking.getUser() != null) {
            User user = booking.getUser();
            int userId = user.getId();
            User newUser = userRepository.findById(userId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
            existingBooking.setUser(newUser);
        }
        if (booking.getProvider() != null) {
            Provider provider = booking.getProvider();
            int providerId = provider.getId();
            Provider newProvider = providerRepository.findById(providerId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Provider not found"));
            double providerPrice = provider.getPrice();
            int numberOfServices = booking.getNumberOfServices(); // Define this in Booking

            double totalPrice = providerPrice * numberOfServices;
            existingBooking.setTotalPrice(totalPrice); // Update total price in existing booking
            existingBooking.setProvider(newProvider);
        }
        return bookingRepository.save(existingBooking);
    }

    @Override
    public void deleteBooking(int id) {
        @SuppressWarnings("unused")
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found"));

        bookingRepository.deleteById(id);
    }

    @Override
    public User getBookingUser(int id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found"));
        return booking.getUser();
    }

    @Override
    public Provider getBookingProvider(int id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found"));
        return booking.getProvider();
    }

    public List<Booking> findBookingsByUser(User user) {
        return bookingRepository.findByUser(user);
    }

    @Override
    public List<Booking> getBookingsForProvider(Provider provider, String status) {
        return bookingRepository.findByProviderAndStatus(provider, status);
    }
}
