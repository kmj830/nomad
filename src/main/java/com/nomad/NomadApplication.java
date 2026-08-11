package com.nomad;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.net.URI;

@SpringBootApplication
public class NomadApplication {

    public static void main(String[] args) {
        String dbUrl = System.getenv("SPRING_DATASOURCE_URL");
        if (dbUrl == null || dbUrl.isBlank()) {
            dbUrl = System.getenv("DATABASE_URL");
        }

        if (dbUrl != null && !dbUrl.isBlank()) {
            try {
                if (dbUrl.startsWith("postgres://") || dbUrl.startsWith("postgresql://")) {
                    URI uri = URI.create(dbUrl.startsWith("postgres://") ? dbUrl.replace("postgres://", "http://") : dbUrl.replace("postgresql://", "http://"));
                    String host = uri.getHost();
                    int port = uri.getPort() > 0 ? uri.getPort() : 5432;
                    String path = uri.getPath();

                    String jdbcUrl = "jdbc:postgresql://" + host + ":" + port + path;
                    System.setProperty("spring.datasource.url", jdbcUrl);
                    System.setProperty("SPRING_DATASOURCE_URL", jdbcUrl);

                    if (uri.getUserInfo() != null) {
                        String[] userInfo = uri.getUserInfo().split(":");
                        if (userInfo.length >= 1) {
                            System.setProperty("spring.datasource.username", userInfo[0]);
                            System.setProperty("SPRING_DATASOURCE_USERNAME", userInfo[0]);
                        }
                        if (userInfo.length >= 2) {
                            System.setProperty("spring.datasource.password", userInfo[1]);
                            System.setProperty("SPRING_DATASOURCE_PASSWORD", userInfo[1]);
                        }
                    }
                    System.out.println("[NOMAD-DB-INIT] Parsed JDBC URL: " + jdbcUrl);
                } else {
                    System.setProperty("spring.datasource.url", dbUrl);
                }
            } catch (Exception e) {
                System.err.println("[NOMAD-DB-INIT] Failed to parse URI: " + e.getMessage());
                System.setProperty("spring.datasource.url", dbUrl);
            }
        }

        SpringApplication.run(NomadApplication.class, args);
    }
}
