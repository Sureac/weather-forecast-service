package io.github.ao.spond.weatherforecastservice.provider.metno;

import io.github.ao.spond.weatherforecastservice.model.Coordinates;
import io.github.ao.spond.weatherforecastservice.model.Forecast;
import io.github.ao.spond.weatherforecastservice.provider.metno.client.MetNoClient;
import io.github.ao.spond.weatherforecastservice.provider.metno.mapper.MetNoForecastMapperImpl;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@Disabled("ad-hoc: makes a real network call to api.met.no; run manually to verify the live integration")
class MetNoWeatherForecastProviderLiveTest {

    private static final String USER_AGENT = "weather-forecast-service github.com/Sureac/weather-forecast-service";

    @Test
    void fetchesLiveForecastFromMetNo() {
        RestClient.Builder builder = RestClient.builder()
                .baseUrl("https://api.met.no")
                .defaultHeader(HttpHeaders.USER_AGENT, USER_AGENT);
        MetNoClient client = HttpServiceProxyFactory
                .builderFor(RestClientAdapter.create(builder.build()))
                .build()
                .createClient(MetNoClient.class);
        MetNoWeatherForecastProvider sut = new MetNoWeatherForecastProvider(client, new MetNoForecastMapperImpl());

        Coordinates oslo = new Coordinates(new BigDecimal("59.9114"), new BigDecimal("10.7579"));
        Forecast forecast = sut.fetchForecast(oslo, Instant.now().plus(Duration.ofHours(2)));

        assertThat(forecast.airTemperatureCelsius()).isNotNull();
        assertThat(forecast.windSpeedMetersPerSecond()).isNotNull();
        assertThat(forecast.forecastFor()).isNotNull();
        assertThat(forecast.expiresAt()).isNotNull();
    }
}
