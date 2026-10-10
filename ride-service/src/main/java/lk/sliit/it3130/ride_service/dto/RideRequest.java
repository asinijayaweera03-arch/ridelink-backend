package lk.sliit.it3130.ride_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * Request DTO used when a rider creates a new ride request.
 */
@Schema(description = "Request body for creating a new ride")
public class RideRequest {

    @NotNull(message = "Rider ID is required")
    @Schema(
            description = "Unique passenger identifier",
            example = "101"
    )
    private Long riderId;

    @NotBlank(message = "Pickup location is required")
    @Schema(
            description = "Pickup location name",
            example = "SLIIT Malabe Campus"
    )
    private String pickupLocation;

    @NotBlank(message = "Destination is required")
    @Schema(
            description = "Destination location name",
            example = "Battaramulla"
    )
    private String destination;

    @NotNull(message = "Pickup latitude is required")
    @DecimalMin(value = "-90.0", message = "Pickup latitude must be at least -90")
    @DecimalMax(value = "90.0", message = "Pickup latitude must be at most 90")
    @Schema(
            description = "Pickup latitude",
            example = "6.9147"
    )
    private Double pickupLatitude;

    @NotNull(message = "Pickup longitude is required")
    @DecimalMin(value = "-180.0", message = "Pickup longitude must be at least -180")
    @DecimalMax(value = "180.0", message = "Pickup longitude must be at most 180")
    @Schema(
            description = "Pickup longitude",
            example = "79.9729"
    )
    private Double pickupLongitude;

    @NotNull(message = "Destination latitude is required")
    @DecimalMin(value = "-90.0", message = "Destination latitude must be at least -90")
    @DecimalMax(value = "90.0", message = "Destination latitude must be at most 90")
    @Schema(
            description = "Destination latitude",
            example = "6.9022"
    )
    private Double destinationLatitude;

    @NotNull(message = "Destination longitude is required")
    @DecimalMin(value = "-180.0", message = "Destination longitude must be at least -180")
    @DecimalMax(value = "180.0", message = "Destination longitude must be at most 180")
    @Schema(
            description = "Destination longitude",
            example = "79.9182"
    )
    private Double destinationLongitude;

    @NotNull(message = "Distance is required")
    @Positive(message = "Distance must be greater than 0")
    @Schema(
            description = "Estimated ride distance in kilometres",
            example = "8.5"
    )
    private Double distanceKm;

    public Long getRiderId() {
        return riderId;
    }

    public void setRiderId(Long riderId) {
        this.riderId = riderId;
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
}