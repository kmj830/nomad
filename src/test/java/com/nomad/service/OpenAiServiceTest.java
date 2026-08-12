package com.nomad.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OpenAiServiceTest {

    private final OpenAiService openAiService = new OpenAiService();

    @Test
    @DisplayName("OpenAI API 키가 없을 때 스마트 Fallback AI 큐레이션 문구 생성")
    void generatePersonalizedStylingAdvice_Fallback() {
        String advice = openAiService.generatePersonalizedStylingAdvice("Bangkok", "Wet Season", "VIP", "MCM 방수 백팩");

        assertThat(advice).contains("VIP");
        assertThat(advice).contains("Bangkok");
        assertThat(advice).contains("MCM 방수 백팩");
    }

    @Test
    @DisplayName("OpenAI API 키가 없을 때 스마트 Fallback 가죽 케어 문구 생성")
    void generateLeatherCareTip_Fallback() {
        String tip = openAiService.generateLeatherCareTip("비세토스 백팩", "고온다습");

        assertThat(tip).contains("비세토스 백팩");
        assertThat(tip).contains("고온다습");
    }
}
