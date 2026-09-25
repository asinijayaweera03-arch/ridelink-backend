package lk.sliit.it3130.ride_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

/**
 * Contains the final ride details required when completing a ride.
 * These details are used to create the payment.
 */
public class RideCompletionRequest {

    @NotNull
    @Positive
    private Double distanceKm;

    @NotNull
    @PositiveOrZero
    private Integer durationMin;

    @NotNull
    private Boolean peakHour;

    @NotBlank
    private String paymentMethod;

    public RideCompletionRequest() {
    }

    public Double getDistanceKm() {
        return distanceKm;
    }

    public void setDistanceKm(Double distanceKm) {
        this.distanceKm = distanceKm;
    }

    public Integer getDurationMin() {
        return durationMin;
    }

    public void setDurationMin(Integer durationMin) {
        this.durationMin = durationMin;
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