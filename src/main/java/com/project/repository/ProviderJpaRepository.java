package com.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

import com.project.module.Provider;

@Repository
public interface ProviderJpaRepository extends JpaRepository<Provider, Integer> {
    Provider findByEmail(String em);
    List<Provider> findByLocationAndService(String location, String service);
    @Query("SELECT COUNT(*) FROM Provider WHERE status = 'Active'")
    int findCountByStatus(String status);
}
