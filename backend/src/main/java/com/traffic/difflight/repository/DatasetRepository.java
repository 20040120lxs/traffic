package com.traffic.difflight.repository;

import com.traffic.difflight.entity.Dataset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DatasetRepository extends JpaRepository<Dataset, Long> {
    List<Dataset> findByOwnerUsername(String ownerUsername);
    List<Dataset> findByRole(Dataset.OwnerRole role);
}