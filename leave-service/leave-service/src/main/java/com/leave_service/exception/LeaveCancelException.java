package com.leave_service.exception;

import jakarta.ws.rs.BadRequestException;

public class LeaveCancelException extends BadRequestException{
    public LeaveCancelException(String message){
        super(message);
    }
}
