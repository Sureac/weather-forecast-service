package io.github.ao.spond.weatherforecastservice.service;

import io.github.ao.spond.weatherforecastservice.provider.WeatherForecastProvider;
import io.github.ao.spond.weatherforecastservice.model.Coordinates;
import io.github.ao.spond.weatherforecastservice.model.Forecast;
import io.github.ao.spond.weatherforecastservice.model.ForecastWindowException;
import io.github.ao.spond.weatherforecastservice.repository.ForecastStore;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class ForecastService {

    private static final Duration FORECAST_WINDOW = Duration.ofDays(7);

    private final WeatherForecastProvider provider;
    private final ForecastStore store;

    public ForecastService(WeatherForecastProvider provider, ForecastStore store) {
        this.provider = provider;
        this.store = store;
    }

    public Forecast getForecast(Coordinates coordinates, Instant eventTime) {
        Instant now = Instant.now();

        // AO 2026-07-27: business rule from task definition
        if (eventTime.isAfter(now.plus(FORECAST_WINDOW))) {
            throw new ForecastWindowException("Event time must be within the next 7 days");
        }

        // AO 2026-07-27: met.no is hourly, so events sharing a location and hour share a forecast
        Instant eventHour = eventTime.truncatedTo(ChronoUnit.HOURS);
        return store.find(coordinates, eventHour)
                .filter(forecast -> forecast.expiresAt().isAfter(now))
                .orElseGet(() -> fetchAndCache(coordinates, eventHour, eventTime));
    }

    private Forecast fetchAndCache(Coordinates coordinates, Instant eventHour, Instant eventTime) {
        Forecast fresh = provider.fetchForecast(coordinates, eventTime);
        store.save(coordinates, eventHour, fresh);
        return fresh;
    }
}
