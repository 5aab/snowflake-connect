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
    public SecurityAuditorAware securityAuditorAware() {
        return new SecurityAuditorAware();
    }

    //please be aware that testPipe doesn't have ** at start and it works that way
    //add ** only if you expect context url to start with something else
    @Override
    protected void configureMicroserviceAPISecurity(ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry urlRegistry) {
        urlRegistry.antMatchers("/**/api/app/**").hasAnyRole("ADMIN");
        urlRegistry.antMatchers("/**/api/admin/**").hasRole("ADMIN");
        urlRegistry.antMatchers("testPipe/**").hasAnyRole("ADMIN");
        urlRegistry.antMatchers("/**/token/**").permitAll();
    }
}
