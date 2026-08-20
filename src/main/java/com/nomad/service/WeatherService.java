package com.nomad.service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class WeatherService {

    private final RestTemplate restTemplate;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class WeatherData {
        private String cityName;
        private double temperature;
        private double humidity;
        private double precipitation;
        private boolean isRainy;
        private boolean isHumid;
        private String weatherDescription;
    }

    @org.springframework.cache.annotation.Cacheable(value = "weatherCache", key = "#destination != null ? #destination.toLowerCase() : 'bkk'")
    public WeatherData fetchDestinationWeather(String destination) {
        // Map common destinations to Lat/Lon
        double lat = 13.7563; // Default Bangkok BKK
        double lon = 100.5018;
        String cityName = "Bangkok";

        if (destination != null) {
            String destUpper = destination.toUpperCase();
            if (destUpper.contains("TYO") || destUpper.contains("NRT") || destUpper.contains("HND") || destUpper.contains("TOKYO") || destUpper.contains("도쿄")) {
                lat = 35.6762;
                lon = 139.6503;
                cityName = "Tokyo";
            } else if (destUpper.contains("ICN") || destUpper.contains("SEL") || destUpper.contains("SEOUL") || destUpper.contains("서울")) {
                lat = 37.5665;
                lon = 126.9780;
                cityName = "Seoul";
            } else if (destUpper.contains("SIN") || destUpper.contains("SINGAPORE") || destUpper.contains("싱가포르")) {
                lat = 1.3521;
                lon = 103.8198;
                cityName = "Singapore";
            } else if (destUpper.contains("FRA") || destUpper.contains("FRANKFURT") || destUpper.contains("프랑크푸르트")) {
                lat = 50.1109;
                lon = 8.6821;
                cityName = "Frankfurt";
            }
        }

        try {
            String url = String.format("https://api.open-meteo.com/v1/forecast?latitude=%.4f&longitude=%.4f&current=temperature_2m,relative_humidity_2m,precipitation", lat, lon);
            Map<?, ?> response = restTemplate.getForObject(url, Map.class);
            if (response != null && response.containsKey("current")) {
                Map<?, ?> current = (Map<?, ?>) response.get("current");
                double temp = current.get("temperature_2m") != null ? Double.parseDouble(current.get("temperature_2m").toString()) : 28.5;
                double humidity = current.get("relative_humidity_2m") != null ? Double.parseDouble(current.get("relative_humidity_2m").toString()) : 85.0;
                double precip = current.get("precipitation") != null ? Double.parseDouble(current.get("precipitation").toString()) : 5.0;

                boolean isRainy = precip > 0.1 || humidity >= 80;
                boolean isHumid = humidity >= 70;

                String desc = String.format("%s 현지 기후: 기온 %.1f°C, 습도 %.0f%%, 강수량 %.1fmm (%s)",
                        cityName, temp, humidity, precip, isRainy ? "우천/스콜 예상" : "쾌적한 날씨");

                return WeatherData.builder()
                        .cityName(cityName)
                        .temperature(temp)
                        .humidity(humidity)
                        .precipitation(precip)
                        .isRainy(isRainy)
                        .isHumid(isHumid)
                        .weatherDescription(desc)
                        .build();
            }
        } catch (Exception e) {
            // Fallback for offline testing or network issues
        }

        return WeatherData.builder()
                .cityName(cityName)
                .temperature(29.0)
                .humidity(88.0)
                .precipitation(12.5)
                .isRainy(true)
                .isHumid(true)
                .weatherDescription(cityName + " 현지 기후: 기온 29.0°C, 습도 88%, 열대성 스콜 예상 (Global Weather API Fallback)")
                .build();
    }
}
