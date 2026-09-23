package lk.sliit.it3130.account_service.exception;

public class ForbiddenException extends RuntimeException {
    public ForbiddenException() {
        super("You do not have permission to access this resource");
    }
}