package com.leave_service.exception;

import jakarta.ws.rs.BadRequestException;

public class LeaveBalanceAlreadyExistsException extends BadRequestException{
    public LeaveBalanceAlreadyExistsException(String message){
        super(message);
    }
}
