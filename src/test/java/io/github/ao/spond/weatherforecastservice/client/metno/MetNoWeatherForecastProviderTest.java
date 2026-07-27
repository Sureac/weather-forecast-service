package io.github.ao.spond.weatherforecastservice.client.metno;

import io.github.ao.spond.weatherforecastservice.client.metno.dto.MetNoForecastResponse;
import io.github.ao.spond.weatherforecastservice.client.metno.dto.MetNoForecastResponse.Properties;
import io.github.ao.spond.weatherforecastservice.client.metno.dto.MetNoForecastResponse.TimeseriesEntry;
import io.github.ao.spond.weatherforecastservice.model.Coordinates;
import io.github.ao.spond.weatherforecastservice.model.Forecast;
import io.github.ao.spond.weatherforecastservice.model.ForecastUnavailableException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.ResourceAccessException;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MetNoWeatherForecastProviderTest {

    private static final Coordinates OSLO = new Coordinates(new BigDecimal("59.9114"), new BigDecimal("10.7579"));
    private static final Forecast MAPPED = new Forecast(BigDecimal.ONE, BigDecimal.TEN, Instant.now(), Instant.now());

    private static final TimeseriesEntry ENTRY_18 = new TimeseriesEntry(Instant.parse("2026-07-27T18:00:00Z"), null);
    private static final TimeseriesEntry ENTRY_19 = new TimeseriesEntry(Instant.parse("2026-07-27T19:00:00Z"), null);

    @Mock
    private MetNoClient client;

    @Mock
    private MetNoForecastMapper mapper;

    @InjectMocks
    private MetNoWeatherForecastProvider provider;

    @Test
    void selectsNearestEntryAndUsesExpiresHeader() {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.EXPIRES, "Mon, 27 Jul 2026 20:00:00 GMT");
        when(client.getCompactForecast(any(), any())).thenReturn(responseWith(headers, ENTRY_18, ENTRY_19));
        when(mapper.toForecast(any(), any())).thenReturn(MAPPED);

        Forecast result = provider.fetchForecast(OSLO, Instant.parse("2026-07-27T18:10:00Z"));

        assertThat(result).isEqualTo(MAPPED);
        verify(mapper).toForecast(eq(ENTRY_18), eq(Instant.parse("2026-07-27T20:00:00Z")));
    }

    @Test
    void fallsBackToTwoHourTtlWhenNoExpiresHeader() {
        when(client.getCompactForecast(any(), any())).thenReturn(responseWith(new HttpHeaders(), ENTRY_18));
        when(mapper.toForecast(any(), any())).thenReturn(MAPPED);

        Instant before = Instant.now().plus(Duration.ofHours(2));
        provider.fetchForecast(OSLO, Instant.parse("2026-07-27T18:00:00Z"));
        Instant after = Instant.now().plus(Duration.ofHours(2));

        ArgumentCaptor<Instant> expiresAt = ArgumentCaptor.forClass(Instant.class);
        verify(mapper).toForecast(eq(ENTRY_18), expiresAt.capture());
        assertThat(expiresAt.getValue()).isBetween(before, after);
    }

    @Test
    void throwsWhenTimeSeriesEmpty() {
        when(client.getCompactForecast(any(), any())).thenReturn(responseWith(new HttpHeaders()));

        assertThatThrownBy(() -> provider.fetchForecast(OSLO, Instant.now()))
                .isInstanceOf(ForecastUnavailableException.class);
        verifyNoInteractions(mapper);
    }

    @Test
    void throwsWhenBodyNull() {
        when(client.getCompactForecast(any(), any()))
                .thenReturn(new ResponseEntity<>((MetNoForecastResponse) null, HttpStatus.OK));

        assertThatThrownBy(() -> provider.fetchForecast(OSLO, Instant.now()))
                .isInstanceOf(ForecastUnavailableException.class);
        verifyNoInteractions(mapper);
    }

    @Test
    void translatesTransportFailureToUnavailable() {
        when(client.getCompactForecast(any(), any())).thenThrow(new ResourceAccessException("timeout"));

        assertThatThrownBy(() -> provider.fetchForecast(OSLO, Instant.now()))
                .isInstanceOf(ForecastUnavailableException.class);
        verifyNoInteractions(mapper);
    }

    private static ResponseEntity<MetNoForecastResponse> responseWith(HttpHeaders headers, TimeseriesEntry... entries) {
        MetNoForecastResponse body = new MetNoForecastResponse(new Properties(List.of(entries)));
        return new ResponseEntity<>(body, headers, HttpStatus.OK);
    }
}
