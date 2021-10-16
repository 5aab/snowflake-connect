package com.example.cqrs.security.config;

import com.example.cqrs.security.audit.SecurityAuditorAware;
import com.example.cqrs.security.rest.MicroserviceSecurityConfigurerAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SecurityConfigs extends MicroserviceSecurityConfigurerAdapter {

    @Bean
    public SecurityAuditorAware securityAuditorAware(){
        return new SecurityAuditorAware();
    }

    @Override
    protected void configureMicroserviceAPISecurity(ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry urlRegistry) {
        urlRegistry.antMatchers("/**/api/app/**").hasAnyRole();
        urlRegistry.antMatchers("/**/api/admin/**").hasRole("ADMIN");
        urlRegistry.antMatchers("/**/testPipe/**").hasRole("ADMIN");
        urlRegistry.antMatchers("/**/token/**").permitAll();
    }
}
