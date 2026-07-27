package io.github.ao.spond.weatherforecastservice.repository.mapper;

import io.github.ao.spond.weatherforecastservice.model.Forecast;
import io.github.ao.spond.weatherforecastservice.repository.ForecastEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ForecastEntityMapper {

    @Mapping(target = "windSpeedMetersPerSecond", source = "windSpeedMps")
    Forecast toForecast(ForecastEntity entity);
}
