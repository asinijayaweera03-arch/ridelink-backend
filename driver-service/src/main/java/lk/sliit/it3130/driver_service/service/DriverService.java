package lk.sliit.it3130.driver_service.service;

import lk.sliit.it3130.driver_service.dto.DriverRegisterRequest;
import lk.sliit.it3130.driver_service.dto.DriverRegisterResponse;
import lk.sliit.it3130.driver_service.entity.Driver;
import lk.sliit.it3130.driver_service.enums.AvailabilityStatus;
import lk.sliit.it3130.driver_service.repository.DriverRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@Service
public class DriverService {

    @Autowired
    private DriverRepository driverRepository;

    public DriverRegisterResponse register(DriverRegisterRequest request) {

        // Duplicate license number check
        if (driverRepository.findByLicenseNumber(request.getLicenseNumber()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "License number already registered");
        }

        Driver driver = new Driver();
        driver.setAccountId(request.getAccountId());
        driver.setLicenseNumber(request.getLicenseNumber());
        driver.setName(request.getName());
        driver.setPhone(request.getPhone());
        driver.setAvailabilityStatus(AvailabilityStatus.UNAVAILABLE);

        Driver savedDriver = driverRepository.save(driver);

        return mapToResponse(savedDriver);
    }

    private DriverRegisterResponse mapToResponse(Driver driver) {
        DriverRegisterResponse response = new DriverRegisterResponse();
        response.setId(driver.getId());
        response.setAccountId(driver.getAccountId());
        response.setLicenseNumber(driver.getLicenseNumber());
        response.setName(driver.getName());
        response.setPhone(driver.getPhone());
        response.setAvailabilityStatus(driver.getAvailabilityStatus());
        response.setCreatedAt(driver.getCreatedAt());
        return response;
    }
}
