package lk.sliit.it3130.ride_service.dto;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Standard error response returned by the Ride Service API.
 */
public class ApiErrorResponse {

    private String code;
    private String message;
    private LocalDateTime timestamp;
    private Map<String, Object> details;

    public ApiErrorResponse() {
    }

    public ApiErrorResponse(
            String code,
            String message,
            LocalDateTime timestamp,
            Map<String, Object> details) {

        this.code = code;
        this.message = message;
        this.timestamp = timestamp;
        this.details = details;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public Map<String, Object> getDetails() {
        return details;
    }

    public void setDetails(
            Map<String, Object> details) {
        this.details = details;
    }
}