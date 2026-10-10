package lk.sliit.it3130.ride_service.exception;

import lk.sliit.it3130.ride_service.dto.ApiErrorResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    void rideNotFoundShouldReturn404() {

        RideNotFoundException exception =
                new RideNotFoundException(
                        "Ride not found: 99"
                );

        ResponseEntity<ApiErrorResponse> response =
                handler.handleRideNotFoundException(
                        exception
                );

        assertEquals(
                HttpStatus.NOT_FOUND,
                response.getStatusCode()
        );

        assertNotNull(response.getBody());

        assertEquals(
                "RIDE_NOT_FOUND",
                response.getBody().getCode()
        );

        assertEquals(
                "Ride not found: 99",
                response.getBody().getMessage()
        );

        assertNotNull(
                response.getBody().getTimestamp()
        );

        assertNotNull(
                response.getBody().getDetails()
        );

        assertTrue(
                response.getBody()
                        .getDetails()
                        .isEmpty()
        );
    }

    @Test
    void noAvailableDriverShouldReturn409() {

        NoAvailableDriverException exception =
                new NoAvailableDriverException(
                        "No available driver found"
                );

        ResponseEntity<ApiErrorResponse> response =
                handler.handleNoAvailableDriverException(
                        exception
                );

        assertEquals(
                HttpStatus.CONFLICT,
                response.getStatusCode()
        );

        assertNotNull(response.getBody());

        assertEquals(
                "NO_AVAILABLE_DRIVER",
                response.getBody().getCode()
        );

        assertEquals(
                "No available driver found",
                response.getBody().getMessage()
        );

        assertNotNull(
                response.getBody().getTimestamp()
        );

        assertTrue(
                response.getBody()
                        .getDetails()
                        .isEmpty()
        );
    }

    @Test
    void driverServiceUnavailableShouldReturn503() {

        DriverServiceUnavailableException exception =
                new DriverServiceUnavailableException(
                        "Driver Service is unavailable"
                );

        ResponseEntity<ApiErrorResponse> response =
                handler.handleDriverServiceUnavailableException(
                        exception
                );

        assertEquals(
                HttpStatus.SERVICE_UNAVAILABLE,
                response.getStatusCode()
        );

        assertNotNull(response.getBody());

        assertEquals(
                "DRIVER_SERVICE_UNAVAILABLE",
                response.getBody().getCode()
        );

        assertEquals(
                "Driver Service is unavailable",
                response.getBody().getMessage()
        );

        assertNotNull(
                response.getBody().getTimestamp()
        );

        assertTrue(
                response.getBody()
                        .getDetails()
                        .isEmpty()
        );
    }

    @Test
    void paymentServiceUnavailableShouldReturn503() {

        PaymentServiceUnavailableException exception =
                new PaymentServiceUnavailableException(
                        "Payment Service is unavailable"
                );

        ResponseEntity<ApiErrorResponse> response =
                handler.handlePaymentServiceUnavailableException(
                        exception
                );

        assertEquals(
                HttpStatus.SERVICE_UNAVAILABLE,
                response.getStatusCode()
        );

        assertNotNull(response.getBody());

        assertEquals(
                "PAYMENT_SERVICE_UNAVAILABLE",
                response.getBody().getCode()
        );

        assertEquals(
                "Payment Service is unavailable",
                response.getBody().getMessage()
        );

        assertNotNull(
                response.getBody().getTimestamp()
        );

        assertTrue(
                response.getBody()
                        .getDetails()
                        .isEmpty()
        );
    }

    @Test
    void illegalStateShouldReturn400() {

        IllegalStateException exception =
                new IllegalStateException(
                        "Invalid ride state"
                );

        ResponseEntity<ApiErrorResponse> response =
                handler.handleIllegalStateException(
                        exception
                );

        assertEquals(
                HttpStatus.BAD_REQUEST,
                response.getStatusCode()
        );

        assertNotNull(response.getBody());

        assertEquals(
                "INVALID_RIDE_STATE",
                response.getBody().getCode()
        );

        assertEquals(
                "Invalid ride state",
                response.getBody().getMessage()
        );

        assertNotNull(
                response.getBody().getTimestamp()
        );

        assertTrue(
                response.getBody()
                        .getDetails()
                        .isEmpty()
        );
    }
}