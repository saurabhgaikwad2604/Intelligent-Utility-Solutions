package com.project.service;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.project.module.Booking;
import com.project.module.Review;
import com.project.module.User;
import com.project.repository.BookingJpaRepository;
import com.project.repository.ReviewJpaRepository;
import com.project.repository.UserJpaRepository;
import com.project.repository.UserRepository;

@Service
public class UserJpaService implements UserRepository {

    @Autowired
    private UserJpaRepository userRepository;
    @Autowired
    private BookingJpaRepository bookingRepository;
    @Autowired
    private ReviewJpaRepository reviewRepository;

    @Override
    public List<User> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users;
    }

    @Override
    public User getUserById(int id) {
        try {
            User user = userRepository.findById(id).get();
            return user;
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public User createUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public User updateUser(int id, User user) {
        try {
            User newUser = userRepository.findById(id)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

            // Update fields
            if (user.getName() != null) {
                newUser.setName(user.getName());
            }
            if (user.getEmail() != null) {
                newUser.setEmail(user.getEmail());
            }
            if (user.getContactno() != null) {
                newUser.setContactno(user.getContactno());
            }
            if (user.getLocation() != null) {
                newUser.setLocation(user.getLocation());
            }
            if (user.getFavoriteColor() != null) {
                newUser.setFavoriteColor(user.getFavoriteColor());
            }
            if (user.getFavoriteFruit() != null) {
                newUser.setFavoriteFruit(user.getFavoriteFruit());
            }
            if (user.getFavoriteAnimal() != null) {
                newUser.setFavoriteAnimal(user.getFavoriteAnimal());
            }

            // Update bookings
            if (user.getBookings() != null) {
                // Clear current user's bookings in `Booking` table if needed.
                List<Booking> newBookings = bookingRepository.findAllById(
                        user.getBookings().stream().map(Booking::getId).collect(Collectors.toList()));

                for (Booking booking : newBookings) {
                    booking.setUser(newUser); // Re-link each booking to the updated user.
                }
                newUser.setBookings(newBookings);
            }

            return userRepository.save(newUser);
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public void deleteUser(int id) {
        try {
            User user = userRepository.findById(id).get();

            // Remove user from bookings
            List<Booking> bookings = bookingRepository.findByUser(user);
            for (Booking booking : bookings) {
                booking.setUser(null); // Set user to null to disassociate
            }
            bookingRepository.saveAll(bookings);

            List<Review> reviews = reviewRepository.findByUser(user);
            for (Review review : reviews) {
                review.setUser(null);
            }
            reviewRepository.saveAll(reviews);

            // Delete the user
            userRepository.deleteById(id);
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        throw new ResponseStatusException(HttpStatus.NO_CONTENT);
    }

    @Override
    public List<Booking> getUserBookings(int id) {
        try {
            User user = userRepository.findById(id).get();
            return bookingRepository.findByUser(user);
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public List<Review> getUserReviews(int id) {
        try {
            User user = userRepository.findById(id).get();
            return reviewRepository.findByUser(user);
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    public User findByEmail(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        return user;
    }
}
