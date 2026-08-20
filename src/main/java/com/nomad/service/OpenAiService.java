package com.nomad.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OpenAiService {

    @Value("${OPENAI_API_KEY:${openai.api.key:}}")
    private String apiKey;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public boolean isApiKeyAvailable() {
        return apiKey != null && !apiKey.isBlank() && !apiKey.startsWith("YOUR_");
    }

    @lombok.Getter
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class AiRecommendationResult {
        private List<Long> recommendedProductIds;
        private String advice;
    }

    public AiRecommendationResult recommendProductsWithAi(
            String destination,
            String weatherInfo,
            String vipTier,
            List<com.nomad.domain.product.Product> catalog
    ) {
        if (catalog == null || catalog.isEmpty()) {
            return AiRecommendationResult.builder()
                    .recommendedProductIds(List.of())
                    .advice("추천 가능한 상품이 없습니다.")
                    .build();
        }

        if (!isApiKeyAvailable()) {
            return fallbackRecommendation(destination, weatherInfo, vipTier, catalog);
        }

        try {
            StringBuilder catalogJson = new StringBuilder("[");
            for (int i = 0; i < catalog.size(); i++) {
                com.nomad.domain.product.Product p = catalog.get(i);
                if (i > 0) catalogJson.append(",");
                catalogJson.append(String.format("{\"id\":%d,\"brand\":\"%s\",\"name\":\"%s\",\"category\":\"%s\",\"price\":%s,\"description\":\"%s\"}",
                        p.getId(), escapeJson(p.getBrand()), escapeJson(p.getName()), p.getCategory(), p.getPrice(), escapeJson(p.getDescription())));
            }
            catalogJson.append("]");

            String prompt = String.format(
                    "You are an AI luxury fashion concierge for Herstory Club. " +
                    "Select 3 to 5 best luxury products from the catalog below that best match the destination weather and luxury travel needs. " +
                    "Ensure brand diversity across multiple luxury brands (e.g. Prada, Gucci, Bottega Veneta, Louis Vuitton, MCM, Hermès, Dior). " +
                    "Client VIP Tier: %s. Destination: %s. Weather: %s.\n\n" +
                    "Product Catalog:\n%s\n\n" +
                    "Respond STRICTLY in JSON format with no markdown blocks:\n" +
                    "{\"recommendedProductIds\": [id1, id2, id3, ...], \"advice\": \"2-sentence elegant Korean styling advice...\"}",
                    vipTier, destination, weatherInfo, catalogJson.toString()
            );

            String responseText = callOpenAiGpt(prompt, 350);
            if (responseText.contains("```json")) {
                responseText = responseText.substring(responseText.indexOf("```json") + 7);
                if (responseText.contains("```")) {
                    responseText = responseText.substring(0, responseText.indexOf("```"));
                }
            } else if (responseText.contains("```")) {
                responseText = responseText.substring(responseText.indexOf("```") + 3);
                if (responseText.contains("```")) {
                    responseText = responseText.substring(0, responseText.indexOf("```"));
                }
            }

            JsonNode node = objectMapper.readTree(responseText.trim());
            List<Long> ids = new java.util.ArrayList<>();
            if (node.has("recommendedProductIds") && node.get("recommendedProductIds").isArray()) {
                for (JsonNode idNode : node.get("recommendedProductIds")) {
                    ids.add(idNode.asLong());
                }
            }
            String advice = node.has("advice") ? node.get("advice").asText() : "";
            if (advice.isBlank()) {
                advice = String.format("[%s VIP AI 큐레이션] %s 기후(%s)에 맞춘 프리미엄 럭셔리 스타일링 제안입니다.", vipTier, destination, weatherInfo);
            }

            if (ids.isEmpty()) {
                return fallbackRecommendation(destination, weatherInfo, vipTier, catalog);
            }

            return AiRecommendationResult.builder()
                    .recommendedProductIds(ids)
                    .advice(advice)
                    .build();
        } catch (Exception e) {
            return fallbackRecommendation(destination, weatherInfo, vipTier, catalog);
        }
    }

    private AiRecommendationResult fallbackRecommendation(
            String destination,
            String weatherInfo,
            String vipTier,
            List<com.nomad.domain.product.Product> catalog
    ) {
        boolean isRainy = weatherInfo != null && (weatherInfo.contains("스콜") || weatherInfo.contains("비") || weatherInfo.contains("Rain") || weatherInfo.contains("습도 8") || weatherInfo.contains("습도 9"));
        List<Long> ids = new java.util.ArrayList<>();

        for (com.nomad.domain.product.Product p : catalog) {
            if (isRainy) {
                if (p.getCategory() == com.nomad.domain.product.ProductCategory.WATERPROOF || p.getCategory() == com.nomad.domain.product.ProductCategory.LEATHER_CARE) {
                    ids.add(p.getId());
                }
            } else {
                if (p.getCategory() != com.nomad.domain.product.ProductCategory.LIMITED_EDITION) {
                    ids.add(p.getId());
                }
            }
            if (ids.size() >= 5) break;
        }

        if (ids.size() < 3) {
            for (com.nomad.domain.product.Product p : catalog) {
                if (!ids.contains(p.getId())) {
                    ids.add(p.getId());
                }
                if (ids.size() >= 4) break;
            }
        }

        String advice = String.format("[%s VIP AI 큐레이션] %s 현지 기후(%s)에 최적화된 글로벌 명품 브랜드 맞춤 룩북을 제안합니다.",
                vipTier, destination, weatherInfo);

        return AiRecommendationResult.builder()
                .recommendedProductIds(ids)
                .advice(advice)
                .build();
    }

    private String escapeJson(String text) {
        if (text == null) return "";
        return text.replace("\"", "\\\"").replace("\n", " ").replace("\r", "");
    }

    public String generatePersonalizedStylingAdvice(String destination, String weatherInfo, String vipTier, String productName) {
        if (!isApiKeyAvailable()) {
            return String.format("[%s VIP 전용 AI 큐레이션] %s 기후(%s)에 맞춰 정밀 설계된 %s 스페셜 룩북을 확인해보세요.",
                    vipTier, destination, weatherInfo, productName);
        }

        try {
            String prompt = String.format(
                    "You are an AI luxury fashion concierge for Herstory Club, providing expert multi-brand luxury styling advice (Chanel, Hermès, Louis Vuitton, Gucci, Dior, Prada, MCM, Bottega Veneta). " +
                            "Generate a sophisticated, 2-sentence personalized styling note in Korean for a %s VIP client traveling to %s. " +
                            "Weather condition: %s. Featured product: %s. Keep the tone elegant, welcoming, and luxury-focused.",
                    vipTier, destination, weatherInfo, productName
            );

            return callOpenAiGpt(prompt, 200);
        } catch (Exception e) {
            return String.format("[%s VIP AI 큐레이션] %s 기후를 위한 %s 럭셔리 스타일링 제안입니다.", vipTier, destination, productName);
        }
    }

    public String generateLeatherCareTip(String productName, String destinationWeather, String lang) {
        String language = (lang != null && !lang.isBlank()) ? lang.toLowerCase() : "ko";
        String langInstruction = "in Korean";
        if ("en".equals(language)) langInstruction = "in English";
        else if ("ja".equals(language)) langInstruction = "in Japanese";
        else if ("zh".equals(language)) langInstruction = "in Simplified Chinese";

        if (!isApiKeyAvailable()) {
            if ("en".equals(language)) {
                return String.format("Herstory Luxury %s leather care and weather protection guide for %s climate.", productName, destinationWeather);
            } else if ("ja".equals(language)) {
                return String.format("%s 気候における プレミアム %s レザーケアおよび防水保護ガイドです。", destinationWeather, productName);
            } else if ("zh".equals(language)) {
                return String.format("针对 %s 气候的 %s 特别皮革保养与防水防护指南。", destinationWeather, productName);
            }
            return String.format("[%s 프리미엄 가죽 케어 솔루션] %s 기후 조건에 대비하여, 가죽 전용 방수 스프레이 도포 및 급격한 습도 변화 시 즉시 마른 융으로 닦아낸 후 통풍이 원활한 곳에 보관하세요.",
                    productName, destinationWeather);
        }

        try {
            String prompt = String.format(
                    "Generate a 2-sentence professional luxury leather care tip %s for %s under destination weather condition: %s.",
                    langInstruction, productName, destinationWeather
            );
            return callOpenAiGpt(prompt, 150);
        } catch (Exception e) {
            return String.format("%s 가죽 제품의 현지 기후 맞춤 케어 팁입니다.", productName);
        }
    }

    public String generateLeatherCareTip(String productName, String destinationWeather) {
        return generateLeatherCareTip(productName, destinationWeather, "ko");
    }

    public Map<String, String> parseBoardingPassText(String rawText) {
        Map<String, String> result = new HashMap<>();
        if (!isApiKeyAvailable()) {
            result.put("pnr", extractFallbackPnr(rawText));
            result.put("origin", "ICN (인천국제공항)");
            result.put("destination", "BKK (방콕 수완나품)");
            result.put("flightNumber", "KE651");
            return result;
        }

        try {
            String prompt = "Extract PNR, Origin airport code, Destination airport code, and Flight Number from this boarding pass text in JSON format with keys: pnr, origin, destination, flightNumber. Text: " + rawText;
            String jsonResponse = callOpenAiGpt(prompt, 200);
            JsonNode node = objectMapper.readTree(jsonResponse);
            if (node.has("pnr")) result.put("pnr", node.get("pnr").asText());
            if (node.has("origin")) result.put("origin", node.get("origin").asText());
            if (node.has("destination")) result.put("destination", node.get("destination").asText());
            if (node.has("flightNumber")) result.put("flightNumber", node.get("flightNumber").asText());
        } catch (Exception e) {
            result.put("pnr", extractFallbackPnr(rawText));
            result.put("origin", "ICN (인천국제공항)");
            result.put("destination", "BKK (방콕 수완나품)");
            result.put("flightNumber", "KE651");
        }
        return result;
    }

    private String callOpenAiGpt(String userPrompt, int maxTokens) {
        String url = "https://api.openai.com/v1/chat/completions";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        Map<String, Object> body = new HashMap<>();
        body.put("model", "gpt-4o-mini");
        body.put("messages", List.of(
                Map.of("role", "system", "content", "You are an AI luxury concierge assistant for Herstory Nomad AI."),
                Map.of("role", "user", "content", userPrompt)
        ));
        body.put("max_tokens", maxTokens);
        body.put("temperature", 0.7);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);

        if (response.getBody() != null && response.getBody().containsKey("choices")) {
            List<?> choices = (List<?>) response.getBody().get("choices");
            if (!choices.isEmpty()) {
                Map<?, ?> firstChoice = (Map<?, ?>) choices.get(0);
                Map<?, ?> message = (Map<?, ?>) firstChoice.get("message");
                return message.get("content").toString().trim();
            }
        }
        return "AI 응답을 가져오는 중 오류가 발생했습니다.";
    }

    private String extractFallbackPnr(String text) {
        if (text != null && text.length() >= 6) {
            return text.substring(0, 6).toUpperCase();
        }
        return "HST777";
    }
}
