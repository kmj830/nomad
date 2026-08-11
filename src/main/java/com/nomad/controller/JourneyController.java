package com.nomad.controller;

import com.nomad.dto.JourneyDto;
import com.nomad.service.JourneyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "2. Journey API (여정 & 큐레이션)", description = "보딩패스 Vision OCR PNR 스캔 및 목적지 날씨/기후 분석 큐레이션 API")
@RestController
@RequestMapping("/api/v1/journey")
@RequiredArgsConstructor
public class JourneyController {

    private final JourneyService journeyService;

    @Operation(summary = "보딩패스 OCR 스캔 & 여정 등록", description = "탑승권 OCR 스캔을 통해 PNR을 추출하고 비행 여정을 저장합니다.")
    @PostMapping("/scan")
    public ResponseEntity<JourneyDto.ScanResponse> scanBoardingPass(@RequestBody JourneyDto.ScanRequest request) {
        return ResponseEntity.ok(journeyService.scanBoardingPass(request));
    }

    @Operation(summary = "목적지 기후 및 여행 분석 데이터 조회", description = "목적지 날씨 및 기후 정보를 분석하여 맞춤형 MCM 상품을 추천합니다.")
    @GetMapping("/analysis/{journeyId}")
    public ResponseEntity<JourneyDto.JourneyAnalysisResponse> analyzeJourney(@PathVariable Long journeyId) {
        return ResponseEntity.ok(journeyService.analyzeJourney(journeyId));
    }
}
