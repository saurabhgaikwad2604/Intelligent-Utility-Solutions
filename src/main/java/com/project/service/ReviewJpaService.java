package com.project.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.project.module.Provider;
import com.project.module.Review;
import com.project.module.User;
import com.project.repository.ProviderJpaRepository;
import com.project.repository.ReviewJpaRepository;
import com.project.repository.ReviewRepository;
import com.project.repository.UserJpaRepository;

@Service
public class ReviewJpaService implements ReviewRepository {

    @Autowired
    private ReviewJpaRepository reviewRepository;

    @Autowired
    private UserJpaRepository userRepository;

    @Autowired
    private ProviderJpaRepository providerRepository;

    public List<Review> getAllReviews() {
        return reviewRepository.findAll();
    }

    public Review getReviewById(int id) {
        return reviewRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Review not found"));
    }

    public Review createReview(Review review) {
        return reviewRepository.save(review);
    }

    public Review updateReview(int id, Review review) {
        Review existingReview = reviewRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Review not found"));
        if (review.getComment() != null) {
            existingReview.setComment(review.getComment());
        }
        if (review.getRating() != 0) {
            existingReview.setRating(review.getRating());
        }
        if (review.getUser() != null) {
            User user = review.getUser();
            int userId = user.getId();
            User newUser = userRepository.findById(userId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
            existingReview.setUser(newUser);
        }
        if (review.getProvider() != null) {
            Provider provider = review.getProvider();
            int providerId = provider.getId();
            Provider newProvider = providerRepository.findById(providerId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Provider not found"));
            existingReview.setProvider(newProvider);
        }
        return reviewRepository.save(existingReview);
    }

    public void deleteReview(int id) {
        @SuppressWarnings("unused")
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Review not found"));
        reviewRepository.deleteById(id);
    }

    @Override
    public User getBookingUser(int id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Review not found"));
        return review.getUser();
    }

    @Override
    public Provider getBookingProvider(int id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Review not found"));
        return review.getProvider();
    }

    @Override
    public List<String> getCommentsByProviderId(int providerId) {
        return reviewRepository.findCommentsByProviderId(providerId);
    }

    @Override
    public Integer getUserIdByCommentAndProviderId(String comment, int providerId) {
        return reviewRepository.findUserIdByCommentAndProviderId(comment, providerId);
    }
}
