package io.github.ao.spond.weatherforecastservice.model;

import java.math.BigDecimal;
import java.time.Instant;

public record Forecast(
        BigDecimal airTemperatureCelsius,
        BigDecimal windSpeedMetersPerSecond,
        Instant forecastFor,
        Instant expiresAt) {
}
