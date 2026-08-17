package com.nomad.controller;

import com.nomad.dto.CareDto;
import com.nomad.service.CareService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "6. Care API (현지 비세토스 스팟 & 케어)", description = "목적지 현지 MCM 스팟(플래그십/Care Desk) 탐색 및 비세토스 가죽 케어 푸시 메시지 API")
@RestController
@RequestMapping("/api/v1/care")
@RequiredArgsConstructor
public class CareController {

    private final CareService careService;
    private final com.nomad.service.GoogleMapsService googleMapsService;
    private final com.nomad.service.FcmService fcmService;
    private final com.nomad.service.OpenAiService openAiService;

    @Operation(summary = "현지 글로벌 럭셔리 매장 스팟 탐색 및 가죽 케어 메시지 조회", description = "목적지의 현지 전 브랜드(샤넬, LV, 구찌, MCM, 에르메스 등) 플래그십 스토어 및 Care Desk 정보를 조회합니다.")
    @GetMapping("/visetos-spots")
    public ResponseEntity<CareDto.CareResponse> getVisetosSpots(
            @RequestParam Long memberId,
            @RequestParam(required = false, defaultValue = "ALL") String brand
    ) {
        return ResponseEntity.ok(careService.getVisetosSpots(memberId, brand));
    }

    @Operation(summary = "SCR-502 Google Maps API 실시간 글로벌 럭셔리 매장 탐색", description = "Google Maps API를 활용해 현지 명품 부티크 위치 좌표 및 구글 지도 길안내 링크를 반환합니다.")
    @GetMapping("/google-maps")
    public ResponseEntity<java.util.List<CareDto.VisetosSpot>> getGoogleMapsSpots(
            @RequestParam(defaultValue = "Bangkok") String destination,
            @RequestParam(required = false, defaultValue = "ALL") String brand
    ) {
        return ResponseEntity.ok(googleMapsService.findSpotsWithMaps(destination, brand));
    }

    @Operation(summary = "SCR-501 OpenAI 기반 가죽 케어 AI 가이드 생성 (다국어 지원: ko, en, ja, zh)", description = "OpenAI GPT-4o를 활용해 구입 제품, 현지 기후 조건 및 다국어(ko, en, ja, zh) 맞춤형 가죽 관리 팁을 생성합니다.")
    @GetMapping("/ai-care-tip")
    public ResponseEntity<String> getAiCareTip(@RequestParam(defaultValue = "MCM Visetos 백팩") String productName,
                                                @RequestParam(defaultValue = "습도 88% 열대성 스콜") String weather,
                                                @RequestParam(defaultValue = "ko") String lang) {
        return ResponseEntity.ok(openAiService.generateLeatherCareTip(productName, weather, lang));
    }

    @Operation(summary = "FCM 디바이스 푸시 알림 테스트", description = "Firebase Cloud Messaging(FCM) 푸시 알림 발송 테스트를 진행합니다.")
    @PostMapping("/push-test")
    public ResponseEntity<com.nomad.service.FcmService.PushResponse> testPushNotification(@RequestParam String title, @RequestParam String body) {
        return ResponseEntity.ok(fcmService.sendPushNotification("SAMPLE_TOKEN", title, body));
    }

    @Operation(summary = "SCR-502 현지 시티 패스포트 스탬프 획득 & 보상 적립", description = "목적지 도시 MCM 부티크 방문 시 패스포트 스탬프를 획득하고 보너스 Herstory Miles를 적립합니다.")
    @PostMapping("/stamp-checkin")
    public ResponseEntity<CareDto.StampResponse> checkInCityStamp(@RequestBody CareDto.StampRequest request) {
        return ResponseEntity.ok(careService.checkInCityStamp(request));
    }
}


