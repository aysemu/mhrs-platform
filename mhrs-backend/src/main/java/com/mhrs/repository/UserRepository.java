package com.mhrs.repository;

import com.mhrs.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);

    Optional<User> findByTcknHash(String tcknHash);

    boolean existsByEmail(String email);

    boolean existsByTcknHash(String tcknHash);
}