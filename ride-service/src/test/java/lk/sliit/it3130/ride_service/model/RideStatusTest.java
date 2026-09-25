package lk.sliit.it3130.ride_service.model;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class RideStatusTest {

    @Test
    void requestedShouldAllowAssignedAndCancelled() {
        assertEquals(
                Set.of(RideStatus.ASSIGNED, RideStatus.CANCELLED),
                RideStatus.REQUESTED.allowedNextStates()
        );
    }

    @Test
    void assignedShouldAllowAcceptedAndCancelled() {
        assertEquals(
                Set.of(RideStatus.ACCEPTED, RideStatus.CANCELLED),
                RideStatus.ASSIGNED.allowedNextStates()
        );
    }

    @Test
    void acceptedShouldAllowInProgressAndCancelled() {
        assertEquals(
                Set.of(RideStatus.IN_PROGRESS, RideStatus.CANCELLED),
                RideStatus.ACCEPTED.allowedNextStates()
        );
    }

    @Test
    void inProgressShouldOnlyAllowCompleted() {
        assertEquals(
                Set.of(RideStatus.COMPLETED),
                RideStatus.IN_PROGRESS.allowedNextStates()
        );
    }

    @Test
    void completedShouldNotAllowAnyNextState() {
        assertTrue(
                RideStatus.COMPLETED.allowedNextStates().isEmpty()
        );
    }

    @Test
    void cancelledShouldNotAllowAnyNextState() {
        assertTrue(
                RideStatus.CANCELLED.allowedNextStates().isEmpty()
        );
    }

    @Test
    void canTransitionToShouldReturnCorrectResult() {
        assertTrue(
                RideStatus.ASSIGNED.canTransitionTo(RideStatus.ACCEPTED)
        );

        assertFalse(
                RideStatus.IN_PROGRESS.canTransitionTo(RideStatus.CANCELLED)
        );

        assertFalse(
                RideStatus.COMPLETED.canTransitionTo(RideStatus.REQUESTED)
        );
    }
}