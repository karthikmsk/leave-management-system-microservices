package com.leave_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.leave_service.dto.LeaveRequestDto;
import com.leave_service.dto.LeaveResponseDto;
import com.leave_service.model.LeaveRequest;


@Mapper(componentModel = "spring")
public interface LeaveMapper {
    @Mapping(target = "employeeName", ignore = true)
    @Mapping(target = "leaveType", ignore = true)
    LeaveResponseDto toLeaveResponseDto(LeaveRequest leaveRequest);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "employeeId", ignore = true)
    @Mapping(target = "numberOfDays", ignore = true)
    @Mapping(target = "leaveStatus", ignore = true)
    @Mapping(target = "managerComment", ignore = true)
    @Mapping(target = "approverId", ignore = true)
    @Mapping(target = "appliedAt", ignore = true)
    LeaveRequest toLeaveRequest(LeaveRequestDto requestDto);

}
