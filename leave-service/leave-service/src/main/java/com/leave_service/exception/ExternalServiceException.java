package com.leave_service.exception;

public class ExternalServiceException extends LeaveBaseException {

    public ExternalServiceException(String message, Throwable cause) {
        super(message,"EXTERNAL_SERVICE_ERROR", cause);
    }
}
