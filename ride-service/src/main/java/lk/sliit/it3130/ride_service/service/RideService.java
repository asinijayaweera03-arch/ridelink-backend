package lk.sliit.it3130.ride_service.service;

import lk.sliit.it3130.ride_service.client.DriverServiceClient;
import lk.sliit.it3130.ride_service.client.PaymentServiceClient;
import lk.sliit.it3130.ride_service.dto.AvailableDriverResponse;
import lk.sliit.it3130.ride_service.dto.PaymentRequest;
import lk.sliit.it3130.ride_service.dto.RideCompletionRequest;
import lk.sliit.it3130.ride_service.dto.RideRequest;
import lk.sliit.it3130.ride_service.dto.RideResponse;
import lk.sliit.it3130.ride_service.exception.NoAvailableDriverException;
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
    private final PaymentServiceClient paymentServiceClient;

    public RideService(
            RideRepository rideRepository,
            DriverServiceClient driverServiceClient,
            PaymentServiceClient paymentServiceClient) {

        this.rideRepository = rideRepository;
        this.driverServiceClient = driverServiceClient;
        this.paymentServiceClient = paymentServiceClient;
    }

    /**
     * Creates a new ride and assigns the first available driver.
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

        ride.setDistanceKm(request.getDistanceKm());

        List<AvailableDriverResponse> availableDrivers =
                driverServiceClient.getAvailableDrivers();

        if (availableDrivers.isEmpty()) {
            throw new NoAvailableDriverException(
                    "No available driver found"
            );
        }

        AvailableDriverResponse selectedDriver =
                availableDrivers.get(0);

        ride.setDriverId(selectedDriver.getId());

        /*
         * @PrePersist has not executed yet for a new entity.
         * Initialize REQUESTED before using the lifecycle state machine.
         */
        ride.setStatus(RideStatus.REQUESTED);
        ride.transitionTo(RideStatus.ASSIGNED);

        Ride savedRide = rideRepository.save(ride);

        return mapToResponse(savedRide);
    }

    /**
     * Returns a single ride by ID.
     */
    public RideResponse getRideById(Long rideId) {

        Ride ride = findRideById(rideId);

        return mapToResponse(ride);
    }

    /**
     * Returns rides using optional passenger, driver,
     * and lifecycle-status filters.
     */
    public List<RideResponse> getRides(
            Long passengerId,
            Long driverId,
            RideStatus status) {

        return rideRepository
                .findByFilters(
                        passengerId,
                        driverId,
                        status
                )
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    /**
     * ASSIGNED -> ACCEPTED
     */
    public RideResponse acceptRide(Long rideId) {

        Ride ride = findRideById(rideId);

        ride.transitionTo(RideStatus.ACCEPTED);

        Ride savedRide = rideRepository.save(ride);

        return mapToResponse(savedRide);
    }

    /**
     * ACCEPTED -> IN_PROGRESS
     */
    public RideResponse startRide(Long rideId) {

        Ride ride = findRideById(rideId);

        ride.transitionTo(RideStatus.IN_PROGRESS);

        Ride savedRide = rideRepository.save(ride);

        return mapToResponse(savedRide);
    }

    /**
     * Completes an IN_PROGRESS ride.
     *
     * Payment failure handling will be improved in Session 8.
     */
    public RideResponse completeRide(
            Long rideId,
            RideCompletionRequest completionRequest) {

        Ride ride = findRideById(rideId);

        /*
         * Validate lifecycle before contacting Payment Service.
         */
        if (!ride.getStatus()
                .canTransitionTo(RideStatus.COMPLETED)) {

            ride.transitionTo(RideStatus.COMPLETED);
        }

        PaymentRequest paymentRequest = new PaymentRequest(
                ride.getId(),
                ride.getRiderId(),
                completionRequest.getDistanceKm(),
                completionRequest.getDurationMin(),
                completionRequest.getPeakHour(),
                completionRequest.getPaymentMethod()
        );

        paymentServiceClient.createPayment(paymentRequest);

        ride.transitionTo(RideStatus.COMPLETED);

        Ride savedRide = rideRepository.save(ride);

        return mapToResponse(savedRide);
    }

    /**
     * REQUESTED / ASSIGNED / ACCEPTED -> CANCELLED.
     *
     * The cancellation reason is optional.
     * IN_PROGRESS rides cannot be cancelled.
     */
    public RideResponse cancelRide(
            Long rideId,
            String reason) {

        Ride ride = findRideById(rideId);

        /*
         * Store the optional reason only when meaningful text
         * has been supplied.
         */
        if (reason != null && !reason.isBlank()) {
            ride.setCancellationReason(
                    reason.trim()
            );
        }

        ride.transitionTo(RideStatus.CANCELLED);

        Ride savedRide = rideRepository.save(ride);

        return mapToResponse(savedRide);
    }

    /**
     * Finds a Ride entity by ID.
     */
    private Ride findRideById(Long rideId) {

        return rideRepository.findById(rideId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Ride not found"
                        ));
    }

    /**
     * Converts a Ride entity into a RideResponse DTO.
     */
    private RideResponse mapToResponse(Ride ride) {

        RideResponse response = new RideResponse();

        response.setId(ride.getId());
        response.setRiderId(ride.getRiderId());
        response.setDriverId(ride.getDriverId());

        response.setPickupLocation(
                ride.getPickupLocation()
        );

        response.setDestination(
                ride.getDestination()
        );

        response.setPickupLatitude(
                ride.getPickupLatitude()
        );

        response.setPickupLongitude(
                ride.getPickupLongitude()
        );

        response.setDestinationLatitude(
                ride.getDestinationLatitude()
        );

        response.setDestinationLongitude(
                ride.getDestinationLongitude()
        );

        response.setDistanceKm(
                ride.getDistanceKm()
        );

        response.setStatus(
                ride.getStatus()
        );

        response.setCancellationReason(
                ride.getCancellationReason()
        );

        response.setCreatedAt(
                ride.getCreatedAt()
        );

        return response;
    }
}