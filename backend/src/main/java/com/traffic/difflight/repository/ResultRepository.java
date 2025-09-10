package com.traffic.difflight.repository;

import com.traffic.difflight.entity.Result;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ResultRepository extends JpaRepository<Result, Long> {
    Optional<Result> findByParameterSetId(Long parameterSetId);
}