package lk.sliit.it3130.payment_service.controller;

import jakarta.validation.Valid;
import lk.sliit.it3130.payment_service.dto.PaymentRequest;
import lk.sliit.it3130.payment_service.dto.PaymentResponse;
import lk.sliit.it3130.payment_service.service.PaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PaymentResponse processPayment(@Valid @RequestBody PaymentRequest request) {
        return paymentService.processPayment(request);
    }

    @GetMapping("/{id}")
    public PaymentResponse getPayment(@PathVariable Long id) {
        return paymentService.getPaymentById(id);
    }

    @GetMapping("/ride/{rideId}")
    public PaymentResponse getPaymentByRide(@PathVariable Long rideId) {
        return paymentService.getPaymentByRideId(rideId);
    }

    @PatchMapping("/{id}/status")
    public PaymentResponse retryPayment(@PathVariable Long id) {
        return paymentService.retryPayment(id);
    }
}