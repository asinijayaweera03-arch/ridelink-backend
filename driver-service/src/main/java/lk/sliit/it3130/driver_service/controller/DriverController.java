package lk.sliit.it3130.driver_service.controller;

import lk.sliit.it3130.driver_service.dto.AvailabilityUpdateRequest;
import lk.sliit.it3130.driver_service.dto.AvailableDriverResponse;
import lk.sliit.it3130.driver_service.dto.DriverRegisterRequest;
import lk.sliit.it3130.driver_service.dto.DriverRegisterResponse;
import lk.sliit.it3130.driver_service.dto.LocationUpdateRequest;
import lk.sliit.it3130.driver_service.dto.VehicleRequest;
import lk.sliit.it3130.driver_service.dto.VehicleResponse;
import lk.sliit.it3130.driver_service.service.DriverService;
import lk.sliit.it3130.driver_service.service.VehicleService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/drivers")
public class DriverController {

    @Autowired
    private DriverService driverService;

    @Autowired
    private VehicleService vehicleService;

    @PostMapping
    public ResponseEntity<DriverRegisterResponse> register(@Valid @RequestBody DriverRegisterRequest request) {
        DriverRegisterResponse response = driverService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/{id}/vehicle")
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<VehicleResponse> attachVehicle(@PathVariable Long id, @Valid @RequestBody VehicleRequest request) {
        VehicleResponse response = vehicleService.attachVehicle(id, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}/vehicle")
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<VehicleResponse> getVehicle(@PathVariable Long id) {
        VehicleResponse response = vehicleService.getVehicleByDriverId(id);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/availability")
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<DriverRegisterResponse> updateAvailability(@PathVariable Long id, @Valid @RequestBody AvailabilityUpdateRequest request) {
        DriverRegisterResponse response = driverService.updateAvailability(id, request);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/location")
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<DriverRegisterResponse> updateLocation(@PathVariable Long id, @Valid @RequestBody LocationUpdateRequest request) {
        DriverRegisterResponse response = driverService.updateLocation(id, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/available")
    public ResponseEntity<List<AvailableDriverResponse>> getAvailableDrivers(@RequestParam(required = false) String serviceArea) {
        List<AvailableDriverResponse> response = driverService.getAvailableDrivers(serviceArea);
        return ResponseEntity.ok(response);
    }
}