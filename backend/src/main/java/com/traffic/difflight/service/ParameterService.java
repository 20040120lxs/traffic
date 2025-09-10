package com.traffic.difflight.service;

import com.traffic.difflight.dto.ApiResponse;
import com.traffic.difflight.dto.ParameterQueryRequest;
import com.traffic.difflight.dto.ParameterQueryResponse;
import com.traffic.difflight.entity.ParamOption;
import com.traffic.difflight.entity.ParameterSet;
import com.traffic.difflight.entity.Result;
import com.traffic.difflight.repository.ParamOptionRepository;
import com.traffic.difflight.repository.ParameterSetRepository;
import com.traffic.difflight.repository.ResultRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class ParameterService {
    
    private final ParamOptionRepository paramOptionRepository;
    private final ParameterSetRepository parameterSetRepository;
    private final ResultRepository resultRepository;
    
    private final String uploadDir = "uploads";

    public ParameterService(ParamOptionRepository paramOptionRepository,
                           ParameterSetRepository parameterSetRepository,
                           ResultRepository resultRepository) {
        this.paramOptionRepository = paramOptionRepository;
        this.parameterSetRepository = parameterSetRepository;
        this.resultRepository = resultRepository;
    }

    public ApiResponse<Map<String, List<String>>> getParameterOptions() {
        List<ParamOption> allOptions = paramOptionRepository.findAll();
        Map<String, List<String>> optionsMap = allOptions.stream()
                .collect(Collectors.groupingBy(
                        ParamOption::getParamKey,
                        Collectors.mapping(ParamOption::getParamValue, Collectors.toList())
                ));
        return ApiResponse.success(optionsMap);
    }

    public ApiResponse<ParameterQueryResponse> queryResults(ParameterQueryRequest request) {
        Optional<ParameterSet> parameterSetOpt = parameterSetRepository
                .findByTrafficFileAndRoadnetFileAndNumIntersectionsAndMissingPatternAndMissingRate(
                        request.getTrafficFile(),
                        request.getRoadnetFile(),
                        request.getNumIntersections(),
                        request.getMissingPattern(),
                        request.getMissingRate()
                );

        ParameterQueryResponse response = new ParameterQueryResponse();
        response.setParameters(request);
        response.setImages(new ArrayList<>());

        if (parameterSetOpt.isPresent()) {
            Optional<Result> resultOpt = resultRepository.findByParameterSetId(parameterSetOpt.get().getId());
            if (resultOpt.isPresent()) {
                Result result = resultOpt.get();
                List<ParameterQueryResponse.ResultImage> images = new ArrayList<>();
                
                if (result.getImputationImgPath() != null) {
                    images.add(new ParameterQueryResponse.ResultImage(
                            "imputation", 
                            "/uploads/results/" + result.getImputationImgPath(),
                            result.getTitleImputation()
                    ));
                }
                if (result.getNoiseImgPath() != null) {
                    images.add(new ParameterQueryResponse.ResultImage(
                            "noise", 
                            "/uploads/results/" + result.getNoiseImgPath(),
                            result.getTitleNoise()
                    ));
                }
                if (result.getDenoiseImgPath() != null) {
                    images.add(new ParameterQueryResponse.ResultImage(
                            "denoise", 
                            "/uploads/results/" + result.getDenoiseImgPath(),
                            result.getTitleDenoise()
                    ));
                }
                
                response.setImages(images);
            }
        }

        return ApiResponse.success(response);
    }

    public ApiResponse<String> addParameterOption(String paramKey, String paramValue) {
        ParamOption option = new ParamOption();
        option.setParamKey(paramKey);
        option.setParamValue(paramValue);
        option.setUpdatedAt(LocalDateTime.now());
        
        paramOptionRepository.save(option);
        return ApiResponse.success("参数选项添加成功");
    }

    public ApiResponse<String> deleteParameterOption(String paramKey, String paramValue) {
        paramOptionRepository.deleteByParamKeyAndParamValue(paramKey, paramValue);
        return ApiResponse.success("参数选项删除成功");
    }

    public ApiResponse<String> uploadResultImages(ParameterQueryRequest parameters,
                                                 MultipartFile imputationImg,
                                                 MultipartFile noiseImg,
                                                 MultipartFile denoiseImg,
                                                 String titleImputation,
                                                 String titleNoise,
                                                 String titleDenoise) {
        try {
            // Find or create parameter set
            Optional<ParameterSet> parameterSetOpt = parameterSetRepository
                    .findByTrafficFileAndRoadnetFileAndNumIntersectionsAndMissingPatternAndMissingRate(
                            parameters.getTrafficFile(),
                            parameters.getRoadnetFile(),
                            parameters.getNumIntersections(),
                            parameters.getMissingPattern(),
                            parameters.getMissingRate()
                    );

            ParameterSet parameterSet;
            if (parameterSetOpt.isPresent()) {
                parameterSet = parameterSetOpt.get();
            } else {
                parameterSet = new ParameterSet();
                parameterSet.setTrafficFile(parameters.getTrafficFile());
                parameterSet.setRoadnetFile(parameters.getRoadnetFile());
                parameterSet.setNumIntersections(parameters.getNumIntersections());
                parameterSet.setMissingPattern(parameters.getMissingPattern());
                parameterSet.setMissingRate(parameters.getMissingRate());
                parameterSet.setUpdatedAt(LocalDateTime.now());
                parameterSet = parameterSetRepository.save(parameterSet);
            }

            // Upload images
            Path resultsPath = Paths.get(uploadDir, "results");
            Files.createDirectories(resultsPath);

            Result result = resultRepository.findByParameterSetId(parameterSet.getId())
                    .orElse(new Result());
            result.setParameterSetId(parameterSet.getId());

            if (imputationImg != null && !imputationImg.isEmpty()) {
                String filename = saveImage(imputationImg, resultsPath);
                result.setImputationImgPath(filename);
                result.setTitleImputation(titleImputation);
            }

            if (noiseImg != null && !noiseImg.isEmpty()) {
                String filename = saveImage(noiseImg, resultsPath);
                result.setNoiseImgPath(filename);
                result.setTitleNoise(titleNoise);
            }

            if (denoiseImg != null && !denoiseImg.isEmpty()) {
                String filename = saveImage(denoiseImg, resultsPath);
                result.setDenoiseImgPath(filename);
                result.setTitleDenoise(titleDenoise);
            }

            result.setUpdatedAt(LocalDateTime.now());
            resultRepository.save(result);

            return ApiResponse.success("结果图片上传成功");

        } catch (IOException e) {
            return ApiResponse.error("图片上传失败: " + e.getMessage());
        }
    }

    private String saveImage(MultipartFile file, Path directory) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String storedFilename = UUID.randomUUID().toString() + fileExtension;
        
        Path filePath = directory.resolve(storedFilename);
        Files.copy(file.getInputStream(), filePath);
        
        return storedFilename;
    }

    public ApiResponse<List<Result>> getAllResults() {
        List<Result> results = resultRepository.findAll();
        return ApiResponse.success(results);
    }

    public ApiResponse<String> deleteResult(Long resultId) {
        Result result = resultRepository.findById(resultId).orElse(null);
        if (result == null) {
            return ApiResponse.error("结果不存在");
        }

        try {
            // Delete image files
            Path resultsPath = Paths.get(uploadDir, "results");
            if (result.getImputationImgPath() != null) {
                Files.deleteIfExists(resultsPath.resolve(result.getImputationImgPath()));
            }
            if (result.getNoiseImgPath() != null) {
                Files.deleteIfExists(resultsPath.resolve(result.getNoiseImgPath()));
            }
            if (result.getDenoiseImgPath() != null) {
                Files.deleteIfExists(resultsPath.resolve(result.getDenoiseImgPath()));
            }

            resultRepository.delete(result);
            return ApiResponse.success("结果删除成功");

        } catch (IOException e) {
            return ApiResponse.error("文件删除失败: " + e.getMessage());
        }
    }
}