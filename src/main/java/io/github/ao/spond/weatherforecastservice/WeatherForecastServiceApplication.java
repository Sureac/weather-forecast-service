package io.github.ao.spond.weatherforecastservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class WeatherForecastServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(WeatherForecastServiceApplication.class, args);
    }

}
