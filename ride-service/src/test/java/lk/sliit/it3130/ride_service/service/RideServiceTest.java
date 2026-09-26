package lk.sliit.it3130.ride_service.service;

import lk.sliit.it3130.ride_service.client.DriverServiceClient;
import lk.sliit.it3130.ride_service.client.PaymentServiceClient;
import lk.sliit.it3130.ride_service.dto.AvailableDriverResponse;
import lk.sliit.it3130.ride_service.dto.PaymentResponse;
import lk.sliit.it3130.ride_service.dto.RideCompletionRequest;
import lk.sliit.it3130.ride_service.dto.RideRequest;
import lk.sliit.it3130.ride_service.dto.RideResponse;
import lk.sliit.it3130.ride_service.exception.DriverServiceUnavailableException;
import lk.sliit.it3130.ride_service.exception.InvalidRideTransitionException;
import lk.sliit.it3130.ride_service.exception.NoAvailableDriverException;
import lk.sliit.it3130.ride_service.exception.PaymentServiceUnavailableException;
import lk.sliit.it3130.ride_service.exception.RideNotFoundException;
import lk.sliit.it3130.ride_service.model.PaymentRecordStatus;
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

        AvailableDriverResponse driver =
                new AvailableDriverResponse();

        driver.setId(10L);

        when(driverServiceClient.getAvailableDrivers())
                .thenReturn(List.of(driver));

        when(rideRepository.save(any(Ride.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0));

        RideResponse response =
                rideService.createRide(request);

        assertEquals(
                10L,
                response.getDriverId()
        );

        assertEquals(
                RideStatus.ASSIGNED,
                response.getStatus()
        );

        assertEquals(
                PaymentRecordStatus.NOT_APPLICABLE,
                response.getPaymentStatus()
        );

        verify(driverServiceClient)
                .getAvailableDrivers();

        verify(rideRepository)
                .save(any(Ride.class));
    }

    @Test
    void createRideShouldFailWhenNoDriverIsAvailable() {

        RideRequest request = createRideRequest();

        when(driverServiceClient.getAvailableDrivers())
                .thenReturn(List.of());

        NoAvailableDriverException exception =
                assertThrows(
                        NoAvailableDriverException.class,
                        () -> rideService.createRide(request)
                );

        assertEquals(
                "No available driver found",
                exception.getMessage()
        );

        verify(driverServiceClient)
                .getAvailableDrivers();

        verify(
                rideRepository,
                never()
        ).save(any(Ride.class));
    }

    @Test
    void createRideShouldFailWhenDriverServiceIsUnavailable() {

        RideRequest request = createRideRequest();

        when(driverServiceClient.getAvailableDrivers())
                .thenThrow(
                        new DriverServiceUnavailableException(
                                "Driver Service is currently unavailable"
                        )
                );

        DriverServiceUnavailableException exception =
                assertThrows(
                        DriverServiceUnavailableException.class,
                        () -> rideService.createRide(request)
                );

        assertEquals(
                "Driver Service is currently unavailable",
                exception.getMessage()
        );

        verify(driverServiceClient)
                .getAvailableDrivers();

        verify(
                rideRepository,
                never()
        ).save(any(Ride.class));
    }

    @Test
    void acceptRideShouldChangeAssignedRideToAccepted() {

        Ride ride =
                createRide(RideStatus.ASSIGNED);

        when(rideRepository.findById(1L))
                .thenReturn(Optional.of(ride));

        when(rideRepository.save(any(Ride.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0));

        RideResponse response =
                rideService.acceptRide(1L);

        assertEquals(
                RideStatus.ACCEPTED,
                response.getStatus()
        );

        assertNotNull(
                ride.getAcceptedAt()
        );

        verify(rideRepository)
                .save(ride);
    }

    @Test
    void startRideShouldChangeAcceptedRideToInProgress() {

        Ride ride =
                createRide(RideStatus.ACCEPTED);

        when(rideRepository.findById(1L))
                .thenReturn(Optional.of(ride));

        when(rideRepository.save(any(Ride.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0));

        RideResponse response =
                rideService.startRide(1L);

        assertEquals(
                RideStatus.IN_PROGRESS,
                response.getStatus()
        );

        assertNotNull(
                ride.getStartedAt()
        );

        verify(rideRepository)
                .save(ride);
    }

    @Test
    void completeRideShouldRecordSuccessfulPayment() {

        Ride ride = createInProgressRide();

        RideCompletionRequest request =
                createCompletionRequest();

        PaymentResponse paymentResponse =
                createPaymentResponse(
                        50L,
                        7250.0,
                        "SUCCESS"
                );

        when(rideRepository.findById(1L))
                .thenReturn(Optional.of(ride));

        when(rideRepository.save(any(Ride.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0));

        when(paymentServiceClient.createPayment(any()))
                .thenReturn(paymentResponse);

        RideResponse response =
                rideService.completeRide(
                        1L,
                        request
                );

        assertEquals(
                RideStatus.COMPLETED,
                response.getStatus()
        );

        assertEquals(
                PaymentRecordStatus.RECORDED,
                response.getPaymentStatus()
        );

        assertEquals(
                50L,
                response.getPaymentId()
        );

        assertEquals(
                7250.0,
                response.getFareAmount()
        );

        assertNotNull(
                ride.getCompletedAt()
        );

        verify(paymentServiceClient)
                .createPayment(any());

        verify(
                rideRepository,
                atLeast(2)
        ).save(ride);
    }

    @Test
    void completeRideShouldMarkPaymentFailedWhenPaymentFails() {

        Ride ride = createInProgressRide();

        RideCompletionRequest request =
                createCompletionRequest();

        PaymentResponse paymentResponse =
                createPaymentResponse(
                        51L,
                        120000.0,
                        "FAILED"
                );

        when(rideRepository.findById(1L))
                .thenReturn(Optional.of(ride));

        when(rideRepository.save(any(Ride.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0));

        when(paymentServiceClient.createPayment(any()))
                .thenReturn(paymentResponse);

        RideResponse response =
                rideService.completeRide(
                        1L,
                        request
                );

        assertEquals(
                RideStatus.COMPLETED,
                response.getStatus()
        );

        assertEquals(
                PaymentRecordStatus.FAILED,
                response.getPaymentStatus()
        );

        assertEquals(
                51L,
                response.getPaymentId()
        );

        assertEquals(
                120000.0,
                response.getFareAmount()
        );
    }

    @Test
    void completeRideShouldRemainPendingWhenPaymentServiceIsUnavailable() {

        Ride ride = createInProgressRide();

        RideCompletionRequest request =
                createCompletionRequest();

        when(rideRepository.findById(1L))
                .thenReturn(Optional.of(ride));

        when(rideRepository.save(any(Ride.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0));

        when(paymentServiceClient.createPayment(any()))
                .thenThrow(
                        new PaymentServiceUnavailableException(
                                "Payment Service is currently unavailable"
                        )
                );

        RideResponse response =
                rideService.completeRide(
                        1L,
                        request
                );

        assertEquals(
                RideStatus.COMPLETED,
                response.getStatus()
        );

        assertEquals(
                PaymentRecordStatus.PENDING,
                response.getPaymentStatus()
        );

        assertNull(
                response.getPaymentId()
        );
    }

    @Test
    void retryPendingPaymentShouldCreateNewPayment() {

        Ride ride = createCompletedRide();

        ride.setPaymentStatus(
                PaymentRecordStatus.PENDING
        );

        PaymentResponse paymentResponse =
                createPaymentResponse(
                        60L,
                        7250.0,
                        "SUCCESS"
                );

        when(rideRepository.findById(1L))
                .thenReturn(Optional.of(ride));

        when(rideRepository.save(any(Ride.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0));

        when(paymentServiceClient.createPayment(any()))
                .thenReturn(paymentResponse);

        RideResponse response =
                rideService.retryPayment(
                        1L,
                        false,
                        "CASH"
                );

        assertEquals(
                PaymentRecordStatus.RECORDED,
                response.getPaymentStatus()
        );

        assertEquals(
                60L,
                response.getPaymentId()
        );

        verify(paymentServiceClient)
                .createPayment(any());

        verify(
                paymentServiceClient,
                never()
        ).retryPayment(anyLong());
    }

    @Test
    void retryFailedPaymentShouldRetryExistingPayment() {

        Ride ride = createCompletedRide();

        ride.setPaymentStatus(
                PaymentRecordStatus.FAILED
        );

        ride.setPaymentId(70L);
        ride.setFareAmount(120000.0);

        PaymentResponse paymentResponse =
                createPaymentResponse(
                        70L,
                        120000.0,
                        "SUCCESS"
                );

        when(rideRepository.findById(1L))
                .thenReturn(Optional.of(ride));

        when(rideRepository.save(any(Ride.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0));

        when(paymentServiceClient.retryPayment(70L))
                .thenReturn(paymentResponse);

        RideResponse response =
                rideService.retryPayment(
                        1L,
                        false,
                        "CASH"
                );

        assertEquals(
                PaymentRecordStatus.RECORDED,
                response.getPaymentStatus()
        );

        assertEquals(
                70L,
                response.getPaymentId()
        );

        verify(paymentServiceClient)
                .retryPayment(70L);

        verify(
                paymentServiceClient,
                never()
        ).createPayment(any());
    }

    @Test
    void retryPaymentShouldFailWhenAlreadyRecorded() {

        Ride ride = createCompletedRide();

        ride.setPaymentStatus(
                PaymentRecordStatus.RECORDED
        );

        when(rideRepository.findById(1L))
                .thenReturn(Optional.of(ride));

        IllegalStateException exception =
                assertThrows(
                        IllegalStateException.class,
                        () -> rideService.retryPayment(
                                1L,
                                false,
                                "CASH"
                        )
                );

        assertEquals(
                "Payment has already been recorded",
                exception.getMessage()
        );

        verifyNoInteractions(
                paymentServiceClient
        );
    }

    @Test
    void cancelRideShouldChangeAssignedRideToCancelled() {

        Ride ride =
                createRide(RideStatus.ASSIGNED);

        when(rideRepository.findById(1L))
                .thenReturn(Optional.of(ride));

        when(rideRepository.save(any(Ride.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0));

        RideResponse response =
                rideService.cancelRide(
                        1L,
                        "Passenger changed travel plans"
                );

        assertEquals(
                RideStatus.CANCELLED,
                response.getStatus()
        );

        assertNotNull(
                ride.getCancelledAt()
        );

        assertEquals(
                "Passenger changed travel plans",
                ride.getCancellationReason()
        );

        assertEquals(
                "Passenger changed travel plans",
                response.getCancellationReason()
        );

        verify(rideRepository)
                .save(ride);
    }

    @Test
    void cancelRideShouldAllowMissingReason() {

        Ride ride =
                createRide(RideStatus.ASSIGNED);

        when(rideRepository.findById(1L))
                .thenReturn(Optional.of(ride));

        when(rideRepository.save(any(Ride.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0));

        RideResponse response =
                rideService.cancelRide(
                        1L,
                        null
                );

        assertEquals(
                RideStatus.CANCELLED,
                response.getStatus()
        );

        assertNull(
                ride.getCancellationReason()
        );

        assertNull(
                response.getCancellationReason()
        );

        assertNotNull(
                ride.getCancelledAt()
        );

        verify(rideRepository)
                .save(ride);
    }

    @Test
    void acceptRideShouldFailWhenRideIsNotAssigned() {

        Ride ride =
                createRide(RideStatus.REQUESTED);

        when(rideRepository.findById(1L))
                .thenReturn(Optional.of(ride));

        InvalidRideTransitionException exception =
                assertThrows(
                        InvalidRideTransitionException.class,
                        () -> rideService.acceptRide(1L)
                );

        assertEquals(
                RideStatus.REQUESTED,
                exception.getCurrentStatus()
        );

        assertEquals(
                RideStatus.REQUESTED.allowedNextStates(),
                exception.getAllowedNextStates()
        );

        verify(
                rideRepository,
                never()
        ).save(any(Ride.class));
    }

    @Test
    void cancelRideShouldFailWhenRideIsInProgress() {

        Ride ride =
                createRide(RideStatus.IN_PROGRESS);

        when(rideRepository.findById(1L))
                .thenReturn(Optional.of(ride));

        InvalidRideTransitionException exception =
                assertThrows(
                        InvalidRideTransitionException.class,
                        () -> rideService.cancelRide(
                                1L,
                                "Passenger requested cancellation"
                        )
                );

        assertEquals(
                RideStatus.IN_PROGRESS,
                exception.getCurrentStatus()
        );

        assertEquals(
                RideStatus.IN_PROGRESS.allowedNextStates(),
                exception.getAllowedNextStates()
        );

        assertTrue(
                exception
                        .getAllowedNextStates()
                        .contains(
                                RideStatus.COMPLETED
                        )
        );

        assertFalse(
                exception
                        .getAllowedNextStates()
                        .contains(
                                RideStatus.CANCELLED
                        )
        );

        verify(
                rideRepository,
                never()
        ).save(any(Ride.class));
    }

    @Test
    void rideNotFoundShouldThrowRideNotFoundException() {

        when(rideRepository.findById(99L))
                .thenReturn(Optional.empty());

        RideNotFoundException exception =
                assertThrows(
                        RideNotFoundException.class,
                        () -> rideService.startRide(99L)
                );

        assertEquals(
                "Ride not found: 99",
                exception.getMessage()
        );

        verify(
                rideRepository,
                never()
        ).save(any(Ride.class));
    }

    @Test
    void retryPaymentShouldKeepPendingWhenPaymentServiceUnavailable() {

        Ride ride = createCompletedRide();

        ride.setPaymentStatus(
                PaymentRecordStatus.PENDING
        );

        when(rideRepository.findById(1L))
                .thenReturn(Optional.of(ride));

        when(rideRepository.save(any(Ride.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0));

        when(paymentServiceClient.createPayment(any()))
                .thenThrow(
                        new PaymentServiceUnavailableException(
                                "Payment Service is unavailable"
                        )
                );

        RideResponse response =
                rideService.retryPayment(
                        1L,
                        false,
                        "CASH"
                );

        assertEquals(
                RideStatus.COMPLETED,
                response.getStatus()
        );

        assertEquals(
                PaymentRecordStatus.PENDING,
                response.getPaymentStatus()
        );

        assertNull(
                response.getPaymentId()
        );

        assertEquals(
                PaymentRecordStatus.PENDING,
                ride.getPaymentStatus()
        );

        verify(paymentServiceClient)
                .createPayment(any());

        verify(
        	rideRepository,
        	times(2)
	).save(ride);
    }

    private RideRequest createRideRequest() {

        RideRequest request =
                new RideRequest();

        request.setRiderId(100L);
        request.setPickupLocation("Colombo");
        request.setDestination("Kandy");

        request.setPickupLatitude(6.9271);
        request.setPickupLongitude(79.8612);

        request.setDestinationLatitude(7.2906);
        request.setDestinationLongitude(80.6337);

        request.setDistanceKm(115.0);

        return request;
    }

    private RideCompletionRequest createCompletionRequest() {

        RideCompletionRequest request =
                new RideCompletionRequest();

        request.setPeakHour(false);
        request.setPaymentMethod("CASH");

        return request;
    }

    private PaymentResponse createPaymentResponse(
            Long paymentId,
            Double amount,
            String status) {

        PaymentResponse response =
                new PaymentResponse();

        response.setId(paymentId);
        response.setRideId(1L);
        response.setPassengerId(100L);
        response.setAmount(amount);
        response.setStatus(status);
        response.setPaymentMethod("CASH");

        return response;
    }

    private Ride createRide(
            RideStatus status) {

        Ride ride = baseRide();

        ride.setStatus(status);

        return ride;
    }

    /**
     * Creates a real IN_PROGRESS ride through the lifecycle
     * so startedAt is populated.
     */
    private Ride createInProgressRide() {

        Ride ride = baseRide();

        ride.setStatus(RideStatus.REQUESTED);

        ride.transitionTo(
                RideStatus.ASSIGNED
        );

        ride.transitionTo(
                RideStatus.ACCEPTED
        );

        ride.transitionTo(
                RideStatus.IN_PROGRESS
        );

        return ride;
    }

    /**
     * Creates a real COMPLETED ride with both startedAt
     * and completedAt populated.
     */
    private Ride createCompletedRide() {

        Ride ride =
                createInProgressRide();

        ride.transitionTo(
                RideStatus.COMPLETED
        );

        return ride;
    }

    private Ride baseRide() {

        Ride ride = new Ride();

        ride.setRiderId(100L);
        ride.setDriverId(10L);

        ride.setPickupLocation("Colombo");
        ride.setDestination("Kandy");

        ride.setPickupLatitude(6.9271);
        ride.setPickupLongitude(79.8612);

        ride.setDestinationLatitude(7.2906);
        ride.setDestinationLongitude(80.6337);

        ride.setDistanceKm(115.0);

        return ride;
    }
}