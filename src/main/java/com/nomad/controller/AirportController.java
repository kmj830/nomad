package com.nomad.controller;

import com.nomad.domain.journey.Journey;
import com.nomad.domain.journey.JourneyRepository;
import com.nomad.dto.CartDto;
import com.nomad.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "4-1. Airport API (프론트엔드 공항 피팅 연동)", description = "프론트엔드 airportApi 전용 VIP 피팅 시작 엔드포인트")
@RestController
@RequestMapping("/api/v1/airport")
@RequiredArgsConstructor
public class AirportController {

    private final JourneyRepository journeyRepository;
    private final CartService cartService;

    @Getter
    @Builder
    public static class FittingResponse {
        private Long journeyId;
        private Long memberId;
        private boolean choiceFit;
        private String message;
    }

    @Operation(summary = "VIP 피팅 신청 (Airport 호환)", description = "여정 ID를 통해 해당 회원의 스마트 장바구니에 ChoiceFit VIP 피팅을 신청합니다.")
    @PostMapping("/{journeyId}/fitting")
    public ResponseEntity<FittingResponse> startFitting(@PathVariable Long journeyId) {
        Journey journey = journeyRepository.findById(journeyId)
                .orElseThrow(() -> new IllegalArgumentException("여정을 찾을 수 없습니다. ID: " + journeyId));

        cartService.updateChoiceFit(new CartDto.ChoiceFitRequest(journey.getMember().getId(), true));

        return ResponseEntity.ok(FittingResponse.builder()
                .journeyId(journeyId)
                .memberId(journey.getMember().getId())
                .choiceFit(true)
                .message("ChoiceFit VIP 피팅 서비스가 성공적으로 신청되었습니다.")
                .build());
    }
}
