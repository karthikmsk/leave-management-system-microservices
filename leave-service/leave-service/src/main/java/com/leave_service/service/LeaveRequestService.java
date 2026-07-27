package com.leave_service.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.leave_service.client.UserClient;
import com.leave_service.dto.ApproveLeaveRequestDto;
import com.leave_service.dto.LeaveRequestDto;
import com.leave_service.dto.LeaveResponseDto;
import com.leave_service.dto.UserResponse;
import com.leave_service.exception.ValidManagerException;
import com.leave_service.mapper.LeaveRequestMapper;
import com.leave_service.exception.ApprovedOrRejectedLeaveException;
import com.leave_service.exception.DateValidationException;
import com.leave_service.exception.LeaveCancelException;
import com.leave_service.exception.LeaveNotFoundException;
import com.leave_service.exception.UnauthorizedLeaveAccessException;
import com.leave_service.model.LeaveRequest;
import com.leave_service.model.LeaveStatus;
import com.leave_service.repository.LeaveRequestRepository;
import com.leave_service.security.CustomUserDetails;

import lombok.RequiredArgsConstructor;

// LeaveService.java
@Service
@RequiredArgsConstructor
public class LeaveRequestService {
    private final UserClient userClient;
    private final LeaveRequestRepository leaveRepository;
    private final LeaveRequestMapper leaveMapper;
    private final LeaveBalanceService leaveBalanceService;

    private Float calculateLeaveDays(LocalDate startDate, LocalDate endDate) {
        return (float) ChronoUnit.DAYS.between(startDate, endDate) + 1;
    }

    private CustomUserDetails getLoggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (CustomUserDetails) authentication.getPrincipal();
    }

    private void validateDates(LeaveRequest leaveRequest) {
        if (leaveRequest.getStartDate().isAfter(leaveRequest.getEndDate())) {
            throw new DateValidationException("Start date must be less than or equal to end date");
        }
    }

    private LeaveResponseDto mapToResponse(LeaveRequest leave) {
        return leaveMapper.toLeaveResponseDto(leave);
    }

    private LeaveResponseDto mapToResponse(LeaveRequest leaveRequest, UserResponse employee) {
        return leaveMapper.toLeaveResponseDto(leaveRequest, employee);
    }

    private LeaveRequest validateManagerAction(Long leaveId, ApproveLeaveRequestDto leaveRequestDto) {
        CustomUserDetails loggedInUser = getLoggedInUser();

        LeaveRequest leave = leaveRepository.findById(leaveId)
                .orElseThrow(() -> new LeaveNotFoundException("Leave not found"));

        if (leave.getLeaveStatus() != LeaveStatus.PENDING) {
            throw new ApprovedOrRejectedLeaveException("Approved or Rejected leaves can not reapproved or accepted");
        }

        UserResponse loggedInManager = userClient.getUserByEmployeeId(loggedInUser.getEmployeeId());

        UserResponse requestingEmployee = userClient.getUserByEmployeeId(leave.getEmployeeId());

        if (!(loggedInManager.getEmployeeId()).equals(requestingEmployee.getManagerId())) {
            throw new ValidManagerException("You are not a manager of this employee");

        }

        leave.setReviewerId(loggedInManager.getEmployeeId());
        leave.setReviewerName(loggedInManager.getName());
        leave.setManagerComment(leaveRequestDto.getManagerComment());

        return leave;

    }

    private List<LeaveResponseDto> getTeamLeavesByStatus(LeaveStatus status) {
        CustomUserDetails loggedInUser = getLoggedInUser();

        UserResponse loggedInManager = userClient.getUserByEmployeeId(loggedInUser.getEmployeeId());
        List<UserResponse> employees = userClient.getTeamMembers(loggedInManager.getEmployeeId());

        List<LeaveResponseDto> result = new ArrayList<>();
        for (UserResponse employee : employees) {
            List<LeaveRequest> leaves = leaveRepository.findByEmployeeId(employee.getEmployeeId());

            for (LeaveRequest leave : leaves) {
                if (leave.getLeaveStatus() == status) {
                    LeaveResponseDto dto = mapToResponse(leave, employee);
                    result.add(dto);
                }
            }
        }
        return result;
    }

    public LeaveResponseDto getLeaveById(Long leaveId) {
        return mapToResponse(leaveRepository.findById(leaveId)
                .orElseThrow(() -> new LeaveNotFoundException("Leave not found")));
    }

    public List<LeaveResponseDto> getMyLeaves() {
        CustomUserDetails loggedInUser = getLoggedInUser();

        UserResponse employee = userClient.getUserByEmployeeId(loggedInUser.getEmployeeId());
        List<LeaveRequest> leaves = leaveRepository.findByEmployeeId(employee.getEmployeeId());
        List<LeaveResponseDto> result = new ArrayList<>();
        for (LeaveRequest leave : leaves) {
            LeaveResponseDto dto = mapToResponse(leave, employee);
            result.add(dto);
        }
        return result;
    }

    @PreAuthorize("hasAnyRole('MANAGER')")
    public List<LeaveResponseDto> getTeamLeaves() {
        CustomUserDetails loggedInUser = getLoggedInUser();

        UserResponse loggedInManager = userClient.getUserByEmployeeId(loggedInUser.getEmployeeId());
        List<UserResponse> employees = userClient.getTeamMembers(loggedInManager.getEmployeeId());

        List<LeaveResponseDto> result = new ArrayList<>();
        for (UserResponse employee : employees) {
            List<LeaveRequest> leaves = leaveRepository.findByEmployeeId(employee.getEmployeeId());
            for (LeaveRequest leave : leaves) {
                LeaveResponseDto dto = mapToResponse(leave, employee);
                result.add(dto);
            }
        }
        return result;
    }

    @PreAuthorize("hasAnyRole('MANAGER')")
    public List<LeaveResponseDto> getPendingLeaves() {
        return getTeamLeavesByStatus(LeaveStatus.PENDING);
    }

    @PreAuthorize("hasAnyRole('MANAGER')")
    public List<LeaveResponseDto> getApprovedLeaves() {
        return getTeamLeavesByStatus(LeaveStatus.PENDING);
    }

    @PreAuthorize("hasAnyRole('MANAGER')")
    public List<LeaveResponseDto> getRejectedLeaves() {
        return getTeamLeavesByStatus(LeaveStatus.PENDING);
    }

    @Transactional
    public LeaveResponseDto applyLeave(LeaveRequestDto leaveRequest) {
        CustomUserDetails loggedInUser = getLoggedInUser();
        UserResponse employee = userClient.getUserByEmployeeId(loggedInUser.getEmployeeId());

        LeaveRequest leave = leaveMapper.toLeaveRequest(leaveRequest);
        leave.setEmployeeId(employee.getEmployeeId());
        leave.setLeaveTypeId(leaveRequest.getLeaveTypeId());
        leave.setStartDate(leaveRequest.getStartDate());
        leave.setEndDate(leaveRequest.getEndDate());
        validateDates(leave);
        leave.setNumberOfDays(calculateLeaveDays(leave.getStartDate(), leave.getEndDate()));
        leaveBalanceService.getLeaveBalance(employee.getEmployeeId(), leave.getLeaveTypeId());
        leave.setReason(leaveRequest.getReason());

        LeaveRequest savedRequest = leaveRepository.save(leave);
        return leaveMapper.toLeaveResponseDto(savedRequest);

    }

    @Transactional
    public LeaveResponseDto approveLeave(Long leaveId, ApproveLeaveRequestDto leaveRequestDto) {

        LeaveRequest leave = validateManagerAction(leaveId, leaveRequestDto);

        UserResponse requestingEmployee = userClient.getUserByEmployeeId(leave.getEmployeeId());

        leaveBalanceService.validateLeaveBalance(requestingEmployee.getEmployeeId(),
                leave.getLeaveTypeId(),
                leave.getNumberOfDays());

        leaveBalanceService.deductLeaveBalance(requestingEmployee.getEmployeeId(),
                leave.getLeaveTypeId(),
                leave.getNumberOfDays());
        leave.setLeaveStatus(LeaveStatus.APPROVED);

        leave.setReviewedAt(LocalDateTime.now());
        LeaveRequest savedLeave = leaveRepository.save(leave);
        return leaveMapper.toLeaveResponseDto(savedLeave);

    }

    @Transactional
    public LeaveResponseDto rejectLeave(Long leaveId, ApproveLeaveRequestDto leaveRequestDto) {
        LeaveRequest leave = validateManagerAction(leaveId, leaveRequestDto);

        leave.setLeaveStatus(LeaveStatus.REJECTED);

        leave.setReviewedAt(LocalDateTime.now());
        LeaveRequest savedLeave = leaveRepository.save(leave);
        return leaveMapper.toLeaveResponseDto(savedLeave);

    }

    @Transactional
    public LeaveResponseDto cancelLeave(Long leaveId) {
        CustomUserDetails loggedInUser = getLoggedInUser();
        UserResponse employee = userClient.getUserByEmployeeId(loggedInUser.getEmployeeId());

        LeaveRequest leave = leaveRepository.findById(leaveId)
                .orElseThrow(() -> new LeaveNotFoundException("Leave not found"));
        if (!employee.getEmployeeId().equals(leave.getEmployeeId())) {
            throw new UnauthorizedLeaveAccessException("You can cancel only your own leave request.");
        }
        if (leave.getLeaveStatus() == LeaveStatus.CANCELLED || leave.getLeaveStatus() == LeaveStatus.REJECTED) {
            throw new LeaveCancelException("You can not cancel the leave which is already canceled or rejected");
        }
        if (!leave.getStartDate().isAfter(LocalDate.now())) {
            throw new LeaveCancelException("This leave can not be canceled beacuase it has already started");
        }

        if (leave.getLeaveStatus() == LeaveStatus.APPROVED) {
            leaveBalanceService.restoreLeaveBalance(employee.getEmployeeId(),
                    leave.getLeaveTypeId(),
                    leave.getNumberOfDays());
        }
        leave.setLeaveStatus(LeaveStatus.CANCELLED);
        leave.setUpdatedAt(LocalDateTime.now());
        LeaveRequest savedLeave = leaveRepository.save(leave);
        return leaveMapper.toLeaveResponseDto(savedLeave);
    }
}
