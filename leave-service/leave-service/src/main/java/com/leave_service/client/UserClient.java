package com.leave_service.client;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service")  
public interface UserClient {

    @GetMapping("/api/users/{userId}")
    Map<String, Object> getUserById(@PathVariable("userId") Long userId);
}
