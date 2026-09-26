package lk.sliit.it3130.ride_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Contains the payment-related details supplied
 * when completing a ride.
 *
 * Distance is taken from the Ride entity and trip
 * duration is calculated from lifecycle timestamps.
 */
public class RideCompletionRequest {

    @NotNull(message = "Peak hour value is required")
    private Boolean peakHour;

    @NotBlank(message = "Payment method is required")
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