package lk.sliit.it3130.payment_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lk.sliit.it3130.payment_service.dto.ReceiptResponse;
import lk.sliit.it3130.payment_service.service.ReceiptService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/receipts")
@Tag(name = "Receipts", description = "Endpoints for retrieving payment receipts")
public class ReceiptController {

    private final ReceiptService receiptService;

    public ReceiptController(ReceiptService receiptService) {
        this.receiptService = receiptService;
    }

    @Operation(summary = "Get receipt by payment ID")
    @ApiResponse(responseCode = "200", description = "Receipt found")
    @ApiResponse(responseCode = "404", description = "No receipt found for this payment")
    @GetMapping("/{paymentId}")
    public ReceiptResponse getReceiptByPayment(@PathVariable Long paymentId) {
        return receiptService.getByPaymentId(paymentId);
    }

    @Operation(summary = "Get receipt by ride ID")
    @ApiResponse(responseCode = "200", description = "Receipt found")
    @ApiResponse(responseCode = "404", description = "No receipt found for this ride")
    @GetMapping("/ride/{rideId}")
    public ReceiptResponse getReceiptByRide(@PathVariable Long rideId) {
        return receiptService.getByRideId(rideId);
    }
}