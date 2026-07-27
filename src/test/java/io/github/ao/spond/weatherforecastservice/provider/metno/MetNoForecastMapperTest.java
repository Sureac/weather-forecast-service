package io.github.ao.spond.weatherforecastservice.provider.metno;

import io.github.ao.spond.weatherforecastservice.provider.metno.dto.MetNoForecastResponse.InstantDetails;
import io.github.ao.spond.weatherforecastservice.provider.metno.dto.MetNoForecastResponse.InstantForecast;
import io.github.ao.spond.weatherforecastservice.provider.metno.dto.MetNoForecastResponse.TimeseriesData;
import io.github.ao.spond.weatherforecastservice.provider.metno.dto.MetNoForecastResponse.TimeseriesEntry;
import io.github.ao.spond.weatherforecastservice.provider.metno.mapper.MetNoForecastMapper;
import io.github.ao.spond.weatherforecastservice.model.Forecast;
import io.github.ao.spond.weatherforecastservice.provider.metno.mapper.MetNoForecastMapperImpl;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class MetNoForecastMapperTest {

    private final MetNoForecastMapper mapper = new MetNoForecastMapperImpl();

    @Test
    void mapsDetailsTimeAndExpiry() {
        TimeseriesEntry entry = new TimeseriesEntry(
                Instant.parse("2026-07-27T18:00:00Z"),
                new TimeseriesData(new InstantForecast(new InstantDetails(new BigDecimal("21.4"), new BigDecimal("3.2")))));
        Instant expiresAt = Instant.parse("2026-07-27T20:00:00Z");

        Forecast forecast = mapper.toForecast(entry, expiresAt);

        assertThat(forecast.airTemperatureCelsius()).isEqualByComparingTo("21.4");
        assertThat(forecast.windSpeedMetersPerSecond()).isEqualByComparingTo("3.2");
        assertThat(forecast.forecastFor()).isEqualTo(Instant.parse("2026-07-27T18:00:00Z"));
        assertThat(forecast.expiresAt()).isEqualTo(expiresAt);
    }
}
