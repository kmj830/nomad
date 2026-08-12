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

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public boolean isApiKeyAvailable() {
        return apiKey != null && !apiKey.isBlank() && !apiKey.startsWith("YOUR_");
    }

    public String generatePersonalizedStylingAdvice(String destination, String weatherInfo, String vipTier, String productName) {
        if (!isApiKeyAvailable()) {
            return String.format("[%s VIP 전용 AI 큐레이션] %s 기후(%s)에 맞춰 정밀 설계된 %s 스페셜 룩북을 확인해보세요.",
                    vipTier, destination, weatherInfo, productName);
        }

        try {
            String prompt = String.format(
                    "You are an AI luxury fashion concierge for MCM Worldwide. " +
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
                return String.format("MCM %s leather care and weather protection guide for %s climate.", productName, destinationWeather);
            } else if ("ja".equals(language)) {
                return String.format("%s 気候における MCM %s レザーケアおよび防水保護ガイドです。", destinationWeather, productName);
            } else if ("zh".equals(language)) {
                return String.format("MCM %s 皮革针对 %s 气候的特别保养与防护指南。", productName, destinationWeather);
            }
            return String.format("MCM %s 가죽 제품의 %s 기후 조건 맞춤 수분/코팅 케어 가이드입니다.", productName, destinationWeather);
        }

        try {
            String prompt = String.format(
                    "Generate a 2-sentence professional MCM Visetos leather care tip %s for %s under destination weather condition: %s.",
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
                Map.of("role", "system", "content", "You are an AI luxury concierge assistant for MCM Nomad Passport AI."),
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
        return "MCM777";
    }
}
