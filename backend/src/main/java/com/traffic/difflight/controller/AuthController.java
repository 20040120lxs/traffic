package com.traffic.difflight.controller;

import com.traffic.difflight.dto.*;
import com.traffic.difflight.entity.User;
import com.traffic.difflight.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "认证管理", description = "用户注册、登录、个人信息管理")
public class AuthController {
    
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    @Operation(summary = "用户注册")
    public ApiResponse<String> register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    @Operation(summary = "用户登录")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @GetMapping("/me")
    @Operation(summary = "获取当前用户信息")
    public ApiResponse<User> getCurrentUser(Authentication authentication) {
        String username = authentication.getName();
        return authService.getCurrentUser(username);
    }

    @PutMapping("/me")
    @Operation(summary = "更新当前用户信息")
    public ApiResponse<String> updateProfile(Authentication authentication, 
                                           @RequestBody UpdateProfileRequest request) {
        String username = authentication.getName();
        return authService.updateProfile(username, request);
    }
}