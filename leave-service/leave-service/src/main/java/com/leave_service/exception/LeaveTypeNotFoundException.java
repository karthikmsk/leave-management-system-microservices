package com.leave_service.exception;

public class LeaveTypeNotFoundException extends RuntimeException{
    public LeaveTypeNotFoundException(String message){
        super(message);
    }
}
