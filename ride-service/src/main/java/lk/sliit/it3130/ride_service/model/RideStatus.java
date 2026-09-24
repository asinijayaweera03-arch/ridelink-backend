package lk.sliit.it3130.ride_service.model;

/**
 * Represents the different stages of a ride.
 */
public enum RideStatus {

    // Ride has been requested by a rider
    REQUESTED,

    // A driver has been assigned to the ride
    ASSIGNED,

    // Assigned driver has accepted the ride
    ACCEPTED,

    // Ride is currently in progress
    IN_PROGRESS,

    // Ride has been successfully completed
    COMPLETED,

    // Ride has been cancelled
    CANCELLED
}