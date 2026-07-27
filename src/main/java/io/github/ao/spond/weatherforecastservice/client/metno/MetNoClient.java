package io.github.ao.spond.weatherforecastservice.client.metno;

import io.github.ao.spond.weatherforecastservice.client.metno.dto.MetNoForecastResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;

import java.math.BigDecimal;

public interface MetNoClient {

    @GetExchange("/weatherapi/locationforecast/2.0/compact")
    ResponseEntity<MetNoForecastResponse> getCompactForecast(@RequestParam BigDecimal lat, @RequestParam BigDecimal lon);
}
