package lk.sliit.it3130.ride_service.exception;

/**
 * Thrown when Ride Service cannot communicate with Driver Service.
 */
public class DriverServiceUnavailableException extends RuntimeException {

    public DriverServiceUnavailableException(String message) {
        super(message);
    }

    public DriverServiceUnavailableException(
            String message,
            Throwable cause) {

        super(message, cause);
    }
}