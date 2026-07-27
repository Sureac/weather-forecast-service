package io.github.ao.spond.weatherforecastservice.provider.metno.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

// AO 2026-07-27: partial mapping of the met.no locationforecast.compact response — only the fields we consume
public record MetNoForecastResponse(Properties properties) {

    public record Properties(List<TimeseriesEntry> timeseries) {
    }

    public record TimeseriesEntry(Instant time, TimeseriesData data) {
    }

    public record TimeseriesData(InstantForecast instant) {
    }

    public record InstantForecast(InstantDetails details) {
    }

    public record InstantDetails(
            @JsonProperty("air_temperature") BigDecimal airTemperature,
            @JsonProperty("wind_speed") BigDecimal windSpeed) {
    }
}
