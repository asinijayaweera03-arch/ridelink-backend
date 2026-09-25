package lk.sliit.it3130.ride_service.service;

import lk.sliit.it3130.ride_service.client.DriverServiceClient;
import lk.sliit.it3130.ride_service.client.PaymentServiceClient;
import lk.sliit.it3130.ride_service.dto.AvailableDriverResponse;
import lk.sliit.it3130.ride_service.dto.RideCompletionRequest;
import lk.sliit.it3130.ride_service.dto.RideRequest;
import lk.sliit.it3130.ride_service.dto.RideResponse;
import lk.sliit.it3130.ride_service.model.Ride;
import lk.sliit.it3130.ride_service.model.RideStatus;
import lk.sliit.it3130.ride_service.repository.RideRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RideServiceTest {

    @Mock
    private RideRepository rideRepository;

    @Mock
    private DriverServiceClient driverServiceClient;

    @Mock
    private PaymentServiceClient paymentServiceClient;

    private RideService rideService;

    @BeforeEach
    void setUp() {
        rideService = new RideService(
                rideRepository,
                driverServiceClient,
                paymentServiceClient
        );
    }

    @Test
    void createRideShouldAssignAvailableDriver() {

        RideRequest request = createRideRequest();

        AvailableDriverResponse driver = new AvailableDriverResponse();
        driver.setId(10L);

        when(driverServiceClient.getAvailableDrivers())
                .thenReturn(List.of(driver));

        when(rideRepository.save(any(Ride.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        RideResponse response = rideService.createRide(request);

        assertEquals(10L, response.getDriverId());
        assertEquals(RideStatus.ASSIGNED, response.getStatus());

        verify(driverServiceClient).getAvailableDrivers();
        verify(rideRepository).save(any(Ride.class));
    }

    @Test
    void acceptRideShouldChangeAssignedRideToAccepted() {

        Ride ride = createRide(RideStatus.ASSIGNED);

        when(rideRepository.findById(1L))
                .thenReturn(Optional.of(ride));

        when(rideRepository.save(any(Ride.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        RideResponse response = rideService.acceptRide(1L);

        assertEquals(RideStatus.ACCEPTED, response.getStatus());

        verify(rideRepository).save(ride);
    }

    @Test
    void startRideShouldChangeAcceptedRideToInProgress() {

        Ride ride = createRide(RideStatus.ACCEPTED);

        when(rideRepository.findById(1L))
                .thenReturn(Optional.of(ride));

        when(rideRepository.save(any(Ride.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        RideResponse response = rideService.startRide(1L);

        assertEquals(RideStatus.IN_PROGRESS, response.getStatus());

        verify(rideRepository).save(ride);
    }

    @Test
    void completeRideShouldCreatePaymentAndMarkRideCompleted() {

        Ride ride = createRide(RideStatus.IN_PROGRESS);

        RideCompletionRequest request = new RideCompletionRequest();
        request.setDistanceKm(12.5);
        request.setDurationMin(30);
        request.setPeakHour(false);
        request.setPaymentMethod("CASH");

        when(rideRepository.findById(1L))
                .thenReturn(Optional.of(ride));

        when(rideRepository.save(any(Ride.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        RideResponse response =
                rideService.completeRide(1L, request);

        assertEquals(RideStatus.COMPLETED, response.getStatus());

        verify(paymentServiceClient, times(1))
                .createPayment(any());

        verify(rideRepository).save(ride);
    }

    @Test
    void cancelRideShouldChangeAssignedRideToCancelled() {

        Ride ride = createRide(RideStatus.ASSIGNED);

        when(rideRepository.findById(1L))
                .thenReturn(Optional.of(ride));

        when(rideRepository.save(any(Ride.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        RideResponse response = rideService.cancelRide(1L);

        assertEquals(RideStatus.CANCELLED, response.getStatus());

        verify(rideRepository).save(ride);
    }

    @Test
    void acceptRideShouldFailWhenRideIsNotAssigned() {

        Ride ride = createRide(RideStatus.REQUESTED);

        when(rideRepository.findById(1L))
                .thenReturn(Optional.of(ride));

        IllegalStateException exception =
                assertThrows(
                        IllegalStateException.class,
                        () -> rideService.acceptRide(1L)
                );

        assertEquals(
                "Only an ASSIGNED ride can be accepted",
                exception.getMessage()
        );

        verify(rideRepository, never())
                .save(any(Ride.class));
    }

    @Test
    void rideNotFoundShouldThrowIllegalArgumentException() {

        when(rideRepository.findById(99L))
                .thenReturn(Optional.empty());

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> rideService.startRide(99L)
                );

        assertEquals(
                "Ride not found",
                exception.getMessage()
        );

        verify(rideRepository, never())
                .save(any(Ride.class));
    }

    private RideRequest createRideRequest() {

        RideRequest request = new RideRequest();

        request.setRiderId(100L);
        request.setPickupLocation("Colombo");
        request.setDestination("Kandy");
        request.setPickupLatitude(6.9271);
        request.setPickupLongitude(79.8612);
        request.setDestinationLatitude(7.2906);
        request.setDestinationLongitude(80.6337);

        return request;
    }

    private Ride createRide(RideStatus status) {

        Ride ride = new Ride();

        ride.setRiderId(100L);
        ride.setDriverId(10L);
        ride.setPickupLocation("Colombo");
        ride.setDestination("Kandy");
        ride.setPickupLatitude(6.9271);
        ride.setPickupLongitude(79.8612);
        ride.setDestinationLatitude(7.2906);
        ride.setDestinationLongitude(80.6337);
        ride.setStatus(status);

        return ride;
    }
}