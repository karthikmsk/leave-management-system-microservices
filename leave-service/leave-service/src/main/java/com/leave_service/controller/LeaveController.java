package com.leave_service.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.leave_service.dto.ApproveLeaveRequestDto;
import com.leave_service.dto.LeaveRequestDto;
import com.leave_service.dto.LeaveResponseDto;
import com.leave_service.service.LeaveRequestService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/leaves")
public class LeaveController {

    private final LeaveRequestService leaveRequestService;

    @GetMapping("/{leaveId}")
    public ResponseEntity<LeaveResponseDto> getLeaveById(@PathVariable Long leaveId) {
        return ResponseEntity.ok(leaveRequestService.getLeaveById(leaveId));
    }

    @GetMapping("/my")
    public ResponseEntity<List<LeaveResponseDto>> getMyLeaves() {
        return ResponseEntity.ok(leaveRequestService.getMyLeaves());
    }

    @GetMapping("/team")
    public ResponseEntity<List<LeaveResponseDto>> getTeamLeaves() {
        return ResponseEntity.ok(leaveRequestService.getTeamLeaves());
    }

    @GetMapping("/pending")
    public ResponseEntity<List<LeaveResponseDto>> getPendingLeaves() {
        return ResponseEntity.ok(leaveRequestService.getPendingLeaves());
    }

    @GetMapping("/approved")
    public ResponseEntity<List<LeaveResponseDto>> getApprovedLeaves() {
        return ResponseEntity.ok(leaveRequestService.getApprovedLeaves());
    }

    @GetMapping("/rejected")
    public ResponseEntity<List<LeaveResponseDto>> getRejectedLeaves() {
        return ResponseEntity.ok(leaveRequestService.getRejectedLeaves());
    }

    @PostMapping()
    public ResponseEntity<LeaveResponseDto> applyLeave(@Valid @RequestBody LeaveRequestDto request) {
        return ResponseEntity.ok(leaveRequestService.applyLeave(request));
    }

    @PatchMapping("/{id}/approve")
    public ResponseEntity<LeaveResponseDto> approveLeave(@Valid @PathVariable Long id,
            @RequestBody ApproveLeaveRequestDto request) {
        return ResponseEntity.ok(leaveRequestService.approveLeave(id, request));
    }

    @PatchMapping("/{id}/reject")
    public ResponseEntity<LeaveResponseDto> rejectLeave(@Valid @PathVariable Long id,
            @RequestBody ApproveLeaveRequestDto request) {
        return ResponseEntity.ok(leaveRequestService.rejectLeave(id, request));
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<LeaveResponseDto> cancelLeave(@Valid @PathVariable Long id) {
        return ResponseEntity.ok(leaveRequestService.cancelLeave(id));
    }
}
