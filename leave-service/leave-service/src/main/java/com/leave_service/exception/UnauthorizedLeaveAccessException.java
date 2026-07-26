package com.leave_service.exception;

import jakarta.ws.rs.BadRequestException;

public class UnauthorizedLeaveAccessException extends BadRequestException{
    public UnauthorizedLeaveAccessException(String message){
        super(message);
    }
}
