package com.traffic.difflight.controller;

import com.traffic.difflight.dto.ApiResponse;
import com.traffic.difflight.dto.LoginRequest;
import com.traffic.difflight.dto.LoginResponse;
import com.traffic.difflight.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@Tag(name = "管理员认证", description = "管理员登录")
public class AdminAuthController {
    
    private final AuthService authService;

    public AdminAuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    @Operation(summary = "管理员登录")
    public ApiResponse<LoginResponse> adminLogin(@Valid @RequestBody LoginRequest request) {
        request.setAdmin(true);
        return authService.login(request);
    }
}