package com.leave_service.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.leave_service.dto.LeaveBalanceDto;
import com.leave_service.service.LeaveBalanceService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/leave-balances")
public class LeaveBalanceController {

    private final LeaveBalanceService leaveBalanceService;
    
    @GetMapping("/{employeeId}/{leaveTypeId}")
    public ResponseEntity<LeaveBalanceDto> getLeaveBalance(@PathVariable Long employeeId, @PathVariable Long leaveTypeId){
        return ResponseEntity.ok(leaveBalanceService.getLeaveBalance(employeeId, leaveTypeId));
    }

    @GetMapping("employee/{employeeId}")
    public ResponseEntity<List<LeaveBalanceDto>> getEmployeeLeaveBalances(@PathVariable Long employeeId){
        return ResponseEntity.ok(leaveBalanceService.getEmployeeLeaveBalances(employeeId));
    }

    @GetMapping("/my")
    public ResponseEntity<List<LeaveBalanceDto>> getMyLeaveBalance(){
        return ResponseEntity.ok(leaveBalanceService.getMyLeaveBalances());
    }


}
