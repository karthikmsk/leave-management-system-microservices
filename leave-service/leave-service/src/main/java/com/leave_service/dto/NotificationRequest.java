package com.leave_service.dto;

import com.leave_service.model.NotificationType;

import lombok.Data;

@Data
public class NotificationRequest {

    private Long employeeId;

    private Long leaveId;

    private NotificationType type;

}
