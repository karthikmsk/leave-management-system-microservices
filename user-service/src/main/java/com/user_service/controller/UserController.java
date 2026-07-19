package com.user_service.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.user_service.dto.PasswordChangeRequest;
import com.user_service.dto.UpdateProfileRequest;
import com.user_service.dto.UpdateDepartmentRequest;
import com.user_service.dto.UpdateManagerRequest;
import com.user_service.dto.UpdateRole;
import com.user_service.dto.UserRequest;
import com.user_service.dto.UserResponse;
import com.user_service.dto.UserStatusRequest;
import com.user_service.model.Department;
import com.user_service.model.Role;
import com.user_service.model.UserStatus;
import com.user_service.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long userId) {
        UserResponse userResponse = userService.getUserById(userId);
        return ResponseEntity.ok(userResponse);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<UserResponse>> getUsersByName(@RequestParam String name, @PageableDefault(size = 10, sort = "employeeId") Pageable pageable) {
        return ResponseEntity.ok(userService.getUsersByName(name,pageable));
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<UserResponse> getUserByEmployeeId(@PathVariable Long employeeId) {
        UserResponse userResponse = userService.getUserByEmployeeId(employeeId);
        return ResponseEntity.ok(userResponse);
    }

    @GetMapping("/email")
    public ResponseEntity<UserResponse> getUserByEmail(@RequestParam String email) {
        UserResponse userResponse = userService.getUserByEmail(email);
        return ResponseEntity.ok(userResponse);
    }

    @GetMapping("/department/{department}")
    public ResponseEntity<Page<UserResponse>> getUsersByDepartment(@PathVariable Department department,
            @PageableDefault(size = 10, sort = "employeeId") Pageable pageable) {
        return ResponseEntity.ok(userService.getUsersByDepartment(department, pageable));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<Page<UserResponse>> getUsersByStatus(@PathVariable UserStatus status,
            @PageableDefault(size = 10, sort = "employeeId") Pageable pageable) {
        return ResponseEntity.ok(userService.getUsersByStatus(status, pageable));
    }

    @GetMapping("/manager/{managerId}")
    public ResponseEntity<Page<UserResponse>> getTeamMembers(@PathVariable Long managerId,
            @PageableDefault(size = 10, sort = "employeeId") Pageable pageable) {
        return ResponseEntity.ok(userService.getTeamMembers(managerId, pageable));
    }

    @GetMapping("/role/{role}")
    public ResponseEntity<Page<UserResponse>> getUsersByRole(@PathVariable Role role,
            @PageableDefault(size = 10, sort = "employeeId") Pageable pageable) {
        return ResponseEntity.ok(userService.getUsersByRole(role, pageable));
    }

    @GetMapping
    public ResponseEntity<Page<UserResponse>> getAllUsers(
            @PageableDefault(size = 10, sort = "employeeId") Pageable pageable) {
        return ResponseEntity.ok(userService.getAllUsers(pageable));
    }

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserRequest userRequest) {
        UserResponse createdUser = userService.createUser(userRequest);
        return ResponseEntity.ok(createdUser);
    }

    @PatchMapping("/change-password")
    public ResponseEntity<String> changePassword(@Valid @RequestBody PasswordChangeRequest request) {
        return ResponseEntity.ok(userService.changePassword(request));
    }

    @PatchMapping("/profile")
    public ResponseEntity<String> updateProfile(@Valid @RequestBody UpdateProfileRequest profile) {
        return ResponseEntity.ok(userService.changeProfile(profile));
    }

    @PatchMapping("/{employeeId}/status")
    public ResponseEntity<UserResponse> updateStatus(@PathVariable Long employeeId,
            @Valid @RequestBody UserStatusRequest statusRequest) {
        return ResponseEntity.ok(userService.updateUserStatus(employeeId, statusRequest));
    }

    @PatchMapping("/{employeeId}/manager")
    public ResponseEntity<UserResponse> assignManager(@PathVariable Long employeeId,
            @Valid @RequestBody UpdateManagerRequest request) {
        return ResponseEntity.ok(userService.assignManager(employeeId, request));
    }

    @PatchMapping("/{employeeId}/role")
    public ResponseEntity<UserResponse> changeRole(@PathVariable Long employeeId,
            @Valid @RequestBody UpdateRole request) {
        return ResponseEntity.ok(userService.changeRole(employeeId, request));
    }

    @PatchMapping("/{employeeId}/department")
    public ResponseEntity<UserResponse> changeDepartment(@PathVariable Long employeeId,
            @Valid @RequestBody UpdateDepartmentRequest request) {
        return ResponseEntity.ok(userService.changeDepartment(employeeId, request));
    }

}
