package com.leave_service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DeActivateLeaveType {

    @NotBlank
    private boolean isActive;
}
