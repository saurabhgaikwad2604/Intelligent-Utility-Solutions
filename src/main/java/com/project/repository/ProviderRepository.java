package com.project.repository;

import java.util.List;
import com.project.module.*;

public interface ProviderRepository {
    List<Provider> getAllProviders();
    Provider getProviderById(int id);
    Provider createProvider(Provider provider);
    Provider updateProvider(int id, Provider provider);
    void deleteProvider(int id);
    List<Booking> getProviderBookings(int id);
    List<Review> getProviderReviews(int id);
    List<Provider> getProvidersByLocationAndService(String location, String service);
    Double getAverageRating(int id);
    List<String> getProviderComments(int id);
}
