package lk.sliit.it3130.ride_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Request used to retry payment processing
 * for a completed ride.
 */
@Schema(description = "Request body for retrying payment processing")
public class PaymentRetryRequest {

    @NotNull(message = "Peak hour value is required")
    @Schema(
            description = "Indicates whether peak-hour pricing applies",
            example = "false"
    )
    private Boolean peakHour;

    @NotBlank(message = "Payment method is required")
    @Schema(
            description = "Simulated payment method",
            example = "CASH"
    )
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