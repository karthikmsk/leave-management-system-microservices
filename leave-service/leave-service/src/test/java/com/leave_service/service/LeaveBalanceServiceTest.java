package com.leave_service.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.leave_service.client.UserClient;
import com.leave_service.dto.LeaveBalanceDto;
import com.leave_service.dto.UserResponse;
import com.leave_service.exception.LeaveBalanceNotFoundException;
import com.leave_service.exception.NotEnoughLeaveBalanceException;
import com.leave_service.mapper.LeaveBalanceMapper;
import com.leave_service.model.LeaveBalance;
import com.leave_service.model.LeaveType;
import com.leave_service.repository.LeaveBalanceRepository;
import com.leave_service.repository.LeaveTypeRepository;

@ExtendWith(MockitoExtension.class)
public class LeaveBalanceServiceTest {

    @InjectMocks
    private LeaveBalanceService leaveBalanceService;

    @Mock
    private LeaveBalanceMapper leaveBalanceMapper;

    @Mock
    private LeaveBalanceRepository leaveBalanceRepository;

    @Mock
    private LeaveTypeRepository leaveTypeRepository;

    @Mock
    private UserClient userClient;

    private LeaveBalance createLeaveBalance() {
        LeaveBalance leaveBalance = new LeaveBalance();
        leaveBalance.setEmployeeId(1L);
        leaveBalance.setLeaveTypeId(1L);
        leaveBalance.setCreditedDays(10F);
        leaveBalance.setUsedDays(5F);
        leaveBalance.setRemainingDays(5F);
        return leaveBalance;
    }

    private LeaveBalanceDto createLeaveBalanceDto() {
        LeaveBalanceDto dto = new LeaveBalanceDto();
        dto.setEmployeeId(1L);
        dto.setEmployeeName("Aman");
        dto.setLeaveTypeId(1L);
        dto.setLeaveTypeName("Annual Leave");
        dto.setCreditedDays(10F);
        dto.setUsedDays(5F);
        dto.setRemainingDays(5F);
        return dto;
    }

    @Test
    void getLeaveBalance_ShouldReturnLeaveBalance() {
        LeaveBalance leaveBalance = createLeaveBalance();
        LeaveBalanceDto leaveBalanceDto = createLeaveBalanceDto();

        LeaveType leaveType = new LeaveType();
        leaveType.setId(1L);
        leaveType.setName("Annual Leave");
        UserResponse employee = new UserResponse();
        employee.setEmployeeId(1L);
        employee.setName("Aman");

        when(userClient.getUserByEmployeeId(1L))
                .thenReturn(employee);
        when(leaveBalanceRepository.findByEmployeeIdAndLeaveTypeId(1L, 1L)).thenReturn(Optional.of(leaveBalance));
        when(userClient.getUserByEmployeeId(1L)).thenReturn(employee);
        when(leaveTypeRepository.findById(1L)).thenReturn(Optional.of(leaveType));
        when(leaveBalanceMapper.toLeaveBalanceDto(leaveBalance)).thenReturn(leaveBalanceDto);
        LeaveBalanceDto result = leaveBalanceService.getLeaveBalance(1L, 1L);

        assertNotNull(result);
        assertEquals("Aman", result.getEmployeeName());
        assertEquals("Annual Leave", result.getLeaveTypeName());
        assertEquals(10F, result.getCreditedDays());
        assertEquals(5F, result.getUsedDays());
        assertEquals(5F, result.getRemainingDays());

        verify(leaveBalanceRepository).findByEmployeeIdAndLeaveTypeId(1L, 1L);
        verify(leaveBalanceMapper, times(1)).toLeaveBalanceDto(leaveBalance);
    }

    @Test
    void getLeaveBalance_ShouldThrowLeaveBalanceNotFoundException_WhenBalanceDoesNotExist() {
        when(leaveBalanceRepository.findByEmployeeIdAndLeaveTypeId(1L, 1L)).thenReturn(Optional.empty());
        LeaveBalanceNotFoundException exception = assertThrows(LeaveBalanceNotFoundException.class,
                () -> leaveBalanceService.getLeaveBalance(1L, 1L));
        assertEquals("No Balance found", exception.getMessage());
        verify(leaveBalanceRepository, times(1)).findByEmployeeIdAndLeaveTypeId(1L, 1L);
        verifyNoInteractions(leaveBalanceMapper);
    }

    @Test
    void getEmployeeLeaveBalances_ShouldReturnEmployeeLeaveBalances() {
        LeaveBalance leaveBalance = createLeaveBalance();
        LeaveBalanceDto leaveBalanceDto = createLeaveBalanceDto();

        UserResponse employee = new UserResponse();
        employee.setEmployeeId(1L);
        employee.setName("Aman");

        LeaveType leaveType = new LeaveType();
        leaveType.setId(1L);
        leaveType.setName("Annual Leave");

        when(leaveTypeRepository.findById(1L))
                .thenReturn(Optional.of(leaveType));
        when(leaveBalanceRepository.findByEmployeeId(1L)).thenReturn(List.of(leaveBalance));
        when(userClient.getUserByEmployeeId(1L)).thenReturn(employee);
        when(leaveBalanceMapper.toLeaveBalanceDto(leaveBalance)).thenReturn(leaveBalanceDto);
        List<LeaveBalanceDto> result = leaveBalanceService.getEmployeeLeaveBalances(1L);

        assertEquals(1, result.size());
        assertEquals("Aman", result.get(0).getEmployeeName());

        verify(leaveBalanceRepository).findByEmployeeId(1L);
        verify(userClient).getUserByEmployeeId(1L);
        verify(leaveBalanceMapper).toLeaveBalanceDto(leaveBalance);
    }

    @Test
    void validateLeaveBalance_ShouldValidateLeaveBalances() {
        LeaveBalance balance = createLeaveBalance();

        when(leaveBalanceRepository.findByEmployeeIdAndLeaveTypeId(1L, 1L)).thenReturn(Optional.of(balance));

        leaveBalanceService.validateLeaveBalance(1L, 1L, 5F);

        verify(leaveBalanceRepository).findByEmployeeIdAndLeaveTypeId(1L, 1L);
    }

    @Test
    void validateLeaveBalance_ShouldThrowLeaveBalanceNotFoundException_WhenBalanceDoesNotExist() {
        when(leaveBalanceRepository.findByEmployeeIdAndLeaveTypeId(1L, 1L)).thenReturn(Optional.empty());
        LeaveBalanceNotFoundException exception = assertThrows(LeaveBalanceNotFoundException.class,
                () -> leaveBalanceService.validateLeaveBalance(1L, 1L, 5F));
        assertEquals("No Balance found", exception.getMessage());
        verify(leaveBalanceRepository, times(1)).findByEmployeeIdAndLeaveTypeId(1L, 1L);
        verifyNoInteractions(leaveBalanceMapper);
    }

    @Test
    void validateLeaveBalance_ShouldThrowInsufficientLeaveBalanceException_WhenRequestedDaysExceedBalance() {
        LeaveBalance balance = createLeaveBalance();
        balance.setRemainingDays(2F);
        when(leaveBalanceRepository.findByEmployeeIdAndLeaveTypeId(1L, 1L)).thenReturn(Optional.of(balance));
        NotEnoughLeaveBalanceException exception = assertThrows(NotEnoughLeaveBalanceException.class,
                () -> leaveBalanceService.validateLeaveBalance(1L, 1L, 5F));
        assertEquals("Requested leave exceeds the available leave balance", exception.getMessage());
        verify(leaveBalanceRepository, times(1)).findByEmployeeIdAndLeaveTypeId(1L, 1L);
        verifyNoInteractions(leaveBalanceMapper);
    }

    @Test
    void deductLeaveBalance_ShouldDeductLeaveBalances() {
        LeaveBalance balance = createLeaveBalance();

        when(leaveBalanceRepository.findByEmployeeIdAndLeaveTypeId(1L, 1L)).thenReturn(Optional.of(balance));

        leaveBalanceService.deductLeaveBalance(1L, 1L, 5F);

        assertEquals(10F, balance.getUsedDays());
        assertEquals(0F, balance.getRemainingDays());

        verify(leaveBalanceRepository).save(balance);
    }

    @Test
    void deductLeaveBalance_ShouldThrowLeaveBalanceNotFoundException_WhenBalanceDoesNotExist() {
        when(leaveBalanceRepository.findByEmployeeIdAndLeaveTypeId(1L, 1L)).thenReturn(Optional.empty());
        LeaveBalanceNotFoundException exception = assertThrows(LeaveBalanceNotFoundException.class,
                () -> leaveBalanceService.deductLeaveBalance(1L, 1L, 5F));
        assertEquals("No Balance found", exception.getMessage());
        verify(leaveBalanceRepository, times(1)).findByEmployeeIdAndLeaveTypeId(1L, 1L);
        verifyNoInteractions(leaveBalanceMapper);
    }

    @Test
    void restoreLeaveBalance_ShouldRestoreLeaveBalances() {
        LeaveBalance balance = createLeaveBalance();

        when(leaveBalanceRepository.findByEmployeeIdAndLeaveTypeId(1L, 1L)).thenReturn(Optional.of(balance));

        leaveBalanceService.restoreLeaveBalance(1L, 1L, 5F);

        assertEquals(0F, balance.getUsedDays());
        assertEquals(10F, balance.getRemainingDays());
        verify(leaveBalanceRepository).save(balance);
    }

    @Test
    void restoreLeaveBalance_ShouldThrowLeaveBalanceNotFoundException_WhenBalanceDoesNotExist() {
        when(leaveBalanceRepository.findByEmployeeIdAndLeaveTypeId(1L, 1L)).thenReturn(Optional.empty());
        LeaveBalanceNotFoundException exception = assertThrows(LeaveBalanceNotFoundException.class,
                () -> leaveBalanceService.restoreLeaveBalance(1L, 1L, 5F));
        assertEquals("No Balance found", exception.getMessage());
        verify(leaveBalanceRepository, times(1)).findByEmployeeIdAndLeaveTypeId(1L, 1L);
        verifyNoInteractions(leaveBalanceMapper);
    }

}
