package com.leave_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.leave_service.dto.LeaveTypeRequestDto;
import com.leave_service.dto.LeaveTypeResponseDto;
import com.leave_service.model.LeaveType;

@Mapper(componentModel = "spring")
public interface LeaveTypeMapper {

    @Mapping(target = "name", ignore = true)
    @Mapping(target = "description", ignore = true)
    @Mapping(target = "annualAllocation", ignore = true)
    @Mapping(target = "carryForwardAllowed", ignore = true)
    @Mapping(target = "maxCarryForwardDays", ignore = true)
    @Mapping(target = "isActive",ignore = true)
    LeaveTypeResponseDto toResponseDto (LeaveType leaveType);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active",ignore = true)
    @Mapping(target = "createdAt",ignore = true)
    @Mapping(target = "updatedAt",ignore = true)
    LeaveType toEntity (LeaveTypeRequestDto requestDto);
}
