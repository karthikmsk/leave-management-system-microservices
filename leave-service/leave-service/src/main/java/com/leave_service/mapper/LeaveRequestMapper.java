package com.leave_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import com.leave_service.dto.LeaveRequestDto;
import com.leave_service.dto.LeaveResponseDto;
import com.leave_service.dto.UserResponse;
import com.leave_service.model.LeaveRequest;


@Mapper(componentModel = "spring")
public interface LeaveRequestMapper {
    @Mapping(target = "employeeName", ignore = true)
    @Mapping(target = "leaveType", ignore = true)
    LeaveResponseDto toLeaveResponseDto(LeaveRequest leaveRequest);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "employeeId", ignore = true)
    @Mapping(target = "numberOfDays", ignore = true)
    @Mapping(target = "leaveStatus", ignore = true)
    @Mapping(target = "managerComment", ignore = true)
    @Mapping(target = "reviewerId", ignore = true)
    @Mapping(target = "reviewerName", ignore = true)
    @Mapping(target = "reviewedAt", ignore = true)
    @Mapping(target = "appliedAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    LeaveRequest toLeaveRequest(LeaveRequestDto requestDto);

    @Mapping(target = "employeeId", source = "leaveRequest.employeeId")
    @Mapping(target = "employeeName", source = "employee.name")
    @Mapping(target = "leaveType", ignore = true)
    @Mapping(target = "startDate", source = "leaveRequest.startDate")
    @Mapping(target = "endDate", source = "leaveRequest.endDate")
    @Mapping(target = "numberOfDays", source = "leaveRequest.numberOfDays")
    @Mapping(target = "reason", source = "leaveRequest.reason")
    @Mapping(target = "leaveStatus", source = "leaveRequest.leaveStatus")
    @Mapping(target = "managerComment", source = "leaveRequest.managerComment")
    @Mapping(target = "reviewerId", source = "leaveRequest.reviewerId")
    @Mapping(target = "reviewerName", source = "leaveRequest.reviewerName")
    @Mapping(target = "appliedAt", source = "leaveRequest.appliedAt")
    @Mapping(target = "reviewedAt", source = "leaveRequest.reviewedAt")
    LeaveResponseDto toLeaveResponseDto(LeaveRequest leaveRequest, UserResponse employee);

}
