package io.github.ao.spond.weatherforecastservice.api.errorhandling;

import io.github.ao.spond.weatherforecastservice.model.ForecastUnavailableException;
import io.github.ao.spond.weatherforecastservice.model.ForecastWindowException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@RestControllerAdvice
class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ForecastWindowException.class)
    ProblemDetail handleForecastWindow(ForecastWindowException ex) {
        log.debug("Rejected event outside forecast window", ex);
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Event must start within the next 7 days");
    }

    @ExceptionHandler(ForecastUnavailableException.class)
    ProblemDetail handleForecastUnavailable(ForecastUnavailableException ex) {
        log.warn("Weather provider unavailable", ex);
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_GATEWAY, "Weather forecast is temporarily unavailable");
    }

    @ExceptionHandler(Exception.class)
    ProblemDetail handleUnexpected(Exception ex) {
        log.error("Unhandled exception", ex);
        return ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error");
    }
}
