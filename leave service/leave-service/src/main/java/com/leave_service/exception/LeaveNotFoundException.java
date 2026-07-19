package com.leave_service.exception;

public class LeaveNotFoundException extends LeaveBaseException {

    public LeaveNotFoundException(String message,Throwable cause) {
        super(message,"LEAVE_NOT_FOUND", cause);
    }
}
