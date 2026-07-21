package com.leave_service.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.leave_service.dto.LeaveTypeRequestDto;
import com.leave_service.dto.LeaveTypeResponseDto;
import com.leave_service.service.LeaveTypeService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/leave-types")
public class LeaveTypeController {

    private final LeaveTypeService leaveTypeService;

    @GetMapping("/{id}")
    public ResponseEntity<LeaveTypeResponseDto> getLeaveTypeById(@PathVariable Long id){
        LeaveTypeResponseDto response = leaveTypeService.getLeaveTypeById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping()
    public ResponseEntity<Page<LeaveTypeResponseDto>> getAllLeaveTypes(@PageableDefault(size = 10, sort = "name") Pageable pageable){
        return ResponseEntity.ok(leaveTypeService.getAllLeaveTypes(pageable));
    }

    @PostMapping()
    public ResponseEntity<LeaveTypeResponseDto> addLeaveType(@RequestBody LeaveTypeRequestDto request){
        return ResponseEntity.ok(leaveTypeService.createLeaveType(request));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<LeaveTypeResponseDto> updateLeaveType(@Valid @PathVariable Long id,LeaveTypeRequestDto requestDto){
        return ResponseEntity.ok(leaveTypeService.updateLeaveType(id, requestDto));
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<LeaveTypeResponseDto> actiateLeaveType(@PathVariable Long id){
        return ResponseEntity.ok(leaveTypeService.activateLeaveType(id));
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<LeaveTypeResponseDto> deActiateLeaveType(@PathVariable Long id){
        return ResponseEntity.ok(leaveTypeService.deActivateLeaveType(id));
    }

}
