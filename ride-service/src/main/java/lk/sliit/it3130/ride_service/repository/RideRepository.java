package lk.sliit.it3130.ride_service.repository;

import lk.sliit.it3130.ride_service.model.Ride;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for managing Ride data in the database.
 * Provides standard CRUD operations such as save, find, and delete.
 */
@Repository
public interface RideRepository extends JpaRepository<Ride, Long> {
}