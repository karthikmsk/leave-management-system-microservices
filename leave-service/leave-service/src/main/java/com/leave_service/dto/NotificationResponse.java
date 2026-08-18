package com.leave_service.dto;

import lombok.Data;

@Data
public class NotificationResponse {
    
    private Long employeeId;

    private Long leaveId;

    private String title;

    private String message;
}
