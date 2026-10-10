package lk.sliit.it3130.payment_service.repository;

import lk.sliit.it3130.payment_service.entity.Receipt;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ReceiptRepository extends JpaRepository<Receipt, Long> {
    Optional<Receipt> findByPaymentId(Long paymentId);
    Optional<Receipt> findByRideId(Long rideId);
}