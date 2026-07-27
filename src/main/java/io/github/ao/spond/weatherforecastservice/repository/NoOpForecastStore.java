package io.github.ao.spond.weatherforecastservice.repository;

import io.github.ao.spond.weatherforecastservice.model.Coordinates;
import io.github.ao.spond.weatherforecastservice.model.Forecast;
import org.springframework.stereotype.Component;

import java.util.Optional;

// AO 2026-07-27: TODO: implement the persistence adapter
@Component
public class NoOpForecastStore implements ForecastStore {

    @Override
    public Optional<Forecast> find(Coordinates coordinates) {
        return Optional.empty();
    }

    @Override
    public void save(Coordinates coordinates, Forecast forecast) {
    }
}
