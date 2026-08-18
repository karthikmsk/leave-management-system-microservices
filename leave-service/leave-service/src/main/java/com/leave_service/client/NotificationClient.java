package com.leave_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.leave_service.dto.NotificationRequest;
import com.leave_service.dto.NotificationResponse;

@FeignClient(name = "NOTIFICATION-SERVICE")
public interface NotificationClient {

    @PostMapping("api/notifications")
    NotificationResponse createNotification(@RequestBody NotificationRequest request);

}

