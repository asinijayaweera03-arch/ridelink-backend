package lk.sliit.it3130.ride_service.dto;

/**
 * Request sent from Ride Service to Payment Service
 * after a ride is completed.
 */
public class PaymentRequest {

    private Long rideId;
    private Long passengerId;
    private Double distanceKm;
    private Double durationMin;
    private Boolean peakHour;
    private String paymentMethod;

    public PaymentRequest() {
    }

    public PaymentRequest(
            Long rideId,
            Long passengerId,
            Double distanceKm,
            Double durationMin,
            Boolean peakHour,
            String paymentMethod) {

        this.rideId = rideId;
        this.passengerId = passengerId;
        this.distanceKm = distanceKm;
        this.durationMin = durationMin;
        this.peakHour = peakHour;
        this.paymentMethod = paymentMethod;
    }

    public Long getRideId() {
        return rideId;
    }

    public void setRideId(Long rideId) {
        this.rideId = rideId;
    }

    public Long getPassengerId() {
        return passengerId;
    }

    public void setPassengerId(Long passengerId) {
        this.passengerId = passengerId;
    }

    public Double getDistanceKm() {
        return distanceKm;
    }

    public void setDistanceKm(Double distanceKm) {
        this.distanceKm = distanceKm;
    }

    public Double getDurationMin() {
        return durationMin;
    }

    public void setDurationMin(Double durationMin) {
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