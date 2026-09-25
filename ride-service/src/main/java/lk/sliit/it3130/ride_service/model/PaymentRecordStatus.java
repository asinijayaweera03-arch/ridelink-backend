package lk.sliit.it3130.ride_service.model;

/**
 * Represents the payment recording state of a ride.
 */
public enum PaymentRecordStatus {

    // Payment is not required yet
    NOT_APPLICABLE,

    // Ride is completed but payment still needs to be recorded
    PENDING,

    // Payment was successfully recorded by Payment Service
    RECORDED,

    // Payment recording failed
    FAILED
}