package lk.sliit.it3130.driver_service.repository;

import lk.sliit.it3130.driver_service.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    Optional<Vehicle> findByDriverId(Long driverId);

    Optional<Vehicle> findByVehicleNumber(String vehicleNumber);
}