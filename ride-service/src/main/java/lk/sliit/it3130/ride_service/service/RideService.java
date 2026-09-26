package lk.sliit.it3130.ride_service.service;

import lk.sliit.it3130.ride_service.client.DriverServiceClient;
import lk.sliit.it3130.ride_service.client.PaymentServiceClient;
import lk.sliit.it3130.ride_service.dto.AvailableDriverResponse;
import lk.sliit.it3130.ride_service.dto.PaymentRequest;
import lk.sliit.it3130.ride_service.dto.PaymentResponse;
import lk.sliit.it3130.ride_service.dto.RideCompletionRequest;
import lk.sliit.it3130.ride_service.dto.RideRequest;
import lk.sliit.it3130.ride_service.dto.RideResponse;
import lk.sliit.it3130.ride_service.exception.NoAvailableDriverException;
import lk.sliit.it3130.ride_service.exception.PaymentServiceUnavailableException;
import lk.sliit.it3130.ride_service.exception.RideNotFoundException;
import lk.sliit.it3130.ride_service.model.PaymentRecordStatus;
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
         * Initialize the initial values before using the
         * lifecycle state machine.
         */
        ride.setStatus(RideStatus.REQUESTED);
        ride.setPaymentStatus(
                PaymentRecordStatus.NOT_APPLICABLE
        );

        ride.transitionTo(RideStatus.ASSIGNED);

        Ride savedRide =
                rideRepository.save(ride);

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

        Ride savedRide =
                rideRepository.save(ride);

        return mapToResponse(savedRide);
    }

    /**
     * ACCEPTED -> IN_PROGRESS
     */
    public RideResponse startRide(Long rideId) {

        Ride ride = findRideById(rideId);

        ride.transitionTo(RideStatus.IN_PROGRESS);

        Ride savedRide =
                rideRepository.save(ride);

        return mapToResponse(savedRide);
    }

    /**
     * Completes an IN_PROGRESS ride and attempts
     * to record its payment.
     *
     * The ride is persisted as COMPLETED + PENDING before
     * Payment Service is contacted. Therefore a downstream
     * outage does not roll the ride back to IN_PROGRESS.
     */
    public RideResponse completeRide(
            Long rideId,
            RideCompletionRequest completionRequest) {

        Ride ride = findRideById(rideId);

        /*
         * Lifecycle validation happens here.
         * A valid IN_PROGRESS ride becomes COMPLETED and
         * completedAt is recorded by the entity.
         */
        ride.transitionTo(RideStatus.COMPLETED);

        /*
         * Payment has not been confirmed yet.
         */
        ride.setPaymentStatus(
                PaymentRecordStatus.PENDING
        );

        /*
         * Persist completion before calling the
         * downstream Payment Service.
         */
        ride = rideRepository.save(ride);

        Long durationMinutes =
                ride.tripDurationMinutes();

        if (durationMinutes == null) {
            throw new IllegalStateException(
                    "Unable to calculate trip duration"
            );
        }

        PaymentRequest paymentRequest =
                new PaymentRequest(
                        ride.getId(),
                        ride.getRiderId(),
                        ride.getDistanceKm(),
                        durationMinutes.doubleValue(),
                        completionRequest.getPeakHour(),
                        completionRequest.getPaymentMethod()
                );

        try {

            PaymentResponse paymentResponse =
                    paymentServiceClient.createPayment(
                            paymentRequest
                    );

            applyPaymentResponse(
                    ride,
                    paymentResponse
            );

        } catch (PaymentServiceUnavailableException exception) {

            /*
             * The ride remains COMPLETED.
             * PENDING indicates that payment recording
             * should be retried later.
             */
            ride.setPaymentStatus(
                    PaymentRecordStatus.PENDING
            );
        }

        Ride savedRide =
                rideRepository.save(ride);

        return mapToResponse(savedRide);
    }

    /**
     * Retries payment processing for a completed ride.
     *
     * PENDING:
     * The previous attempt did not create a known payment
     * record, therefore create the payment again.
     *
     * FAILED:
     * Payment Service already created a payment record,
     * therefore retry that existing record by payment ID.
     */
    public RideResponse retryPayment(
            Long rideId,
            Boolean peakHour,
            String paymentMethod) {

        Ride ride = findRideById(rideId);

        if (ride.getStatus() != RideStatus.COMPLETED) {
            throw new IllegalStateException(
                    "Payment can only be retried for a completed ride"
            );
        }

        if (ride.getPaymentStatus()
                == PaymentRecordStatus.RECORDED) {

            throw new IllegalStateException(
                    "Payment has already been recorded"
            );
        }

        if (ride.getPaymentStatus()
                == PaymentRecordStatus.NOT_APPLICABLE) {

            throw new IllegalStateException(
                    "Payment is not pending or failed"
            );
        }

        Long durationMinutes =
                ride.tripDurationMinutes();

        if (durationMinutes == null) {
            throw new IllegalStateException(
                    "Unable to calculate trip duration"
            );
        }

        try {

            PaymentResponse paymentResponse;

            /*
             * FAILED means Payment Service has already
             * created a payment record.
             */
            if (ride.getPaymentStatus()
                            == PaymentRecordStatus.FAILED
                    && ride.getPaymentId() != null) {

                paymentResponse =
                        paymentServiceClient.retryPayment(
                                ride.getPaymentId()
                        );

            } else {

                /*
                 * PENDING means there is no confirmed
                 * payment record yet.
                 */
                ride.setPaymentStatus(
                        PaymentRecordStatus.PENDING
                );

                ride = rideRepository.save(ride);

                PaymentRequest paymentRequest =
                        new PaymentRequest(
                                ride.getId(),
                                ride.getRiderId(),
                                ride.getDistanceKm(),
                                durationMinutes.doubleValue(),
                                peakHour,
                                paymentMethod
                        );

                paymentResponse =
                        paymentServiceClient.createPayment(
                                paymentRequest
                        );
            }

            applyPaymentResponse(
                    ride,
                    paymentResponse
            );

        } catch (PaymentServiceUnavailableException exception) {

            /*
             * Preserve FAILED when an existing failed
             * payment could not be retried.
             *
             * Otherwise preserve PENDING.
             */
            if (ride.getPaymentStatus()
                    != PaymentRecordStatus.FAILED) {

                ride.setPaymentStatus(
                        PaymentRecordStatus.PENDING
                );
            }
        }

        Ride savedRide =
                rideRepository.save(ride);

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

        if (reason != null && !reason.isBlank()) {
            ride.setCancellationReason(
                    reason.trim()
            );
        }

        ride.transitionTo(RideStatus.CANCELLED);

        Ride savedRide =
                rideRepository.save(ride);

        return mapToResponse(savedRide);
    }

    /**
     * Applies the result returned by Payment Service
     * to the Ride entity.
     */
    private void applyPaymentResponse(
            Ride ride,
            PaymentResponse paymentResponse) {

        ride.setPaymentId(
                paymentResponse.getId()
        );

        ride.setFareAmount(
                paymentResponse.getAmount()
        );

        if ("SUCCESS".equalsIgnoreCase(
                paymentResponse.getStatus())) {

            ride.setPaymentStatus(
                    PaymentRecordStatus.RECORDED
            );

        } else {

            ride.setPaymentStatus(
                    PaymentRecordStatus.FAILED
            );
        }
    }

    /**
     * Finds a Ride entity by ID.
     */
    private Ride findRideById(Long rideId) {

        return rideRepository.findById(rideId)
                .orElseThrow(() ->
                        new RideNotFoundException(
                                "Ride not found: " + rideId
                        )
                );
    }

    /**
     * Converts a Ride entity into a RideResponse DTO.
     */
    private RideResponse mapToResponse(Ride ride) {

        RideResponse response =
                new RideResponse();

        response.setId(
                ride.getId()
        );

        response.setRiderId(
                ride.getRiderId()
        );

        response.setDriverId(
                ride.getDriverId()
        );

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

        response.setPaymentStatus(
                ride.getPaymentStatus()
        );

        response.setPaymentId(
                ride.getPaymentId()
        );

        response.setFareAmount(
                ride.getFareAmount()
        );

        response.setCancellationReason(
                ride.getCancellationReason()
        );

        response.setCreatedAt(
                ride.getCreatedAt()
        );

        response.setAssignedAt(
                ride.getAssignedAt()
        );

        response.setAcceptedAt(
                ride.getAcceptedAt()
        );

        response.setStartedAt(
                ride.getStartedAt()
        );

        response.setCompletedAt(
                ride.getCompletedAt()
        );

        response.setCancelledAt(
                ride.getCancelledAt()
        );

        return response;
    }
}