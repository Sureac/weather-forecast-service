package io.github.ao.spond.weatherforecastservice.provider;

import io.github.ao.spond.weatherforecastservice.model.Coordinates;
import io.github.ao.spond.weatherforecastservice.model.Forecast;

import java.time.Instant;

public interface WeatherForecastProvider {

    Forecast fetchForecast(Coordinates coordinates, Instant time);
}
