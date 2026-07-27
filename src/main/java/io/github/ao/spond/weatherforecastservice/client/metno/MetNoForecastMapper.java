package io.github.ao.spond.weatherforecastservice.client.metno;

import io.github.ao.spond.weatherforecastservice.client.metno.dto.MetNoForecastResponse.TimeseriesEntry;
import io.github.ao.spond.weatherforecastservice.model.Forecast;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.Instant;

@Mapper(componentModel = "spring")
public interface MetNoForecastMapper {

    @Mapping(target = "airTemperatureCelsius", source = "entry.data.instant.details.airTemperature")
    @Mapping(target = "windSpeedMetersPerSecond", source = "entry.data.instant.details.windSpeed")
    @Mapping(target = "forecastFor", source = "entry.time")
    Forecast toForecast(TimeseriesEntry entry, Instant expiresAt);
}
