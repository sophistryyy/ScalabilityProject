package seng468.scalability.models.Exceptions;

public class UsernameExistsException extends RuntimeException {
    public UsernameExistsException(String errorMessage) {
        super(errorMessage);
    }
    
}
