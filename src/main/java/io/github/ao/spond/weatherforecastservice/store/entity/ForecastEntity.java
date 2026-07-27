package io.github.ao.spond.weatherforecastservice.store.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "forecast")
@Getter
@Setter
@NoArgsConstructor
public class ForecastEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private BigDecimal latitude;
    private BigDecimal longitude;
    private Instant eventTime;
    private BigDecimal airTemperatureCelsius;
    private BigDecimal windSpeedMps;
    private Instant forecastFor;
    private Instant expiresAt;
    private Instant fetchedAt;
}
