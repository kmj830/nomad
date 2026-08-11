package com.nomad.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.util.HashMap;
import java.util.Map;

public class RenderDatabaseEnvironmentPostProcessor implements EnvironmentPostProcessor {

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        String dbUrl = environment.getProperty("SPRING_DATASOURCE_URL");
        if (dbUrl == null || dbUrl.isBlank()) {
            dbUrl = environment.getProperty("spring.datasource.url");
        }

        if (dbUrl != null && !dbUrl.isBlank()) {
            String fixedUrl = dbUrl;
            if (dbUrl.startsWith("postgresql://")) {
                fixedUrl = "jdbc:" + dbUrl;
            } else if (dbUrl.startsWith("postgres://")) {
                fixedUrl = "jdbc:postgresql://" + dbUrl.substring("postgres://".length());
            }

            Map<String, Object> map = new HashMap<>();
            map.put("spring.datasource.url", fixedUrl);
            map.put("SPRING_DATASOURCE_URL", fixedUrl);

            if (environment.getPropertySources().contains("renderDbUrlFix")) {
                environment.getPropertySources().replace("renderDbUrlFix", new MapPropertySource("renderDbUrlFix", map));
            } else {
                environment.getPropertySources().addFirst(new MapPropertySource("renderDbUrlFix", map));
            }
        }
    }
}
