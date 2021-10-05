package com.example.cqrs.config;

import an.awesome.pipelinr.Command;
import com.example.cqrs.custom.logging.Loggable;
import com.example.cqrs.custom.retry.ReTry;
import com.example.cqrs.custom.telemetery.MicrometerCounting;
import com.example.cqrs.custom.telemetery.MicrometerTiming;
import com.example.cqrs.custom.tx.Transactional;
import com.example.cqrs.custom.validations.Validations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import javax.validation.Validation;

@Configuration
public class BusConfigs {

    @Bean
    public Command.Middleware loggable(){
        return new Loggable();
    }

    @Bean
    public Command.Middleware micrometerCounting(){
        return new MicrometerCounting();
    }

    @Bean
    public Command.Middleware micrometerTiming(){
        return new MicrometerTiming();
    }

    @Bean
    public Command.Middleware validations(){
        return new Validations(Validation.buildDefaultValidatorFactory().getValidator());
    }

    @Bean
    public Command.Middleware transactional(PlatformTransactionManager manager){
        return new Transactional(manager);
    }

    @Bean
    public Command.Middleware retry(){
        return new ReTry();
    }


}
