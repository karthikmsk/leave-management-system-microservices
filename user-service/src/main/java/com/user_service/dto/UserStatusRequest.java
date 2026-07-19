package com.user_service.dto;

import com.user_service.model.UserStatus;

import lombok.Data;

@Data
public class UserStatusRequest {

    private UserStatus status;
    
}
