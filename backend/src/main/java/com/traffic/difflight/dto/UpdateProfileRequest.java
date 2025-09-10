package com.traffic.difflight.dto;

import lombok.Data;

@Data
public class UpdateProfileRequest {
    private String username;
    private String oldPassword;
    private String newPassword;
    private String phone;
}