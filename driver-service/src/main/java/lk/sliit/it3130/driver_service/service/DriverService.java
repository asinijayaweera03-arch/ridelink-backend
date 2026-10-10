package lk.sliit.it3130.driver_service.service;

import lk.sliit.it3130.driver_service.dto.DriverRegisterRequest;
import lk.sliit.it3130.driver_service.dto.AvailabilityUpdateRequest;
import lk.sliit.it3130.driver_service.dto.LocationUpdateRequest;
import lk.sliit.it3130.driver_service.dto.DriverRegisterResponse;
import lk.sliit.it3130.driver_service.entity.Driver;
import lk.sliit.it3130.driver_service.enums.AvailabilityStatus;
import lk.sliit.it3130.driver_service.repository.DriverRepository;
import lk.sliit.it3130.driver_service.exception.DriverNotFoundException;
import lk.sliit.it3130.driver_service.exception.DuplicateLicenseException;
import lk.sliit.it3130.driver_service.exception.InvalidAvailabilityException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lk.sliit.it3130.driver_service.dto.AvailableDriverResponse;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DriverService {

    @Autowired
    private DriverRepository driverRepository;

    public DriverRegisterResponse register(DriverRegisterRequest request) {

        // Duplicate license number check
        if (driverRepository.findByLicenseNumber(request.getLicenseNumber()).isPresent()) {
            throw new DuplicateLicenseException("License number already registered");
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

    public DriverRegisterResponse updateAvailability(Long driverId, AvailabilityUpdateRequest request) {

        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new DriverNotFoundException("Driver not found"));

        if (request.getAvailabilityStatus() == AvailabilityStatus.ON_TRIP) {
            throw new InvalidAvailabilityException("Cannot manually set status to ON_TRIP");
        }

        driver.setAvailabilityStatus(request.getAvailabilityStatus());
        Driver updated = driverRepository.save(driver);

        return mapToResponse(updated);
    }

    public DriverRegisterResponse updateLocation(Long driverId, LocationUpdateRequest request) {

        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new DriverNotFoundException("Driver not found"));

        driver.setCurrentLatitude(request.getCurrentLatitude());
        driver.setCurrentLongitude(request.getCurrentLongitude());
        if (request.getServiceArea() != null) {
            driver.setServiceArea(request.getServiceArea());
        }

        Driver updated = driverRepository.save(driver);
        return mapToResponse(updated);
    }

    public List<AvailableDriverResponse> getAvailableDrivers(String serviceArea) {

        List<Driver> drivers;

        if (serviceArea != null && !serviceArea.isBlank()) {
            drivers = driverRepository.findByAvailabilityStatusAndServiceArea(AvailabilityStatus.AVAILABLE, serviceArea);
        } else {
            drivers = driverRepository.findByAvailabilityStatus(AvailabilityStatus.AVAILABLE);
        }

        return drivers.stream()
                .map(this::mapToAvailableDriverResponse)
                .collect(Collectors.toList());
    }

    private AvailableDriverResponse mapToAvailableDriverResponse(Driver driver) {
        AvailableDriverResponse response = new AvailableDriverResponse();
        response.setId(driver.getId());
        response.setName(driver.getName());
        response.setServiceArea(driver.getServiceArea());
        response.setCurrentLatitude(driver.getCurrentLatitude());
        response.setCurrentLongitude(driver.getCurrentLongitude());
        return response;
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