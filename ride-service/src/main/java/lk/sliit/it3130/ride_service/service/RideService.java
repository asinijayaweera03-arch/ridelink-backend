package lk.sliit.it3130.ride_service.service;

import lk.sliit.it3130.ride_service.dto.RideRequest;
import lk.sliit.it3130.ride_service.dto.RideResponse;
import lk.sliit.it3130.ride_service.model.Ride;
import lk.sliit.it3130.ride_service.repository.RideRepository;
import org.springframework.stereotype.Service;

/**
 * Contains business logic related to ride management.
 */
@Service
public class RideService {

    private final RideRepository rideRepository;

    /**
     * Constructor injection for RideRepository.
     */
    public RideService(RideRepository rideRepository) {
        this.rideRepository = rideRepository;
    }

    /**
     * Creates and saves a new ride request.
     *
     * @param request ride information received from the client
     * @return details of the newly created ride
     */
    public RideResponse createRide(RideRequest request) {

        // Convert request DTO into Ride entity
        Ride ride = new Ride();

        ride.setRiderId(request.getRiderId());
        ride.setPickupLocation(request.getPickupLocation());
        ride.setDestination(request.getDestination());

        ride.setPickupLatitude(request.getPickupLatitude());
        ride.setPickupLongitude(request.getPickupLongitude());

        ride.setDestinationLatitude(request.getDestinationLatitude());
        ride.setDestinationLongitude(request.getDestinationLongitude());

        // Save the ride.
        // Ride status and createdAt are automatically set by @PrePersist.
        Ride savedRide = rideRepository.save(ride);

        // Convert saved entity into response DTO
        return mapToResponse(savedRide);
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