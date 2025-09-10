package com.traffic.difflight.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ParameterQueryResponse {
    private ParameterQueryRequest parameters;
    private List<ResultImage> images;
    
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ResultImage {
        private String type; // "imputation", "noise", "denoise"
        private String path;
        private String title;
    }
}