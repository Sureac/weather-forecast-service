package io.github.ao.spond.weatherforecastservice.repository;

import io.github.ao.spond.weatherforecastservice.model.Coordinates;
import io.github.ao.spond.weatherforecastservice.model.Forecast;
import io.github.ao.spond.weatherforecastservice.repository.mapper.ForecastEntityMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

@Component
class JpaForecastStore implements ForecastStore {

    private final ForecastRepository repository;
    private final ForecastEntityMapper mapper;

    JpaForecastStore(ForecastRepository repository, ForecastEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Optional<Forecast> find(Coordinates coordinates, Instant eventTime) {
        return repository
                .findByLatitudeAndLongitudeAndEventTime(coordinates.latitude(), coordinates.longitude(), eventTime)
                .map(mapper::toForecast);
    }

    @Override
    @Transactional
    public void save(Coordinates coordinates, Instant eventTime, Forecast forecast) {
        repository.upsert(
                coordinates.latitude(),
                coordinates.longitude(),
                eventTime,
                forecast.airTemperatureCelsius(),
                forecast.windSpeedMetersPerSecond(),
                forecast.forecastFor(),
                forecast.expiresAt());
    }
}
