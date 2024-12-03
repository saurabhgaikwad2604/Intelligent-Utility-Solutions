package com.project.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.module.Contact;

public interface ContactJpaRepository extends JpaRepository<Contact, Long> {
    List<Contact> findByStatus(String status);
}
