package com.traffic.difflight.repository;

import com.traffic.difflight.entity.ParameterSet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ParameterSetRepository extends JpaRepository<ParameterSet, Long> {
    Optional<ParameterSet> findByTrafficFileAndRoadnetFileAndNumIntersectionsAndMissingPatternAndMissingRate(
        String trafficFile, String roadnetFile, Integer numIntersections, String missingPattern, String missingRate);
}