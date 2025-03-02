package com.doziem.jamTesSystem.exceptions;

public class UserNotAllowedException extends RuntimeException{
    public UserNotAllowedException(String message){
        super(message);
    }
}
