package lk.sliit.it3130.driver_service.controller;

import lk.sliit.it3130.driver_service.dto.DriverRegisterRequest;
import lk.sliit.it3130.driver_service.dto.DriverRegisterResponse;
import lk.sliit.it3130.driver_service.service.DriverService;
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

    @PostMapping
    public ResponseEntity<DriverRegisterResponse> register(@Valid @RequestBody DriverRegisterRequest request) {
        DriverRegisterResponse response = driverService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}