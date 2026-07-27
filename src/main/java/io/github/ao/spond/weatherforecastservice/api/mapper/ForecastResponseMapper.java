package io.github.ao.spond.weatherforecastservice.api.mapper;

import io.github.ao.spond.weatherforecastservice.api.dto.ForecastResponse;
import io.github.ao.spond.weatherforecastservice.model.Forecast;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ForecastResponseMapper {

    ForecastResponse toResponse(Forecast forecast);
}
