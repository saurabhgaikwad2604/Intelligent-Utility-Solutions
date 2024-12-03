package com.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.module.User;

@Repository
public interface UserJpaRepository extends JpaRepository<User, Integer> {
    User findByEmail(String em);
}
