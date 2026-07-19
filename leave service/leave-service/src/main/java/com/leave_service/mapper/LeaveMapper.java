package com.leave_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.leave_service.dto.LeaveRequest;
import com.leave_service.dto.LeaveResponse;
import com.leave_service.model.Leave;

@Mapper(componentModel = "spring")
public interface LeaveMapper {
    LeaveResponse toResponse(Leave leave);

    @Mapping(target = "id", ignore = true)  
    Leave toRequest(LeaveRequest request);
}
