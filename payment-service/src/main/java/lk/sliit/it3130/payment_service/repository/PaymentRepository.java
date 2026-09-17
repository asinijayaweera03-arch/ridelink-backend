package lk.sliit.it3130.payment_service.repository;

import lk.sliit.it3130.payment_service.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByRideId(Long rideId);
}