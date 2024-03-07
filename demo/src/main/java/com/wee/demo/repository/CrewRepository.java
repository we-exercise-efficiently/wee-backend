package com.wee.demo.repository;

import com.wee.demo.domain.community.CrewCommunity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CrewRepository extends JpaRepository<CrewCommunity, Long> {
    Optional<CrewCommunity> findById(Long crewId);
}
