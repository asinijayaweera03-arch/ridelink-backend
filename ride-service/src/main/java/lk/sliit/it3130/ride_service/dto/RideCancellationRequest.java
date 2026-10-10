package lk.sliit.it3130.ride_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

/**
 * Request DTO used when cancelling a ride.
 * The cancellation reason is optional.
 */
@Schema(description = "Optional cancellation details for a ride")
public class RideCancellationRequest {

    @Size(
            max = 500,
            message = "Cancellation reason must not exceed 500 characters"
    )
    @Schema(
            description = "Optional reason for cancelling the ride",
            example = "Passenger plans changed"
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