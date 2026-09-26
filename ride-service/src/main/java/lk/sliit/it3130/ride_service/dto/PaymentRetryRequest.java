package lk.sliit.it3130.ride_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Request used to retry payment processing
 * for a completed ride.
 */
public class PaymentRetryRequest {

    @NotNull(message = "Peak hour value is required")
    private Boolean peakHour;

    @NotBlank(message = "Payment method is required")
    private String paymentMethod;

    public PaymentRetryRequest() {
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