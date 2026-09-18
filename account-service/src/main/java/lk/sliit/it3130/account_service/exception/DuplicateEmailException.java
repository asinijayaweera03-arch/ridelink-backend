package lk.sliit.it3130.account_service.exception;

public class DuplicateEmailException extends RuntimeException {
    public DuplicateEmailException(String email) {
        super("An account with email " + email + " already exists");
    }
}