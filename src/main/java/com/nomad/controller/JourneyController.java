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
    private final com.nomad.service.PassKitService passKitService;

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

    @Operation(summary = "SCR-102 AI 라이브 카드 위젯 정보 조회", description = "실시간 항공편 탑승 카운트다운, 게이트 번호, 공항 라운지 현황 및 안내 메시지를 조회합니다.")
    @GetMapping("/live-card/{journeyId}")
    public ResponseEntity<JourneyDto.LiveCardResponse> getLiveCard(@PathVariable Long journeyId) {
        return ResponseEntity.ok(journeyService.getLiveCard(journeyId));
    }

    @Operation(summary = "SCR-201 Apple Wallet 디지털 패스(.pkpass) 생성", description = "비행 탑승권 및 VIP 여정 보딩패스를 Apple Wallet PKPass 형태로 생성합니다.")
    @GetMapping("/apple-wallet-pass/{journeyId}")
    public ResponseEntity<com.nomad.service.PassKitService.AppleWalletPassResponse> getAppleWalletPass(@PathVariable Long journeyId) {
        return ResponseEntity.ok(passKitService.generateNomadPassportPass(journeyId, "MCM999", "BKK (방콕 수완나품)"));
    }

    @Operation(summary = "Apple Wallet (.pkpass) 바이너리 파일 다운로드", description = "iOS 아이폰 디바이스 지갑(Wallet) 앱에 직접 추가할 수 있는 .pkpass 패스 파일을 다운로드합니다.")
    @GetMapping(value = {"/apple-wallet-pass/download/{journeyId}", "/apple-wallet-pass/download/{journeyId}.pkpass"}, produces = "application/vnd.apple.pkpass")
    public ResponseEntity<byte[]> downloadAppleWalletPass(@PathVariable String journeyId) {
        byte[] pkpassBytes = passKitService.generatePkpassZipBytes(1L, journeyId, "BKK");
        return ResponseEntity.ok()
                .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"mcm-boarding-pass.pkpass\"")
                .body(pkpassBytes);
    }

    @Operation(summary = "아이폰 범용 패스 파일 다운로드 (Safari 다운로드 호환)", description = "iOS Safari 보안 차단 없이 아이폰 파일 앱(Downloads)으로 직접 다운로드할 수 있는 패스 파일 API입니다.")
    @GetMapping(value = {"/apple-wallet-pass/download-file/{journeyId}", "/apple-wallet-pass/download-file/{journeyId}.zip"}, produces = org.springframework.http.MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<byte[]> downloadAppleWalletPassFile(@PathVariable String journeyId) {
        byte[] pkpassBytes = passKitService.generatePkpassZipBytes(1L, journeyId, "BKK");
        return ResponseEntity.ok()
                .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"mcm-pass-" + journeyId + ".pkpass\"")
                .body(pkpassBytes);
    }
}


