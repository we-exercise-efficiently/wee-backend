package com.wee.demo.repository;

import com.wee.demo.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByUserId(Long userId);
    Optional<User> findByNickname(String nickname);
    @Transactional
    void deleteByUserId(Long userId);
}

