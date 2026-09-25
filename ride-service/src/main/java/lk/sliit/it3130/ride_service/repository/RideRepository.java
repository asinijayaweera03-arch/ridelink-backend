package lk.sliit.it3130.ride_service.repository;

import lk.sliit.it3130.ride_service.model.Ride;
import lk.sliit.it3130.ride_service.model.RideStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for managing Ride data in the database.
 * Provides CRUD operations and ride-specific search methods.
 */
@Repository
public interface RideRepository extends JpaRepository<Ride, Long> {

    // Find all rides requested by a specific rider/passenger
    List<Ride> findByRiderId(Long riderId);

    // Find all rides assigned to a specific driver
    List<Ride> findByDriverId(Long driverId);

    // Find all rides with a specific lifecycle status
    List<Ride> findByStatus(RideStatus status);
}