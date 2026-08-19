package com.nomad.controller;

import com.nomad.dto.MileageDto;
import com.nomad.service.MileageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "5-1. Mileage API (Nomad Miles 적립/사용/양도/혜택/히스토리)", description = "Nomad Miles 잔액 조회, 상세 거래 내역, 마일리지 사용, 타 회원 양도 및 VIP 라운지/피팅 혜택 교환 API")
@RestController
@RequestMapping({"/api/v1/miles", "/api/v1/mileage"})
@RequiredArgsConstructor
public class MileageController {

    private final MileageService mileageService;

    @Operation(summary = "마일리지 잔액 및 소멸 예정 정보 조회", description = "회원의 현재 보유 Nomad Miles 잔액, VIP 등급 및 연말 소멸 예정 마일리지를 조회합니다.")
    @GetMapping({"/{memberId}", "/balance/{memberId}"})
    public ResponseEntity<MileageDto.BalanceResponse> getBalance(@PathVariable Long memberId) {
        return ResponseEntity.ok(mileageService.getBalance(memberId));
    }

    @Operation(summary = "마일리지 상세 거래 내역 조회 (History)", description = "최근 12개월간의 마일리지 적립, 사용, 양도 및 소멸 예정 상세 내역 리스트를 최신순으로 조회합니다.")
    @GetMapping({"/history/{memberId}", "/histories/{memberId}"})
    public ResponseEntity<MileageDto.HistoryResponse> getHistory(@PathVariable Long memberId) {
        return ResponseEntity.ok(mileageService.getHistory(memberId));
    }

    @Operation(summary = "마일리지 직접 사용", description = "보유한 Nomad Miles를 특정 사용처(라운지, 서비스 등)에 직접 차감 사용합니다.")
    @PostMapping("/use")
    public ResponseEntity<MileageDto.UseMilesResponse> useMiles(@RequestBody MileageDto.UseMilesRequest request) {
        return ResponseEntity.ok(mileageService.useMiles(request));
    }

    @Operation(summary = "마일리지 타 회원 양도 (선물하기)", description = "보유한 Nomad Miles를 다른 회원(이메일 또는 회원 ID)에게 실시간으로 양도(선물)합니다.")
    @PostMapping("/transfer")
    public ResponseEntity<MileageDto.TransferResponse> transferMiles(@RequestBody MileageDto.TransferRequest request) {
        return ResponseEntity.ok(mileageService.transferMiles(request));
    }

    @Operation(summary = "VIP 혜택 교환 (라운지 이용권, 피팅 의전 등)", description = "마일리지를 사용하여 프리미엄 라운지 이용권, VIP 피팅룸 의전, 레더 케어 키트 등의 혜택 쿠폰을 즉시 발급받습니다.")
    @PostMapping({"/redeem", "/redeem-benefit"})
    public ResponseEntity<MileageDto.RedeemBenefitResponse> redeemBenefit(@RequestBody MileageDto.RedeemBenefitRequest request) {
        return ResponseEntity.ok(mileageService.redeemBenefit(request));
    }
}
