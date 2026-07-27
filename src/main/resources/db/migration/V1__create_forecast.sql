create table forecast (
    id                      bigserial primary key,
    latitude                numeric(6, 4) not null,
    longitude               numeric(7, 4) not null,
    event_time              timestamptz   not null,
    -- scale 1 assumes met.no /compact returns 1 decimal (observed); a 2-dp value would be rounded here
    air_temperature_celsius numeric(3, 1) not null,
    wind_speed_mps          numeric(4, 1) not null,
    forecast_for            timestamptz   not null,
    expires_at              timestamptz   not null,
    fetched_at              timestamptz   not null default now(),
    constraint uq_forecast_location_time unique (latitude, longitude, event_time)
);

create index idx_forecast_event_time on forecast (event_time);
