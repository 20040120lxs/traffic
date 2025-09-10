package com.traffic.difflight.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "results")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Result {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long parameterSetId;

    @Column
    private String imputationImgPath;

    @Column
    private String noiseImgPath;

    @Column
    private String denoiseImgPath;

    @Column
    private String titleImputation;

    @Column
    private String titleNoise;

    @Column
    private String titleDenoise;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}