package io.github.ao.spond.weatherforecastservice.forecast;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.Instant;

@RestController
@RequestMapping("/api/v1/forecast")
@Tag(name = "Forecast", description = "Weather forecast for a Spond event location and time")
public class ForecastController {

    @Operation(
            summary = "Get the forecast for an event",
            description = "Returns air temperature (degrees Celsius) and wind speed (m/s) for the given location at the event start time. The event must start within the next 7 days.")
    @GetMapping
    public ForecastResponse getForecast(
            @Parameter(description = "Event latitude in decimal degrees", example = "59.9139")
            @RequestParam @DecimalMin("-90") @DecimalMax("90") BigDecimal lat,
            @Parameter(description = "Event longitude in decimal degrees", example = "10.7522")
            @RequestParam @DecimalMin("-180") @DecimalMax("180") BigDecimal lon,
            @Parameter(description = "Event start time, ISO-8601 instant in UTC", example = "2026-07-27T18:00:00Z")
            @RequestParam Instant time) {

        // AO 2026-07-26: TODO: call forecast service
        // AO 2026-07-26: TODO: make sure that only 4 dp is used in domain for coorrdinates
        // AO 2026-07-26: TODO: make sure that time of event is not more than 7d in the future (global exception handler?)
        return new ForecastResponse(BigDecimal.ZERO, BigDecimal.ZERO, time, time);
    }
}
