package com.example.cqrs.error.config;

import brave.Tracer;
import com.example.cqrs.error.RestExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebResponseConfig {

    @Bean
    RestExceptionHandler restExceptionHandlerEnvelop(Tracer tracer){
        return new RestExceptionHandler(tracer);
    }
}
