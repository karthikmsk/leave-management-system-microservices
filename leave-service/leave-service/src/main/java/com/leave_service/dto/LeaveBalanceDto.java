package com.leave_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LeaveBalanceDto {
    
    @NotNull
    private Long employeeId;

    @NotBlank
    private String employeeName;

    @NotBlank
    private Long leaveTypeId;

    @NotBlank
    private String leaveTypeName;

    private Float remainingDays;

    private Float usedDays;

    @NotNull
    private Float creditedDays;

}
