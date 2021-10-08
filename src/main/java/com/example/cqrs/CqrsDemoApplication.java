package com.example.cqrs;

import io.micrometer.core.instrument.FunctionTimer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.function.Function;
import java.util.function.Supplier;

@SpringBootApplication
public class CqrsDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(CqrsDemoApplication.class, args);
    }

    @Bean
    public Supplier<String> value() {
        return () -> "Hello Value";
    }

    // try in browser http://localhost:9090/uppercase/abc
    @Bean
    public Function<String, String> uppercase() {
        return value -> value.toUpperCase();
    }

}
