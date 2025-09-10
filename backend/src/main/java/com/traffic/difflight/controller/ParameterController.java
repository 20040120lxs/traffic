package com.traffic.difflight.controller;

import com.traffic.difflight.dto.ApiResponse;
import com.traffic.difflight.dto.ParameterQueryRequest;
import com.traffic.difflight.dto.ParameterQueryResponse;
import com.traffic.difflight.service.ParameterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/params")
@Tag(name = "参数管理", description = "参数选项和结果查询")
public class ParameterController {
    
    private final ParameterService parameterService;

    public ParameterController(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    @GetMapping("/options")
    @Operation(summary = "获取参数选项")
    public ApiResponse<Map<String, List<String>>> getParameterOptions() {
        return parameterService.getParameterOptions();
    }

    @PostMapping("/query")
    @Operation(summary = "查询参数结果")
    public ApiResponse<ParameterQueryResponse> queryResults(@RequestBody ParameterQueryRequest request) {
        return parameterService.queryResults(request);
    }
}