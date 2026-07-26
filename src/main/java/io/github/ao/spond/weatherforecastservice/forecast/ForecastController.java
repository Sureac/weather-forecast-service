package io.github.ao.spond.weatherforecastservice.forecast;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.Instant;

@RestController
@RequestMapping("/api/v1/forecast")
@Validated
public class ForecastController {

    @GetMapping
    public ForecastResponse getForecast(
            @RequestParam @DecimalMin("-90") @DecimalMax("90") BigDecimal lat,
            @RequestParam @DecimalMin("-180") @DecimalMax("180") BigDecimal lon,
            @RequestParam Instant time) {

        // AO 2026-07-26: TODO: call forecast service
        // AO 2026-07-26: TODO: make sure that only 4 dp is used in domain for coorrdinates
        // AO 2026-07-26: TODO: make sure that time of event is not more than 7d in the future (global exception handler?)
        return new ForecastResponse(BigDecimal.ZERO, BigDecimal.ZERO, time, time);
    }
}
