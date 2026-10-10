package lk.sliit.it3130.payment_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lk.sliit.it3130.payment_service.dto.FareEstimateRequest;
import lk.sliit.it3130.payment_service.dto.FareEstimateResponse;
import lk.sliit.it3130.payment_service.service.FareService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/fares")
@Tag(name = "Fare Estimation", description = "Endpoints for estimating ride fares before booking")
public class FareController {

    private final FareService fareService;

    public FareController(FareService fareService) {
        this.fareService = fareService;
    }

    @Operation(summary = "Create a fare estimate", description = "Calculates an estimated fare based on pickup, destination and distance.")
    @ApiResponse(responseCode = "201", description = "Estimate created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid input (e.g. negative distance)")
    @PostMapping("/estimate")
    @ResponseStatus(HttpStatus.CREATED)
    public FareEstimateResponse createEstimate(@Valid @RequestBody FareEstimateRequest request) {
        return fareService.createEstimate(request);
    }

    @Operation(summary = "Get a fare estimate by ID", description = "Retrieves a previously created fare estimate.")
    @ApiResponse(responseCode = "200", description = "Estimate found")
    @ApiResponse(responseCode = "404", description = "Estimate not found")
    @GetMapping("/estimate/{id}")
    public FareEstimateResponse getEstimate(@PathVariable Long id) {
        return fareService.getEstimateById(id);
    }
}