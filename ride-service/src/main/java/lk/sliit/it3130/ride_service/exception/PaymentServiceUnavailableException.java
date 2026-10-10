package lk.sliit.it3130.ride_service.exception;

/**
 * Thrown when Ride Service cannot communicate
 * with Payment Service.
 */
public class PaymentServiceUnavailableException
        extends RuntimeException {

    public PaymentServiceUnavailableException(
            String message) {

        super(message);
    }

    public PaymentServiceUnavailableException(
            String message,
            Throwable cause) {

        super(message, cause);
    }
}