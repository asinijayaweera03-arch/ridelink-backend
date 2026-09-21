package lk.sliit.it3130.driver_service.controller;

import lk.sliit.it3130.driver_service.dto.DriverRegisterRequest;
import lk.sliit.it3130.driver_service.dto.DriverRegisterResponse;
import lk.sliit.it3130.driver_service.dto.VehicleRequest;
import lk.sliit.it3130.driver_service.dto.VehicleResponse;
import lk.sliit.it3130.driver_service.service.DriverService;
import lk.sliit.it3130.driver_service.service.VehicleService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<VehicleResponse> attachVehicle(@PathVariable Long id, @Valid @RequestBody VehicleRequest request) {
        VehicleResponse response = vehicleService.attachVehicle(id, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}/vehicle")
    public ResponseEntity<VehicleResponse> getVehicle(@PathVariable Long id) {
        VehicleResponse response = vehicleService.getVehicleByDriverId(id);
        return ResponseEntity.ok(response);
    }
}