package com.leave_service.exception;

public class LeaveNotFoundException extends RuntimeException {

    public LeaveNotFoundException(String message) {
        super(message);
    }
}
