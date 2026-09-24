package lk.sliit.it3130.driver_service.service;

import lk.sliit.it3130.driver_service.dto.VehicleRequest;
import lk.sliit.it3130.driver_service.entity.Driver;
import lk.sliit.it3130.driver_service.entity.Vehicle;
import lk.sliit.it3130.driver_service.exception.DriverNotFoundException;
import lk.sliit.it3130.driver_service.exception.DuplicateVehicleException;
import lk.sliit.it3130.driver_service.exception.VehicleNotFoundException;
import lk.sliit.it3130.driver_service.repository.DriverRepository;
import lk.sliit.it3130.driver_service.repository.VehicleRepository;

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
class VehicleServiceTest {

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private DriverRepository driverRepository;

    @InjectMocks
    private VehicleService vehicleService;

    private VehicleRequest vehicleRequest;

    @BeforeEach
    void setUp() {
        vehicleRequest = new VehicleRequest();
        vehicleRequest.setVehicleNumber("CAB-1234");
        vehicleRequest.setVehicleType("CAR");
        vehicleRequest.setColor("White");
        vehicleRequest.setCapacity(4);
    }

    // =========================================================
    // ATTACH VEHICLE TESTS
    // =========================================================

    @Test
    void attachVehicle_savesNewVehicle_whenDriverExistsAndHasNoVehicle() {

        Driver driver = new Driver();
        driver.setId(1L);

        when(driverRepository.findById(1L))
                .thenReturn(Optional.of(driver));

        when(vehicleRepository.findByDriverId(1L))
                .thenReturn(Optional.empty());

        when(vehicleRepository.save(any(Vehicle.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        var response = vehicleService.attachVehicle(1L, vehicleRequest);

        assertEquals("CAB-1234", response.getVehicleNumber());
        assertEquals(1L, response.getDriverId());

        verify(vehicleRepository).save(any(Vehicle.class));
    }

    @Test
    void attachVehicle_throwsDriverNotFoundException_whenDriverDoesNotExist() {

        when(driverRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                DriverNotFoundException.class,
                () -> vehicleService.attachVehicle(999L, vehicleRequest)
        );

        verify(vehicleRepository, never()).save(any(Vehicle.class));
    }

    @Test
    void attachVehicle_throwsDuplicateVehicleException_whenDriverAlreadyHasVehicle() {

        Driver driver = new Driver();
        driver.setId(1L);

        when(driverRepository.findById(1L))
                .thenReturn(Optional.of(driver));

        when(vehicleRepository.findByDriverId(1L))
                .thenReturn(Optional.of(new Vehicle()));

        assertThrows(
                DuplicateVehicleException.class,
                () -> vehicleService.attachVehicle(1L, vehicleRequest)
        );

        verify(vehicleRepository, never()).save(any(Vehicle.class));
    }

    // =========================================================
    // GET VEHICLE TESTS
    // =========================================================

    @Test
    void getVehicleByDriverId_returnsVehicle_whenVehicleExists() {

        Vehicle vehicle = new Vehicle();
        vehicle.setId(1L);
        vehicle.setDriverId(1L);
        vehicle.setVehicleNumber("CAB-1234");

        when(vehicleRepository.findByDriverId(1L))
                .thenReturn(Optional.of(vehicle));

        var response = vehicleService.getVehicleByDriverId(1L);

        assertEquals("CAB-1234", response.getVehicleNumber());
    }

    @Test
    void getVehicleByDriverId_throwsVehicleNotFoundException_whenNoVehicleExists() {

        when(vehicleRepository.findByDriverId(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                VehicleNotFoundException.class,
                () -> vehicleService.getVehicleByDriverId(999L)
        );
    }
}