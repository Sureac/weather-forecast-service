package io.github.ao.spond.weatherforecastservice;

import org.springframework.boot.SpringApplication;

public class TestWeatherForecastServiceApplication {

    public static void main(String[] args) {
        SpringApplication.from(WeatherForecastServiceApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
