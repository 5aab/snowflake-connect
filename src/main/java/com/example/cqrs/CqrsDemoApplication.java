package com.example.cqrs;

import com.example.cqrs.app.domain.vehicle.event.CreateVehicleEvent;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.function.Consumer;


@SpringBootApplication
public class CqrsDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(CqrsDemoApplication.class, args);
    }

   /* @Bean
    public Supplier<String> value() {
        return () -> "Hello Value";
    }

    // try in browser http://localhost:9090/uppercase/abc
    @Bean
    public Function<String, String> uppercase() {
        return value -> value.toUpperCase();
    }*/

    @Bean
    public Consumer<CreateVehicleEvent> buyer() {
        return System.out::println;
    }

    @Bean
    public Consumer<CreateVehicleEvent> investor() {
        return System.out::println;
    }

}
