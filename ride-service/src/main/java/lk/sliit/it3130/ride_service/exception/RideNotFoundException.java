package lk.sliit.it3130.ride_service.exception;

/**
 * Thrown when a requested ride does not exist.
 */
public class RideNotFoundException extends RuntimeException {

    public RideNotFoundException(String message) {
        super(message);
    }
}