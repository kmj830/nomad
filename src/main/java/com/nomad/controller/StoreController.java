package com.nomad.controller;

import com.nomad.dto.StoreDto;
import com.nomad.service.StoreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "4. Store API (공항 면세점 체크인 & 태블릿 연동)", description = "BLE/NFC/QR 체크인 및 매장 직원 어시스턴트 태블릿 알림 연동 API")
@RestController
@RequestMapping("/api/v1/store")
@RequiredArgsConstructor
public class StoreController {

    private final StoreService storeService;
    private final com.nomad.service.SseService sseService;

    @Operation(summary = "공항 면세점 체크인 처리", description = "BLE/NFC 또는 수동 QR 체크인을 진행하고 매장 어시스턴트 태블릿 알림 및 웰컴 쿠폰을 전달합니다.")
    @PostMapping("/check-in")
    public ResponseEntity<StoreDto.CheckInResponse> checkIn(@RequestBody StoreDto.CheckInRequest request) {
        return ResponseEntity.ok(storeService.checkIn(request));
    }

    @Operation(summary = "매장 재방문(Re-entry Flow) 선택 분기 조회", description = "구매 미완료 고객 재방문 시 바로 결제 / 다시 피팅 / 새 상품 보기 분기를 제공합니다.")
    @GetMapping("/re-entry-options/{memberId}")
    public ResponseEntity<StoreDto.ReEntryResponse> getReEntryOptions(@PathVariable Long memberId) {
        return ResponseEntity.ok(storeService.getReEntryOptions(memberId));
    }

    @Operation(summary = "SCR-402 매장 직원 태블릿 실시간 SSE 알림 스트림", description = "고객이 면세점에 체크인할 때 매장 어시스턴트 태블릿으로 실시간 푸시 이벤트를 전송(Server-Sent Events)합니다.")
    @GetMapping(value = "/notifications/stream", produces = org.springframework.http.MediaType.TEXT_EVENT_STREAM_VALUE)
    public org.springframework.web.servlet.mvc.method.annotation.SseEmitter subscribeStaffTablet() {
        return sseService.subscribeStaffTablet();
    }
}


