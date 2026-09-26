package lk.sliit.it3130.ride_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Contains the payment-related details supplied
 * when completing a ride.
 *
 * Distance is taken from the Ride entity and trip
 * duration is calculated from lifecycle timestamps.
 */
@Schema(description = "Payment details supplied when completing a ride")
public class RideCompletionRequest {

    @NotNull(message = "Peak hour value is required")
    @Schema(
            description = "Indicates whether the ride was completed during peak hours",
            example = "false"
    )
    private Boolean peakHour;

    @NotBlank(message = "Payment method is required")
    @Schema(
            description = "Simulated payment method",
            example = "CASH"
    )
    private String paymentMethod;

    public RideCompletionRequest() {
    }

    public Boolean getPeakHour() {
        return peakHour;
    }

    public void setPeakHour(Boolean peakHour) {
        this.peakHour = peakHour;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
}