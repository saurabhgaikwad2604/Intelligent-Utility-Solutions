package com.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.project.module.Provider;
import com.project.module.Review;
import com.project.module.User;

import java.util.List;

@Repository
public interface ReviewJpaRepository extends JpaRepository<Review, Integer> {
    List<Review> findByUser(User user);

    List<Review> findByProvider(Provider provider);

    @Query("SELECT r.comment FROM Review r WHERE r.provider.id = :providerId")
    List<String> findCommentsByProviderId(int providerId);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.provider.id = :providerId")
    Double findAverageRatingByProviderId(int providerId);

    @Query("SELECT r.user.id FROM Review r WHERE r.comment = :comment AND r.provider.id = :providerId")
    Integer findUserIdByCommentAndProviderId(String comment, int providerId);

}
