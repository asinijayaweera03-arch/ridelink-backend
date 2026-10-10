package lk.sliit.it3130.payment_service.service;

import lk.sliit.it3130.payment_service.dto.ReceiptResponse;
import lk.sliit.it3130.payment_service.entity.Payment;
import lk.sliit.it3130.payment_service.entity.Receipt;
import lk.sliit.it3130.payment_service.exception.ReceiptNotFoundException;
import lk.sliit.it3130.payment_service.repository.ReceiptRepository;
import org.springframework.stereotype.Service;

@Service
public class ReceiptService {

    // Same rate constants used for the breakdown display
    private static final double BASE_FARE = 100.0;
    private static final double PER_KM_RATE = 60.0;
    private static final double PER_MIN_RATE = 5.0;

    private final ReceiptRepository receiptRepository;

    public ReceiptService(ReceiptRepository receiptRepository) {
        this.receiptRepository = receiptRepository;
    }

    /**
     * Called by PaymentService once a payment succeeds, to auto-generate a receipt
     * with a documented fare breakdown.
     */
    public Receipt generateReceipt(Payment payment, double distanceKm, double durationMin, boolean peakHour) {
        double distanceCharge = distanceKm * PER_KM_RATE;
        double timeCharge = durationMin * PER_MIN_RATE;
        double subtotal = BASE_FARE + distanceCharge + timeCharge;
        double surcharge = peakHour ? subtotal * 0.2 : 0.0;

        Receipt receipt = new Receipt();
        receipt.setPaymentId(payment.getId());
        receipt.setRideId(payment.getRideId());
        receipt.setBaseFare(BASE_FARE);
        receipt.setDistanceCharge(distanceCharge);
        receipt.setTimeCharge(timeCharge);
        receipt.setSurcharge(surcharge);
        receipt.setTotalAmount(payment.getAmount());

        return receiptRepository.save(receipt);
    }

    public ReceiptResponse getByPaymentId(Long paymentId) {
        Receipt receipt = receiptRepository.findByPaymentId(paymentId)
                .orElseThrow(() -> new ReceiptNotFoundException("No receipt found for payment id: " + paymentId));
        return new ReceiptResponse(receipt);
    }

    public ReceiptResponse getByRideId(Long rideId) {
        Receipt receipt = receiptRepository.findByRideId(rideId)
                .orElseThrow(() -> new ReceiptNotFoundException("No receipt found for ride id: " + rideId));
        return new ReceiptResponse(receipt);
    }
}