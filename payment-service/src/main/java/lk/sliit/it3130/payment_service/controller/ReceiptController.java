package lk.sliit.it3130.payment_service.controller;

import lk.sliit.it3130.payment_service.dto.ReceiptResponse;
import lk.sliit.it3130.payment_service.service.ReceiptService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/receipts")
public class ReceiptController {

    private final ReceiptService receiptService;

    public ReceiptController(ReceiptService receiptService) {
        this.receiptService = receiptService;
    }

    @GetMapping("/{paymentId}")
    public ReceiptResponse getReceiptByPayment(@PathVariable Long paymentId) {
        return receiptService.getByPaymentId(paymentId);
    }

    @GetMapping("/ride/{rideId}")
    public ReceiptResponse getReceiptByRide(@PathVariable Long rideId) {
        return receiptService.getByRideId(rideId);
    }
}