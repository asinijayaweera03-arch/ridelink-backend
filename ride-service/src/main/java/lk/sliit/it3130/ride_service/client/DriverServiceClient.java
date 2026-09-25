package lk.sliit.it3130.ride_service.client;

import lk.sliit.it3130.ride_service.dto.AvailableDriverResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.net.http.HttpClient;
import java.time.Duration;
import java.util.List;

/**
 * Handles communication between Ride Service and Driver Service.
 */
@Component
public class DriverServiceClient {

    private final RestClient restClient;

    /**
     * Creates a client using the Driver Service base URL and
     * HTTP timeout values configured in application.properties.
     */
    public DriverServiceClient(
            @Value("${ridelink.driver-service.base-url}")
            String driverServiceBaseUrl,

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
                .baseUrl(driverServiceBaseUrl)
                .requestFactory(requestFactory)
                .build();
    }

    /**
     * Gets all currently available drivers from Driver Service.
     *
     * @return list of available drivers
     */
    public List<AvailableDriverResponse> getAvailableDrivers() {

        return restClient
                .method(HttpMethod.GET)
                .uri("/api/drivers/available")
                .retrieve()
                .body(new ParameterizedTypeReference<
                        List<AvailableDriverResponse>>() {});
    }
}