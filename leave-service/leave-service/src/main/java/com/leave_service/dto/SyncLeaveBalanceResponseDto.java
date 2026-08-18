package com.leave_service.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class SyncLeaveBalanceResponseDto {

    private int employeesProcessed;

    private int leaveBalancesCreated;

    private int leaveBalancesSkipped;

}
