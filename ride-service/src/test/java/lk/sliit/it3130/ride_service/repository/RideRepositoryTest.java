package lk.sliit.it3130.ride_service.repository;

import lk.sliit.it3130.ride_service.model.PaymentRecordStatus;
import lk.sliit.it3130.ride_service.model.Ride;
import lk.sliit.it3130.ride_service.model.RideStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests database persistence and query operations for Ride entities.
 */
@SpringBootTest
class RideRepositoryTest {

    @Autowired
    private RideRepository rideRepository;

    /**
     * Creates a ride with common test data.
     */
    private Ride createRide(
            Long riderId,
            Long driverId,
            RideStatus status) {

        Ride ride = new Ride();

        ride.setRiderId(riderId);
        ride.setDriverId(driverId);

        ride.setPickupLocation("Colombo");
        ride.setDestination("Kandy");

        ride.setPickupLatitude(6.9271);
        ride.setPickupLongitude(79.8612);

        ride.setDestinationLatitude(7.2906);
        ride.setDestinationLongitude(80.6337);

        ride.setDistanceKm(115.0);
        ride.setStatus(status);

        return ride;
    }

    /**
     * Verifies that a new ride can be saved successfully.
     * Also checks default payment status and creation time.
     */
    @Test
    void shouldSaveRide() {

        Ride ride = createRide(
                1001L,
                null,
                RideStatus.REQUESTED
        );

        Ride savedRide = rideRepository.save(ride);

        assertNotNull(savedRide.getId());

        assertEquals(
                RideStatus.REQUESTED,
                savedRide.getStatus()
        );

        assertEquals(
                PaymentRecordStatus.NOT_APPLICABLE,
                savedRide.getPaymentStatus()
        );

        assertNotNull(savedRide.getCreatedAt());

        assertEquals(
                1001L,
                savedRide.getRiderId()
        );

        assertEquals(
                "Colombo",
                savedRide.getPickupLocation()
        );

        assertEquals(
                "Kandy",
                savedRide.getDestination()
        );
    }

    /**
     * Verifies searching rides by rider/passenger ID.
     */
    @Test
    void shouldFindRidesByRiderId() {

        Ride ride = createRide(
                2001L,
                null,
                RideStatus.REQUESTED
        );

        rideRepository.save(ride);

        List<Ride> rides =
                rideRepository.findByRiderId(2001L);

        assertFalse(rides.isEmpty());

        assertTrue(
                rides.stream()
                        .anyMatch(r ->
                                r.getRiderId().equals(2001L))
        );
    }

    /**
     * Verifies searching rides by driver ID.
     */
    @Test
    void shouldFindRidesByDriverId() {

        Ride ride = createRide(
                3001L,
                9001L,
                RideStatus.ASSIGNED
        );

        rideRepository.save(ride);

        List<Ride> rides =
                rideRepository.findByDriverId(9001L);

        assertFalse(rides.isEmpty());

        assertTrue(
                rides.stream()
                        .anyMatch(r ->
                                r.getDriverId().equals(9001L))
        );
    }

    /**
     * Verifies searching rides by lifecycle status.
     */
    @Test
    void shouldFindRidesByStatus() {

        Ride ride = createRide(
                4001L,
                null,
                RideStatus.REQUESTED
        );

        rideRepository.save(ride);

        List<Ride> rides =
                rideRepository.findByStatus(
                        RideStatus.REQUESTED
                );

        assertFalse(rides.isEmpty());

        assertTrue(
                rides.stream()
                        .anyMatch(r ->
                                r.getStatus()
                                        == RideStatus.REQUESTED)
        );
    }
}