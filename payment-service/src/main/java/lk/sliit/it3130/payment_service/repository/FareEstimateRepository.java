package lk.sliit.it3130.payment_service.repository;

import lk.sliit.it3130.payment_service.entity.FareEstimate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FareEstimateRepository extends JpaRepository<FareEstimate, Long> {
}