package com.leave_service.exception;


public class LeaveBalanceAlreadyExistsException extends RuntimeException{
    public LeaveBalanceAlreadyExistsException(String message){
        super(message);
    }
}
