package io.github.ao.spond.weatherforecastservice.api.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record ForecastResponse(
        BigDecimal airTemperatureCelsius,
        BigDecimal windSpeedMetersPerSecond,
        Instant forecastFor,
        Instant expiresAt) {
}
