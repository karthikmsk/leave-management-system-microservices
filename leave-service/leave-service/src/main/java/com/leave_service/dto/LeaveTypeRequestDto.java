package com.leave_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LeaveTypeRequestDto {

    @NotBlank
    private String name;

    @NotBlank
    private String description;

    @NotNull
    private Float annualAllocation;

    private Boolean carryForwardAllowed;

    private Float maxCarryForwardDays;

}
