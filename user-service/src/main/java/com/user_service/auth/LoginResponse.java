package com.user_service.auth;

import com.user_service.model.Role;

import lombok.Data;

@Data
public class LoginResponse {
    private Long employeeId;
    private String name;
    private String email;
    private Role role;
    private String message;
    private String token;
}
