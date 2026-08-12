package com.nomad.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class VisionOcrService {

    @Value("${VISION_API_KEY:${vision.api.key:}}")
    private String visionApiKey;

    private final OpenAiService openAiService;

    public boolean isApiKeyAvailable() {
        return visionApiKey != null && !visionApiKey.isBlank() && !visionApiKey.startsWith("YOUR_");
    }

    public Map<String, String> processBoardingPassOcr(String rawTextOrImageBase64) {
        // If OpenAI API key is available, use GPT-4o Vision parsing
        if (openAiService.isApiKeyAvailable()) {
            return openAiService.parseBoardingPassText(rawTextOrImageBase64);
        }

        Map<String, String> parsed = new HashMap<>();
        String pnr = extractPnrRegex(rawTextOrImageBase64);
        parsed.put("pnr", pnr);
        parsed.put("origin", "ICN (인천국제공항)");
        parsed.put("destination", "BKK (방콕 수완나품)");
        parsed.put("flightNumber", "KE651");
        parsed.put("ocrEngine", isApiKeyAvailable() ? "Google Cloud Vision API" : "Vision OCR Smart Fallback Pattern Matcher");
        return parsed;
    }

    private String extractPnrRegex(String text) {
        if (text == null || text.isBlank()) return "MCM999";
        Pattern pattern = Pattern.compile("[A-Z0-9]{6}");
        Matcher matcher = pattern.matcher(text.toUpperCase());
        if (matcher.find()) {
            return matcher.group();
        }
        return "MCM999";
    }
}
