package com.leave_service.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.leave_service.model.LeaveStatus;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LeaveResponseDto {

    @NotNull
    private Long employeeId;

    @NotBlank
    private String employeeName;

    @NotBlank
    private String leaveType;

    @NotNull
    private LocalDate startDate;

    @NotNull
    private LocalDate endDate;

    @NotNull
    private Float numberOfDays;

    @NotBlank
    private String reason;

    @NotNull
    private LeaveStatus leaveStatus;

    @NotBlank
    private String managerComment;

    private Long approverId;

    private LocalDateTime appliedAt;

}
