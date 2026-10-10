package lk.sliit.it3130.driver_service.repository;

import lk.sliit.it3130.driver_service.entity.Driver;
import lk.sliit.it3130.driver_service.enums.AvailabilityStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DriverRepository extends JpaRepository<Driver, Long> {

    Optional<Driver> findByLicenseNumber(String licenseNumber);

    List<Driver> findByAvailabilityStatus(AvailabilityStatus status);

    List<Driver> findByAvailabilityStatusAndServiceArea(AvailabilityStatus status, String serviceArea);
}