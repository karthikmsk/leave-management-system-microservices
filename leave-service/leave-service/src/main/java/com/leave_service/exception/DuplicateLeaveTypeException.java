package com.leave_service.exception;

public class DuplicateLeaveTypeException extends RuntimeException{
    public DuplicateLeaveTypeException(String message){
        super(message);
    }
}
