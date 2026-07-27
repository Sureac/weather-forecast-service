package io.github.ao.spond.weatherforecastservice.repository;

import io.github.ao.spond.weatherforecastservice.model.Coordinates;
import io.github.ao.spond.weatherforecastservice.model.Forecast;

import java.util.Optional;

public interface ForecastStore {

    Optional<Forecast> find(Coordinates coordinates);

    void save(Coordinates coordinates, Forecast forecast);
}
