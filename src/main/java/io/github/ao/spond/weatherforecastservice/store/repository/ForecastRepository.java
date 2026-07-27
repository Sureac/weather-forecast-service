package io.github.ao.spond.weatherforecastservice.store.repository;

import io.github.ao.spond.weatherforecastservice.store.entity.ForecastEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;

public interface ForecastRepository extends JpaRepository<ForecastEntity, Long> {

    Optional<ForecastEntity> findByLatitudeAndLongitudeAndEventTime(BigDecimal latitude, BigDecimal longitude, Instant eventTime);

    @Modifying
    @Query(value = """
            insert into forecast (
                latitude,
                longitude,
                event_time,
                air_temperature_celsius,
                wind_speed_mps,
                forecast_for,
                expires_at,
                fetched_at
            )
            values (
                :latitude,
                :longitude,
                :eventTime,
                :airTemperature,
                :windSpeed,
                :forecastFor,
                :expiresAt,
                now()
            )
            on conflict (latitude, longitude, event_time) do update set
                air_temperature_celsius = excluded.air_temperature_celsius,
                wind_speed_mps          = excluded.wind_speed_mps,
                forecast_for            = excluded.forecast_for,
                expires_at              = excluded.expires_at,
                fetched_at              = now()
            """,
            nativeQuery = true
    )
    void upsert(
            @Param("latitude") BigDecimal latitude,
            @Param("longitude") BigDecimal longitude,
            @Param("eventTime") Instant eventTime,
            @Param("airTemperature") BigDecimal airTemperature,
            @Param("windSpeed") BigDecimal windSpeed,
            @Param("forecastFor") Instant forecastFor,
            @Param("expiresAt") Instant expiresAt
    );

    long deleteByEventTimeBefore(Instant cutoff);
}
