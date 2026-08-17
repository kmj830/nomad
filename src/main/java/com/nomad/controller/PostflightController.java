package com.nomad.controller;

import com.nomad.domain.member.Member;
import com.nomad.domain.member.MemberRepository;
import com.nomad.dto.CareDto;
import com.nomad.service.CareService;
import com.nomad.service.OpenAiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "6-1. Postflight API (프론트엔드 착륙 후 여정 연동)", description = "프론트엔드 postflightApi 전용 가죽 케어 가이드, 스팟 맵, 마일리지 조회 엔드포인트")
@RestController
@RequestMapping("/api/v1/postflight")
@RequiredArgsConstructor
public class PostflightController {

    private final CareService careService;
    private final OpenAiService openAiService;
    private final MemberRepository memberRepository;

    @Getter
    @Builder
    public static class MilesResponse {
        private Long memberId;
        private String memberName;
        private String vipTier;
        private Long nomadMiles;
    }

    @Getter
    @Builder
    public static class AiCareTipResponse {
        private String productName;
        private String weatherCondition;
        private String language;
        private String aiCareTip;
    }

    @Operation(summary = "목적지 맞춤 가죽 케어 가이드 조회", description = "OpenAI GPT-4o 기반 목적지 날씨 맞춤 비세토스 가죽 관리 가이드를 반환합니다.")
    @GetMapping("/leather-care")
    public ResponseEntity<AiCareTipResponse> getLeatherCareGuide(
            @RequestParam(defaultValue = "MCM 비세토스 트래블 백팩") String productName,
            @RequestParam(defaultValue = "열대성 고온다습 스콜 (기온 32°C, 습도 85%)") String weather,
            @RequestParam(defaultValue = "ko") String lang
    ) {
        String tip = openAiService.generateLeatherCareTip(productName, weather, lang);
        return ResponseEntity.ok(AiCareTipResponse.builder()
                .productName(productName)
                .weatherCondition(weather)
                .language(lang)
                .aiCareTip(tip)
                .build());
    }

    @Operation(summary = "현지 비세토스 스팟 맵 정보 조회", description = "목적지 현지 플래그십 및 공항 Care Desk 위치 목록을 반환합니다.")
    @GetMapping("/visetos-map")
    public ResponseEntity<CareDto.CareResponse> getVisetosSpots(@RequestParam(required = false) Long memberId) {
        Long targetMemberId = memberId != null ? memberId : 1L;
        return ResponseEntity.ok(careService.getVisetosSpots(targetMemberId));
    }

    @Operation(summary = "회원 마일리지 및 등급 정보 조회", description = "회원 ID를 통해 Herstory Miles 잔액 및 VIP 등급을 조회합니다.")
    @GetMapping("/miles/{memberId}")
    public ResponseEntity<MilesResponse> getNomadMiles(@PathVariable Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다. ID: " + memberId));

        return ResponseEntity.ok(MilesResponse.builder()
                .memberId(member.getId())
                .memberName(member.getName())
                .vipTier(member.getVipTier().name())
                .nomadMiles(member.getNomadMiles())
                .build());
    }
}
