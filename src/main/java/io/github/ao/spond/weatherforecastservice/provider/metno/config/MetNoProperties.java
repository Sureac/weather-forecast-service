package io.github.ao.spond.weatherforecastservice.provider.metno.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "metno")
public record MetNoProperties(String baseUrl, String userAgent, Duration connectTimeout, Duration readTimeout) {
}
