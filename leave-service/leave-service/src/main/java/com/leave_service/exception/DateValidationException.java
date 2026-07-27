package com.leave_service.exception;

public class DateValidationException extends RuntimeException{

    public DateValidationException(String message){
        super(message);
    }
}
