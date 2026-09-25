package lk.sliit.it3130.ride_service.model;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class RideTest {

    @Test
    void onCreateShouldSetDefaultStatusAndPaymentStatus() {
        Ride ride = new Ride();

        ride.onCreate();

        assertEquals(
                RideStatus.REQUESTED,
                ride.getStatus()
        );

        assertEquals(
                PaymentRecordStatus.NOT_APPLICABLE,
                ride.getPaymentStatus()
        );

        assertNotNull(ride.getCreatedAt());
    }

    @Test
    void transitionToAssignedShouldStampAssignedTime() {
        Ride ride = new Ride();
        ride.setStatus(RideStatus.REQUESTED);

        ride.transitionTo(RideStatus.ASSIGNED);

        assertEquals(
                RideStatus.ASSIGNED,
                ride.getStatus()
        );

        assertNotNull(ride.getAssignedAt());
    }

    @Test
    void lifecycleTransitionsShouldStampTimestamps() {
        Ride ride = new Ride();
        ride.setStatus(RideStatus.REQUESTED);

        ride.transitionTo(RideStatus.ASSIGNED);
        ride.transitionTo(RideStatus.ACCEPTED);
        ride.transitionTo(RideStatus.IN_PROGRESS);
        ride.transitionTo(RideStatus.COMPLETED);

        assertEquals(
                RideStatus.COMPLETED,
                ride.getStatus()
        );

        assertNotNull(ride.getAssignedAt());
        assertNotNull(ride.getAcceptedAt());
        assertNotNull(ride.getStartedAt());
        assertNotNull(ride.getCompletedAt());
    }

    @Test
    void cancellationShouldStampCancelledTime() {
        Ride ride = new Ride();
        ride.setStatus(RideStatus.REQUESTED);

        ride.transitionTo(RideStatus.CANCELLED);

        assertEquals(
                RideStatus.CANCELLED,
                ride.getStatus()
        );

        assertNotNull(ride.getCancelledAt());
    }

    @Test
    void invalidTransitionShouldThrowException() {
        Ride ride = new Ride();
        ride.setStatus(RideStatus.IN_PROGRESS);

        IllegalStateException exception =
                assertThrows(
                        IllegalStateException.class,
                        () -> ride.transitionTo(
                                RideStatus.CANCELLED
                        )
                );

        assertTrue(
                exception.getMessage().contains(
                        "Invalid ride transition"
                )
        );
    }

    @Test
    void tripDurationShouldBeNullBeforeRideIsCompleted() {
        Ride ride = new Ride();
        ride.setStatus(RideStatus.REQUESTED);

        ride.transitionTo(RideStatus.ASSIGNED);
        ride.transitionTo(RideStatus.ACCEPTED);
        ride.transitionTo(RideStatus.IN_PROGRESS);

        assertNull(
                ride.tripDurationMinutes()
        );
    }

    @Test
    void tripDurationShouldCalculateExactMinutes() {
        Ride ride = new Ride();

        LocalDateTime startedAt =
                LocalDateTime.of(
                        2026,
                        1,
                        1,
                        10,
                        0
                );

        LocalDateTime completedAt =
                startedAt.plusMinutes(42);

        ReflectionTestUtils.setField(
                ride,
                "startedAt",
                startedAt
        );

        ReflectionTestUtils.setField(
                ride,
                "completedAt",
                completedAt
        );

        assertEquals(
                42L,
                ride.tripDurationMinutes()
        );
    }
}