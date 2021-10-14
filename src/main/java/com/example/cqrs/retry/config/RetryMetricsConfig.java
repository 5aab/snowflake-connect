package com.example.cqrs.retry.config;

import com.example.cqrs.retry.RetrySettings;
import com.google.common.collect.Lists;
import io.micrometer.core.instrument.ImmutableTag;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Configuration
public class RetryMetricsConfig {

    private static final String DESTINATION = "destination";

    @Bean
    public Map<String, AtomicInteger> gauges(RetrySettings retrysettings) {
        Map<String, AtomicInteger> gaugesMap = new HashMap();
        Map<String, String> healthCheckUrlMap = retrysettings.getHealthCheckUrls();
        if (healthCheckUrlMap != null) {
            for (Map.Entry<String, String> entry : healthCheckUrlMap.entrySet()) {
                List<Tag> tags = Lists.newArrayList(new ImmutableTag(DESTINATION, entry.getKey()));
                AtomicInteger atomicInteger = Metrics.gauge("frozen.binding", tags, new AtomicInteger(0));
                gaugesMap.put(entry.getKey(), atomicInteger);
            }
        }
        return gaugesMap;
    }
}
