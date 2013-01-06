package ants.state;

/**
 * This exception is thrown when trying to add an ant to a task that is already at its resource limit.
 * 
 * @author kases1, kustl1
 * 
 */
public class InsufficientResourcesException extends RuntimeException {

    public InsufficientResourcesException(String message) {
        super(message);
    }

    private static final long serialVersionUID = 1L;

}