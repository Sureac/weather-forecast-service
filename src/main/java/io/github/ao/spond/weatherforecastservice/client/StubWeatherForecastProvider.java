package io.github.ao.spond.weatherforecastservice.client;

import io.github.ao.spond.weatherforecastservice.model.Coordinates;
import io.github.ao.spond.weatherforecastservice.model.Forecast;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;

// AO 2026-07-27: TODO: implement met.no locationforecast adapter:
//   - mandatory User-Agent header
//   - respect Expires / If-Modified-Since headers
@Component
public class StubWeatherForecastProvider implements WeatherForecastProvider {

    @Override
    public Forecast fetchForecast(Coordinates coordinates, Instant time) {
        return new Forecast(BigDecimal.ZERO, BigDecimal.ZERO, time, Instant.now().plus(Duration.ofHours(2)));
    }
}
