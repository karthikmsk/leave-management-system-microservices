package com.leave_service.exception;

import jakarta.ws.rs.BadRequestException;

public class NotEnoughLeaveBalanceException extends BadRequestException{
    public NotEnoughLeaveBalanceException(String message){
        super(message);
    }
}
