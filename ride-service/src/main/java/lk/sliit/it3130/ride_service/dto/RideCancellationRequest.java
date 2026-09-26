package lk.sliit.it3130.ride_service.dto;

import jakarta.validation.constraints.Size;

/**
 * Request DTO used when cancelling a ride.
 * The cancellation reason is optional.
 */
public class RideCancellationRequest {

    @Size(
            max = 500,
            message = "Cancellation reason must not exceed 500 characters"
    )
    private String reason;

    public RideCancellationRequest() {
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}