package com.leave_service.exception;

public class LeaveBalanceNotFoundException extends RuntimeException{
    public LeaveBalanceNotFoundException(String message){
        super(message);
    }
}
