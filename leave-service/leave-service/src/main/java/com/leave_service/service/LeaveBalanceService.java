package com.leave_service.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.leave_service.client.UserClient;
import com.leave_service.dto.LeaveBalanceDto;
import com.leave_service.dto.MyLeaveBalance;
import com.leave_service.dto.SyncLeaveBalanceResponseDto;
import com.leave_service.dto.UserResponse;
import com.leave_service.exception.LeaveBalanceAlreadyExistsException;
import com.leave_service.exception.LeaveBalanceNotFoundException;
import com.leave_service.exception.LeaveTypeNotFoundException;
import com.leave_service.exception.NotEnoughLeaveBalanceException;
import com.leave_service.mapper.LeaveBalanceMapper;
import com.leave_service.model.LeaveBalance;
import com.leave_service.model.LeaveType;
import com.leave_service.repository.LeaveBalanceRepository;
import com.leave_service.repository.LeaveTypeRepository;
import com.leave_service.security.CustomUserDetails;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LeaveBalanceService {

    private final LeaveBalanceRepository leaveBalanceRepository;
    private final LeaveTypeRepository leaveTypeRepository;
    private final UserClient userClient;
    private final LeaveBalanceMapper balanceMapper;

    private LeaveBalanceDto mapToResponse(LeaveBalance leaveBalance, UserResponse employee) {
        LeaveType leaveType = leaveTypeRepository.findById(leaveBalance.getLeaveTypeId())
                .orElseThrow(() -> new LeaveTypeNotFoundException("Leave type not found"));

        LeaveBalanceDto balanceDto = balanceMapper.toLeaveBalanceDto(leaveBalance);
        balanceDto.setEmployeeName(employee.getName());
        balanceDto.setLeaveTypeName(leaveType.getName());

        return balanceDto;
    }

    private CustomUserDetails getLoggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (CustomUserDetails) authentication.getPrincipal();
    }

    public LeaveBalanceDto getLeaveBalance(Long employeeId, Long leaveTypeId) {
        LeaveBalance leaveBalance = leaveBalanceRepository.findByEmployeeIdAndLeaveTypeId(employeeId, leaveTypeId)
                .orElseThrow(() -> new LeaveBalanceNotFoundException("No Balance found"));
        UserResponse employee = userClient.getUserByEmployeeId(employeeId);

        return mapToResponse(leaveBalance, employee);
    }

    @PreAuthorize("hasAnyRole('EMPLOYEE')")
    public List<MyLeaveBalance> getMyLeaveBalances() {
        CustomUserDetails loggedInUser = getLoggedInUser();
        List<LeaveBalance> leaveBalances = leaveBalanceRepository.findByEmployeeId(loggedInUser.getEmployeeId());
        List<LeaveType> leaveTypes = leaveTypeRepository.findAll();
        Map<Long, String> leaveTypeMap = leaveTypes.stream()
                .collect(Collectors.toMap(LeaveType::getId, LeaveType::getName));
        List<MyLeaveBalance> result = new ArrayList<>();
        for (LeaveBalance leaveBalance : leaveBalances) {
            MyLeaveBalance dto = balanceMapper.toBalance(leaveBalance);
            dto.setLeaveTypeName(leaveTypeMap.get(leaveBalance.getLeaveTypeId()));
            result.add(dto);
        }
        return result;
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','HR')")
    public List<LeaveBalanceDto> getEmployeeLeaveBalances(Long employeeId) {
        List<LeaveBalance> leaveBalances = leaveBalanceRepository.findByEmployeeId(employeeId);
        UserResponse employee = userClient.getUserByEmployeeId(employeeId);
        List<LeaveBalanceDto> result = new ArrayList<>();
        for (LeaveBalance leaveBalance : leaveBalances) {
            LeaveBalanceDto dto = mapToResponse(leaveBalance, employee);
            result.add(dto);
        }
        return result;

    }

    @Transactional
    public void initializeLeaveBalances(Long employeeId) {
        List<LeaveType> leaveTypes = leaveTypeRepository.findByIsActiveTrue();
        if (leaveBalanceRepository.existsByEmployeeId(employeeId)) {
            throw new LeaveBalanceAlreadyExistsException("Leave balances already exist for employee: " + employeeId);
        }
        List<LeaveBalance> leaveBalances = new ArrayList<>();

        for (LeaveType leaveType : leaveTypes) {
            LeaveBalance leaveBalance = new LeaveBalance();
            leaveBalance.setEmployeeId(employeeId);
            leaveBalance.setLeaveTypeId(leaveType.getId());
            leaveBalance.setCreditedDays(leaveType.getAnnualAllocation());
            leaveBalance.setRemainingDays(leaveType.getAnnualAllocation());
            leaveBalance.setUsedDays(0F);
            leaveBalances.add(leaveBalance);
        }
        leaveBalanceRepository.saveAll(leaveBalances);
    }

    public void validateLeaveBalance(Long employeeId, Long leaveTypeId, Float requestedDays) {
        LeaveBalance leaveBalance = leaveBalanceRepository.findByEmployeeIdAndLeaveTypeId(employeeId, leaveTypeId)
                .orElseThrow(() -> new LeaveBalanceNotFoundException("No Balance found"));
        System.out.println("Remaining : " + leaveBalance.getRemainingDays());
        System.out.println("Requested : " + requestedDays);
        if (leaveBalance.getRemainingDays() < requestedDays) {
            throw new NotEnoughLeaveBalanceException("Requested leave exceeds the available leave balance");
        }
    }

    @Transactional
    public void deductLeaveBalance(Long employeeId, Long leaveTypeId, Float requestedDays) {
        LeaveBalance leaveBalance = leaveBalanceRepository.findByEmployeeIdAndLeaveTypeId(employeeId, leaveTypeId)
                .orElseThrow(() -> new LeaveBalanceNotFoundException("No Balance found"));

        leaveBalance.setRemainingDays(leaveBalance.getRemainingDays() - requestedDays);
        leaveBalance.setUsedDays(leaveBalance.getUsedDays() + requestedDays);
        leaveBalanceRepository.save(leaveBalance);

    }

    @Transactional
    public void restoreLeaveBalance(Long employeeId, Long leaveTypeId, Float requestedDays) {
        LeaveBalance leaveBalance = leaveBalanceRepository.findByEmployeeIdAndLeaveTypeId(employeeId, leaveTypeId)
                .orElseThrow(() -> new LeaveBalanceNotFoundException("No Balance found"));

        leaveBalance.setRemainingDays(leaveBalance.getRemainingDays() + requestedDays);
        leaveBalance.setUsedDays(leaveBalance.getUsedDays() - requestedDays);
        leaveBalanceRepository.save(leaveBalance);
    }

    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN')")
    public SyncLeaveBalanceResponseDto syncLeaveBalances() {
        System.out.println("Before calling User Service");

        List<UserResponse> employees = userClient.getAllUsers();

        System.out.println("After calling User Service");
        List<LeaveType> leaveTypes = leaveTypeRepository.findAll();

        int created = 0;
        int skipped = 0;

        for (UserResponse employee : employees) {
            for (LeaveType leaveType : leaveTypes) {
                boolean exists = leaveBalanceRepository.existsByEmployeeIdAndLeaveTypeId(employee.getEmployeeId(),
                        leaveType.getId());
                if (!exists) {
                    LeaveBalance balance = new LeaveBalance();
                    balance.setEmployeeId(employee.getEmployeeId());
                    balance.setLeaveTypeId(leaveType.getId());
                    balance.setCreditedDays(leaveType.getAnnualAllocation());
                    balance.setUsedDays(0F);
                    balance.setRemainingDays(leaveType.getAnnualAllocation());

                    leaveBalanceRepository.save(balance);
                    created++;
                } else {
                    skipped++;
                }
            }
        }
        SyncLeaveBalanceResponseDto response = new SyncLeaveBalanceResponseDto();
        response.setEmployeesProcessed(employees.size());
        response.setLeaveBalancesCreated(created);
        response.setLeaveBalancesSkipped(skipped);

        return response;
    }
}
