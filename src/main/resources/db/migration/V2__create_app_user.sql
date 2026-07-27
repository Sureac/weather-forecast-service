create table app_user (
    id            bigserial primary key,
    username      varchar(100) not null unique,
    password_hash varchar(100) not null,
    enabled       boolean      not null default true
);

-- demo credentials: spond / spond-secret (BCrypt).
insert into app_user (username, password_hash)
values ('spond', '$2a$10$1TAlH.fjjzNK8RZ67AY95eALXI.SxwMb4Fscok2Edyr3v1OH1spby');
