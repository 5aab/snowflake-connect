package com.example.cqrs.retry;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.Nullable;
import org.springframework.validation.annotation.Validated;

import java.util.Map;

@Setter
@Getter
@Validated
@RefreshScope
@Configuration
@ConfigurationProperties("sys.retry")
public class RetrySettings {

    private String[] simpleRetryExceptions;

    private boolean enableHealthCheck;

    @Nullable
    private Map<String, String> healthCheckUrls;

    public String getHealthCheckUrl(String destination) {
        return healthCheckUrls.get(destination);
    }

}
