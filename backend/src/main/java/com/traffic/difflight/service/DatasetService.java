package com.traffic.difflight.service;

import com.traffic.difflight.dto.ApiResponse;
import com.traffic.difflight.dto.DatasetResponse;
import com.traffic.difflight.entity.Dataset;
import com.traffic.difflight.entity.DatasetDownload;
import com.traffic.difflight.repository.DatasetDownloadRepository;
import com.traffic.difflight.repository.DatasetRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class DatasetService {
    
    private final DatasetRepository datasetRepository;
    private final DatasetDownloadRepository downloadRepository;
    
    @Value("${file.upload-dir}")
    private String uploadDir;

    public DatasetService(DatasetRepository datasetRepository, 
                         DatasetDownloadRepository downloadRepository) {
        this.datasetRepository = datasetRepository;
        this.downloadRepository = downloadRepository;
    }

    public ApiResponse<String> uploadDataset(String username, MultipartFile file, String description, Dataset.OwnerRole role) {
        try {
            if (file.isEmpty()) {
                return ApiResponse.error("文件不能为空");
            }
            
            String originalFilename = file.getOriginalFilename();
            String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String storedFilename = UUID.randomUUID().toString() + fileExtension;
            
            Path datasetsPath = Paths.get(uploadDir, "datasets");
            Files.createDirectories(datasetsPath);
            
            Path filePath = datasetsPath.resolve(storedFilename);
            Files.copy(file.getInputStream(), filePath);
            
            Dataset dataset = new Dataset();
            dataset.setOwnerUsername(username);
            dataset.setRole(role);
            dataset.setStoredFilename(storedFilename);
            dataset.setOriginalFilename(originalFilename);
            dataset.setSize(file.getSize());
            dataset.setUploadTime(LocalDateTime.now());
            dataset.setDescription(description);
            
            datasetRepository.save(dataset);
            return ApiResponse.success("文件上传成功");
            
        } catch (IOException e) {
            return ApiResponse.error("文件上传失败: " + e.getMessage());
        }
    }

    public ApiResponse<List<DatasetResponse>> getMyDatasets(String username) {
        List<Dataset> datasets = datasetRepository.findByOwnerUsername(username);
        List<DatasetResponse> responses = datasets.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        return ApiResponse.success(responses);
    }

    public ApiResponse<List<DatasetResponse>> getSharedDatasets() {
        List<Dataset> datasets = datasetRepository.findByRole(Dataset.OwnerRole.ADMIN);
        List<DatasetResponse> responses = datasets.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        return ApiResponse.success(responses);
    }

    public ApiResponse<List<DatasetResponse>> getAllDatasets() {
        List<Dataset> datasets = datasetRepository.findAll();
        List<DatasetResponse> responses = datasets.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        return ApiResponse.success(responses);
    }

    public ApiResponse<String> downloadDataset(Long datasetId, String username) {
        Dataset dataset = datasetRepository.findById(datasetId).orElse(null);
        if (dataset == null) {
            return ApiResponse.error("数据集不存在");
        }
        
        // Record download
        DatasetDownload download = new DatasetDownload();
        download.setUsername(username);
        download.setDatasetId(datasetId);
        download.setDownloadTime(LocalDateTime.now());
        downloadRepository.save(download);
        
        String downloadUrl = "/uploads/datasets/" + dataset.getStoredFilename();
        return ApiResponse.success("获取下载链接成功", downloadUrl);
    }

    public ApiResponse<String> deleteDataset(Long datasetId, String username, boolean isAdmin) {
        Dataset dataset = datasetRepository.findById(datasetId).orElse(null);
        if (dataset == null) {
            return ApiResponse.error("数据集不存在");
        }
        
        if (!isAdmin && !dataset.getOwnerUsername().equals(username)) {
            return ApiResponse.error("无权限删除此数据集");
        }
        
        try {
            Path filePath = Paths.get(uploadDir, "datasets", dataset.getStoredFilename());
            Files.deleteIfExists(filePath);
            datasetRepository.delete(dataset);
            return ApiResponse.success("数据集删除成功");
        } catch (IOException e) {
            return ApiResponse.error("文件删除失败: " + e.getMessage());
        }
    }

    private DatasetResponse convertToResponse(Dataset dataset) {
        return new DatasetResponse(
                dataset.getId(),
                dataset.getOwnerUsername(),
                dataset.getRole().name(),
                dataset.getOriginalFilename(),
                dataset.getSize(),
                dataset.getUploadTime(),
                dataset.getDescription()
        );
    }
}