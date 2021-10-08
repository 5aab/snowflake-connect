package com.example.cqrs.security.config;

import com.example.cqrs.security.SecurityAuditorAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SecurityConfigs {

    @Bean
    public SecurityAuditorAware securityAuditorAware(){
        return new SecurityAuditorAware();
    }
}
