package com.leave_service.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.leave_service.dto.UserResponse;

@FeignClient(name = "user-service")  
public interface UserClient {

    @GetMapping("/api/users/employee/{employeeId}")
    UserResponse getUserByEmployeeId(@PathVariable Long employeeId);

    @GetMapping("/api/users/team/{managerId}")
    List<UserResponse> getTeamMembers(@PathVariable Long managerId);

    @GetMapping("/api/users/email/{email}")
    UserResponse getUserByEmail(@PathVariable String email);

}
