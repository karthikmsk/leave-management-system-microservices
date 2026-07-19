package com.user_service.dto;

import com.user_service.model.Department;

import lombok.Data;

@Data
public class UpdateDepartmentRequest {

    private Department department;
    
}
