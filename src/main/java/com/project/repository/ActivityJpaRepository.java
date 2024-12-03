package com.project.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.module.Activity;

@Repository
public interface ActivityJpaRepository extends JpaRepository<Activity, Long> {
    List<Activity> findTop10ByOrderByActivityIdDesc(); // Retrieves the 10 latest activities
}
