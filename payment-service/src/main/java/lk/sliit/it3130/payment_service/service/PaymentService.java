 package lk.sliit.it3130.payment_service.service;

import lk.sliit.it3130.payment_service.dto.PaymentRequest;
import lk.sliit.it3130.payment_service.dto.PaymentResponse;
import lk.sliit.it3130.payment_service.entity.Payment;
import lk.sliit.it3130.payment_service.exception.PaymentException;
import lk.sliit.it3130.payment_service.exception.PaymentNotFoundException;
import lk.sliit.it3130.payment_service.repository.PaymentRepository;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {

    private static final double BASE_FARE = 100.0;
    private static final double PER_KM_RATE = 60.0;
    private static final double PER_MIN_RATE = 5.0;
    private static final double PEAK_SURCHARGE_MULTIPLIER = 1.2;
    private static final double SIMULATED_DECLINE_THRESHOLD = 100000.0;

    private final PaymentRepository paymentRepository;
    private final ReceiptService receiptService;

    public PaymentService(PaymentRepository paymentRepository, ReceiptService receiptService) {
        this.paymentRepository = paymentRepository;
        this.receiptService = receiptService;
    }

    public PaymentResponse processPayment(PaymentRequest request) {

        paymentRepository.findByRideId(request.getRideId()).ifPresent(existing -> {
            throw new PaymentException("A payment already exists for ride ID: " + request.getRideId());
        });

        double fare = BASE_FARE
                + (request.getDistanceKm() * PER_KM_RATE)
                + (request.getDurationMin() * PER_MIN_RATE);

        if (request.isPeakHour()) {
            fare *= PEAK_SURCHARGE_MULTIPLIER;
        }

        Payment payment = new Payment();
        payment.setRideId(request.getRideId());
        payment.setPassengerId(request.getPassengerId());
        payment.setAmount(fare);

        try {
            payment.setPaymentMethod(
                    request.getPaymentMethod() != null
                            ? Payment.PaymentMethod.valueOf(request.getPaymentMethod().toUpperCase())
                            : Payment.PaymentMethod.CASH
            );
        } catch (IllegalArgumentException e) {
            throw new PaymentException("Invalid payment method: " + request.getPaymentMethod());
        }

        if (fare > SIMULATED_DECLINE_THRESHOLD) {
            payment.setStatus(Payment.PaymentStatus.FAILED);
        } else {
            payment.setStatus(Payment.PaymentStatus.SUCCESS);
        }

        Payment saved = paymentRepository.save(payment);

        if (saved.getStatus() == Payment.PaymentStatus.SUCCESS) {
            receiptService.generateReceipt(saved, request.getDistanceKm(), request.getDurationMin(), request.isPeakHour());
        }

        return new PaymentResponse(saved);
    }

    public PaymentResponse getPaymentById(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found with id: " + id));
        return new PaymentResponse(payment);
    }

    public PaymentResponse getPaymentByRideId(Long rideId) {
        Payment payment = paymentRepository.findByRideId(rideId)
                .orElseThrow(() -> new PaymentNotFoundException("No payment found for ride id: " + rideId));
        return new PaymentResponse(payment);
    }

    public PaymentResponse retryPayment(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found with id: " + id));

        if (payment.getStatus() == Payment.PaymentStatus.SUCCESS) {
            throw new PaymentException("Cannot retry a payment that already succeeded.");
        }

        payment.setStatus(Payment.PaymentStatus.SUCCESS);
        Payment updated = paymentRepository.save(payment);
        return new PaymentResponse(updated);
    }
}