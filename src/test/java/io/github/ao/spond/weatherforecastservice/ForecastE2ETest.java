package io.github.ao.spond.weatherforecastservice;

import io.github.ao.spond.weatherforecastservice.api.dto.ForecastResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestRestTemplate
@Import(TestcontainersConfiguration.class)
class ForecastE2ETest {

    private static final String FORECAST = "/api/v1/forecast?lat=59.9139&lon=10.7522&time={time}";

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void scalarIsPublicAndReturnsOk() {
        ResponseEntity<String> response = restTemplate.getForEntity("/scalar", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void forecastRequiresAuthentication() {
        ResponseEntity<String> response = restTemplate.getForEntity(FORECAST, String.class, nearFuture());
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void forecastEchoesRequestedTimeWhenAuthenticated() {
        Instant time = nearFuture();

        ResponseEntity<ForecastResponse> response = restTemplate
                .withBasicAuth("spond", "spond-secret")
                .getForEntity(FORECAST, ForecastResponse.class, time);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().airTemperatureCelsius()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(response.getBody().windSpeedMetersPerSecond()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    private static Instant nearFuture() {
        return Instant.now().plus(1, ChronoUnit.DAYS);
    }
}
