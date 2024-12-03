package com.project.repository;

import com.project.module.*;
import java.util.List;


public interface UserRepository {
    List<User> getAllUsers();
    User getUserById(int id);
    User createUser(User user);
    User updateUser(int id, User user);
    void deleteUser(int id);
    List<Booking> getUserBookings(int id);
    List<Review> getUserReviews(int id);
}
