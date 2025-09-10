package com.traffic.difflight.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "parameter_sets", 
       uniqueConstraints = @UniqueConstraint(columnNames = {
           "trafficFile", "roadnetFile", "numIntersections", "missingPattern", "missingRate"
       }))
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class ParameterSet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String trafficFile;

    @Column(nullable = false)
    private String roadnetFile;

    @Column(nullable = false)
    private Integer numIntersections;

    @Column(nullable = false)
    private String missingPattern;

    @Column(nullable = false)
    private String missingRate;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}