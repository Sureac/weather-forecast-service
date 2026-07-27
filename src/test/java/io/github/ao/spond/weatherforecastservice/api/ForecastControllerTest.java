package io.github.ao.spond.weatherforecastservice.api;

import io.github.ao.spond.weatherforecastservice.model.Coordinates;
import io.github.ao.spond.weatherforecastservice.model.Forecast;
import io.github.ao.spond.weatherforecastservice.service.ForecastService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ForecastController.class)
@AutoConfigureMockMvc(addFilters = false) // skip security filter chain: these tests target validation, not auth
class ForecastControllerTest {

    private static final String PATH = "/api/v1/forecast";
    private static final String TIME = Instant.now().plus(2, ChronoUnit.DAYS).toString();

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ForecastService forecastService;

    private void stubForecast() {
        when(forecastService.getForecast(any(Coordinates.class), any(Instant.class)))
                .thenReturn(new Forecast(BigDecimal.ZERO, BigDecimal.ZERO, Instant.now(), Instant.now()));
    }

    @Test
    void acceptsValidRequest() throws Exception {
        stubForecast();
        mockMvc.perform(get(PATH)
                        .param("lat", "59.9139")
                        .param("lon", "10.7522")
                        .param("time", TIME))
                .andExpect(status().isOk());
    }

    @Test
    void acceptsMinBoundaryCoordinates() throws Exception {
        stubForecast();
        mockMvc.perform(get(PATH)
                        .param("lat", "-90")
                        .param("lon", "-180")
                        .param("time", TIME))
                .andExpect(status().isOk());
    }

    @Test
    void acceptsMaxBoundaryCoordinates() throws Exception {
        stubForecast();
        mockMvc.perform(get(PATH)
                        .param("lat", "90")
                        .param("lon", "180")
                        .param("time", TIME))
                .andExpect(status().isOk());
    }

    @Test
    void rejectsLatitudeBelowMin() throws Exception {
        mockMvc.perform(get(PATH)
                        .param("lat", "-90.1")
                        .param("lon", "10.7522")
                        .param("time", TIME))
                .andExpect(status().isBadRequest());
    }

    @Test
    void rejectsLatitudeAboveMax() throws Exception {
        mockMvc.perform(get(PATH)
                        .param("lat", "90.1")
                        .param("lon", "10.7522")
                        .param("time", TIME))
                .andExpect(status().isBadRequest());
    }

    @Test
    void rejectsLongitudeBelowMin() throws Exception {
        mockMvc.perform(get(PATH)
                        .param("lat", "59.9139")
                        .param("lon", "-180.1")
                        .param("time", TIME))
                .andExpect(status().isBadRequest());
    }

    @Test
    void rejectsLongitudeAboveMax() throws Exception {
        mockMvc.perform(get(PATH)
                        .param("lat", "59.9139")
                        .param("lon", "180.1")
                        .param("time", TIME))
                .andExpect(status().isBadRequest());
    }

    @Test
    void rejectsNonNumericCoordinate() throws Exception {
        mockMvc.perform(get(PATH)
                        .param("lat", "north")
                        .param("lon", "10.7522")
                        .param("time", TIME))
                .andExpect(status().isBadRequest());
    }

    @Test
    void rejectsUnparseableTime() throws Exception {
        mockMvc.perform(get(PATH)
                        .param("lat", "59.9139")
                        .param("lon", "10.7522")
                        .param("time", "not-a-time"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void rejectsTimeWithoutOffset() throws Exception {
        mockMvc.perform(get(PATH)
                        .param("lat", "59.9139")
                        .param("lon", "10.7522")
                        .param("time", "2026-07-27T18:00:00"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void rejectsPastEvent() throws Exception {
        String past = Instant.now().minus(1, ChronoUnit.DAYS).toString();
        mockMvc.perform(get(PATH)
                        .param("lat", "59.9139")
                        .param("lon", "10.7522")
                        .param("time", past))
                .andExpect(status().isBadRequest());
    }

    @Test
    void rejectsMissingLatitude() throws Exception {
        mockMvc.perform(get(PATH)
                        .param("lon", "10.7522")
                        .param("time", TIME))
                .andExpect(status().isBadRequest());
    }

    @Test
    void rejectsMissingLongitude() throws Exception {
        mockMvc.perform(get(PATH)
                        .param("lat", "59.9139")
                        .param("time", TIME))
                .andExpect(status().isBadRequest());
    }

    @Test
    void rejectsMissingTime() throws Exception {
        mockMvc.perform(get(PATH)
                        .param("lat", "59.9139")
                        .param("lon", "10.7522"))
                .andExpect(status().isBadRequest());
    }
}
