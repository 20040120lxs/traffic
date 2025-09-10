package com.traffic.difflight.service;

import com.traffic.difflight.dto.*;
import com.traffic.difflight.entity.Admin;
import com.traffic.difflight.entity.User;
import com.traffic.difflight.repository.AdminRepository;
import com.traffic.difflight.repository.UserRepository;
import com.traffic.difflight.security.JwtTokenProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AuthService {
    
    private final UserRepository userRepository;
    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthService(UserRepository userRepository, AdminRepository adminRepository,
                      PasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.adminRepository = adminRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public ApiResponse<String> register(RegisterRequest request) {
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            return ApiResponse.error("密码和确认密码不一致");
        }
        
        if (userRepository.existsByUsername(request.getUsername())) {
            return ApiResponse.error("用户名已存在");
        }
        
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setPhone(request.getPhone());
        
        userRepository.save(user);
        return ApiResponse.success("注册成功");
    }

    public ApiResponse<LoginResponse> login(LoginRequest request) {
        if (request.isAdmin()) {
            return adminLogin(request);
        } else {
            return userLogin(request);
        }
    }

    private ApiResponse<LoginResponse> userLogin(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElse(null);
        
        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            return ApiResponse.error("用户名或密码错误");
        }
        
        String token = jwtTokenProvider.generateToken(user.getUsername(), "USER");
        LoginResponse response = new LoginResponse(token, user.getUsername(), "USER", user.getPhone());
        return ApiResponse.success(response);
    }

    private ApiResponse<LoginResponse> adminLogin(LoginRequest request) {
        Admin admin = adminRepository.findByUsername(request.getUsername())
                .orElse(null);
        
        if (admin == null || !passwordEncoder.matches(request.getPassword(), admin.getPasswordHash())) {
            return ApiResponse.error("管理员用户名或密码错误");
        }
        
        String token = jwtTokenProvider.generateToken(admin.getUsername(), "ADMIN");
        LoginResponse response = new LoginResponse(token, admin.getUsername(), "ADMIN", admin.getPhone());
        return ApiResponse.success(response);
    }

    public ApiResponse<User> getCurrentUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElse(null);
        if (user == null) {
            return ApiResponse.error("用户不存在");
        }
        return ApiResponse.success(user);
    }

    public ApiResponse<String> updateProfile(String username, UpdateProfileRequest request) {
        User user = userRepository.findByUsername(username)
                .orElse(null);
        if (user == null) {
            return ApiResponse.error("用户不存在");
        }

        if (request.getUsername() != null && !request.getUsername().equals(username)) {
            if (userRepository.existsByUsername(request.getUsername())) {
                return ApiResponse.error("新用户名已存在");
            }
            user.setUsername(request.getUsername());
        }

        if (request.getNewPassword() != null && !request.getNewPassword().isEmpty()) {
            if (request.getOldPassword() == null || 
                !passwordEncoder.matches(request.getOldPassword(), user.getPasswordHash())) {
                return ApiResponse.error("原密码错误");
            }
            user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        }

        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }

        userRepository.save(user);
        return ApiResponse.success("个人信息更新成功");
    }
}