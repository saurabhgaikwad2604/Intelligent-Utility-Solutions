package com.project.repository;

import java.util.List;


import com.project.module.*;

public interface ReviewRepository {
    List<Review> getAllReviews();
    Review getReviewById(int id);
    Review createReview(Review review);
    Review updateReview(int id, Review review);
    void deleteReview(int id);
    User getBookingUser(int id);
    Provider getBookingProvider(int id);
    List<String> getCommentsByProviderId(int id);
    Integer getUserIdByCommentAndProviderId(String comment, int providerId);
}
