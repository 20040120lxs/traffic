package com.traffic.difflight.dto;

import lombok.Data;

@Data
public class ParameterQueryRequest {
    private String trafficFile;
    private String roadnetFile;
    private Integer numIntersections;
    private String missingPattern;
    private String missingRate;
}