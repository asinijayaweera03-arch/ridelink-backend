package lk.sliit.it3130.driver_service.service;

import lk.sliit.it3130.driver_service.dto.AvailabilityUpdateRequest;
import lk.sliit.it3130.driver_service.dto.DriverRegisterRequest;
import lk.sliit.it3130.driver_service.entity.Driver;
import lk.sliit.it3130.driver_service.enums.AvailabilityStatus;
import lk.sliit.it3130.driver_service.exception.DriverNotFoundException;
import lk.sliit.it3130.driver_service.exception.DuplicateLicenseException;
import lk.sliit.it3130.driver_service.exception.InvalidAvailabilityException;
import lk.sliit.it3130.driver_service.repository.DriverRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DriverServiceTest {

    @Mock
    private DriverRepository driverRepository;

    @InjectMocks
    private DriverService driverService;

    private DriverRegisterRequest registerRequest;

    @BeforeEach
    void setUp() {
        registerRequest = new DriverRegisterRequest();
        registerRequest.setAccountId(1L);
        registerRequest.setLicenseNumber("LIC12345");
        registerRequest.setName("Test Driver");
        registerRequest.setPhone("0771234567");
    }

    // =========================================================
    // REGISTER TESTS
    // =========================================================

    @Test
    void register_savesNewDriver_whenLicenseNotTaken() {

        when(driverRepository.findByLicenseNumber("LIC12345"))
                .thenReturn(Optional.empty());

        when(driverRepository.save(any(Driver.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        var response = driverService.register(registerRequest);

        assertEquals("Test Driver", response.getName());
        assertEquals(AvailabilityStatus.UNAVAILABLE, response.getAvailabilityStatus());

        verify(driverRepository).save(any(Driver.class));
    }

    @Test
    void register_throwsDuplicateLicenseException_whenLicenseAlreadyExists() {

        when(driverRepository.findByLicenseNumber("LIC12345"))
                .thenReturn(Optional.of(new Driver()));

        assertThrows(
                DuplicateLicenseException.class,
                () -> driverService.register(registerRequest)
        );

        verify(driverRepository, never()).save(any(Driver.class));
    }

    // =========================================================
    // UPDATE AVAILABILITY TESTS
    // =========================================================

    @Test
    void updateAvailability_updatesStatus_whenDriverExistsAndStatusIsValid() {

        Driver driver = new Driver();
        driver.setId(1L);
        driver.setAvailabilityStatus(AvailabilityStatus.UNAVAILABLE);

        AvailabilityUpdateRequest request = new AvailabilityUpdateRequest();
        request.setAvailabilityStatus(AvailabilityStatus.AVAILABLE);

        when(driverRepository.findById(1L))
                .thenReturn(Optional.of(driver));

        when(driverRepository.save(any(Driver.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        var response = driverService.updateAvailability(1L, request);

        assertEquals(AvailabilityStatus.AVAILABLE, response.getAvailabilityStatus());
    }

    @Test
    void updateAvailability_throwsDriverNotFoundException_whenDriverDoesNotExist() {

        AvailabilityUpdateRequest request = new AvailabilityUpdateRequest();
        request.setAvailabilityStatus(AvailabilityStatus.AVAILABLE);

        when(driverRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                DriverNotFoundException.class,
                () -> driverService.updateAvailability(999L, request)
        );
    }

    @Test
    void updateAvailability_throwsInvalidAvailabilityException_whenStatusIsOnTrip() {

        Driver driver = new Driver();
        driver.setId(1L);

        AvailabilityUpdateRequest request = new AvailabilityUpdateRequest();
        request.setAvailabilityStatus(AvailabilityStatus.ON_TRIP);

        when(driverRepository.findById(1L))
                .thenReturn(Optional.of(driver));

        assertThrows(
                InvalidAvailabilityException.class,
                () -> driverService.updateAvailability(1L, request)
        );

        verify(driverRepository, never()).save(any(Driver.class));
    }
}