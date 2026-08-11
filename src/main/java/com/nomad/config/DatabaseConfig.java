package com.nomad.config;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("prod")
public class DatabaseConfig {

    @Bean
    @Primary
    public DataSourceProperties dataSourceProperties(DataSourceProperties properties) {
        String url = properties.getUrl();
        if (url != null) {
            if (url.startsWith("postgresql://")) {
                properties.setUrl("jdbc:" + url);
            } else if (url.startsWith("postgres://")) {
                properties.setUrl("jdbc:postgresql://" + url.substring("postgres://".length()));
            }
        }
        return properties;
    }
}
