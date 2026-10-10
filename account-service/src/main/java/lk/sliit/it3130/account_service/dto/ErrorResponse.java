package lk.sliit.it3130.account_service.dto;

import java.time.LocalDateTime;
import java.util.Map;

public class ErrorResponse {

    private String error;
    private String message;
    private LocalDateTime timestamp;
    private Map<String, String> fieldErrors;

    public ErrorResponse(String error, String message) {
        this.error = error;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }

    public ErrorResponse(String error, String message, Map<String, String> fieldErrors) {
        this(error, message);
        this.fieldErrors = fieldErrors;
    }

    public String getError() {
        return error;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public Map<String, String> getFieldErrors() {
        return fieldErrors;
    }
}