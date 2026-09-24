package lk.sliit.it3130.ride_service.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Represents a ride request in the Ride Management Service.
 * Stores rider, driver, location, status, and creation details.
 */
@Entity
@Table(name = "rides")
public class Ride {

    // Unique ID automatically generated for each ride
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ID of the rider who requested the ride
    private Long riderId;

    // ID of the assigned driver
    // This can be null until a driver is assigned
    private Long driverId;

    // Pickup location name or address
    private String pickupLocation;

    // Destination location name or address
    private String destination;

    // Pickup location coordinates
    private Double pickupLatitude;
    private Double pickupLongitude;

    // Destination location coordinates
    private Double destinationLatitude;
    private Double destinationLongitude;

    // Current status of the ride
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RideStatus status;

    // Date and time when the ride request was created
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Default constructor required by JPA
    public Ride() {
    }

    /**
     * Runs automatically before a new ride is saved.
     * Sets the creation time and default ride status.
     */
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();

        // A newly created ride starts with REQUESTED status
        if (this.status == null) {
            this.status = RideStatus.REQUESTED;
        }
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

    public RideStatus getStatus() {
        return status;
    }

    public void setStatus(RideStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}