package com.leave_service.exception;

public class LeaveBaseException extends RuntimeException {

    final String errorCode;

    public LeaveBaseException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public LeaveBaseException(String message, String errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

}
