package io.github.ao.spond.weatherforecastservice.client.metno;

import io.github.ao.spond.weatherforecastservice.client.WeatherForecastProvider;
import io.github.ao.spond.weatherforecastservice.client.metno.dto.MetNoForecastResponse;
import io.github.ao.spond.weatherforecastservice.client.metno.dto.MetNoForecastResponse.TimeseriesEntry;
import io.github.ao.spond.weatherforecastservice.model.Coordinates;
import io.github.ao.spond.weatherforecastservice.model.Forecast;
import io.github.ao.spond.weatherforecastservice.model.ForecastUnavailableException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;

import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;

@Component
public class MetNoWeatherForecastProvider implements WeatherForecastProvider {

    // AO 2026-07-27: fallback in case of missing Expires header; the task requires data no older than 2h
    private static final Duration FALLBACK_TTL = Duration.ofHours(2);

    private final MetNoClient client;
    private final MetNoForecastMapper mapper;

    public MetNoWeatherForecastProvider(MetNoClient client, MetNoForecastMapper mapper) {
        this.client = client;
        this.mapper = mapper;
    }

    @Override
    public Forecast fetchForecast(Coordinates coordinates, Instant time) {
        ResponseEntity<MetNoForecastResponse> response = getForecast(coordinates);

        List<TimeseriesEntry> series = timeSeriesOf(response.getBody());
        TimeseriesEntry nearest = nearestTo(time, series);

        return mapper.toForecast(nearest, expiresAt(response.getHeaders()));
    }

    private ResponseEntity<MetNoForecastResponse> getForecast(Coordinates coordinates) {
        try {
            return client.getCompactForecast(coordinates.latitude(), coordinates.longitude());
        } catch (RestClientException ex) {
            throw new ForecastUnavailableException("met.no request failed", ex);
        }
    }

    private static List<TimeseriesEntry> timeSeriesOf(MetNoForecastResponse body) {
        if (body == null
                || body.properties() == null
                || body.properties().timeseries() == null
                || body.properties().timeseries().isEmpty()
        ) {
            throw new ForecastUnavailableException("met.no returned no forecast data");
        }

        return body.properties().timeseries();
    }

    private static TimeseriesEntry nearestTo(Instant time, List<TimeseriesEntry> series) {
        return series.stream()
                .min(Comparator.comparingLong(entry -> Math.abs(Duration.between(time, entry.time()).toSeconds())))
                .orElseThrow();
    }

    private static Instant expiresAt(HttpHeaders headers) {
        long expires = headers.getExpires();
        return expires >= 0 ? Instant.ofEpochMilli(expires) : Instant.now().plus(FALLBACK_TTL);
    }
}
