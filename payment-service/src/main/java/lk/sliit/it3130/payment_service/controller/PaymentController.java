package lk.sliit.it3130.payment_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lk.sliit.it3130.payment_service.dto.PaymentRequest;
import lk.sliit.it3130.payment_service.dto.PaymentResponse;
import lk.sliit.it3130.payment_service.service.PaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
@Tag(name = "Payments", description = "Endpoints for recording and retrieving ride payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @Operation(summary = "Process a ride payment (internal)", description = "Called by Ride Management Service when a ride completes. Requires X-Internal-Api-Key header.")
    @ApiResponse(responseCode = "201", description = "Payment processed")
    @ApiResponse(responseCode = "403", description = "Missing or invalid internal API key")
    @ApiResponse(responseCode = "409", description = "Payment already exists for this ride")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PaymentResponse processPayment(@Valid @RequestBody PaymentRequest request) {
        return paymentService.processPayment(request);
    }

    @Operation(summary = "Get payment by ID", description = "Requires a valid JWT.")
    @ApiResponse(responseCode = "200", description = "Payment found")
    @ApiResponse(responseCode = "401", description = "Missing or invalid JWT")
    @ApiResponse(responseCode = "404", description = "Payment not found")
    @GetMapping("/{id}")
    public PaymentResponse getPayment(@Parameter(description = "Payment ID") @PathVariable Long id) {
        return paymentService.getPaymentById(id);
    }

    @Operation(summary = "Get payment by ride ID", description = "Requires a valid JWT.")
    @ApiResponse(responseCode = "200", description = "Payment found")
    @ApiResponse(responseCode = "404", description = "No payment found for this ride")
    @GetMapping("/ride/{rideId}")
    public PaymentResponse getPaymentByRide(@PathVariable Long rideId) {
        return paymentService.getPaymentByRideId(rideId);
    }

    @Operation(summary = "Retry a failed payment", description = "Marks a previously failed payment as successful.")
    @ApiResponse(responseCode = "200", description = "Payment retried successfully")
    @ApiResponse(responseCode = "409", description = "Payment already succeeded")
    @PatchMapping("/{id}/status")
    public PaymentResponse retryPayment(@PathVariable Long id) {
        return paymentService.retryPayment(id);
    }
}