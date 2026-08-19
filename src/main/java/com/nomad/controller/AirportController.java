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
    private final com.nomad.domain.product.ProductRepository productRepository;

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

    @Operation(summary = "공항 스팟 추천 한정 상품 3종 조회 (Airport 호환)", description = "공항 스팟 페이지에서 노출할 LIMITED_EDITION 한정판 추천 아이템 3종을 배열로 반환합니다.")
    @GetMapping({"/popup-items", "/spots/items", "/items"})
    public ResponseEntity<java.util.List<com.nomad.domain.product.Product>> getAirportPopupItems() {
        java.util.List<com.nomad.domain.product.Product> limited = productRepository.findByCategory(com.nomad.domain.product.ProductCategory.LIMITED_EDITION);
        if (limited.isEmpty()) {
            limited = productRepository.findAll().stream().limit(3).toList();
        } else if (limited.size() > 3) {
            limited = limited.subList(0, 3);
        }
        return ResponseEntity.ok(limited);
    }
}
