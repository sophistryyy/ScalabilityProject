package com.wallet.temp;

public class UsernameExistsException extends RuntimeException {
    public UsernameExistsException(String errorMessage) {
        super(errorMessage);
    }
    
}
