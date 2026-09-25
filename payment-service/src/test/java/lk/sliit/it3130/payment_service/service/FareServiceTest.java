package lk.sliit.it3130.payment_service.service;

import lk.sliit.it3130.payment_service.dto.FareEstimateRequest;
import lk.sliit.it3130.payment_service.dto.FareEstimateResponse;
import lk.sliit.it3130.payment_service.entity.FareEstimate;
import lk.sliit.it3130.payment_service.repository.FareEstimateRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FareServiceTest {

    @Mock
    private FareEstimateRepository fareEstimateRepository;

    @InjectMocks
    private FareService fareService;

    private FareEstimateRequest request;

    @BeforeEach
    void setUp() {
        request = new FareEstimateRequest();
        request.setPickupLocation("Colombo Fort");
        request.setDestinationLocation("Malabe");
        request.setDistanceKm(12.5);
    }

    @Test
    void createEstimate_calculatesCorrectFare() {
        // base 100 + 12.5 * 60 = 850
        FareEstimate saved = new FareEstimate();
        saved.setId(1L);
        saved.setPickupLocation("Colombo Fort");
        saved.setDestinationLocation("Malabe");
        saved.setDistanceKm(12.5);
        saved.setEstimatedFare(850.0);

        when(fareEstimateRepository.save(any(FareEstimate.class))).thenReturn(saved);

        FareEstimateResponse response = fareService.createEstimate(request);

        assertEquals(850.0, response.getEstimatedFare());
        assertEquals("Colombo Fort", response.getPickupLocation());
        verify(fareEstimateRepository, times(1)).save(any(FareEstimate.class));
    }

    @Test
    void createEstimate_zeroDistance_returnsBaseFareOnly() {
        request.setDistanceKm(0.0);

        FareEstimate saved = new FareEstimate();
        saved.setId(2L);
        saved.setEstimatedFare(100.0);

        when(fareEstimateRepository.save(any(FareEstimate.class))).thenReturn(saved);

        FareEstimateResponse response = fareService.createEstimate(request);

        assertEquals(100.0, response.getEstimatedFare());
    }

    @Test
    void getEstimateById_found_returnsResponse() {
        FareEstimate estimate = new FareEstimate();
        estimate.setId(1L);
        estimate.setEstimatedFare(850.0);

        when(fareEstimateRepository.findById(1L)).thenReturn(Optional.of(estimate));

        FareEstimateResponse response = fareService.getEstimateById(1L);

        assertEquals(1L, response.getId());
        assertEquals(850.0, response.getEstimatedFare());
    }

    @Test
    void getEstimateById_notFound_throwsException() {
        when(fareEstimateRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> fareService.getEstimateById(99L));
    }
}