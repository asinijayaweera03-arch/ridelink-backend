package lk.sliit.it3130.ride_service.controller;

import jakarta.validation.Valid;
import lk.sliit.it3130.ride_service.dto.PaymentRetryRequest;
import lk.sliit.it3130.ride_service.dto.RideCancellationRequest;
import lk.sliit.it3130.ride_service.dto.RideCompletionRequest;
import lk.sliit.it3130.ride_service.dto.RideRequest;
import lk.sliit.it3130.ride_service.dto.RideResponse;
import lk.sliit.it3130.ride_service.model.RideStatus;
import lk.sliit.it3130.ride_service.service.RideService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for ride management operations.
 */
@RestController
@RequestMapping("/api/rides")
public class RideController {

    private final RideService rideService;

    /**
     * Constructor injection for RideService.
     */
    public RideController(RideService rideService) {
        this.rideService = rideService;
    }

    /**
     * Creates a new ride request.
     *
     * Only passengers can request rides.
     *
     * POST /api/rides
     */
    @PostMapping
    @PreAuthorize("hasRole('PASSENGER')")
    public ResponseEntity<RideResponse> createRide(
            @Valid @RequestBody RideRequest request) {

        RideResponse response =
                rideService.createRide(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    /**
     * Returns rides using optional filters.
     *
     * Any authenticated user can access this endpoint.
     */
    @GetMapping
    public ResponseEntity<List<RideResponse>> getRides(
            @RequestParam(
                    name = "passengerId",
                    required = false
            ) Long passengerId,

            @RequestParam(
                    name = "driverId",
                    required = false
            ) Long driverId,

            @RequestParam(
                    name = "status",
                    required = false
            ) RideStatus status) {

        List<RideResponse> response =
                rideService.getRides(
                        passengerId,
                        driverId,
                        status
                );

        return ResponseEntity.ok(response);
    }

    /**
     * Returns a ride by ID.
     *
     * Any authenticated user can access this endpoint.
     */
    @GetMapping("/{id}")
    public ResponseEntity<RideResponse> getRideById(
            @PathVariable Long id) {

        RideResponse response =
                rideService.getRideById(id);

        return ResponseEntity.ok(response);
    }

    /**
     * Accepts an assigned ride.
     *
     * Only drivers can accept rides.
     */
    @PatchMapping("/{id}/accept")
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<RideResponse> acceptRide(
            @PathVariable Long id) {

        RideResponse response =
                rideService.acceptRide(id);

        return ResponseEntity.ok(response);
    }

    /**
     * Starts an accepted ride.
     *
     * Only drivers can start rides.
     */
    @PatchMapping("/{id}/start")
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<RideResponse> startRide(
            @PathVariable Long id) {

        RideResponse response =
                rideService.startRide(id);

        return ResponseEntity.ok(response);
    }

    /**
     * Completes an in-progress ride and attempts
     * to record its payment.
     *
     * Only drivers can complete rides.
     *
     * PATCH /api/rides/{id}/complete
     */
    @PatchMapping("/{id}/complete")
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<RideResponse> completeRide(
            @PathVariable Long id,
            @Valid @RequestBody
            RideCompletionRequest request) {

        RideResponse response =
                rideService.completeRide(
                        id,
                        request
                );

        return ResponseEntity.ok(response);
    }

    /**
     * Retries payment processing for a completed ride.
     *
     * Only drivers can retry payment processing.
     *
     * PATCH /api/rides/{id}/payment/retry
     */
    @PatchMapping("/{id}/payment/retry")
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<RideResponse> retryPayment(
            @PathVariable Long id,
            @Valid @RequestBody
            PaymentRetryRequest request) {

        RideResponse response =
                rideService.retryPayment(
                        id,
                        request.getPeakHour(),
                        request.getPaymentMethod()
                );

        return ResponseEntity.ok(response);
    }

    /**
     * Cancels a ride.
     *
     * Only passengers can cancel rides.
     * Cancellation reason is optional.
     */
    @PatchMapping("/{id}/cancel")
    @PreAuthorize("hasRole('PASSENGER')")
    public ResponseEntity<RideResponse> cancelRide(
            @PathVariable Long id,
            @Valid @RequestBody(required = false)
            RideCancellationRequest request) {

        String reason =
                request == null
                        ? null
                        : request.getReason();

        RideResponse response =
                rideService.cancelRide(
                        id,
                        reason
                );

        return ResponseEntity.ok(response);
    }
}