package lk.sliit.it3130.ride_service.client;

import lk.sliit.it3130.ride_service.dto.PaymentRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.net.http.HttpClient;
import java.time.Duration;

/**
 * Client used by Ride Service to communicate with Payment Service.
 */
@Component
public class PaymentServiceClient {

    private final RestClient restClient;
    private final String internalApiKey;

    /**
     * Creates a client using Payment Service configuration
     * and HTTP timeout values from application.properties.
     */
    public PaymentServiceClient(
            @Value("${ridelink.payment-service.base-url}")
            String paymentServiceBaseUrl,

            @Value("${ridelink.payment-service.internal-api-key}")
            String internalApiKey,

            @Value("${ridelink.http.connect-timeout}")
            Duration connectTimeout,

            @Value("${ridelink.http.read-timeout}")
            Duration readTimeout) {

        HttpClient httpClient = HttpClient.newBuilder()
                .connectTimeout(connectTimeout)
                .build();

        JdkClientHttpRequestFactory requestFactory =
                new JdkClientHttpRequestFactory(httpClient);

        requestFactory.setReadTimeout(readTimeout);

        this.restClient = RestClient.builder()
                .baseUrl(paymentServiceBaseUrl)
                .requestFactory(requestFactory)
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