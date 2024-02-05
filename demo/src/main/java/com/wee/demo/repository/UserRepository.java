package com.wee.demo.repository;

import com.wee.demo.domain.Todo;
import com.wee.demo.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
