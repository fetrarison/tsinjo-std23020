package com.examen.demo;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

import org.springframework.boot.jdbc.DataSourceBuilder;

@Configuration
class DataSourceConfig {

    @Bean
    public DataSource dataSource() {
        return DataSourceBuilder.create()
                .url("jdbc:postgresql://localhost:5432/tsinjo")
                .username("fetra")
                .password("Irving11")
                .driverClassName("org.postgresql.Driver")
                .build();
    }
}

