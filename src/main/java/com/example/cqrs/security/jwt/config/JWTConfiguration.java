package com.example.cqrs.security.jwt.config;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.convert.DurationUnit;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@Getter
@Validated
@Configuration
@ConfigurationProperties(prefix= "security.jwt")
public class JWTConfiguration {

    @NonNull
    private String secret;

    @DurationUnit(ChronoUnit.MINUTES)
    private Duration tokenExpiryInMinutes = Duration.ofHours(10);
}
