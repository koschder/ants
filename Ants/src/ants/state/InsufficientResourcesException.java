package ants.state;

public class InsufficientResourcesException extends RuntimeException {

    public InsufficientResourcesException(String message) {
        super(message);
    }

    private static final long serialVersionUID = 1L;

}