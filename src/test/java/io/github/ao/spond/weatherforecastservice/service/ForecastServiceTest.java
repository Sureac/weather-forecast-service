package io.github.ao.spond.weatherforecastservice.service;

import io.github.ao.spond.weatherforecastservice.provider.WeatherForecastProvider;
import io.github.ao.spond.weatherforecastservice.model.Coordinates;
import io.github.ao.spond.weatherforecastservice.model.Forecast;
import io.github.ao.spond.weatherforecastservice.model.ForecastWindowException;
import io.github.ao.spond.weatherforecastservice.repository.ForecastStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ForecastServiceTest {

    private static final Coordinates OSLO = new Coordinates(new BigDecimal("59.9114"), new BigDecimal("10.7579"));

    @Mock
    private WeatherForecastProvider provider;

    @Mock
    private ForecastStore store;

    @InjectMocks
    private ForecastService sut;

    private Instant eventTime;
    private Instant eventHour;

    @BeforeEach
    void setUp() {
        eventTime = Instant.now().plus(Duration.ofDays(1));
        eventHour = eventTime.truncatedTo(ChronoUnit.HOURS);
    }

    @Test
    void fetchesFromProviderAndCachesOnCacheMiss() {
        Forecast fresh = forecastExpiringIn(Duration.ofHours(2));
        when(store.find(OSLO, eventHour)).thenReturn(Optional.empty());
        when(provider.fetchForecast(OSLO, eventTime)).thenReturn(fresh);

        Forecast result = sut.getForecast(OSLO, eventTime);

        assertThat(result).isEqualTo(fresh);
        verify(store).save(OSLO, eventHour, fresh);
    }

    @Test
    void returnsCachedForecastWhenStillFresh() {
        Forecast cached = forecastExpiringIn(Duration.ofHours(1));
        when(store.find(OSLO, eventHour)).thenReturn(Optional.of(cached));

        Forecast result = sut.getForecast(OSLO, eventTime);

        assertThat(result).isEqualTo(cached);
        verifyNoInteractions(provider);
        verify(store, never()).save(any(), any(), any());
    }

    @Test
    void refetchesWhenCachedForecastExpired() {
        Forecast expired = forecastExpiringIn(Duration.ofHours(-1));
        Forecast fresh = forecastExpiringIn(Duration.ofHours(2));
        when(store.find(OSLO, eventHour)).thenReturn(Optional.of(expired));
        when(provider.fetchForecast(OSLO, eventTime)).thenReturn(fresh);

        Forecast result = sut.getForecast(OSLO, eventTime);

        assertThat(result).isEqualTo(fresh);
        verify(store).save(OSLO, eventHour, fresh);
    }

    @Test
    void rejectsEventBeyondForecastWindow() {
        Instant tooFar = Instant.now().plus(Duration.ofDays(8));

        assertThatThrownBy(() -> sut.getForecast(OSLO, tooFar))
                .isInstanceOf(ForecastWindowException.class);

        verifyNoInteractions(provider, store);
    }

    private static Forecast forecastExpiringIn(Duration ttl) {
        return new Forecast(BigDecimal.ZERO, BigDecimal.ZERO, Instant.now(), Instant.now().plus(ttl));
    }
}
