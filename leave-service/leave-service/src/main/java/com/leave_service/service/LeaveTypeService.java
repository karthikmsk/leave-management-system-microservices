package com.leave_service.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.leave_service.dto.LeaveTypeRequestDto;
import com.leave_service.dto.LeaveTypeResponseDto;
import com.leave_service.exception.DuplicateLeaveTypeException;
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

    private LeaveTypeResponseDto mapToResponse(LeaveType leaveType) {
        return leaveTypeMapper.toResponseDto(leaveType);
    }

    private LeaveTypeResponseDto updateLeaveTypeStatus(Long id, boolean active) {
        LeaveType existingLeaveType = leaveTypeRepository.findById(id)
                .orElseThrow(() -> new LeaveTypeNotFoundException("Leave Type not found"));
        existingLeaveType.setActive(active);
        LeaveType updatedLeaveType = leaveTypeRepository.save(existingLeaveType);
        return mapToResponse(updatedLeaveType);
    }

    public Page<LeaveTypeResponseDto> getAllLeaveTypes(Pageable pageable) {
        return leaveTypeRepository.findAll(pageable).map(leaveTypeMapper::toResponseDto);
    }

    public LeaveTypeResponseDto getLeaveTypeById(Long id) {
        return mapToResponse(leaveTypeRepository.findById(id)
                .orElseThrow(() -> new LeaveTypeNotFoundException("Leave not found")));
    }

    @Transactional
    public LeaveTypeResponseDto createLeaveType(LeaveTypeRequestDto request) {
        if (leaveTypeRepository.existsByName(request.getName())) {
            throw new DuplicateLeaveTypeException("Leave type already exists");
        }
        LeaveType leaveType = leaveTypeMapper.toEntity(request);
        leaveType.setActive(true);
        LeaveType savedLeaveType = leaveTypeRepository.save(leaveType);

        return leaveTypeMapper.toResponseDto(savedLeaveType);
    }

    @Transactional
    public LeaveTypeResponseDto updateLeaveType(Long id, LeaveTypeRequestDto request) {
        LeaveType existingLeaveType = leaveTypeRepository.findById(id)
                .orElseThrow(() -> new LeaveTypeNotFoundException("LeaveType not found"));
        existingLeaveType.setName(request.getName());
        existingLeaveType.setDescription(request.getDescription());
        existingLeaveType.setAnnualAllocation(request.getAnnualAllocation());
        existingLeaveType.setCarryForwardAllowed(request.getCarryForwardAllowed());
        if (request.getCarryForwardAllowed()) {
            if (request.getMaxCarryForwardDays() < 0) {
                throw new IllegalArgumentException("Max carry forward days cannot be negative.");
            }
            existingLeaveType.setMaxCarryForwardDays(request.getMaxCarryForwardDays());
        } else {
            existingLeaveType.setMaxCarryForwardDays(0F);
        }

        LeaveType savedLeaveType = leaveTypeRepository.save(existingLeaveType);
        return mapToResponse(savedLeaveType);
    }

    @Transactional
    public LeaveTypeResponseDto activateLeaveType(Long id) {
        return updateLeaveTypeStatus(id, true);
    }

    @Transactional
    public LeaveTypeResponseDto deActivateLeaveType(Long id) {
        return updateLeaveTypeStatus(id, false);
    }

}
