package lk.sliit.it3130.ride_service.controller;

import jakarta.validation.Valid;
import lk.sliit.it3130.ride_service.dto.RideRequest;
import lk.sliit.it3130.ride_service.dto.RideResponse;
import lk.sliit.it3130.ride_service.service.RideService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
     * POST /api/rides
     */
    @PostMapping
    public ResponseEntity<RideResponse> createRide(
            @Valid @RequestBody RideRequest request) {

        RideResponse response = rideService.createRide(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    /**
     * Accepts an assigned ride.
     *
     * PATCH /api/rides/{id}/accept
     */
    @PatchMapping("/{id}/accept")
    public ResponseEntity<RideResponse> acceptRide(
            @PathVariable Long id) {

        RideResponse response = rideService.acceptRide(id);

        return ResponseEntity.ok(response);
    }

    /**
     * Starts an accepted ride.
     *
     * PATCH /api/rides/{id}/start
     */
    @PatchMapping("/{id}/start")
    public ResponseEntity<RideResponse> startRide(
            @PathVariable Long id) {

        RideResponse response = rideService.startRide(id);

        return ResponseEntity.ok(response);
    }

    /**
     * Completes a ride that is in progress.
     *
     * PATCH /api/rides/{id}/complete
     */
    @PatchMapping("/{id}/complete")
    public ResponseEntity<RideResponse> completeRide(
            @PathVariable Long id) {

        RideResponse response = rideService.completeRide(id);

        return ResponseEntity.ok(response);
    }

    /**
     * Cancels a ride.
     *
     * PATCH /api/rides/{id}/cancel
     */
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<RideResponse> cancelRide(
            @PathVariable Long id) {

        RideResponse response = rideService.cancelRide(id);

        return ResponseEntity.ok(response);
    }
}