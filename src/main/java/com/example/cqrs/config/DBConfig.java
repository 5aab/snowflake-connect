package com.example.cqrs.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;

import java.lang.reflect.InvocationTargetException;

@Slf4j
//@Configuration
//@EnableAutoConfiguration(exclude = { DataSourceAutoConfiguration.class })
public class DBConfig {

   // @Bean
    JdbcTemplate jdbcTemplate() throws IllegalAccessException, InvocationTargetException, InstantiationException {
        log.info("-----Configuring JDBCTemplate------");

        HikariConfig config = new HikariConfig();
        config.setDriverClassName("net.snowflake.client.jdbc.SnowflakeDriver");
        // config.setDataSourceProperties(properties);
        config.setJdbcUrl("jdbc:snowflake://n75069.australia-east.azure.snowflakecomputing.com/?warehouse=COMPUTE_WH&db=TESTDB&schema=PUBLIC");
        config.setUsername("ishmahajan");
        config.setPassword("BoeingB17^");
        HikariDataSource ds = new HikariDataSource(config);

        return new JdbcTemplate(ds);
    }
}
