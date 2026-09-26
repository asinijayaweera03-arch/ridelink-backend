package lk.sliit.it3130.ride_service.repository;

import lk.sliit.it3130.ride_service.model.Ride;
import lk.sliit.it3130.ride_service.model.RideStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    /**
     * Finds rides using optional filters.
     *
     * Any filter can be null. When a filter is null,
     * that condition is ignored.
     */
    @Query("""
            SELECT r
            FROM Ride r
            WHERE (:riderId IS NULL OR r.riderId = :riderId)
              AND (:driverId IS NULL OR r.driverId = :driverId)
              AND (:status IS NULL OR r.status = :status)
            ORDER BY r.createdAt DESC
            """)
    List<Ride> findByFilters(
            @Param("riderId") Long riderId,
            @Param("driverId") Long driverId,
            @Param("status") RideStatus status
    );
}