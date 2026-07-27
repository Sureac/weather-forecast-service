package io.github.ao.spond.weatherforecastservice.service;

import io.github.ao.spond.weatherforecastservice.client.WeatherForecastProvider;
import io.github.ao.spond.weatherforecastservice.model.Coordinates;
import io.github.ao.spond.weatherforecastservice.model.Forecast;
import io.github.ao.spond.weatherforecastservice.model.ForecastWindowException;
import io.github.ao.spond.weatherforecastservice.repository.ForecastStore;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;

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

        return store.find(coordinates)
                .filter(forecast -> forecast.expiresAt().isAfter(now))
                .orElseGet(() -> fetchAndCache(coordinates, eventTime));
    }

    private Forecast fetchAndCache(Coordinates coordinates, Instant eventTime) {
        Forecast fresh = provider.fetchForecast(coordinates, eventTime);
        store.save(coordinates, fresh);
        return fresh;
    }
}
