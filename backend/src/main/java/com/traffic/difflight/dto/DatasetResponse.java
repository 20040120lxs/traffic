package com.traffic.difflight.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DatasetResponse {
    private Long id;
    private String ownerUsername;
    private String role;
    private String originalFilename;
    private Long size;
    private LocalDateTime uploadTime;
    private String description;
}