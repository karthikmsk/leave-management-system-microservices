package com.leave_service.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class LeaveResponse {

    private Long id;
    private Long employeeId;
    private String leaveType;
    private LocalDate startDate;
    private LocalDate endDate;
    private String leaveStatus;
    private String description;
    private Long approverId;
    private float totalDays;
}
