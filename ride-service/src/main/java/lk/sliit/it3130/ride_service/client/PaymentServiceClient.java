package lk.sliit.it3130.ride_service.client;

import lk.sliit.it3130.ride_service.dto.PaymentRequest;
import lk.sliit.it3130.ride_service.dto.PaymentResponse;
import lk.sliit.it3130.ride_service.exception.PaymentServiceUnavailableException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

import java.net.http.HttpClient;
import java.time.Duration;

/**
 * Client used by Ride Service to communicate
 * with Payment Service.
 */
@Component
public class PaymentServiceClient {

    private final RestClient restClient;
    private final String internalApiKey;

    public PaymentServiceClient(
            @Value("${ridelink.payment-service.base-url}")
            String paymentServiceBaseUrl,

            @Value("${ridelink.payment-service.internal-api-key}")
            String internalApiKey,

            @Value("${ridelink.http.connect-timeout}")
            Duration connectTimeout,

            @Value("${ridelink.http.read-timeout}")
            Duration readTimeout) {

        HttpClient httpClient =
                HttpClient.newBuilder()
                        .connectTimeout(connectTimeout)
                        .build();

        JdkClientHttpRequestFactory requestFactory =
                new JdkClientHttpRequestFactory(httpClient);

        requestFactory.setReadTimeout(readTimeout);

        this.restClient =
                RestClient.builder()
                        .baseUrl(paymentServiceBaseUrl)
                        .requestFactory(requestFactory)
                        .build();

        this.internalApiKey = internalApiKey;
    }

    /**
     * Creates a new payment record for a completed ride.
     */
    public PaymentResponse createPayment(
            PaymentRequest paymentRequest) {

        try {

            PaymentResponse response =
                    restClient.post()
                            .uri("/payments")
                            .header(
                                    "X-Internal-Api-Key",
                                    internalApiKey
                            )
                            .body(paymentRequest)
                            .retrieve()
                            .body(PaymentResponse.class);

            return requireResponse(response);

        } catch (ResourceAccessException exception) {

            throw unavailable(exception);
        }
    }

    /**
     * Retries a payment record that Payment Service
     * previously stored with FAILED status.
     */
    public PaymentResponse retryPayment(Long paymentId) {

        try {

            PaymentResponse response =
                    restClient.patch()
                            .uri("/payments/{id}/status", paymentId)
                            .header(
                                    "X-Internal-Api-Key",
                                    internalApiKey
                            )
                            .retrieve()
                            .body(PaymentResponse.class);

            return requireResponse(response);

        } catch (ResourceAccessException exception) {

            throw unavailable(exception);
        }
    }

    private PaymentResponse requireResponse(
            PaymentResponse response) {

        if (response == null) {
            throw new PaymentServiceUnavailableException(
                    "Payment Service returned an empty response"
            );
        }

        return response;
    }

    private PaymentServiceUnavailableException unavailable(
            ResourceAccessException exception) {

        return new PaymentServiceUnavailableException(
                "Payment Service is currently unavailable",
                exception
        );
    }
}