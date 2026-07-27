package io.github.ao.spond.weatherforecastservice.store;

import io.github.ao.spond.weatherforecastservice.model.Coordinates;
import io.github.ao.spond.weatherforecastservice.model.Forecast;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CachingForecastStoreTest {

    private static final Coordinates OSLO = new Coordinates(new BigDecimal("59.9114"), new BigDecimal("10.7579"));
    private static final Instant EVENT_HOUR = Instant.parse("2026-07-28T18:00:00Z");
    private static final Forecast FORECAST = new Forecast(new BigDecimal("21.4"), new BigDecimal("3.2"), Instant.now(), Instant.now());

    @Mock
    private JpaForecastStore fallback;

    private CachingForecastStore sut;

    @BeforeEach
    void setUp() {
        sut = new CachingForecastStore(fallback);
    }

    @Test
    void cacheMissHitsDelegateThenSecondReadServesFromMemory() {
        when(fallback.find(OSLO, EVENT_HOUR)).thenReturn(Optional.of(FORECAST));

        assertThat(sut.find(OSLO, EVENT_HOUR)).contains(FORECAST);
        assertThat(sut.find(OSLO, EVENT_HOUR)).contains(FORECAST);

        verify(fallback, times(1)).find(OSLO, EVENT_HOUR);
    }

    @Test
    void saveWritesThroughAndPopulatesCache() {
        sut.save(OSLO, EVENT_HOUR, FORECAST);

        assertThat(sut.find(OSLO, EVENT_HOUR)).contains(FORECAST);

        verify(fallback).save(OSLO, EVENT_HOUR, FORECAST);
        verify(fallback, never()).find(any(), any());
    }
}
