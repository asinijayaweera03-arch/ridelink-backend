package lk.sliit.it3130.payment_service.service;

import lk.sliit.it3130.payment_service.dto.PaymentRequest;
import lk.sliit.it3130.payment_service.dto.PaymentResponse;
import lk.sliit.it3130.payment_service.entity.Payment;
import lk.sliit.it3130.payment_service.exception.PaymentException;
import lk.sliit.it3130.payment_service.exception.PaymentNotFoundException;
import lk.sliit.it3130.payment_service.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private ReceiptService receiptService;

    @InjectMocks
    private PaymentService paymentService;

    private PaymentRequest request;

    @BeforeEach
    void setUp() {
        request = new PaymentRequest();
        request.setRideId(1L);
        request.setPassengerId(1L);
        request.setDistanceKm(12.5);
        request.setDurationMin(25.0);
        request.setPeakHour(false);
        request.setPaymentMethod("CARD");
    }

    @Test
    void processPayment_success_savesAndReturnsPayment() {
        when(paymentRepository.findByRideId(1L)).thenReturn(Optional.empty());

        Payment saved = new Payment();
        saved.setId(1L);
        saved.setRideId(1L);
        saved.setAmount(975.0); // 100 + 12.5*60 + 25*5
        saved.setStatus(Payment.PaymentStatus.SUCCESS);
        saved.setPaymentMethod(Payment.PaymentMethod.CARD);

        when(paymentRepository.save(any(Payment.class))).thenReturn(saved);

        PaymentResponse response = paymentService.processPayment(request);

        assertEquals("SUCCESS", response.getStatus());
        assertEquals(975.0, response.getAmount());
        verify(receiptService, times(1)).generateReceipt(any(), anyDouble(), anyDouble(), anyBoolean());
    }

    @Test
    void processPayment_duplicateRide_throwsPaymentException() {
        when(paymentRepository.findByRideId(1L)).thenReturn(Optional.of(new Payment()));

        assertThrows(PaymentException.class, () -> paymentService.processPayment(request));
        verify(paymentRepository, never()).save(any());
    }

    @Test
    void processPayment_peakHour_appliesSurcharge() {
        request.setPeakHour(true);
        when(paymentRepository.findByRideId(1L)).thenReturn(Optional.empty());

        Payment saved = new Payment();
        saved.setId(2L);
        saved.setAmount(1170.0); // 975 * 1.2
        saved.setStatus(Payment.PaymentStatus.SUCCESS);

        when(paymentRepository.save(any(Payment.class))).thenReturn(saved);

        PaymentResponse response = paymentService.processPayment(request);

        assertEquals(1170.0, response.getAmount());
    }

    @Test
    void getPaymentById_notFound_throwsException() {
        when(paymentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(PaymentNotFoundException.class, () -> paymentService.getPaymentById(99L));
    }

    @Test
    void retryPayment_alreadySuccess_throwsException() {
        Payment existing = new Payment();
        existing.setId(1L);
        existing.setStatus(Payment.PaymentStatus.SUCCESS);

        when(paymentRepository.findById(1L)).thenReturn(Optional.of(existing));

        assertThrows(PaymentException.class, () -> paymentService.retryPayment(1L));
    }
}