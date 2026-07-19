package com.user_service.service;

import org.springframework.security.core.Authentication;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.user_service.dto.PasswordChangeRequest;
import com.user_service.dto.UpdateProfileRequest;
import com.user_service.dto.UpdateDepartmentRequest;
import com.user_service.dto.UpdateManagerRequest;
import com.user_service.dto.UpdateRole;
import com.user_service.dto.UserRequest;
import com.user_service.dto.UserResponse;
import com.user_service.dto.UserStatusRequest;
import com.user_service.exception.DuplicateEmailException;
import com.user_service.exception.InvalidPasswordException;
import com.user_service.exception.ManagerNotFoundException;
import com.user_service.exception.PasswordMismatchException;
import com.user_service.exception.UserNotFoundException;
import com.user_service.exception.UserNotManagerException;
import com.user_service.mapper.UserMapper;
import com.user_service.model.Department;
import com.user_service.model.Role;
import com.user_service.model.User;
import com.user_service.model.UserStatus;
import com.user_service.repository.UserRepository;
import com.user_service.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    private UserResponse mapToResponse(User user) {
        return userMapper.toUserResponse(user);
    }

    private void validateDuplicateEmail(UserRequest userRequest) {
        if (userRepository.existsByEmail(userRequest.getEmail())) {
            throw new DuplicateEmailException("Email already exists");
        }
    }

    private void validateManager(UserRequest userRequest) {
        if (userRequest.getManagerId() != null) {
            User manager = userRepository.findByEmployeeId(userRequest.getManagerId())
                    .orElseThrow(() -> new ManagerNotFoundException("Manager not found"));
            if (manager.getRole() != Role.MANAGER) {
                throw new UserNotManagerException("User is not a manager");
            }
        }
    }

    private Long generateEmployeeId() {
        Long lastId = userRepository.findHighestEmployeeId();
        if (lastId == null) {
            return 1001L;
        } else
            return lastId + 1;
    }

    private User getLoggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails user = (CustomUserDetails) authentication.getPrincipal();
        return userRepository.findByEmployeeId(user.getEmployeeId())
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    public Page<UserResponse> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(userMapper::toUserResponse);
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    public UserResponse getUserById(Long userId) {
        return mapToResponse(userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found")));
    }

    @PreAuthorize("hasAnyRole('ADMIN','HR','MANAGER')")
    public Page<UserResponse> getUsersByName(String name, Pageable pageable) {
        return userRepository.findByNameContainingIgnoreCase(name, pageable).map(userMapper::toUserResponse);

    }

    @PreAuthorize("hasAnyRole('ADMIN','HR','MANAGER','EMPLOYEE','EXTERNAL')")
    public UserResponse getUserByEmail(String email) {
        return mapToResponse(userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found")));

    }

    @PreAuthorize("hasAnyRole('ADMIN','HR') || #employeeId == authentication.principal.employeeId")
    public UserResponse getUserByEmployeeId(Long employeeId) {
        return mapToResponse(userRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new UserNotFoundException("User not found")));
    }

    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    public Page<UserResponse> getUsersByDepartment(Department department, Pageable pageable) {
        return userRepository.findByDepartment(department, pageable).map(userMapper::toUserResponse);
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    public Page<UserResponse> getUsersByStatus(UserStatus status, Pageable pageable) {
        return userRepository.findByStatus(status, pageable).map(userMapper::toUserResponse);
    }

    @PreAuthorize("hasAnyRole('ADMIN','HR','MANAGER')")
    public Page<UserResponse> getUsersByRole(Role role, Pageable pageable) {
        return userRepository.findByRole(role, pageable).map(userMapper::toUserResponse);
    }

    @PreAuthorize("hasAnyRole('ADMIN','HR','MANAGER','EMPLOYEE','EXTERNAL')")
    public Page<UserResponse> getTeamMembers(Long managerId, Pageable pageable) {
        return userRepository.findByManagerId(managerId, pageable).map(userMapper::toUserResponse);
    }

    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    public UserResponse createUser(UserRequest userRequest) {
        validateDuplicateEmail(userRequest);
        User user = userMapper.toUser(userRequest);
        user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        user.setEmployeeId(generateEmployeeId());
        if (userRequest.getRole() != Role.ADMIN) {
            validateManager(userRequest);
        }
        user.setDepartment(userRequest.getDepartment());
        user.setPhoneNumber(userRequest.getPhoneNumber());
        user.setJoiningDate(userRequest.getJoiningDate());
        User savedUser = userRepository.save(user);
        return userMapper.toUserResponse(savedUser);
    }

    @PreAuthorize("isAuthenticated()")
    @Transactional
    public String changePassword(PasswordChangeRequest request) {
        User employee = getLoggedInUser();
        if (!passwordEncoder.matches(request.getCurrentPassword(), employee.getPassword())) {
            throw new InvalidPasswordException("Current password is incorrect");
        }
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new PasswordMismatchException("Password do not match");
        }
        if (passwordEncoder.matches(request.getNewPassword(), employee.getPassword())) {
            throw new IllegalArgumentException("New Password must be different from the current password");
        }

        employee.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(employee);
        return "Password Changed Successfully";

    }

    @PreAuthorize("isAuthenticated()")
    @Transactional
    public String changeProfile(UpdateProfileRequest profile) {
        User employee = getLoggedInUser();
        employee.setName(profile.getName());
        employee.setPhoneNumber(profile.getPhoneNumber());
        userRepository.save(employee);

        return "Profile Updated Successfully";
    }

    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    public UserResponse updateUserStatus(Long employeeId, UserStatusRequest request) {
        User existingUser = userRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        existingUser.setStatus(request.getStatus());
        User updatedUserStatus = userRepository.save(existingUser);
        return userMapper.toUserResponse(updatedUserStatus);
    }

    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    public UserResponse assignManager(Long employeeId, UpdateManagerRequest request) {
        User employee = userRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        User manager = userRepository.findByEmployeeId(request.getManagerId())
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        if (manager.getRole() != Role.MANAGER) {
            throw new UserNotManagerException("Selected user is not a manager");
        }
        employee.setManagerId(request.getManagerId());
        User updatedUser = userRepository.save(employee);
        return userMapper.toUserResponse(updatedUser);
    }

    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    public UserResponse changeDepartment(Long employeeId, UpdateDepartmentRequest request) {
        User existingUser = userRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        existingUser.setDepartment(request.getDepartment());
        User updatedUserStatus = userRepository.save(existingUser);
        return userMapper.toUserResponse(updatedUserStatus);
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    public UserResponse changeRole(Long employeeId, UpdateRole request) {
        User existingUser = userRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        existingUser.setRole(request.getRole());
        User updatedUserStatus = userRepository.save(existingUser);
        return userMapper.toUserResponse(updatedUserStatus);
    }

}
