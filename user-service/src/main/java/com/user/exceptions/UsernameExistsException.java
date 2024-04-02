package com.user.exceptions;


public class UsernameExistsException extends RuntimeException {
    public UsernameExistsException(String errorMessage) {
        super(errorMessage);
    }
    
}
