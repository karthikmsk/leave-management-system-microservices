package com.user_service.exception;

public class UserNotManagerException extends RuntimeException{
    public UserNotManagerException(String message){
        super(message);
    }
}
