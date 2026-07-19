package com.user_service.dto;

import java.time.LocalDate;
import com.user_service.model.Department;
import com.user_service.model.Role;
import com.user_service.model.UserStatus;
import lombok.Data;

@Data
public class UserResponse {

    private Long employeeId;

    private String name;

    private String email;

    private Role role;

    private Long managerId;

    private String phoneNumber;

    private Department department;

    private UserStatus status;

    private LocalDate joiningDate;

}
