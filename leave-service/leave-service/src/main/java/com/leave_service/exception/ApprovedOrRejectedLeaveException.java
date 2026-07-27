package com.leave_service.exception;

import jakarta.ws.rs.BadRequestException;

public class ApprovedOrRejectedLeaveException extends BadRequestException{
    public ApprovedOrRejectedLeaveException(String message){
        super(message);
    }
}
