package lk.sliit.it3130.ride_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(
        name = "Ride Management",
        description = "Ride request, assignment, lifecycle, cancellation and payment operations"
)
public class RideController {

    private final RideService rideService;

    public RideController(RideService rideService) {
        this.rideService = rideService;
    }

    /**
     * Creates a new ride request.
     * Only passengers can request rides.
     */
    @PostMapping
    @PreAuthorize("hasRole('PASSENGER')")
    @Operation(
            summary = "Request a ride",
            description = "Creates a new ride request for a passenger and attempts to assign an available driver. Requires PASSENGER role."
    )
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
     */
    @GetMapping
    @Operation(
            summary = "Retrieve rides",
            description = "Returns rides and optionally filters them by passenger ID, driver ID or ride status. Requires authentication."
    )
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
     */
    @GetMapping("/{id}")
    @Operation(
            summary = "Retrieve a ride by ID",
            description = "Returns the ride matching the supplied ride ID. Requires authentication."
    )
    public ResponseEntity<RideResponse> getRideById(
            @PathVariable Long id) {

        RideResponse response =
                rideService.getRideById(id);

        return ResponseEntity.ok(response);
    }

    /**
     * Accepts an assigned ride.
     * Only drivers can accept rides.
     */
    @PatchMapping("/{id}/accept")
    @PreAuthorize("hasRole('DRIVER')")
    @Operation(
            summary = "Accept an assigned ride",
            description = "Transitions an ASSIGNED ride to ACCEPTED. Requires DRIVER role."
    )
    public ResponseEntity<RideResponse> acceptRide(
            @PathVariable Long id) {

        RideResponse response =
                rideService.acceptRide(id);

        return ResponseEntity.ok(response);
    }

    /**
     * Starts an accepted ride.
     * Only drivers can start rides.
     */
    @PatchMapping("/{id}/start")
    @PreAuthorize("hasRole('DRIVER')")
    @Operation(
            summary = "Start an accepted ride",
            description = "Transitions an ACCEPTED ride to IN_PROGRESS. Requires DRIVER role."
    )
    public ResponseEntity<RideResponse> startRide(
            @PathVariable Long id) {

        RideResponse response =
                rideService.startRide(id);

        return ResponseEntity.ok(response);
    }

    /**
     * Completes an in-progress ride and attempts
     * to record its payment.
     */
    @PatchMapping("/{id}/complete")
    @PreAuthorize("hasRole('DRIVER')")
    @Operation(
            summary = "Complete a ride",
            description = "Transitions an IN_PROGRESS ride to COMPLETED and attempts to record the simulated payment using the Fare & Payment Service. Requires DRIVER role."
    )
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
     */
    @PatchMapping("/{id}/payment/retry")
    @PreAuthorize("hasRole('DRIVER')")
    @Operation(
            summary = "Retry ride payment",
            description = "Retries payment processing for a completed ride when payment is pending or previously failed. Requires DRIVER role."
    )
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
     * Only passengers can cancel rides.
     */
    @PatchMapping("/{id}/cancel")
    @PreAuthorize("hasRole('PASSENGER')")
    @Operation(
            summary = "Cancel a ride",
            description = "Cancels a ride when cancellation is valid for its current lifecycle state. An optional cancellation reason may be supplied. Requires PASSENGER role."
    )
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