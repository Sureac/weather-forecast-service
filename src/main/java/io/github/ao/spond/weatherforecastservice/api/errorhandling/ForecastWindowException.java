package io.github.ao.spond.weatherforecastservice.api.errorhandling;

public class ForecastWindowException extends RuntimeException {

    public ForecastWindowException(String message) {
        super(message);
    }
}
