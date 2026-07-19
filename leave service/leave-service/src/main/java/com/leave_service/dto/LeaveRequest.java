package com.leave_service.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class LeaveRequest {
    private Long employeeId;
    private String leaveType;
    private LocalDate startDate;
    private LocalDate endDate;
    private String description;
}
