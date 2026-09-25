package lk.sliit.it3130.ride_service.model;

import java.util.Set;

/**
 * Represents the different stages of a ride
 * and defines the valid lifecycle transitions.
 */
public enum RideStatus {

    REQUESTED,
    ASSIGNED,
    ACCEPTED,
    IN_PROGRESS,
    COMPLETED,
    CANCELLED;

    /**
     * Returns the ride statuses that can legally follow
     * the current status.
     *
     * @return allowed next ride statuses
     */
    public Set<RideStatus> allowedNextStates() {

        return switch (this) {

            case REQUESTED ->
                    Set.of(ASSIGNED, CANCELLED);

            case ASSIGNED ->
                    Set.of(ACCEPTED, CANCELLED);

            case ACCEPTED ->
                    Set.of(IN_PROGRESS, CANCELLED);

            case IN_PROGRESS ->
                    Set.of(COMPLETED);

            case COMPLETED, CANCELLED ->
                    Set.of();
        };
    }

    /**
     * Checks whether a transition from the current status
     * to the supplied status is allowed.
     */
    public boolean canTransitionTo(RideStatus nextStatus) {
        return allowedNextStates().contains(nextStatus);
    }
}