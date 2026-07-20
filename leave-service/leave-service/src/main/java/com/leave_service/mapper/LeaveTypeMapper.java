package com.leave_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.leave_service.dto.LeaveTypeRequestDto;
import com.leave_service.dto.LeaveTypeResponseDto;
import com.leave_service.model.LeaveType;

@Mapper(componentModel = "spring")
public interface LeaveTypeMapper {

    LeaveTypeResponseDto toResponseDto (LeaveType leaveType);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active",ignore = true)
    @Mapping(target = "createdAt",ignore = true)
    @Mapping(target = "updatedAt",ignore = true)
    LeaveType toEntity (LeaveTypeRequestDto requestDto);
}
