package com.traffic.difflight.controller;

import com.traffic.difflight.dto.ApiResponse;
import com.traffic.difflight.dto.DatasetResponse;
import com.traffic.difflight.entity.Dataset;
import com.traffic.difflight.service.DatasetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/datasets")
@Tag(name = "数据集管理", description = "用户数据集上传、下载、管理")
public class DatasetController {
    
    private final DatasetService datasetService;

    public DatasetController(DatasetService datasetService) {
        this.datasetService = datasetService;
    }

    @PostMapping("/upload")
    @Operation(summary = "上传数据集")
    public ApiResponse<String> uploadDataset(Authentication authentication,
                                           @RequestParam("file") MultipartFile file,
                                           @RequestParam(value = "description", required = false) String description) {
        String username = authentication.getName();
        return datasetService.uploadDataset(username, file, description, Dataset.OwnerRole.USER);
    }

    @GetMapping("/my")
    @Operation(summary = "获取我的数据集")
    public ApiResponse<List<DatasetResponse>> getMyDatasets(Authentication authentication) {
        String username = authentication.getName();
        return datasetService.getMyDatasets(username);
    }

    @GetMapping("/shared")
    @Operation(summary = "获取共享数据集")
    public ApiResponse<List<DatasetResponse>> getSharedDatasets() {
        return datasetService.getSharedDatasets();
    }

    @GetMapping("/{id}/download")
    @Operation(summary = "下载数据集")
    public ApiResponse<String> downloadDataset(@PathVariable Long id, Authentication authentication) {
        String username = authentication.getName();
        return datasetService.downloadDataset(id, username);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除数据集")
    public ApiResponse<String> deleteDataset(@PathVariable Long id, Authentication authentication) {
        String username = authentication.getName();
        return datasetService.deleteDataset(id, username, false);
    }
}