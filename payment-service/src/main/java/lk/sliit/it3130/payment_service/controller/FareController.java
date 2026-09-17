package lk.sliit.it3130.payment_service.controller;

import jakarta.validation.Valid;
import lk.sliit.it3130.payment_service.dto.FareEstimateRequest;
import lk.sliit.it3130.payment_service.dto.FareEstimateResponse;
import lk.sliit.it3130.payment_service.service.FareService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/fares")
public class FareController {

    private final FareService fareService;

    public FareController(FareService fareService) {
        this.fareService = fareService;
    }

    @PostMapping("/estimate")
    @ResponseStatus(HttpStatus.CREATED)
    public FareEstimateResponse createEstimate(@Valid @RequestBody FareEstimateRequest request) {
        return fareService.createEstimate(request);
    }

    @GetMapping("/estimate/{id}")
    public FareEstimateResponse getEstimate(@PathVariable Long id) {
        return fareService.getEstimateById(id);
    }
}