package com.nomad;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class NomadApplication {

    public static void main(String[] args) {
        String dbUrl = System.getenv("SPRING_DATASOURCE_URL");
        if (dbUrl == null || dbUrl.isBlank()) {
            dbUrl = System.getenv("DATABASE_URL");
        }
        if (dbUrl == null || dbUrl.isBlank()) {
            dbUrl = System.getProperty("spring.datasource.url");
        }

        if (dbUrl != null && !dbUrl.isBlank()) {
            if (dbUrl.startsWith("postgresql://")) {
                dbUrl = "jdbc:" + dbUrl;
            } else if (dbUrl.startsWith("postgres://")) {
                dbUrl = "jdbc:postgresql://" + dbUrl.substring("postgres://".length());
            } else if (!dbUrl.startsWith("jdbc:")) {
                dbUrl = "jdbc:postgresql://" + dbUrl;
            }
            System.setProperty("spring.datasource.url", dbUrl);
            System.setProperty("SPRING_DATASOURCE_URL", dbUrl);
            System.out.println("[NOMAD-DB-INIT] Fixed JDBC URL: " + dbUrl.replaceAll(":[^/@]+@", ":****@"));
        } else {
            System.out.println("[NOMAD-DB-INIT] No DB URL env found, using default profile configuration");
        }

        SpringApplication.run(NomadApplication.class, args);
    }
}
