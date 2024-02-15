package com.wee.demo.repository;

import com.wee.demo.domain.User;
import com.wee.demo.domain.community.QuestionCommunity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface QuestionRepository extends JpaRepository<QuestionCommunity, Long> {
    Optional<QuestionCommunity> findById(Long questionId);
}
