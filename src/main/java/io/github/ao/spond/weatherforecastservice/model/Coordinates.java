package io.github.ao.spond.weatherforecastservice.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

public record Coordinates(BigDecimal latitude, BigDecimal longitude) {

    // AO 2026-07-27: met.no ToS: coordinates with >4 decimals return 403
    private static final int MET_NO_SCALE = 4;

    public Coordinates {
        latitude = latitude.setScale(MET_NO_SCALE, RoundingMode.HALF_UP);
        longitude = longitude.setScale(MET_NO_SCALE, RoundingMode.HALF_UP);
    }
}
