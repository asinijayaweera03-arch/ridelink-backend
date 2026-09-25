package lk.sliit.it3130.ride_service.client;

import lk.sliit.it3130.ride_service.dto.PaymentRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/**
 * Client used by Ride Service to communicate with Payment Service.
 */
@Component
public class PaymentServiceClient {

    private final RestClient restClient;
    private final String internalApiKey;

    public PaymentServiceClient(
            @Value("${payment-service.base-url}") String paymentServiceBaseUrl,
            @Value("${payment-service.internal-api-key}") String internalApiKey) {

        this.restClient = RestClient.builder()
                .baseUrl(paymentServiceBaseUrl)
                .build();

        this.internalApiKey = internalApiKey;
    }

    /**
     * Creates a payment after a ride is completed.
     *
     * @param paymentRequest payment details for the completed ride
     */
    public void createPayment(PaymentRequest paymentRequest) {

        restClient.post()
                .uri("/payments")
                .header("X-Internal-Api-Key", internalApiKey)
                .body(paymentRequest)
                .retrieve()
                .toBodilessEntity();
    }
}