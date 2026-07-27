package io.github.ao.spond.weatherforecastservice.store.repository;

import io.github.ao.spond.weatherforecastservice.store.entity.AppUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AppUserRepository extends JpaRepository<AppUserEntity, Long> {

    Optional<AppUserEntity> findByUsername(String username);
}
