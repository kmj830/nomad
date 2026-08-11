package com.nomad;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class NomadApplication {

    public static void main(String[] args) {
        // Render environment variable URL normalization (JVM System Property override)
        String dbUrl = System.getenv("SPRING_DATASOURCE_URL");
        if (dbUrl == null || dbUrl.isBlank()) {
            dbUrl = System.getenv("spring.datasource.url");
        }

        if (dbUrl != null && !dbUrl.isBlank()) {
            if (dbUrl.startsWith("postgresql://")) {
                dbUrl = "jdbc:" + dbUrl;
            } else if (dbUrl.startsWith("postgres://")) {
                dbUrl = "jdbc:postgresql://" + dbUrl.substring("postgres://".length());
            }
            System.setProperty("spring.datasource.url", dbUrl);
            System.setProperty("SPRING_DATASOURCE_URL", dbUrl);
        }

        SpringApplication.run(NomadApplication.class, args);
    }
}
