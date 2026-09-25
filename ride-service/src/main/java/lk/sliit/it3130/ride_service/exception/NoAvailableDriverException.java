package lk.sliit.it3130.ride_service.exception;

/**
 * Thrown when no available driver can be found
 * for a new ride request.
 */
public class NoAvailableDriverException extends RuntimeException {

    public NoAvailableDriverException(String message) {
        super(message);
    }
}