package com.leave_service.dto;

import lombok.Data;

@Data
public class UserResponse {
 private Long employeeId;

    private String name;

    private String email;

    private Long managerId;

    private String phoneNumber;

}
