package com.example.cqrs.security.rest;

import com.example.cqrs.security.AuthoritiesConstants;
import com.example.cqrs.security.header.AuthorizationRequestHeaderFilter;
import com.example.cqrs.security.jwt.JsonWebTokenAuthenticationProvider;
import com.example.cqrs.security.jwt.JwtAuthenticationEntryPoint;
import com.example.cqrs.security.jwt.JwtTokenUtil;
import com.example.cqrs.security.jwt.config.JWTConfiguration;
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.boot.actuate.info.InfoEndpoint;
import org.springframework.boot.actuate.metrics.export.prometheus.PrometheusScrapeEndpoint;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.security.web.authentication.preauth.RequestHeaderAuthenticationFilter;

public abstract class MicroserviceSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {
    private static final String JANUS_USER = "janus_user";
    private static final String AUTHORIZATION = "Authorization";

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        // This method is here with the @Bean annotation so that Spring can autowire it
        return super.authenticationManagerBean();
    }

    @Bean
    public JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint() {
        return new JwtAuthenticationEntryPoint();
    }

    @Bean
    public JwtTokenUtil jwtTokenUtil(JWTConfiguration jwtConfiguration) {
        return new JwtTokenUtil(jwtConfiguration);
    }

    @Bean
    public JWTConfiguration jwtConfiguration() {
        return new JWTConfiguration();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(new JsonWebTokenAuthenticationProvider(jwtTokenUtil(jwtConfiguration())));
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.exceptionHandling().authenticationEntryPoint(jwtAuthenticationEntryPoint()).and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and().csrf().disable().httpBasic().disable()
                .formLogin().disable();
        configureAPISecurity(http);
        setupAdditionConfiguration(http);
    }

    private void configureAPISecurity(HttpSecurity httpSecurity) throws Exception {
        ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry expressionInterceptUrlRegistry = httpSecurity.authorizeRequests()
                .requestMatchers(EndpointRequest.to(InfoEndpoint.class, HealthEndpoint.class, PrometheusScrapeEndpoint.class)).permitAll()
                .requestMatchers(EndpointRequest.toAnyEndpoint()).hasRole(AuthoritiesConstants.ACTUATOR)
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll();
        configureMicroserviceAPISecurity(expressionInterceptUrlRegistry);
    }

    protected abstract void configureMicroserviceAPISecurity(ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry urlRegistry);

    protected RequestHeaderAuthenticationFilter jsonWebTokenAuthenticationFilter(AuthenticationManager authenticationManager) {
        RequestHeaderAuthenticationFilter rhaf = new RequestHeaderAuthenticationFilter();
        rhaf.setPrincipalRequestHeader(AUTHORIZATION);
        rhaf.setCredentialsRequestHeader(JANUS_USER);
        rhaf.setExceptionIfHeaderMissing(false);
        rhaf.setAuthenticationManager(authenticationManager);
        return rhaf;
    }

    protected void setupAdditionConfiguration(HttpSecurity http) throws Exception {
        http.headers().httpStrictTransportSecurity()
                .and().frameOptions().sameOrigin().cacheControl()
                .and().contentTypeOptions().and().xssProtection().xssProtectionEnabled(true)
                .and().and().addFilterBefore(new AuthorizationRequestHeaderFilter(authenticationManager()), AbstractPreAuthenticatedProcessingFilter.class)
                .addFilterBefore(jsonWebTokenAuthenticationFilter(authenticationManager()), UsernamePasswordAuthenticationFilter.class);
    }
}