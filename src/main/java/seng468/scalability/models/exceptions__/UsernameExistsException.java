package seng468.scalability.models.exceptions__;

public class UsernameExistsException extends RuntimeException {
    public UsernameExistsException(String errorMessage) {
        super(errorMessage);
    }
    
}
