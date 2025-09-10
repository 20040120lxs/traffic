package com.traffic.difflight.repository;

import com.traffic.difflight.entity.DatasetDownload;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DatasetDownloadRepository extends JpaRepository<DatasetDownload, Long> {
}