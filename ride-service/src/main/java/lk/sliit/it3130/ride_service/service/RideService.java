package lk.sliit.it3130.ride_service.service;

import lk.sliit.it3130.ride_service.client.DriverServiceClient;
import lk.sliit.it3130.ride_service.dto.AvailableDriverResponse;
import lk.sliit.it3130.ride_service.dto.RideRequest;
import lk.sliit.it3130.ride_service.dto.RideResponse;
import lk.sliit.it3130.ride_service.model.Ride;
import lk.sliit.it3130.ride_service.model.RideStatus;
import lk.sliit.it3130.ride_service.repository.RideRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Contains business logic related to ride management.
 */
@Service
public class RideService {

    private final RideRepository rideRepository;
    private final DriverServiceClient driverServiceClient;

    /**
     * Constructor injection for required dependencies.
     */
    public RideService(
            RideRepository rideRepository,
            DriverServiceClient driverServiceClient) {

        this.rideRepository = rideRepository;
        this.driverServiceClient = driverServiceClient;
    }

    /**
     * Creates and saves a new ride request.
     *
     * @param request ride information received from the client
     * @return details of the newly created ride
     */
    public RideResponse createRide(RideRequest request) {

        Ride ride = new Ride();

        ride.setRiderId(request.getRiderId());
        ride.setPickupLocation(request.getPickupLocation());
        ride.setDestination(request.getDestination());

        ride.setPickupLatitude(request.getPickupLatitude());
        ride.setPickupLongitude(request.getPickupLongitude());

        ride.setDestinationLatitude(request.getDestinationLatitude());
        ride.setDestinationLongitude(request.getDestinationLongitude());

        // Get currently available drivers from Driver Service
        List<AvailableDriverResponse> availableDrivers =
                driverServiceClient.getAvailableDrivers();

        // Assign the first available driver, if one exists
        if (!availableDrivers.isEmpty()) {

            AvailableDriverResponse selectedDriver =
                    availableDrivers.get(0);

            ride.setDriverId(selectedDriver.getId());
            ride.setStatus(RideStatus.ASSIGNED);
        }

        // If no driver is assigned, @PrePersist sets status to REQUESTED
        Ride savedRide = rideRepository.save(ride);

        return mapToResponse(savedRide);
    }

    /**
     * Accepts an assigned ride.
     *
     * ASSIGNED -> ACCEPTED
     */
    public RideResponse acceptRide(Long rideId) {

        Ride ride = findRideById(rideId);

        if (ride.getStatus() != RideStatus.ASSIGNED) {
            throw new IllegalStateException(
                    "Only an ASSIGNED ride can be accepted"
            );
        }

        ride.setStatus(RideStatus.ACCEPTED);

        Ride savedRide = rideRepository.save(ride);

        return mapToResponse(savedRide);
    }

    /**
     * Starts an accepted ride.
     *
     * ACCEPTED -> IN_PROGRESS
     */
    public RideResponse startRide(Long rideId) {

        Ride ride = findRideById(rideId);

        if (ride.getStatus() != RideStatus.ACCEPTED) {
            throw new IllegalStateException(
                    "Only an ACCEPTED ride can be started"
            );
        }

        ride.setStatus(RideStatus.IN_PROGRESS);

        Ride savedRide = rideRepository.save(ride);

        return mapToResponse(savedRide);
    }

    /**
     * Completes a ride that is currently in progress.
     *
     * IN_PROGRESS -> COMPLETED
     */
    public RideResponse completeRide(Long rideId) {

        Ride ride = findRideById(rideId);

        if (ride.getStatus() != RideStatus.IN_PROGRESS) {
            throw new IllegalStateException(
                    "Only an IN_PROGRESS ride can be completed"
            );
        }

        ride.setStatus(RideStatus.COMPLETED);

        Ride savedRide = rideRepository.save(ride);

        return mapToResponse(savedRide);
    }

    /**
     * Cancels a ride.
     *
     * REQUESTED / ASSIGNED / ACCEPTED -> CANCELLED
     */
    public RideResponse cancelRide(Long rideId) {

        Ride ride = findRideById(rideId);

        RideStatus currentStatus = ride.getStatus();

        if (currentStatus != RideStatus.REQUESTED
                && currentStatus != RideStatus.ASSIGNED
                && currentStatus != RideStatus.ACCEPTED) {

            throw new IllegalStateException(
                    "Ride cannot be cancelled in its current status"
            );
        }

        ride.setStatus(RideStatus.CANCELLED);

        Ride savedRide = rideRepository.save(ride);

        return mapToResponse(savedRide);
    }

    /**
     * Finds a ride by ID.
     */
    private Ride findRideById(Long rideId) {

        return rideRepository.findById(rideId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Ride not found"));
    }

    /**
     * Converts a Ride entity into a RideResponse DTO.
     */
    private RideResponse mapToResponse(Ride ride) {

        RideResponse response = new RideResponse();

        response.setId(ride.getId());
        response.setRiderId(ride.getRiderId());
        response.setDriverId(ride.getDriverId());

        response.setPickupLocation(ride.getPickupLocation());
        response.setDestination(ride.getDestination());

        response.setPickupLatitude(ride.getPickupLatitude());
        response.setPickupLongitude(ride.getPickupLongitude());

        response.setDestinationLatitude(ride.getDestinationLatitude());
        response.setDestinationLongitude(ride.getDestinationLongitude());

        response.setStatus(ride.getStatus());
        response.setCreatedAt(ride.getCreatedAt());

        return response;
    }
}