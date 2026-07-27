package io.github.ao.spond.weatherforecastservice.api;

import io.github.ao.spond.weatherforecastservice.api.dto.ForecastResponse;
import io.github.ao.spond.weatherforecastservice.model.Coordinates;
import io.github.ao.spond.weatherforecastservice.model.Forecast;
import io.github.ao.spond.weatherforecastservice.service.ForecastService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.FutureOrPresent;
import org.springframework.http.ProblemDetail;
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

    private final ForecastService forecastService;

    public ForecastController(ForecastService forecastService) {
        this.forecastService = forecastService;
    }

    @Operation(
            summary = "Get the forecast for an event",
            description = "Returns air temperature (degrees Celsius) and wind speed (m/s) for the given location at the " +
                    "event start time. The event must start within the next 7 days.")
    @ApiResponses({
            @ApiResponse(responseCode = "400",
                    description = "Invalid input: coordinate out of range, malformed value, missing parameter, an event " +
                            "time that is not a UTC instant, an event in the past, or an event more than 7 days in the future.",
                    content = @Content(
                            mediaType = "application/problem+json",
                            schema = @Schema(implementation = ProblemDetail.class)
                    )
            ),
            @ApiResponse(responseCode = "401",
                    description = "Missing or invalid credentials",
                    content = @Content(
                            mediaType = "application/problem+json",
                            schema = @Schema(implementation = ProblemDetail.class)
                    )
            )
    })
    @GetMapping
    public ForecastResponse getForecast(
            @Parameter(description = "Event latitude in decimal degrees", example = "59.9139")
            @RequestParam @DecimalMin("-90") @DecimalMax("90") BigDecimal lat,
            @Parameter(description = "Event longitude in decimal degrees", example = "10.7522")
            @RequestParam @DecimalMin("-180") @DecimalMax("180") BigDecimal lon,
            @Parameter(description = "Event start time, ISO-8601 instant in UTC", example = "2026-07-27T18:00:00Z")
            @RequestParam @FutureOrPresent Instant time
    ) {
        Forecast forecast = forecastService.getForecast(new Coordinates(lat, lon), time);
        return new ForecastResponse(
                forecast.airTemperatureCelsius(),
                forecast.windSpeedMetersPerSecond(),
                forecast.forecastFor(),
                forecast.expiresAt());
    }
}
