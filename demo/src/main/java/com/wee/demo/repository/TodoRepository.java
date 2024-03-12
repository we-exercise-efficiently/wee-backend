package com.wee.demo.repository;

import com.wee.demo.domain.Todo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.Optional;

public interface TodoRepository extends JpaRepository<Todo, Long> {
    Optional<Todo> findByToday(Date today);
}
