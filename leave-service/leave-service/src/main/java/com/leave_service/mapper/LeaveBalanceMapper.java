package com.leave_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.leave_service.dto.LeaveBalanceDto;
import com.leave_service.model.LeaveBalance;

@Mapper(componentModel = "spring")
public interface LeaveBalanceMapper {

    @Mapping(target = "employeeName", ignore = true)
    @Mapping(target = "leaveTypeName", ignore = true)
    LeaveBalanceDto toLeaveBalanceDto(LeaveBalance leaveBalance);

}
