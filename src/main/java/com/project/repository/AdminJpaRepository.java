package com.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.module.Admin;


@Repository
public interface AdminJpaRepository extends JpaRepository<Admin, Integer> {
    Admin findByEmail(String em);
}
