package com.nomad.controller;

import com.nomad.dto.CouponDto;
import com.nomad.service.CouponService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "7. Coupon API (쿠폰 & 혜택 보관함)", description = "보유 쿠폰 및 마일리지 교환 혜택 바우처 목록 조회 API")
@RestController
@RequestMapping({"/api/v1/coupons", "/api/v1/coupon"})
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;

    @Operation(summary = "내 보유 쿠폰/혜택 목록 조회", description = "회원이 보유한 면세 할인, 라운지 이용권, VIP 피팅 우선 예약권 등 유효 쿠폰 목록을 조회합니다.")
    @GetMapping({"/my", "/list", "/{memberId}"})
    public ResponseEntity<CouponDto.CouponListResponse> getMyCoupons(
            @RequestParam(required = false) Long memberId,
            @PathVariable(required = false) Long memberIdPath
    ) {
        Long targetMemberId = memberIdPath != null ? memberIdPath : (memberId != null ? memberId : 1L);
        return ResponseEntity.ok(couponService.getMyCoupons(targetMemberId));
    }
}
