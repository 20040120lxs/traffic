package com.traffic.difflight.controller;

import com.traffic.difflight.dto.ApiResponse;
import com.traffic.difflight.dto.DatasetResponse;
import com.traffic.difflight.dto.ParameterQueryRequest;
import com.traffic.difflight.entity.Dataset;
import com.traffic.difflight.entity.Result;
import com.traffic.difflight.service.DatasetService;
import com.traffic.difflight.service.ParameterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@Tag(name = "管理员功能", description = "管理员数据集、参数、结果管理")
public class AdminController {
    
    private final DatasetService datasetService;
    private final ParameterService parameterService;

    public AdminController(DatasetService datasetService, ParameterService parameterService) {
        this.datasetService = datasetService;
        this.parameterService = parameterService;
    }

    // 数据集管理
    @GetMapping("/datasets")
    @Operation(summary = "获取所有数据集")
    public ApiResponse<List<DatasetResponse>> getAllDatasets() {
        return datasetService.getAllDatasets();
    }

    @PostMapping("/datasets/upload")
    @Operation(summary = "管理员上传数据集")
    public ApiResponse<String> uploadDataset(Authentication authentication,
                                           @RequestParam("file") MultipartFile file,
                                           @RequestParam(value = "description", required = false) String description) {
        String username = authentication.getName();
        return datasetService.uploadDataset(username, file, description, Dataset.OwnerRole.ADMIN);
    }

    @DeleteMapping("/datasets/{id}")
    @Operation(summary = "删除任意数据集")
    public ApiResponse<String> deleteDataset(@PathVariable Long id, Authentication authentication) {
        String username = authentication.getName();
        return datasetService.deleteDataset(id, username, true);
    }

    // 参数管理
    @PostMapping("/params/options")
    @Operation(summary = "添加参数选项")
    public ApiResponse<String> addParameterOption(@RequestParam String paramKey, 
                                                 @RequestParam String paramValue) {
        return parameterService.addParameterOption(paramKey, paramValue);
    }

    @DeleteMapping("/params/options")
    @Operation(summary = "删除参数选项")
    public ApiResponse<String> deleteParameterOption(@RequestParam String paramKey, 
                                                    @RequestParam String paramValue) {
        return parameterService.deleteParameterOption(paramKey, paramValue);
    }

    // 结果管理
    @PostMapping("/results")
    @Operation(summary = "上传结果图片")
    public ApiResponse<String> uploadResults(@RequestParam String trafficFile,
                                           @RequestParam String roadnetFile,
                                           @RequestParam Integer numIntersections,
                                           @RequestParam String missingPattern,
                                           @RequestParam String missingRate,
                                           @RequestParam(required = false) MultipartFile imputationImg,
                                           @RequestParam(required = false) MultipartFile noiseImg,
                                           @RequestParam(required = false) MultipartFile denoiseImg,
                                           @RequestParam(required = false) String titleImputation,
                                           @RequestParam(required = false) String titleNoise,
                                           @RequestParam(required = false) String titleDenoise) {
        ParameterQueryRequest parameters = new ParameterQueryRequest();
        parameters.setTrafficFile(trafficFile);
        parameters.setRoadnetFile(roadnetFile);
        parameters.setNumIntersections(numIntersections);
        parameters.setMissingPattern(missingPattern);
        parameters.setMissingRate(missingRate);
        
        return parameterService.uploadResultImages(parameters, imputationImg, noiseImg, denoiseImg,
                titleImputation, titleNoise, titleDenoise);
    }

    @GetMapping("/results")
    @Operation(summary = "获取所有结果")
    public ApiResponse<List<Result>> getAllResults() {
        return parameterService.getAllResults();
    }

    @DeleteMapping("/results/{id}")
    @Operation(summary = "删除结果")
    public ApiResponse<String> deleteResult(@PathVariable Long id) {
        return parameterService.deleteResult(id);
    }
}