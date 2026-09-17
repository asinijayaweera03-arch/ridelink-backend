package lk.sliit.it3130.payment_service.service;

import lk.sliit.it3130.payment_service.dto.FareEstimateRequest;
import lk.sliit.it3130.payment_service.dto.FareEstimateResponse;
import lk.sliit.it3130.payment_service.entity.FareEstimate;
import lk.sliit.it3130.payment_service.repository.FareEstimateRepository;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
public class FareService {

    // Documented fare calculation rule
    private static final double BASE_FARE = 100.0;
    private static final double PER_KM_RATE = 60.0;

    private final FareEstimateRepository fareEstimateRepository;

    public FareService(FareEstimateRepository fareEstimateRepository) {
        this.fareEstimateRepository = fareEstimateRepository;
    }

    public FareEstimateResponse createEstimate(FareEstimateRequest request) {
        double estimatedFare = BASE_FARE + (request.getDistanceKm() * PER_KM_RATE);

        FareEstimate estimate = new FareEstimate();
        estimate.setPickupLocation(request.getPickupLocation());
        estimate.setDestinationLocation(request.getDestinationLocation());
        estimate.setDistanceKm(request.getDistanceKm());
        estimate.setEstimatedFare(estimatedFare);

        FareEstimate saved = fareEstimateRepository.save(estimate);

        return toResponse(saved);
    }

    public FareEstimateResponse getEstimateById(Long id) {
        FareEstimate estimate = fareEstimateRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Fare estimate not found with id: " + id));
        return toResponse(estimate);
    }

    private FareEstimateResponse toResponse(FareEstimate estimate) {
        return new FareEstimateResponse(
                estimate.getId(),
                estimate.getPickupLocation(),
                estimate.getDestinationLocation(),
                estimate.getDistanceKm(),
                estimate.getEstimatedFare()
        );
    }
}