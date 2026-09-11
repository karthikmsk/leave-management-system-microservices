package com.leave_service.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.leave_service.security.CustomUserDetails;
import com.leave_service.client.NotificationClient;
import com.leave_service.client.UserClient;
import com.leave_service.dto.LeaveRequestDto;
import com.leave_service.dto.LeaveResponseDto;
import com.leave_service.dto.UserResponse;
import com.leave_service.exception.DateValidationException;
import com.leave_service.exception.LeaveBalanceNotFoundException;
import com.leave_service.exception.LeaveNotFoundException;
import com.leave_service.mapper.LeaveRequestMapper;
import com.leave_service.model.LeaveRequest;
import com.leave_service.model.LeaveStatus;
import com.leave_service.model.LeaveType;
import com.leave_service.repository.LeaveRequestRepository;
import com.leave_service.repository.LeaveTypeRepository;

@ExtendWith(MockitoExtension.class)
public class LeaveRequestServiceTest {

    @InjectMocks
    private LeaveRequestService leaveRequestService;

    @Mock
    private UserClient userClient;

    @Mock
    private NotificationClient notificationClient;

    @Mock
    private LeaveRequestRepository leaveRepository;

    @Mock
    private LeaveRequestMapper leaveMapper;

    @Mock
    private LeaveTypeRepository leaveTypeRepository;

    @Mock
    private LeaveBalanceService leaveBalanceService;

    private LeaveRequest createLeaveRequest() {
        LeaveRequest request = new LeaveRequest();
        request.setId(1L);
        request.setEmployeeId(1L);
        request.setLeaveTypeId(1L);
        request.setStartDate(LocalDate.now().plusDays(1));
        request.setEndDate(LocalDate.now().plusDays(4));
        request.setNumberOfDays(4F);
        request.setReason("Family Function");
        request.setLeaveStatus(LeaveStatus.PENDING);
        request.setManagerComment(null);
        request.setReviewerName(null);
        return request;
    }

    private LeaveRequestDto createRequestDto() {
        LeaveRequestDto dto = new LeaveRequestDto();
        dto.setLeaveTypeId(1L);
        dto.setStartDate(LocalDate.now().plusDays(1));
        dto.setEndDate(LocalDate.now().plusDays(4));
        dto.setReason("Family Function");
        return dto;
    }

    private LeaveResponseDto createResponseDto() {
        LeaveResponseDto response = new LeaveResponseDto();
        response.setEmployeeId(1L);
        response.setEmployeeName("Aman");
        response.setLeaveType("Basket of Leave");
        response.setStartDate(LocalDate.now().plusDays(1));
        response.setEndDate(LocalDate.now().plusDays(4));
        response.setNumberOfDays(4F);
        response.setReason("Family Function");
        response.setLeaveStatus(LeaveStatus.PENDING);
        response.setAppliedAt(LocalDateTime.now());
        response.setManagerComment(null);
        response.setReviewerId(null);
        response.setReviewedAt(null);
        response.setReviewerName(null);

        return response;
    }

    private void mockLoggedInUser(Long employeeId) {
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        CustomUserDetails loggedInUser = mock(CustomUserDetails.class);

        SecurityContextHolder.setContext(securityContext);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(loggedInUser);
        when(loggedInUser.getEmployeeId()).thenReturn(employeeId);
    }

    @Test
    void getLeaveById_ShouldReturnLeave() {
        LeaveRequest request = createLeaveRequest();
        LeaveResponseDto responseDto = createResponseDto();

        when(leaveRepository.findById(1L)).thenReturn(Optional.of(request));
        when(leaveMapper.toLeaveResponseDto(request)).thenReturn(responseDto);
        LeaveResponseDto result = leaveRequestService.getLeaveById(1L);

        assertNotNull(result);
        assertEquals(responseDto.getEmployeeId(), result.getEmployeeId());
        assertEquals(responseDto.getEmployeeName(), result.getEmployeeName());
        assertEquals(responseDto.getLeaveType(), result.getLeaveType());
        assertEquals(responseDto.getStartDate(), result.getStartDate());
        assertEquals(responseDto.getEndDate(), result.getEndDate());
        assertEquals(4F, result.getNumberOfDays());
        assertEquals("Family Function", result.getReason());
        assertEquals(LeaveStatus.PENDING, result.getLeaveStatus());
        assertNull(result.getManagerComment());
        assertNotNull(result.getAppliedAt());
        assertNull(result.getReviewerId());
        assertNull(result.getReviewerName());
        assertNull(result.getReviewedAt());

        verify(leaveRepository).findById(1L);
        verify(leaveMapper).toLeaveResponseDto(request);

    }

    @Test
    void getLeaveById_ShouldThrowLeaveNotFoundException_WhenIdDoesNotExist() {
        when(leaveRepository.findById(1L)).thenReturn(Optional.empty());
        LeaveNotFoundException exception = assertThrows(LeaveNotFoundException.class,
                () -> leaveRequestService.getLeaveById(1L));
        assertEquals("Leave not found", exception.getMessage());

        verify(leaveRepository).findById(1L);
    }

    @Test
    void getMyLeaves_ShouldReturnMyLeaves() {

        LeaveRequest request = createLeaveRequest();
        LeaveResponseDto responseDto = createResponseDto();

        mockLoggedInUser(1L);

        UserResponse employee = new UserResponse();
        employee.setEmployeeId(1L);
        employee.setName("Aman");

        LeaveType leaveType = new LeaveType();
        leaveType.setId(1L);
        leaveType.setName("Annual Leave");

        when(leaveRepository.findByEmployeeId(1L))
                .thenReturn(List.of(request));

        when(userClient.getUserByEmployeeId(1L))
                .thenReturn(employee);

        when(leaveTypeRepository.findById(1L))
                .thenReturn(Optional.of(leaveType));

        when(leaveMapper.toLeaveResponseDto(request))
                .thenReturn(responseDto);

        List<LeaveResponseDto> result = leaveRequestService.getMyLeaves();

        assertEquals(1, result.size());
        assertEquals("Aman", result.get(0).getEmployeeName());

        verify(leaveRepository).findByEmployeeId(1L);
        verify(userClient).getUserByEmployeeId(1L);
        verify(leaveTypeRepository, times(2)).findById(1L);
        verify(leaveMapper).toLeaveResponseDto(request);
    }

    @Test
    void getTeamLeaves_ShouldReturnTeamLeaves() {

        mockLoggedInUser(1L);

        LeaveRequest request = createLeaveRequest();
        LeaveResponseDto responseDto = createResponseDto();

        UserResponse employee = new UserResponse();
        employee.setEmployeeId(1L);
        employee.setName("Aman");

        List<UserResponse> employees = List.of(employee);

        LeaveType leaveType = new LeaveType();
        leaveType.setId(1L);
        leaveType.setName("Annual Leave");

        // Manager -> team members
        when(userClient.getTeamMembers(1L))
                .thenReturn(employees);

        // Team member -> leave requests
        when(leaveRepository.findByEmployeeIdIn(List.of(1L)))
                .thenReturn(List.of(request));

        // Used by getTeamLeaves()
        when(leaveTypeRepository.findAll())
                .thenReturn(List.of(leaveType));

        // Used by mapToResponse()
        when(leaveTypeRepository.findById(1L))
                .thenReturn(Optional.of(leaveType));

        when(leaveMapper.toLeaveResponseDto(request))
                .thenReturn(responseDto);

        List<LeaveResponseDto> result = leaveRequestService.getTeamLeaves();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Aman", result.get(0).getEmployeeName());
        assertEquals("Annual Leave", result.get(0).getLeaveType());

        verify(userClient).getTeamMembers(1L);
        verify(leaveRepository).findByEmployeeIdIn(List.of(1L));
        verify(leaveTypeRepository).findAll();
        verify(leaveTypeRepository).findById(1L);
        verify(leaveMapper).toLeaveResponseDto(request);
    }

    @Test
    void applyLeave_ShouldApplyLeave() {

        mockLoggedInUser(1L);

        LeaveRequest leave = createLeaveRequest();
        LeaveRequestDto requestDto = createRequestDto();
        LeaveResponseDto responseDto = createResponseDto();

        UserResponse employee = new UserResponse();
        employee.setEmployeeId(1L);
        employee.setName("Aman");

        LeaveType leaveType = new LeaveType();
        leaveType.setId(1L);
        leaveType.setName("Annual Leave");

        when(userClient.getUserByEmployeeId(1L))
                .thenReturn(employee);

        when(leaveTypeRepository.findById(1L))
                .thenReturn(Optional.of(leaveType));

        when(leaveMapper.toLeaveRequest(requestDto))
                .thenReturn(leave);

        when(leaveRepository.save(any(LeaveRequest.class)))
                .thenReturn(leave);

        when(leaveMapper.toLeaveResponseDto(leave))
                .thenReturn(responseDto);

        doNothing().when(leaveBalanceService)
                .validateLeaveBalance(1L, 1L, 4F);

        LeaveResponseDto result = leaveRequestService.applyLeave(requestDto);

        assertNotNull(result);
        assertEquals(1L, result.getEmployeeId());
        assertEquals(LocalDate.now().plusDays(1), result.getStartDate());
        assertEquals(LocalDate.now().plusDays(4), result.getEndDate());
        assertEquals("Family Function", result.getReason());

        verify(userClient, times(2)).getUserByEmployeeId(1L);
        verify(leaveBalanceService)
                .validateLeaveBalance(1L, 1L, 4F);
        verify(leaveTypeRepository).findById(1L);
        verify(leaveMapper).toLeaveRequest(requestDto);
        verify(leaveRepository).save(leave);
        verify(leaveMapper).toLeaveResponseDto(leave);
    }

    @Test
    void applyLeave_ShouldThrowDateValidationException_WhenStartDateAfterEndDate() {

        mockLoggedInUser(1L);

        LeaveRequestDto requestDto = createRequestDto();

        LocalDate startDate = LocalDate.now().plusDays(5);
        LocalDate endDate = LocalDate.now().plusDays(1);

        requestDto.setStartDate(startDate);
        requestDto.setEndDate(endDate);

        LeaveRequest leave = createLeaveRequest();

        // IMPORTANT: mapper result must also contain invalid dates
        leave.setStartDate(startDate);
        leave.setEndDate(endDate);

        UserResponse employee = new UserResponse();
        employee.setEmployeeId(1L);

        when(userClient.getUserByEmployeeId(1L))
                .thenReturn(employee);

        when(leaveMapper.toLeaveRequest(requestDto))
                .thenReturn(leave);

        DateValidationException exception = assertThrows(
                DateValidationException.class,
                () -> leaveRequestService.applyLeave(requestDto));

        assertEquals(
                "Start date must be less than or equal to end date",
                exception.getMessage());

        verify(leaveMapper).toLeaveRequest(requestDto);

        verify(leaveBalanceService, never())
                .validateLeaveBalance(any(), any(), any());

        verify(leaveRepository, never())
                .save(any());
    }

    @Test
    void applyLeave_ShouldThrowDateValidationException_WhenStartDateIsInPast() {

        mockLoggedInUser(1L);

        LeaveRequestDto requestDto = createRequestDto();
        requestDto.setStartDate(LocalDate.now().minusDays(1));
        requestDto.setEndDate(LocalDate.now().plusDays(2));

        LeaveRequest request = createLeaveRequest();
        request.setStartDate(LocalDate.now().minusDays(1));
        request.setEndDate(LocalDate.now().plusDays(2));

        UserResponse employee = new UserResponse();
        employee.setEmployeeId(1L);

        when(userClient.getUserByEmployeeId(1L)).thenReturn(employee);
        when(leaveMapper.toLeaveRequest(requestDto)).thenReturn(request);

        DateValidationException exception = assertThrows(
                DateValidationException.class,
                () -> leaveRequestService.applyLeave(requestDto));

        assertEquals("Leave cannot be applied for past dates", exception.getMessage());
    }

    @Test
    void applyLeave_ShouldThrowLeaveBalanceNotFoundException_WhenLeaveBalanceDoesNotExist() {

        mockLoggedInUser(1L);

        LeaveRequestDto requestDto = createRequestDto();

        LocalDate startDate = LocalDate.now().plusDays(1);
        LocalDate endDate = LocalDate.now().plusDays(4);

        requestDto.setStartDate(startDate);
        requestDto.setEndDate(endDate);

        LeaveRequest leave = createLeaveRequest();

        leave.setStartDate(startDate);
        leave.setEndDate(endDate);
        leave.setNumberOfDays(4F);

        UserResponse employee = new UserResponse();
        employee.setEmployeeId(1L);
        employee.setName("Aman");

        when(userClient.getUserByEmployeeId(1L))
                .thenReturn(employee);

        when(leaveMapper.toLeaveRequest(requestDto))
                .thenReturn(leave);

        doThrow(new LeaveBalanceNotFoundException("No Balance found"))
                .when(leaveBalanceService)
                .validateLeaveBalance(1L, 1L, 4F);

        LeaveBalanceNotFoundException exception = assertThrows(
                LeaveBalanceNotFoundException.class,
                () -> leaveRequestService.applyLeave(requestDto));

        assertEquals(
                "No Balance found",
                exception.getMessage());

        verify(leaveBalanceService)
                .validateLeaveBalance(1L, 1L, 4F);

        verify(leaveRepository, never())
                .save(any());
    }

    @Test
    void approveLeave_ShouldApproveLeave() {

    }
}
