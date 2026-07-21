package com.leave_service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ActivateLeaveType {

    @NotBlank
    private boolean isActive;
}
