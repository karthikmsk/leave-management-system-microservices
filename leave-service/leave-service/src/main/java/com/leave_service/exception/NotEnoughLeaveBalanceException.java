package com.leave_service.exception;

public class NotEnoughLeaveBalanceException extends RuntimeException{
    public NotEnoughLeaveBalanceException(String message){
        super(message);
    }
}
