package com.nomad.service;

import com.nomad.dto.CareDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GoogleMapsService {

    @Value("${GOOGLE_MAPS_API_KEY:${google.maps.api.key:}}")
    private String mapsApiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public boolean isApiKeyAvailable() {
        return mapsApiKey != null && !mapsApiKey.isBlank() && !mapsApiKey.startsWith("YOUR_");
    }

    public List<CareDto.VisetosSpot> findMcmSpotsWithMaps(String destination) {
        List<CareDto.VisetosSpot> spots = new ArrayList<>();

        if (isApiKeyAvailable()) {
            try {
                String query = URLEncoder.encode("MCM " + destination, StandardCharsets.UTF_8);
                String url = String.format("https://maps.googleapis.com/maps/api/place/textsearch/json?query=%s&key=%s", query, mapsApiKey);

                Map<?, ?> response = restTemplate.getForObject(url, Map.class);
                if (response != null && "OK".equals(response.get("status"))) {
                    List<?> results = (List<?>) response.get("results");
                    for (Object res : results) {
                        Map<?, ?> place = (Map<?, ?>) res;
                        String name = place.get("name") != null ? place.get("name").toString() : "MCM Store";
                        String address = place.get("formatted_address") != null ? place.get("formatted_address").toString() : destination;
                        Map<?, ?> geometry = (Map<?, ?>) place.get("geometry");
                        Map<?, ?> location = geometry != null ? (Map<?, ?>) geometry.get("location") : Map.of();

                        double lat = location.get("lat") != null ? Double.parseDouble(location.get("lat").toString()) : 13.7460;
                        double lng = location.get("lng") != null ? Double.parseDouble(location.get("lng").toString()) : 100.5348;

                        String mapsUrl = "https://www.google.com/maps/search/?api=1&query=" + URLEncoder.encode(name + " " + address, StandardCharsets.UTF_8);

                        spots.add(CareDto.VisetosSpot.builder()
                                .spotName(name)
                                .address(address)
                                .locationType("MCM Flagship / Duty Free (Google Maps Verified)")
                                .latitude(lat)
                                .longitude(lng)
                                .careServiceAvailable("가죽 스팀 케어, 워터프루프 코팅, 지도 길안내: " + mapsUrl)
                                .build());
                    }
                }
            } catch (Exception e) {
                // Fallback to static curated list
            }
        }

        if (spots.isEmpty()) {
            spots = getCuratedFallbackSpots(destination);
        }

        return spots;
    }

    private List<CareDto.VisetosSpot> getCuratedFallbackSpots(String destination) {
        return List.of(
                CareDto.VisetosSpot.builder()
                        .spotName("MCM 방콕 시암파라곤 플래그십 스토어")
                        .address("Siam Paragon, M Floor, Bangkok 10330")
                        .locationType("Flagship Store & VIP Lounge")
                        .latitude(13.7460)
                        .longitude(100.5348)
                        .careServiceAvailable("가죽 스팀 케어, 워터프루프 코팅 케어, 방수 커버 제공 (Google Maps 연동 준비됨)")
                        .build(),
                CareDto.VisetosSpot.builder()
                        .spotName("MCM 수완나품 공항 면세 Care Desk")
                        .address("Suvarnabhumi Airport Departure Hall, Concourse D")
                        .locationType("Airport Care Desk")
                        .latitude(13.6900)
                        .longitude(100.7501)
                        .careServiceAvailable("긴급 가죽 왁싱, 여권지갑 리페어")
                        .build()
        );
    }
}
