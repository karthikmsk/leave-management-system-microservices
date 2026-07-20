package com.leave_service.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.leave_service.dto.LeaveTypeRequestDto;
import com.leave_service.dto.LeaveTypeResponseDto;
import com.leave_service.exception.LeaveNotFoundException;
import com.leave_service.exception.LeaveTypeNotFoundException;
import com.leave_service.mapper.LeaveTypeMapper;
import com.leave_service.model.LeaveType;
import com.leave_service.repository.LeaveTypeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LeaveTypeService {

    private final LeaveTypeRepository leaveTypeRepository;
    private final LeaveTypeMapper leaveTypeMapper;

    private LeaveTypeResponseDto mapToResponse(LeaveType leaveType){
        return leaveTypeMapper.toResponseDto(leaveType);
    }

    public Page<LeaveTypeResponseDto> getAllLeaveTypes(Pageable pageable) {
        return leaveTypeRepository.findAll(pageable).map(leaveTypeMapper::toResponseDto);
    }

    public LeaveTypeResponseDto getLeaveTypeById(Long id){
        return mapToResponse(leaveTypeRepository.findById(id)
        .orElseThrow(() -> new LeaveNotFoundException("Leave not found")));
    }

    public LeaveTypeResponseDto createLeaveType(LeaveTypeRequestDto request){
        LeaveType leaveType = leaveTypeMapper.toEntity(request);
        leaveType.setName(request.getName());
        leaveType.setDescription(request.getDescription());
        leaveType.setAnnualAllocation(request.getAnnualAllocation());
        leaveType.setCarryForwardAllowed(request.getCarryForwardAllowed());
        leaveType.setMaxCarryForwardDays(request.getMaxCarryForwardDays());
        LeaveType savedLeaveType = leaveTypeRepository.save(leaveType);

        return leaveTypeMapper.toResponseDto(savedLeaveType);
    }

    public LeaveTypeResponseDto updateLeaveType(Long id, LeaveTypeRequestDto request){
        LeaveType leaveType = leaveTypeMapper.toEntity(request);
        leaveType.setName(request.getName());
        leaveType.setDescription(request.getDescription());
        leaveType.setAnnualAllocation(request.getAnnualAllocation());
        leaveType.setCarryForwardAllowed(request.getCarryForwardAllowed());
        leaveType.setMaxCarryForwardDays(request.getMaxCarryForwardDays());

        LeaveType savedLeaveType = leaveTypeRepository.save(leaveType);
        return leaveTypeMapper.toResponseDto(savedLeaveType);
    }

    public LeaveTypeResponseDto activateLeaveType(Long id){
        LeaveType existingleaveType = leaveTypeRepository.findById(id)
        .orElseThrow(() -> new LeaveTypeNotFoundException("Leave Type not found"));
        existingleaveType.setActive(true);
        LeaveType activatedLeaveType = leaveTypeRepository.save(existingleaveType);
        return leaveTypeMapper.toResponseDto(activatedLeaveType);
    }

    public LeaveTypeResponseDto deActivateLeaveType(Long id){
        LeaveType existingleaveType = leaveTypeRepository.findById(id)
        .orElseThrow(() -> new LeaveTypeNotFoundException("Leave Type not found"));
        existingleaveType.setActive(false);
        LeaveType activatedLeaveType = leaveTypeRepository.save(existingleaveType);
        return leaveTypeMapper.toResponseDto(activatedLeaveType);
    }
    
}
