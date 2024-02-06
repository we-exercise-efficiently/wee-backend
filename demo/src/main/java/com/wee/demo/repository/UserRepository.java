package com.wee.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.wee.demo.domain.User;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByUserId(Long userId);
    @Transactional
    void deleteByUserId(Long userId);
}

