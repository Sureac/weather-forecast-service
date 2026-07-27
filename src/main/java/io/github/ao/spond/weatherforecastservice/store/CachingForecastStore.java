package io.github.ao.spond.weatherforecastservice.store;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Expiry;
import io.github.ao.spond.weatherforecastservice.model.Coordinates;
import io.github.ao.spond.weatherforecastservice.model.Forecast;
import org.jspecify.annotations.NonNull;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

@Primary
@Component
public class CachingForecastStore implements ForecastStore {

    // AO 2026-07-27: assume that at max ~10k concurrent events can happen at the same time
    private static final int MAX_ENTRIES = 10_000;
    // AO 2026-07-27: per-entry TTL keeps memory staleness under the 2h task limit
    private static final Duration TTL_BASE = Duration.ofMinutes(90);
    // AO 2026-07-27: jitter avoids a synchronized met.no refresh burst
    private static final Duration TTL_JITTER = Duration.ofMinutes(30);

    private final ForecastStore fallback;
    private final Cache<ForecastKey, Forecast> cache = Caffeine.newBuilder()
            .maximumSize(MAX_ENTRIES)
            .expireAfter(jitteredExpiry())
            .build();

    public CachingForecastStore(JpaForecastStore fallback) {
        this.fallback = fallback;
    }

    @Override
    public Optional<Forecast> find(Coordinates coordinates, Instant eventTime) {
        ForecastKey key = new ForecastKey(coordinates, eventTime);
        Forecast cached = cache.getIfPresent(key);
        if (cached != null) return Optional.of(cached);

        Optional<Forecast> fromDb = fallback.find(coordinates, eventTime);
        fromDb.ifPresent(forecast -> cache.put(key, forecast));
        return fromDb;
    }

    @Override
    public void save(Coordinates coordinates, Instant eventTime, Forecast forecast) {
        fallback.save(coordinates, eventTime, forecast);
        cache.put(new ForecastKey(coordinates, eventTime), forecast);
    }

    private static Expiry<ForecastKey, Forecast> jitteredExpiry() {
        return new Expiry<>() {
            @Override
            public long expireAfterCreate(@NonNull ForecastKey key, @NonNull Forecast value, long currentTime) {
                return nextTtlNanos();
            }

            @Override
            public long expireAfterUpdate(@NonNull ForecastKey key, @NonNull Forecast value, long currentTime, long currentDuration) {
                return nextTtlNanos();
            }

            @Override
            public long expireAfterRead(@NonNull ForecastKey key, @NonNull Forecast value, long currentTime, long currentDuration) {
                return currentDuration;
            }
        };
    }

    private static long nextTtlNanos() {
        return TTL_BASE.toNanos() + ThreadLocalRandom.current().nextLong(TTL_JITTER.toNanos());
    }

    private record ForecastKey(Coordinates coordinates, Instant eventTime) {
    }
}
