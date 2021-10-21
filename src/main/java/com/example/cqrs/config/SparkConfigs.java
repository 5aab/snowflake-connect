package com.example.cqrs.config;

import org.apache.spark.sql.SparkSession;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SparkConfigs {
    private static final String FALSE = "false";

    @Bean
    public SparkSession sparkSessionBuilder() {
        SparkSession sparkSession = SparkSession.builder()
                .master("local[25]")
                .appName("SparkService")
                .config("spark.sql.orc.impl", "native")
                .config("parquet.enable.summary-metadata", FALSE)
                .config("mapreduce.fileoutputcommitter.marksuccessfuljobs", FALSE)
                .config("spark.sql.shuffle partitions", "2")
                .config("spark.ui.enabled", FALSE)
                .config("spark.broadcast.compress", FALSE)
                .config("spark.shuffle.compress", FALSE)
                .config("spark.shuffle.spill.compress", FALSE)
                .getOrCreate();

        sparkSession.sparkContext().setLogLevel("ERROR");
        return sparkSession;
    }
}