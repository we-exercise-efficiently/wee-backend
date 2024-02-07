package com.wee.demo.repository;

<<<<<<< HEAD
import com.wee.demo.domain.Todo;
import com.wee.demo.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
=======
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

>>>>>>> 12eb692bfdcbdd5802e67bbb6eb01b91b6f93b77
