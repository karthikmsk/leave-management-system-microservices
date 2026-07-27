package com.leave_service.exception;

import jakarta.ws.rs.BadRequestException;

public class ValidManagerException extends BadRequestException{
    public ValidManagerException(String message){
        super(message);
    }
}
