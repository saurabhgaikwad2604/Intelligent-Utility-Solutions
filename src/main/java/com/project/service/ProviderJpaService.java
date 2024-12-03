package com.project.service;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.project.module.Booking;
import com.project.module.Provider;
import com.project.module.Review;
import com.project.repository.BookingJpaRepository;
import com.project.repository.ProviderJpaRepository;
import com.project.repository.ProviderRepository;
import com.project.repository.ReviewJpaRepository;

@Service
public class ProviderJpaService implements ProviderRepository {

    @Autowired
    private ProviderJpaRepository providerRepository;

    @Autowired
    private BookingJpaRepository bookingRepository;

    @Autowired
    private ReviewJpaRepository reviewRepository;

    @Override
    public List<Provider> getAllProviders() {
        return providerRepository.findAll();
    }

    @Override
    public Provider getProviderById(int id) {
        return providerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Provider not found"));
    }

    @Override
    public Provider createProvider(Provider provider) {
        return providerRepository.save(provider);
    }

    @Override
    public Provider updateProvider(int id, Provider provider) {
        Provider existingProvider = providerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Provider not found"));

        if (provider.getName() != null) {
            existingProvider.setName(provider.getName());
        }
        if (provider.getEmail() != null) {
            existingProvider.setEmail(provider.getEmail());
        }
        if (provider.getService() != null) {
            existingProvider.setService(provider.getService());
        }
        if (provider.getStatus() != null) {
            existingProvider.setStatus(provider.getStatus());
        }
        if (provider.getPrice() != 0) {
            existingProvider.setPrice(provider.getPrice());
        }
        if (provider.getFavoriteColor() != null) {
            existingProvider.setFavoriteColor(provider.getFavoriteColor());
        }
        if (provider.getFavoriteFruit() != null) {
            existingProvider.setFavoriteFruit(provider.getFavoriteFruit());
        }
        if (provider.getFavoriteAnimal() != null) {
            existingProvider.setFavoriteAnimal(provider.getFavoriteAnimal());
        }

        if (provider.getBookings() != null) {
            // Clear current user's bookings in `Booking` table if needed.
            List<Booking> newBookings = bookingRepository.findAllById(
                    provider.getBookings().stream().map(Booking::getId).collect(Collectors.toList()));

            for (Booking booking : newBookings) {
                booking.setProvider(existingProvider); // Re-link each booking to the updated user.
            }
            existingProvider.setBookings(newBookings);
        }

        return providerRepository.save(existingProvider);
    }

    @Override
    public void deleteProvider(int id) {
        try {
            Provider provider = providerRepository.findById(id).get();

            // Remove provider from bookings
            List<Booking> bookings = bookingRepository.findByProvider(provider);
            for (Booking booking : bookings) {
                booking.setProvider(null); // Set provider to null to disassociate
            }
            bookingRepository.saveAll(bookings);

            List<Review> reviews = reviewRepository.findByProvider(provider);
            for (Review review : reviews) {
                review.setProvider(null);
            }
            reviewRepository.saveAll(reviews);

            // Delete the user
            providerRepository.deleteById(id);
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        throw new ResponseStatusException(HttpStatus.NO_CONTENT);
    }

    @Override
    public List<Booking> getProviderBookings(int id) {
        Provider provider = providerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Provider not found"));

        List<Booking> bookings = bookingRepository.findByProvider(provider);
        return bookings;
    }

    @Override
    public List<Review> getProviderReviews(int id) {
        Provider provider = providerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Provider not found"));

        List<Review> reviews = reviewRepository.findByProvider(provider);
        return reviews;
    }

    @Override
    public List<Provider> getProvidersByLocationAndService(String location, String service) {
        return providerRepository.findByLocationAndService(location, service);
    }

    @Override
    public Double getAverageRating(int id) {
        Double averageRating = reviewRepository.findAverageRatingByProviderId(id);
        return (averageRating != null) ? averageRating : 0.0;
    }

    @Override
    public List<String> getProviderComments(int id) {
        return reviewRepository.findCommentsByProviderId(id);
    }

    public Provider findByEmail(String email) {
        Provider provider = providerRepository.findByEmail(email);
        if (provider == null) {
            throw new RuntimeException("Provider not found");
        }
        return provider;
    }
}
