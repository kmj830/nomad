package com.nomad.controller;

import com.nomad.domain.journey.Journey;
import com.nomad.domain.journey.JourneyRepository;
import com.nomad.domain.member.Member;
import com.nomad.domain.member.MemberRepository;
import com.nomad.dto.JourneyDto;
import com.nomad.service.JourneyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "2-1. Preflight API (프론트엔드 출국 전 화면 연동 호환)", description = "프론트엔드 preflightApi 전용 허브, 라이브카드, 기후 가이드 엔드포인트")
@RestController
@RequestMapping("/api/v1/preflight")
@RequiredArgsConstructor
public class PreflightController {

    private final JourneyService journeyService;
    private final JourneyRepository journeyRepository;
    private final MemberRepository memberRepository;

    @Getter
    @Builder
    public static class HubResponse {
        private Long memberId;
        private String memberName;
        private String vipTier;
        private Long nomadMiles;
        private Long activeJourneyId;
        private String activePnr;
        private String destination;
        private String weatherInfo;
        private String statusMessage;
    }

    @Operation(summary = "Nomad Hub 대시보드 종합 데이터 조회", description = "프론트엔드 NomadHubPage에서 호출하는 메인 대시보드 종합 데이터를 반환합니다.")
    @GetMapping("/hub")
    public ResponseEntity<HubResponse> getHub(@RequestParam(required = false) Long memberId) {
        Long targetMemberId = memberId != null ? memberId : 1L;
        Member member = memberRepository.findById(targetMemberId)
                .orElseGet(() -> memberRepository.findAll().stream().findFirst().orElse(null));

        Journey latestJourney = journeyRepository.findTopByMemberIdOrderByDepartureDateTimeDesc(
                member != null ? member.getId() : 1L
        ).orElseGet(() -> journeyRepository.findAll().stream().findFirst().orElse(null));

        HubResponse response = HubResponse.builder()
                .memberId(member != null ? member.getId() : 1L)
                .memberName(member != null ? member.getName() : "김노마드 (VIP)")
                .vipTier(member != null ? member.getVipTier().name() : "VIP")
                .nomadMiles(member != null ? member.getNomadMiles() : 15000L)
                .activeJourneyId(latestJourney != null ? latestJourney.getId() : 1L)
                .activePnr(latestJourney != null ? latestJourney.getPnr() : "HST999")
                .destination(latestJourney != null ? latestJourney.getDestination() : "BKK (방콕 수완나품)")
                .weatherInfo(latestJourney != null ? latestJourney.getDestinationWeather() : "열대성 스콜 (기온 32°C, 습도 85%)")
                .statusMessage("Herstory VIP Hub에 오신 것을 환영합니다.")
                .build();

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "실시간 AI 라이브 카드 조회 (Preflight 호환)", description = "여정 ID가 주어지지 않으면 최신 여정의 라이브 카드를 반환합니다.")
    @GetMapping({"/live-card", "/{journeyId}/live-card"})
    public ResponseEntity<JourneyDto.LiveCardResponse> getLiveCard(@PathVariable(required = false) Long journeyId) {
        Long targetJourneyId = journeyId;
        if (targetJourneyId == null) {
            targetJourneyId = journeyRepository.findAll().stream()
                    .map(Journey::getId)
                    .findFirst()
                    .orElse(1L);
        }
        return ResponseEntity.ok(journeyService.getLiveCard(targetJourneyId));
    }

    @Operation(summary = "목적지 기후 가이드 조회 (Preflight 호환)", description = "여정 ID를 통해 목적지 기후 및 추천 상품 분석 데이터를 반환합니다.")
    @GetMapping({"/{journeyId}/climate", "/climate"})
    public ResponseEntity<JourneyDto.JourneyAnalysisResponse> getClimateGuide(@PathVariable(required = false) Long journeyId) {
        Long targetJourneyId = journeyId != null ? journeyId : 1L;
        return ResponseEntity.ok(journeyService.analyzeJourney(targetJourneyId));
    }
}
