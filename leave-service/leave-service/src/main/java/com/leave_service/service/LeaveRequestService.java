package com.leave_service.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
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
import com.leave_service.kafka.event.LeaveEvent;
import com.leave_service.kafka.event.LeaveEventType;
import com.leave_service.kafka.producer.LeaveEventProducer;
import com.leave_service.mapper.LeaveRequestMapper;
import com.leave_service.exception.ApprovedOrRejectedLeaveException;
import com.leave_service.exception.DateValidationException;
import com.leave_service.exception.LeaveCancelException;
import com.leave_service.exception.LeaveNotFoundException;
import com.leave_service.exception.LeaveTypeNotFoundException;
import com.leave_service.exception.UnauthorizedLeaveAccessException;
import com.leave_service.model.LeaveRequest;
import com.leave_service.model.LeaveStatus;
import com.leave_service.model.LeaveType;
import com.leave_service.repository.LeaveRequestRepository;
import com.leave_service.repository.LeaveTypeRepository;
import com.leave_service.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LeaveRequestService {
    private final UserClient userClient;
    private final LeaveRequestRepository leaveRepository;
    private final LeaveTypeRepository leaveTypeRepository;
    private final LeaveRequestMapper leaveMapper;
    private final LeaveBalanceService leaveBalanceService;
    private final LeaveEventProducer leaveEventProducer;
    
    private Float calculateLeaveDays(LocalDate startDate, LocalDate endDate) {
        return (float) ChronoUnit.DAYS.between(startDate, endDate) + 1;
    }

    private CustomUserDetails getLoggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (CustomUserDetails) authentication.getPrincipal();
    }

    private void validateDates(LeaveRequest leaveRequest) {
        if (leaveRequest.getStartDate().isBefore(LocalDate.now())) {
            throw new DateValidationException("Leave cannot be applied for past dates");
        }
        if (leaveRequest.getStartDate().isAfter(leaveRequest.getEndDate())) {
            throw new DateValidationException("Start date must be less than or equal to end date");
        }
    }

    private LeaveEvent buildLeaveEvent(LeaveRequest leaveRequest, UserResponse employee,
            LeaveType leaveType, LeaveEventType eventType) {

        LeaveEvent event = new LeaveEvent();

        event.setEmployeeId(employee.getEmployeeId());
        event.setEmployeeName(employee.getName());
        event.setLeaveRequestId(leaveRequest.getId());
        event.setLeaveType(leaveType.getName());
        event.setStartDate(leaveRequest.getStartDate());
        event.setEndDate(leaveRequest.getEndDate());
        event.setStatus(leaveRequest.getLeaveStatus().name());
        event.setEventType(eventType);

        return event;
    }

    private LeaveResponseDto mapToResponse(LeaveRequest leave) {
        return leaveMapper.toLeaveResponseDto(leave);
    }

    private LeaveResponseDto mapToResponse(LeaveRequest leave, UserResponse employee) {
        LeaveResponseDto dto = leaveMapper.toLeaveResponseDto(leave);

        dto.setEmployeeName(employee.getName());

        LeaveType leaveType = leaveTypeRepository.findById(leave.getLeaveTypeId())
                .orElseThrow(() -> new LeaveTypeNotFoundException("Leave type not found"));

        dto.setLeaveType(leaveType.getName());

        return dto;
    }

    private LeaveRequest validateManagerAction(Long leaveId, ApproveLeaveRequestDto leaveRequestDto) {
        CustomUserDetails loggedInUser = getLoggedInUser();

        LeaveRequest leave = leaveRepository.findById(leaveId)
                .orElseThrow(() -> new LeaveNotFoundException("Leave not found"));

        if (leave.getLeaveStatus() != LeaveStatus.PENDING) {
            throw new ApprovedOrRejectedLeaveException("Approved or Rejected leaves can not reapproved or accepted");
        }
        System.out.println("Manager Id : " + loggedInUser.getEmployeeId());
        UserResponse loggedInManager = userClient.getUserByEmployeeId(loggedInUser.getEmployeeId());
        System.out.println("Logged In Manager : " + loggedInManager.getEmployeeId());
        UserResponse requestingEmployee = userClient.getUserByEmployeeId(leave.getEmployeeId());
        System.out.println("Employee : " + requestingEmployee.getEmployeeId());
        System.out.println("Employee Manager : " + requestingEmployee.getManagerId());
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

    private LeaveResponseDto toResponse(LeaveRequest leaveRequest) {
        LeaveResponseDto dto = leaveMapper.toLeaveResponseDto(leaveRequest);
        UserResponse employee = userClient.getUserByEmployeeId(leaveRequest.getEmployeeId());
        LeaveType leaveType = leaveTypeRepository.findById(leaveRequest.getLeaveTypeId())
                .orElseThrow(() -> new LeaveTypeNotFoundException("Leave type not found"));
        dto.setEmployeeName(employee.getName());
        dto.setLeaveType(leaveType.getName());

        return dto;
    }

    @PreAuthorize("hasAnyRole('EMPLOYEE','MANAGER','HR','ADMIN')")
    public LeaveResponseDto getLeaveById(Long leaveId) {
        return mapToResponse(leaveRepository.findById(leaveId)
                .orElseThrow(() -> new LeaveNotFoundException("Leave not found")));
    }

    @PreAuthorize("hasAnyRole('EMPLOYEE','MANAGER','HR','ADMIN')")
    public List<LeaveResponseDto> getMyLeaves() {
        CustomUserDetails loggedInUser = getLoggedInUser();

        UserResponse employee = userClient.getUserByEmployeeId(loggedInUser.getEmployeeId());
        List<LeaveRequest> leaves = leaveRepository.findByEmployeeId(employee.getEmployeeId());
        List<LeaveResponseDto> result = new ArrayList<>();
        for (LeaveRequest leave : leaves) {
            LeaveResponseDto dto = mapToResponse(leave, employee);
            dto.setEmployeeName(employee.getName());
            LeaveType leaveType = leaveTypeRepository.findById(leave.getLeaveTypeId())
                    .orElseThrow(() -> new LeaveTypeNotFoundException("Leave type not found"));
            dto.setLeaveType(leaveType.getName());

            result.add(dto);
        }
        return result;
    }

    @PreAuthorize("hasRole('MANAGER')")
    public List<LeaveResponseDto> getTeamLeaves() {

        CustomUserDetails loggedInUser = getLoggedInUser();

        List<UserResponse> employees = userClient.getTeamMembers(loggedInUser.getEmployeeId());

        if (employees.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> employeeIds = employees.stream()
                .map(UserResponse::getEmployeeId)
                .toList();

        Map<Long, UserResponse> employeeMap = employees.stream()
                .collect(Collectors.toMap(
                        UserResponse::getEmployeeId,
                        Function.identity()));

        List<LeaveRequest> leaves = leaveRepository.findByEmployeeIdIn(employeeIds);
        Map<Long, String> leaveTypeMap = leaveTypeRepository.findAll()
                .stream()
                .collect(Collectors.toMap(
                        LeaveType::getId,
                        LeaveType::getName));

        return leaves.stream()
                .map(leave -> {
                    LeaveResponseDto dto = mapToResponse(leave, employeeMap.get(leave.getEmployeeId()));
                    dto.setLeaveType(leaveTypeMap.get(leave.getLeaveTypeId()));
                    return dto;
                }

                ).toList();
    }

    @PreAuthorize("hasAnyRole('MANAGER')")
    public List<LeaveResponseDto> getPendingLeaves() {
        return getTeamLeavesByStatus(LeaveStatus.PENDING);
    }

    @PreAuthorize("hasAnyRole('MANAGER')")
    public List<LeaveResponseDto> getApprovedLeaves() {
        return getTeamLeavesByStatus(LeaveStatus.APPROVED);
    }

    @PreAuthorize("hasAnyRole('MANAGER')")
    public List<LeaveResponseDto> getRejectedLeaves() {
        return getTeamLeavesByStatus(LeaveStatus.REJECTED);
    }

    @Transactional
    @PreAuthorize("hasAnyRole('EMPLOYEE','MANAGER','HR','ADMIN')")
    public LeaveResponseDto applyLeave(LeaveRequestDto leaveRequest) {
        CustomUserDetails loggedInUser = getLoggedInUser();
        UserResponse employee = userClient.getUserByEmployeeId(loggedInUser.getEmployeeId());

        LeaveRequest leave = leaveMapper.toLeaveRequest(leaveRequest);

        leave.setEmployeeId(employee.getEmployeeId());
        leave.setLeaveTypeId(leaveRequest.getLeaveTypeId());
        validateDates(leave);
        leave.setNumberOfDays(calculateLeaveDays(leave.getStartDate(), leave.getEndDate()));
        leaveBalanceService.validateLeaveBalance(employee.getEmployeeId(), leave.getLeaveTypeId(),
                leave.getNumberOfDays());
        LeaveRequest savedRequest = leaveRepository.save(leave);

        LeaveType leaveType = leaveTypeRepository.findById(savedRequest.getLeaveTypeId())
                .orElseThrow(() -> new LeaveTypeNotFoundException("Leave type not found"));

        LeaveEvent event = buildLeaveEvent(savedRequest, employee, leaveType, LeaveEventType.LEAVE_APPLIED);
        leaveEventProducer.publishLeaveEvent(event);

        return toResponse(savedRequest);

    }

    @Transactional
    @PreAuthorize("hasAnyRole('MANAGER')")
    public LeaveResponseDto approveLeave(Long leaveId, ApproveLeaveRequestDto leaveRequestDto) {

        LeaveRequest leave = validateManagerAction(leaveId, leaveRequestDto);

        UserResponse requestingEmployee = userClient.getUserByEmployeeId(leave.getEmployeeId());
        System.out.println("Approve -> Employee : " + requestingEmployee.getEmployeeId());

        leaveBalanceService.validateLeaveBalance(requestingEmployee.getEmployeeId(),
                leave.getLeaveTypeId(),
                leave.getNumberOfDays());
        System.out.println("Leave balance validated");
        leaveBalanceService.deductLeaveBalance(requestingEmployee.getEmployeeId(),
                leave.getLeaveTypeId(),
                leave.getNumberOfDays());
        System.out.println("Leave balance deducted");
        leave.setLeaveStatus(LeaveStatus.APPROVED);

        leave.setReviewedAt(LocalDateTime.now());
        LeaveRequest savedLeave = leaveRepository.save(leave);

        LeaveType leaveType = leaveTypeRepository.findById(savedLeave.getLeaveTypeId())
                .orElseThrow(() -> new LeaveTypeNotFoundException("Leave type not found"));

        LeaveEvent event = buildLeaveEvent(leave, requestingEmployee, leaveType, LeaveEventType.LEAVE_APPROVED);
        leaveEventProducer.publishLeaveEvent(event);

        return leaveMapper.toLeaveResponseDto(savedLeave);

    }

    @Transactional
    @PreAuthorize("hasAnyRole('MANAGER')")
    public LeaveResponseDto rejectLeave(Long leaveId, ApproveLeaveRequestDto leaveRequestDto) {

        LeaveRequest leave = validateManagerAction(leaveId, leaveRequestDto);
        UserResponse requestingEmployee = userClient.getUserByEmployeeId(leave.getEmployeeId());
        leave.setLeaveStatus(LeaveStatus.REJECTED);

        leave.setReviewedAt(LocalDateTime.now());
        LeaveRequest savedLeave = leaveRepository.save(leave);

        LeaveType leaveType = leaveTypeRepository.findById(savedLeave.getLeaveTypeId())
                .orElseThrow(() -> new LeaveTypeNotFoundException("Leave type not found"));

        LeaveEvent event = buildLeaveEvent(leave, requestingEmployee, leaveType, LeaveEventType.LEAVE_REJECTED);
        leaveEventProducer.publishLeaveEvent(event);

        return leaveMapper.toLeaveResponseDto(savedLeave);

    }

    @Transactional
    @PreAuthorize("hasAnyRole('EMPLOYEE','MANAGER','HR','ADMIN')")
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

        LeaveType leaveType = leaveTypeRepository.findById(savedLeave.getLeaveTypeId())
                .orElseThrow(() -> new LeaveTypeNotFoundException("Leave type not found"));

        LeaveEvent event = buildLeaveEvent(leave, employee, leaveType, LeaveEventType.LEAVE_CANCELED);
        leaveEventProducer.publishLeaveEvent(event);

        return leaveMapper.toLeaveResponseDto(savedLeave);
    }

}
