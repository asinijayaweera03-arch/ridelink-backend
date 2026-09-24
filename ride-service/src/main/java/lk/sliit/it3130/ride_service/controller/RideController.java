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
     *
     * @param request ride details received from the client
     * @return newly created ride with HTTP 201 status
     */
    @PostMapping
    public ResponseEntity<RideResponse> createRide(
            @Valid @RequestBody RideRequest request) {

        RideResponse response = rideService.createRide(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
}