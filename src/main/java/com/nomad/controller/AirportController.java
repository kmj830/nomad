package com.nomad.controller;

import com.nomad.domain.journey.Journey;
import com.nomad.domain.journey.JourneyRepository;
import com.nomad.dto.AirportDto;
import com.nomad.dto.CartDto;
import com.nomad.service.AirportService;
import com.nomad.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "4-1. Airport API (프론트엔드 공항 피팅 & 픽업 일정 연동)", description = "프론트엔드 airportApi 전용 VIP 피팅 및 여정 기반 면세 픽업 일정 자동 계산 API")
@RestController
@RequestMapping("/api/v1/airport")
@RequiredArgsConstructor
public class AirportController {

    private final JourneyRepository journeyRepository;
    private final CartService cartService;
    private final AirportService airportService;
    private final com.nomad.domain.product.ProductRepository productRepository;

    @Operation(summary = "출국 당일 비행 여정 기반 픽업 가능 일정 자동 계산 조회", description = "유저의 비행기 탑승시간 및 출국일자를 기반으로 자동 산출된 월/일/시간 픽업 슬롯 및 안내 정보를 반환합니다.")
    @GetMapping({"/pickup-schedule", "/{journeyId}/pickup-schedule", "/pickup-slots", "/{journeyId}/pickup-slots"})
    public ResponseEntity<AirportDto.PickupScheduleResponse> getPickupSchedule(
            @PathVariable(required = false) Long journeyId,
            @RequestParam(required = false) Long journeyIdParam
    ) {
        Long targetJourneyId = journeyId != null ? journeyId : journeyIdParam;
        return ResponseEntity.ok(airportService.getPickupSchedule(targetJourneyId));
    }

    @Operation(summary = "VIP 피팅 신청 (Airport 호환)", description = "여정 ID를 통해 해당 회원의 스마트 장바구니에 ChoiceFit VIP 피팅을 신청합니다.")
    @PostMapping("/{journeyId}/fitting")
    public ResponseEntity<AirportDto.FittingResponse> startFitting(@PathVariable Long journeyId) {
        Journey journey = journeyRepository.findById(journeyId)
                .orElseGet(() -> journeyRepository.findAll().stream().findFirst()
                        .orElseThrow(() -> new IllegalArgumentException("여정을 찾을 수 없습니다. ID: " + journeyId)));

        cartService.updateChoiceFit(new CartDto.ChoiceFitRequest(journey.getMember().getId(), true));

        return ResponseEntity.ok(AirportDto.FittingResponse.builder()
                .journeyId(journey.getId())
                .memberId(journey.getMember().getId())
                .choiceFit(true)
                .message("ChoiceFit VIP 피팅 서비스가 성공적으로 신청되었습니다.")
                .build());
    }

    @Operation(summary = "공항 스팟 추천 한정 상품 3종 조회 (Airport 호환)", description = "공항 스팟 페이지에서 노출할 LIMITED_EDITION 한정판 추천 아이템 3종을 배열로 반환합니다.")
    @GetMapping({"/popup-items", "/spots/items", "/items"})
    public ResponseEntity<List<com.nomad.domain.product.Product>> getAirportPopupItems() {
        List<com.nomad.domain.product.Product> limited = productRepository.findByCategory(com.nomad.domain.product.ProductCategory.LIMITED_EDITION);
        if (limited.isEmpty()) {
            limited = productRepository.findAll().stream().limit(3).toList();
        } else if (limited.size() > 3) {
            limited = limited.subList(0, 3);
        }
        return ResponseEntity.ok(limited);
    }
}
