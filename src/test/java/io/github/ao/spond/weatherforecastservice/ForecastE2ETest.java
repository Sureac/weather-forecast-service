package io.github.ao.spond.weatherforecastservice;

import com.github.tomakehurst.wiremock.WireMockServer;
import io.github.ao.spond.weatherforecastservice.api.dto.ForecastResponse;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestRestTemplate
@Import(TestcontainersConfiguration.class)
class ForecastE2ETest {

    private static final String FORECAST = "/api/v1/forecasts?lat=59.9114&lon=10.7579&time={time}";
    private static final String COMPACT_PATH = "/weatherapi/locationforecast/2.0/compact";
    private static final String COMPACT_RESPONSE = readFixture();

    private static final WireMockServer METNO = new WireMockServer(wireMockConfig().dynamicPort());

    static {
        METNO.start();
    }

    @DynamicPropertySource
    static void metNoProperties(DynamicPropertyRegistry registry) {
        registry.add("metno.base-url", () -> "http://localhost:" + METNO.port());
        registry.add("metno.read-timeout", () -> "1500ms");
    }

    @AfterAll
    static void stopWireMock() {
        METNO.stop();
    }

    @Autowired
    private TestRestTemplate restTemplate;

    @BeforeEach
    void stubMetNo() {
        METNO.resetAll();
        METNO.stubFor(get(urlPathEqualTo(COMPACT_PATH))
                .willReturn(aResponse()
                        .withHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                        .withBody(COMPACT_RESPONSE)));
    }

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
    void returnsForecastFromMetNoWhenAuthenticated() {
        ResponseEntity<ForecastResponse> response = authenticated().getForEntity(FORECAST, ForecastResponse.class, nearFuture());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().airTemperatureCelsius()).isNotNull();
        assertThat(response.getBody().windSpeedMetersPerSecond()).isNotNull();
        assertThat(response.getBody().forecastFor()).isNotNull();
    }

    @Test
    void returnsBadGatewayWhenMetNoErrors() {
        METNO.resetAll();
        METNO.stubFor(get(urlPathEqualTo(COMPACT_PATH)).willReturn(aResponse().withStatus(500)));

        ResponseEntity<String> response = authenticated().getForEntity(FORECAST, String.class, nearFuture());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_GATEWAY);
    }

    @Test
    void returnsBadGatewayWhenMetNoTimesOut() {
        METNO.resetAll();
        METNO.stubFor(get(urlPathEqualTo(COMPACT_PATH))
                .willReturn(aResponse().withFixedDelay(4000).withBody(COMPACT_RESPONSE)));

        ResponseEntity<String> response = authenticated().getForEntity(FORECAST, String.class, nearFuture());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_GATEWAY);
    }

    private TestRestTemplate authenticated() {
        return restTemplate.withBasicAuth("spond", "spond-secret");
    }

    private static Instant nearFuture() {
        return Instant.now().plus(1, ChronoUnit.DAYS);
    }

    private static String readFixture() {
        try (var in = ForecastE2ETest.class.getResourceAsStream("/metno/compact-response.json")) {
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new IllegalStateException("failed to read met.no fixture", e);
        }
    }
}
