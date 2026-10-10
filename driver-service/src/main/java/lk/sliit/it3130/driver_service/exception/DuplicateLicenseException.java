package lk.sliit.it3130.driver_service.exception;

public class DuplicateLicenseException extends RuntimeException {
    public DuplicateLicenseException(String message) {
        super(message);
    }
}
