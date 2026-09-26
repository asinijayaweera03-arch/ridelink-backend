package lk.sliit.it3130.ride_service.exception;

import lk.sliit.it3130.ride_service.dto.ApiErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Handles exceptions globally for the Ride Service API.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles cases where a requested ride does not exist.
     */
    @ExceptionHandler(RideNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleRideNotFoundException(
            RideNotFoundException exception) {

        return buildResponse(
                HttpStatus.NOT_FOUND,
                "RIDE_NOT_FOUND",
                exception.getMessage(),
                Map.of()
        );
    }

    /**
     * Handles invalid ride lifecycle transitions.
     */
    @ExceptionHandler(InvalidRideTransitionException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidRideTransitionException(
            InvalidRideTransitionException exception) {

        Map<String, Object> details =
                new LinkedHashMap<>();

        details.put(
                "currentStatus",
                exception.getCurrentStatus()
        );

        details.put(
                "allowedNextStates",
                exception.getAllowedNextStates()
        );

        return buildResponse(
                HttpStatus.CONFLICT,
                "INVALID_RIDE_TRANSITION",
                "Invalid ride transition",
                details
        );
    }

    /**
     * Handles cases where no driver is currently available.
     */
    @ExceptionHandler(NoAvailableDriverException.class)
    public ResponseEntity<ApiErrorResponse> handleNoAvailableDriverException(
            NoAvailableDriverException exception) {

        return buildResponse(
                HttpStatus.CONFLICT,
                "NO_AVAILABLE_DRIVER",
                exception.getMessage(),
                Map.of()
        );
    }

    /**
     * Handles cases where Driver Service cannot be reached.
     */
    @ExceptionHandler(DriverServiceUnavailableException.class)
    public ResponseEntity<ApiErrorResponse> handleDriverServiceUnavailableException(
            DriverServiceUnavailableException exception) {

        return buildResponse(
                HttpStatus.SERVICE_UNAVAILABLE,
                "DRIVER_SERVICE_UNAVAILABLE",
                exception.getMessage(),
                Map.of()
        );
    }

    /**
     * Handles cases where Payment Service cannot be reached.
     */
    @ExceptionHandler(PaymentServiceUnavailableException.class)
    public ResponseEntity<ApiErrorResponse> handlePaymentServiceUnavailableException(
            PaymentServiceUnavailableException exception) {

        return buildResponse(
                HttpStatus.SERVICE_UNAVAILABLE,
                "PAYMENT_SERVICE_UNAVAILABLE",
                exception.getMessage(),
                Map.of()
        );
    }

    /**
     * Handles invalid business-state operations.
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalStateException(
            IllegalStateException exception) {

        return buildResponse(
                HttpStatus.BAD_REQUEST,
                "INVALID_RIDE_STATE",
                exception.getMessage(),
                Map.of()
        );
    }

    /**
     * Handles request validation failures.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationException(
            MethodArgumentNotValidException exception) {

        Map<String, Object> details =
                new LinkedHashMap<>();

        exception.getBindingResult()
                .getFieldErrors()
                .forEach(error ->
                        details.put(
                                error.getField(),
                                error.getDefaultMessage()
                        )
                );

        return buildResponse(
                HttpStatus.BAD_REQUEST,
                "VALIDATION_ERROR",
                "Request validation failed",
                details
        );
    }

    /**
     * Creates the common API error structure.
     */
    private ResponseEntity<ApiErrorResponse> buildResponse(
            HttpStatus status,
            String code,
            String message,
            Map<String, Object> details) {

        ApiErrorResponse errorResponse =
                new ApiErrorResponse(
                        code,
                        message,
                        LocalDateTime.now(),
                        details
                );

        return ResponseEntity
                .status(status)
                .body(errorResponse);
    }
}