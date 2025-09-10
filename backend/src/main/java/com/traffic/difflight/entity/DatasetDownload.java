package com.traffic.difflight.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "dataset_downloads")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DatasetDownload {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private Long datasetId;

    @Column(nullable = false)
    private LocalDateTime downloadTime;
}