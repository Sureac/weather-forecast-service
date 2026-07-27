package io.github.ao.spond.weatherforecastservice.repository;

import io.github.ao.spond.weatherforecastservice.model.Coordinates;
import io.github.ao.spond.weatherforecastservice.model.Forecast;

import java.time.Instant;
import java.util.Optional;

public interface ForecastStore {

    Optional<Forecast> find(Coordinates coordinates, Instant eventTime);

    void save(Coordinates coordinates, Instant eventTime, Forecast forecast);
}
