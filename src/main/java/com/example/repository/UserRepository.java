package com.example.repository;

import com.example.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    // Find user by email
    Optional<User> findByEmail(String email);

    // Check if a user exists with the given email
    boolean existsByEmail(String email);

    // Find user by username
    Optional<User> findByUserName(String userName);

    // Check if a user exists with the given username
    boolean existsByUserName(String userName);
}