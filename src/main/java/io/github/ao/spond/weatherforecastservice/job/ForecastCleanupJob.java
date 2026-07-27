package io.github.ao.spond.weatherforecastservice.job;

import io.github.ao.spond.weatherforecastservice.store.repository.ForecastRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;

@Slf4j
@Component
class ForecastCleanupJob {

    // AO 2026-07-27: events in the past can never be requested again; +1 day of grace period before removal
    private static final Duration GRACE = Duration.ofDays(1);

    private final ForecastRepository repository;

    ForecastCleanupJob(ForecastRepository repository) {
        this.repository = repository;
    }

    @Scheduled(cron = "${forecast.cleanup.cron:0 0 3 * * *}")
    @Transactional
    public void purgePastForecasts() {
        long removed = repository.deleteByEventTimeBefore(Instant.now().minus(GRACE));
        log.info("Forecast cleanup removed {} past-event rows", removed);
    }
}
