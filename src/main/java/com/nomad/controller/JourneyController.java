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
@RequestMapping({"/api/v1/journey", "/api/v1/journeys"})
@RequiredArgsConstructor
public class JourneyController {

    private final JourneyService journeyService;
    private final com.nomad.service.PassKitService passKitService;
    private final com.nomad.domain.journey.JourneyRepository journeyRepository;
    private final com.nomad.service.CartService cartService;

    @Operation(summary = "보딩패스 OCR 스캔 & 여정 등록", description = "탑승권 OCR 스캔을 통해 PNR을 추출하고 비행 여정을 저장합니다.")
    @PostMapping("/scan")
    public ResponseEntity<JourneyDto.ScanResponse> scanBoardingPass(@RequestBody JourneyDto.ScanRequest request) {
        return ResponseEntity.ok(journeyService.scanBoardingPass(request));
    }

    @Operation(summary = "보딩패스 생성/스캔 (Frontend 호환)", description = "여정 ID를 통해 보딩패스 스캔 완료 응답을 반환합니다.")
    @PostMapping("/{journeyId}/boarding-pass")
    public ResponseEntity<JourneyDto.ScanResponse> createBoardingPass(@PathVariable Long journeyId) {
        var journey = journeyRepository.findById(journeyId)
                .orElseThrow(() -> new IllegalArgumentException("여정을 찾을 수 없습니다. ID: " + journeyId));

        return ResponseEntity.ok(JourneyDto.ScanResponse.builder()
                .journeyId(journey.getId())
                .pnr(journey.getPnr())
                .origin(journey.getOrigin())
                .destination(journey.getDestination())
                .departureDateTime(journey.getDepartureDateTime())
                .flightStatus(journey.getFlightStatus())
                .message("보딩패스 발급이 성공적으로 완료되었습니다.")
                .build());
    }

    @Operation(summary = "ChoiceFit 피팅 신청 토글 (Frontend 호환)", description = "여정 ID를 통해 해당 회원의 스마트 장바구니에 ChoiceFit VIP 피팅을 신청합니다.")
    @PatchMapping("/{journeyId}/choice-fit")
    public ResponseEntity<com.nomad.dto.CartDto.CartResponse> submitChoiceFit(
            @PathVariable Long journeyId,
            @RequestBody java.util.Map<String, Boolean> body
    ) {
        var journey = journeyRepository.findById(journeyId)
                .orElseThrow(() -> new IllegalArgumentException("여정을 찾을 수 없습니다. ID: " + journeyId));

        boolean choiceFit = body.getOrDefault("choiceFit", true);
        return ResponseEntity.ok(cartService.updateChoiceFit(
                new com.nomad.dto.CartDto.ChoiceFitRequest(journey.getMember().getId(), choiceFit)
        ));
    }

    @Operation(summary = "스타일 엔진 추천 상품 목록 (Frontend 호환)", description = "여정 맞춤 추천 상품 리스트를 반환합니다.")
    @GetMapping("/{journeyId}/style-engine")
    public ResponseEntity<java.util.List<com.nomad.domain.product.Product>> getStyleEngine(@PathVariable Long journeyId) {
        var analysis = journeyService.analyzeJourney(journeyId);
        return ResponseEntity.ok(analysis.getRecommendedProducts());
    }

    @Operation(summary = "여정 회원 장바구니 품목 조회 (Frontend 호환)", description = "여정 소유 회원의 장바구니 품목 리스트를 반환합니다.")
    @GetMapping("/{journeyId}/cart")
    public ResponseEntity<com.nomad.dto.CartDto.CartResponse> getCartByJourney(@PathVariable Long journeyId) {
        var journey = journeyRepository.findById(journeyId)
                .orElseThrow(() -> new IllegalArgumentException("여정을 찾을 수 없습니다. ID: " + journeyId));

        return ResponseEntity.ok(cartService.getMyCart(journey.getMember().getId()));
    }

    @Operation(summary = "여정 회원 장바구니 상품 담기 (Frontend 호환)", description = "여정 소유 회원의 장바구니에 상품을 추가합니다.")
    @PostMapping("/{journeyId}/cart/items")
    public ResponseEntity<com.nomad.dto.CartDto.CartResponse> addItemByJourney(
            @PathVariable Long journeyId,
            @RequestBody java.util.Map<String, Object> body
    ) {
        var journey = journeyRepository.findById(journeyId)
                .orElseThrow(() -> new IllegalArgumentException("여정을 찾을 수 없습니다. ID: " + journeyId));

        Long productId = Long.valueOf(body.get("productId").toString());
        return ResponseEntity.ok(cartService.addToCart(
                new com.nomad.dto.CartDto.AddItemRequest(journey.getMember().getId(), productId, 1)
        ));
    }

    @Operation(summary = "여정 기본 상세 단건 조회", description = "여정 ID를 통해 PNR, 출/도착지, 출발 일시, 운항 상태 및 기후 분석 정보를 조회합니다.")
    @GetMapping("/{journeyId}")
    public ResponseEntity<JourneyDto.JourneyResponse> getJourney(@PathVariable Long journeyId) {
        return ResponseEntity.ok(journeyService.getJourney(journeyId));
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


