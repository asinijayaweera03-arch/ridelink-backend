package lk.sliit.it3130.driver_service.service;

import lk.sliit.it3130.driver_service.dto.VehicleRequest;
import lk.sliit.it3130.driver_service.dto.VehicleResponse;
import lk.sliit.it3130.driver_service.entity.Driver;
import lk.sliit.it3130.driver_service.entity.Vehicle;
import lk.sliit.it3130.driver_service.repository.DriverRepository;
import lk.sliit.it3130.driver_service.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class VehicleService {

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private DriverRepository driverRepository;

    public VehicleResponse attachVehicle(Long driverId, VehicleRequest request) {

        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Driver not found"));

        if (vehicleRepository.findByDriverId(driverId).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Driver already has a vehicle attached");
        }

        Vehicle vehicle = new Vehicle();
        vehicle.setDriverId(driverId);
        vehicle.setVehicleNumber(request.getVehicleNumber());
        vehicle.setVehicleType(request.getVehicleType());
        vehicle.setColor(request.getColor());
        vehicle.setCapacity(request.getCapacity());

        Vehicle saved = vehicleRepository.save(vehicle);
        return mapToResponse(saved);
    }

    public VehicleResponse getVehicleByDriverId(Long driverId) {
        Vehicle vehicle = vehicleRepository.findByDriverId(driverId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vehicle not found for this driver"));
        return mapToResponse(vehicle);
    }

    private VehicleResponse mapToResponse(Vehicle vehicle) {
        VehicleResponse response = new VehicleResponse();
        response.setId(vehicle.getId());
        response.setDriverId(vehicle.getDriverId());
        response.setVehicleNumber(vehicle.getVehicleNumber());
        response.setVehicleType(vehicle.getVehicleType());
        response.setColor(vehicle.getColor());
        response.setCapacity(vehicle.getCapacity());
        return response;
    }
}