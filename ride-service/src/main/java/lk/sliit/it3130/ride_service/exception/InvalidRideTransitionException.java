package lk.sliit.it3130.ride_service.exception;

import lk.sliit.it3130.ride_service.model.RideStatus;

import java.util.Set;

/**
 * Thrown when an invalid ride lifecycle transition is attempted.
 */
public class InvalidRideTransitionException extends RuntimeException {

    private final RideStatus currentStatus;
    private final Set<RideStatus> allowedNextStates;

    public InvalidRideTransitionException(
            RideStatus currentStatus,
            Set<RideStatus> allowedNextStates) {

        super(
                "Invalid ride transition from "
                        + currentStatus
                        + ". Allowed next states: "
                        + allowedNextStates
        );

        this.currentStatus = currentStatus;
        this.allowedNextStates = allowedNextStates;
    }

    public RideStatus getCurrentStatus() {
        return currentStatus;
    }

    public Set<RideStatus> getAllowedNextStates() {
        return allowedNextStates;
    }
}