package io.github.ao.spond.weatherforecastservice.repository;

import io.github.ao.spond.weatherforecastservice.TestcontainersConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;

import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.BIG_DECIMAL;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestcontainersConfiguration.class)
class ForecastRepositoryTest {

    private static final BigDecimal LAT = new BigDecimal("59.9114");
    private static final BigDecimal LON = new BigDecimal("10.7579");
    private static final Instant EVENT_HOUR = Instant.parse("2026-07-28T18:00:00Z");

    @Autowired
    private ForecastRepository sut;

    @Test
    void upsertInsertsThenUpdatesSameKeyWithoutDuplicating() {
        sut.upsert(
                LAT,
                LON,
                EVENT_HOUR,
                new BigDecimal("21.40"),
                new BigDecimal("3.20"),
                EVENT_HOUR,
                Instant.parse("2026-07-28T20:00:00Z")
        );
        sut.upsert(
                LAT,
                LON,
                EVENT_HOUR,
                new BigDecimal("18.90"),
                new BigDecimal("5.10"),
                EVENT_HOUR,
                Instant.parse("2026-07-28T21:00:00Z")
        );

        assertThat(sut.count()).isEqualTo(1);
        assertThat(sut.findByLatitudeAndLongitudeAndEventTime(LAT, LON, EVENT_HOUR))
                .get()
                .extracting(ForecastEntity::getAirTemperatureCelsius, as(BIG_DECIMAL))
                .isEqualByComparingTo("18.90");
    }

    @Test
    void deletesOnlyPastEvents() {
        Instant now = Instant.now();
        sut.save(entity(new BigDecimal("1.0000"), now.minus(Duration.ofDays(2))));
        sut.save(entity(new BigDecimal("2.0000"), now.plus(Duration.ofDays(1))));

        long removed = sut.deleteByEventTimeBefore(now.minus(Duration.ofDays(1)));

        assertThat(removed).isEqualTo(1);
        assertThat(sut.count()).isEqualTo(1);
    }

    private static ForecastEntity entity(BigDecimal coordinate, Instant eventTime) {
        ForecastEntity entity = new ForecastEntity();
        entity.setLatitude(coordinate);
        entity.setLongitude(coordinate);
        entity.setEventTime(eventTime);
        entity.setAirTemperatureCelsius(new BigDecimal("21.40"));
        entity.setWindSpeedMps(new BigDecimal("3.20"));
        entity.setForecastFor(eventTime);
        entity.setExpiresAt(Instant.parse("2026-07-28T20:00:00Z"));
        entity.setFetchedAt(Instant.now());
        return entity;
    }
}
