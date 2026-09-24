package lk.sliit.it3130.ride_service.repository;

import lk.sliit.it3130.ride_service.model.Ride;
import lk.sliit.it3130.ride_service.model.RideStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests database persistence operations for Ride entities.
 */
@SpringBootTest
class RideRepositoryTest {

    @Autowired
    private RideRepository rideRepository;

    /**
     * Verifies that a new ride can be saved successfully.
     * Also checks that the default status and creation time
     * are automatically assigned.
     */
    @Test
    void shouldSaveRide() {

        // Create a new ride
        Ride ride = new Ride();

        ride.setRiderId(1L);
        ride.setPickupLocation("Colombo");
        ride.setDestination("Kandy");

        ride.setPickupLatitude(6.9271);
        ride.setPickupLongitude(79.8612);

        ride.setDestinationLatitude(7.2906);
        ride.setDestinationLongitude(80.6337);

        // Save the ride to the database
        Ride savedRide = rideRepository.save(ride);

        // Verify that the database generated an ID
        assertNotNull(savedRide.getId());

        // Verify the default ride status
        assertEquals(RideStatus.REQUESTED, savedRide.getStatus());

        // Verify that createdAt was automatically generated
        assertNotNull(savedRide.getCreatedAt());

        // Verify important ride information
        assertEquals(1L, savedRide.getRiderId());
        assertEquals("Colombo", savedRide.getPickupLocation());
        assertEquals("Kandy", savedRide.getDestination());
    }
}