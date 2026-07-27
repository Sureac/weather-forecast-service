package io.github.ao.spond.weatherforecastservice.model;

public class ForecastUnavailableException extends RuntimeException {

    public ForecastUnavailableException(String message) {
        super(message);
    }

    public ForecastUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
