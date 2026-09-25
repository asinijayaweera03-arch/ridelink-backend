package lk.sliit.it3130.ride_service.model;

import jakarta.persistence.*;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Represents a ride request in the Ride Management Service.
 * Stores rider, driver, location, lifecycle, and payment details.
 */
@Entity
@Table(name = "rides")
public class Ride {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Existing rider identifier used by the current API
    private Long riderId;

    // Optional passenger display name
    private String passengerName;

    // Assigned driver. Null until a driver is assigned.
    private Long driverId;

    private String pickupLocation;
    private String destination;

    private Double pickupLatitude;
    private Double pickupLongitude;

    private Double destinationLatitude;
    private Double destinationLongitude;

    // Trip distance used when completing/payment processing
    private Double distanceKm;

    // Current lifecycle status of the ride
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RideStatus status;

    // Payment recording status
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private PaymentRecordStatus paymentStatus;

    // Time when the ride request was created
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Lifecycle timestamps
    private LocalDateTime assignedAt;
    private LocalDateTime acceptedAt;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private LocalDateTime cancelledAt;

    // Default constructor required by JPA
    public Ride() {
    }

    /**
     * Initializes default values when a ride is first persisted.
     */
    @PrePersist
    protected void onCreate() {

        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }

        if (status == null) {
            status = RideStatus.REQUESTED;
        }

        if (paymentStatus == null) {
            paymentStatus = PaymentRecordStatus.NOT_APPLICABLE;
        }
    }

    /**
     * Changes the ride status only when the state-machine transition
     * is allowed and stamps the appropriate lifecycle timestamp.
     */
    public void transitionTo(RideStatus nextStatus) {

        if (nextStatus == null) {
            throw new IllegalArgumentException(
                    "Next ride status cannot be null"
            );
        }

        if (status == null) {
            throw new IllegalStateException(
                    "Current ride status is not initialized"
            );
        }

        if (!status.canTransitionTo(nextStatus)) {
            throw new IllegalStateException(
                    "Invalid ride transition from "
                            + status + " to " + nextStatus
            );
        }

        status = nextStatus;

        LocalDateTime now = LocalDateTime.now();

        switch (nextStatus) {

            case ASSIGNED ->
                    assignedAt = now;

            case ACCEPTED ->
                    acceptedAt = now;

            case IN_PROGRESS ->
                    startedAt = now;

            case COMPLETED ->
                    completedAt = now;

            case CANCELLED ->
                    cancelledAt = now;

            default -> {
                // REQUESTED is the initial state.
            }
        }
    }

    /**
     * Calculates the duration of a completed trip in whole minutes.
     *
     * @return trip duration in minutes, or null if the ride
     * has not yet been started/completed
     */
    public Long tripDurationMinutes() {

        if (startedAt == null || completedAt == null) {
            return null;
        }

        return Duration.between(
                startedAt,
                completedAt
        ).toMinutes();
    }

    // Getters and setters

    public Long getId() {
        return id;
    }

    public Long getRiderId() {
        return riderId;
    }

    public void setRiderId(Long riderId) {
        this.riderId = riderId;
    }

    public String getPassengerName() {
        return passengerName;
    }

    public void setPassengerName(String passengerName) {
        this.passengerName = passengerName;
    }

    public Long getDriverId() {
        return driverId;
    }

    public void setDriverId(Long driverId) {
        this.driverId = driverId;
    }

    public String getPickupLocation() {
        return pickupLocation;
    }

    public void setPickupLocation(String pickupLocation) {
        this.pickupLocation = pickupLocation;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public Double getPickupLatitude() {
        return pickupLatitude;
    }

    public void setPickupLatitude(Double pickupLatitude) {
        this.pickupLatitude = pickupLatitude;
    }

    public Double getPickupLongitude() {
        return pickupLongitude;
    }

    public void setPickupLongitude(Double pickupLongitude) {
        this.pickupLongitude = pickupLongitude;
    }

    public Double getDestinationLatitude() {
        return destinationLatitude;
    }

    public void setDestinationLatitude(Double destinationLatitude) {
        this.destinationLatitude = destinationLatitude;
    }

    public Double getDestinationLongitude() {
        return destinationLongitude;
    }

    public void setDestinationLongitude(Double destinationLongitude) {
        this.destinationLongitude = destinationLongitude;
    }

    public Double getDistanceKm() {
        return distanceKm;
    }

    public void setDistanceKm(Double distanceKm) {
        this.distanceKm = distanceKm;
    }

    public RideStatus getStatus() {
        return status;
    }

    public void setStatus(RideStatus status) {
        this.status = status;
    }

    public PaymentRecordStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentRecordStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getAssignedAt() {
        return assignedAt;
    }

    public LocalDateTime getAcceptedAt() {
        return acceptedAt;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public LocalDateTime getCancelledAt() {
        return cancelledAt;
    }
}