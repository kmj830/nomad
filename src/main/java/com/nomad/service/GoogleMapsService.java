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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GoogleMapsService {

    @Value("${GOOGLE_MAPS_API_KEY:${google.maps.api.key:}}")
    private String mapsApiKey;

    private final RestTemplate restTemplate;

    public boolean isApiKeyAvailable() {
        return mapsApiKey != null && !mapsApiKey.isBlank() && !mapsApiKey.startsWith("YOUR_");
    }

    @org.springframework.cache.annotation.Cacheable(value = "googleMapsCache", key = "(#destination != null ? #destination.toLowerCase() : 'bkk') + '_' + (#brand != null ? #brand.toLowerCase() : 'all')")
    public List<CareDto.VisetosSpot> findSpotsWithMaps(String destination, String brand) {
        List<CareDto.VisetosSpot> spots = new ArrayList<>();
        String targetCity = destination != null ? destination : "Bangkok";
        String targetBrand = (brand != null && !brand.isBlank() && !"ALL".equalsIgnoreCase(brand)) ? brand : "";

        if (isApiKeyAvailable()) {
            try {
                String searchKeyword = targetBrand.isBlank() ? ("Luxury Boutique " + targetCity) : (targetBrand + " " + targetCity);
                String query = URLEncoder.encode(searchKeyword, StandardCharsets.UTF_8);
                String url = String.format("https://maps.googleapis.com/maps/api/place/textsearch/json?query=%s&key=%s", query, mapsApiKey);

                Map<?, ?> response = restTemplate.getForObject(url, Map.class);
                if (response != null && "OK".equals(response.get("status"))) {
                    List<?> results = (List<?>) response.get("results");
                    for (Object res : results) {
                        Map<?, ?> place = (Map<?, ?>) res;
                        String name = place.get("name") != null ? place.get("name").toString() : (targetBrand.isBlank() ? "Luxury Boutique" : targetBrand + " Store");
                        String address = place.get("formatted_address") != null ? place.get("formatted_address").toString() : targetCity;
                        Map<?, ?> geometry = (Map<?, ?>) place.get("geometry");
                        Map<?, ?> location = geometry != null ? (Map<?, ?>) geometry.get("location") : Map.of();

                        double lat = location.get("lat") != null ? Double.parseDouble(location.get("lat").toString()) : 13.7460;
                        double lng = location.get("lng") != null ? Double.parseDouble(location.get("lng").toString()) : 100.5348;

                        String mapsUrl = "https://www.google.com/maps/search/?api=1&query=" + URLEncoder.encode(name + " " + address, StandardCharsets.UTF_8);

                        String detectedBrand = detectBrand(name, targetBrand);

                        spots.add(CareDto.VisetosSpot.builder()
                                .spotName(name)
                                .brand(detectedBrand)
                                .address(address)
                                .locationType("Luxury Flagship / Duty Free (Google Maps Verified)")
                                .latitude(lat)
                                .longitude(lng)
                                .careServiceAvailable("VIP 컨시어지, 가죽 케어/수선 안내, Google Maps 길안내: " + mapsUrl)
                                .build());
                    }
                }
            } catch (Exception e) {
                // Fallback to static verified real store catalog
            }
        }

        if (spots.isEmpty()) {
            spots = getCuratedGlobalLuxurySpots(targetCity, targetBrand);
        }

        return spots;
    }

    public List<CareDto.VisetosSpot> findMcmSpotsWithMaps(String destination) {
        return findSpotsWithMaps(destination, "ALL");
    }

    public List<CareDto.VisetosSpot> findLuxurySpotsWithMaps(String destination) {
        return findSpotsWithMaps(destination, "ALL");
    }

    private String detectBrand(String name, String fallbackBrand) {
        if (name == null) return fallbackBrand.isBlank() ? "HERSTORY LUXURY" : fallbackBrand;
        String upper = name.toUpperCase();
        if (upper.contains("CHANEL") || upper.contains("샤넬")) return "CHANEL";
        if (upper.contains("LOUIS VUITTON") || upper.contains("루이비통") || upper.contains("LV")) return "LOUIS VUITTON";
        if (upper.contains("HERMES") || upper.contains("에르메스")) return "HERMES";
        if (upper.contains("GUCCI") || upper.contains("구찌")) return "GUCCI";
        if (upper.contains("DIOR") || upper.contains("디올")) return "DIOR";
        if (upper.contains("PRADA") || upper.contains("프라다")) return "PRADA";
        if (upper.contains("MCM")) return "MCM";
        if (upper.contains("BOTTEGA") || upper.contains("보테가")) return "BOTTEGA VENETA";
        if (upper.contains("CELINE") || upper.contains("셀린느")) return "CELINE";
        if (upper.contains("SAINT LAURENT") || upper.contains("생로랑") || upper.contains("YSL")) return "SAINT LAURENT";
        return fallbackBrand.isBlank() ? "LUXURY BRAND" : fallbackBrand;
    }

    private List<CareDto.VisetosSpot> getCuratedGlobalLuxurySpots(String destination, String brandFilter) {
        String destUpper = destination != null ? destination.toUpperCase() : "BKK";
        List<CareDto.VisetosSpot> allSpots = new ArrayList<>();

        if (destUpper.contains("BKK") || destUpper.contains("BANGKOK") || destUpper.contains("방콕")) {
            allSpots.addAll(List.of(
                    CareDto.VisetosSpot.builder()
                            .spotName("CHANEL Bangkok Siam Paragon Boutique")
                            .brand("CHANEL")
                            .address("Siam Paragon, M Floor, Rama I Rd, Pathum Wan, Bangkok 10330")
                            .locationType("Flagship Boutique")
                            .latitude(13.7462).longitude(100.5347)
                            .careServiceAvailable("샤넬 VIP 가죽 케어 & 폴리싱 클리닝, 프라이빗 살롱 피팅")
                            .build(),
                    CareDto.VisetosSpot.builder()
                            .spotName("LOUIS VUITTON Bangkok Gaysorn Amarin Flagship")
                            .brand("LOUIS VUITTON")
                            .address("Gaysorn Amarin, Ground Floor, Ploenchit Rd, Bangkok 10330")
                            .locationType("Maison Flagship Store")
                            .latitude(13.7441).longitude(100.5410)
                            .careServiceAvailable("LV 모노그램 핫스탬핑 각인, 가죽 리페어 컨시어지")
                            .build(),
                    CareDto.VisetosSpot.builder()
                            .spotName("HERMÈS Bangkok ICONSIAM Store")
                            .brand("HERMES")
                            .address("ICONSIAM, ICONLUXE, Ground Floor, Charoen Nakhon Rd, Bangkok 10600")
                            .locationType("Flagship Store")
                            .latitude(13.7267).longitude(100.5108)
                            .careServiceAvailable("에르메스 가죽 스파 & 전담 아티잔 케어")
                            .build(),
                    CareDto.VisetosSpot.builder()
                            .spotName("GUCCI Bangkok Central Embassy Boutique")
                            .brand("GUCCI")
                            .address("Central Embassy, Level G, Ploenchit Rd, Bangkok 10330")
                            .locationType("Luxury Boutique")
                            .latitude(13.7446).longitude(100.5463)
                            .careServiceAvailable("구찌 VIP 프리미엄 가죽 코팅 및 맞춤 피팅")
                            .build(),
                    CareDto.VisetosSpot.builder()
                            .spotName("DIOR Bangkok Siam Paragon Boutique")
                            .brand("DIOR")
                            .address("Siam Paragon, M Floor, Rama I Rd, Bangkok 10330")
                            .locationType("Luxury Boutique")
                            .latitude(13.7460).longitude(100.5349)
                            .careServiceAvailable("디올 레이디 백 가죽 케어 & VIP 프라이빗 룸 피팅")
                            .build(),
                    CareDto.VisetosSpot.builder()
                            .spotName("MCM Bangkok Siam Paragon Boutique")
                            .brand("MCM")
                            .address("Siam Paragon, M Floor, Bangkok 10330")
                            .locationType("Flagship Store & VIP Lounge")
                            .latitude(13.7460).longitude(100.5348)
                            .careServiceAvailable("비세토스 가죽 스팀 케어, 워터프루프 방수 코팅")
                            .build(),
                    CareDto.VisetosSpot.builder()
                            .spotName("Herstory 타임 부티크")
                            .brand("HERSTORY")
                            .address("인천공항 제1여객터미널 GATE 12 인근")
                            .locationType("Airport Time Store")
                            .latitude(37.4602).longitude(126.4407)
                            .walkingMinutes(2)
                            .careServiceAvailable("공항 한정판 트렌치코트 & 스니커즈, 즉시 VIP 피팅 (도보 2분)")
                            .build(),
                    CareDto.VisetosSpot.builder()
                            .spotName("한정판 팝업 스팟")
                            .brand("HERSTORY")
                            .address("인천공항 제1여객터미널 T1, 2층 면세구역 중앙")
                            .locationType("Limited Edition Pop-up Store")
                            .latitude(37.4610).longitude(126.4415)
                            .walkingMinutes(8)
                            .careServiceAvailable("공항 한정 백팩 키링 및 럭셔리 트래블 캡슐 컬렉션 (도보 8분)")
                            .build(),
                    CareDto.VisetosSpot.builder()
                            .spotName("Bangkok Suvarnabhumi Airport King Power Duty Free Luxury Hall")
                            .brand("MULTI-BRAND")
                            .address("Suvarnabhumi Airport International Departure, Concourse D")
                            .locationType("Airport Duty Free & Care Desk")
                            .latitude(13.6900).longitude(100.7501)
                            .walkingMinutes(5)
                            .careServiceAvailable("전 브랜드 면세 즉시 수령, 긴급 가죽 케어, 탑승 전 Fast Checkout")
                            .build()
            ));
        } else if (destUpper.contains("TYO") || destUpper.contains("HND") || destUpper.contains("NRT") || destUpper.contains("TOKYO") || destUpper.contains("도쿄")) {
            allSpots.addAll(List.of(
                    CareDto.VisetosSpot.builder()
                            .spotName("CHANEL Ginza Namiki Flagship")
                            .brand("CHANEL")
                            .address("6-7-19 Ginza, Chuo City, Tokyo 104-0061")
                            .locationType("Maison Flagship")
                            .latitude(35.6705).longitude(139.7628)
                            .careServiceAvailable("샤넬 긴자 프라이빗 VIP 살롱 & 가죽 리페어")
                            .build(),
                    CareDto.VisetosSpot.builder()
                            .spotName("LOUIS VUITTON Ginza Namiki-dori Store")
                            .brand("LOUIS VUITTON")
                            .address("7-6-1 Ginza, Chuo City, Tokyo 104-0061")
                            .locationType("Flagship Store")
                            .latitude(35.6698).longitude(139.7618)
                            .careServiceAvailable("LV 가죽 핫스탬핑 & 트래블 케어")
                            .build(),
                    CareDto.VisetosSpot.builder()
                            .spotName("HERMÈS Maison Ginza")
                            .brand("HERMES")
                            .address("5-4-1 Ginza, Chuo City, Tokyo 104-0061")
                            .locationType("Maison Hermès")
                            .latitude(35.6718).longitude(139.7635)
                            .careServiceAvailable("에르메스 장인 전담 가죽 스파")
                            .build(),
                    CareDto.VisetosSpot.builder()
                            .spotName("PRADA Aoyama Flagship")
                            .brand("PRADA")
                            .address("5-2-6 Minamiaoyama, Minato City, Tokyo 107-0062")
                            .locationType("Architectural Flagship")
                            .latitude(35.6635).longitude(139.7153)
                            .careServiceAvailable("프라다 리나일론 & 사피아노 가죽 케어")
                            .build(),
                    CareDto.VisetosSpot.builder()
                            .spotName("Tokyo Haneda Airport TIAT Duty Free Luxury Gate")
                            .brand("MULTI-BRAND")
                            .address("Haneda Airport Terminal 3, International Departure Hall")
                            .locationType("Airport Duty Free & Care Desk")
                            .latitude(35.5494).longitude(139.7798)
                            .careServiceAvailable("도쿄 공항 면세 즉시 수령 & 탑승 전 VIP 피팅")
                            .build()
            ));
        } else {
            // Seoul / General fallback
            allSpots.addAll(List.of(
                    CareDto.VisetosSpot.builder()
                            .spotName("CHANEL Seoul Cheongdam Flagship")
                            .brand("CHANEL")
                            .address("431 Apgujeong-ro, Gangnam-gu, Seoul")
                            .locationType("Maison Flagship")
                            .latitude(37.5258).longitude(127.0450)
                            .careServiceAvailable("샤넬 서울 VIP 살롱 & 가죽 케어")
                            .build(),
                    CareDto.VisetosSpot.builder()
                            .spotName("LOUIS VUITTON Maison Seoul Cheongdam")
                            .brand("LOUIS VUITTON")
                            .address("454 Apgujeong-ro, Gangnam-gu, Seoul")
                            .locationType("Maison Flagship")
                            .latitude(37.5250).longitude(127.0465)
                            .careServiceAvailable("LV 프라이빗 핫스탬핑 및 가죽 클리닝")
                            .build(),
                    CareDto.VisetosSpot.builder()
                            .spotName("HERMÈS Maison Dosan Park")
                            .brand("HERMES")
                            .address("7 Dosan-daero 45-gil, Gangnam-gu, Seoul")
                            .locationType("Maison Hermès")
                            .latitude(37.5240).longitude(127.0372)
                            .careServiceAvailable("에르메스 아틀리에 가죽 복원")
                            .build(),
                    CareDto.VisetosSpot.builder()
                            .spotName("Incheon International Airport Terminal 2 Luxury Duty Free")
                            .brand("MULTI-BRAND")
                            .address("Incheon Airport T2 Departure Hall, Gate 250")
                            .locationType("Airport Duty Free Lounge")
                            .latitude(37.4602).longitude(126.4407)
                            .careServiceAvailable("인천공항 전 브랜드 면세 수령 & 사전 피팅")
                            .build()
            ));
        }

        if (brandFilter != null && !brandFilter.isBlank() && !"ALL".equalsIgnoreCase(brandFilter)) {
            String filterUpper = brandFilter.toUpperCase();
            List<CareDto.VisetosSpot> filtered = allSpots.stream()
                    .filter(s -> s.getBrand() != null && s.getBrand().toUpperCase().contains(filterUpper))
                    .collect(Collectors.toList());
            if (!filtered.isEmpty()) return filtered;
        }

        return allSpots;
    }
}
