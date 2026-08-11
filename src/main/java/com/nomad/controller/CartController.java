package com.nomad.controller;

import com.nomad.dto.CartDto;
import com.nomad.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "3. Cart API (스마트 장바구니 & ChoiceFit)", description = "스마트 장바구니 상품 담기 및 VIP 피팅 신청(ChoiceFit) 분기 설정 API")
@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @Operation(summary = "스마트 장바구니 상품 추가", description = "추천 상품을 스마트 장바구니에 담습니다.")
    @PostMapping("/add")
    public ResponseEntity<CartDto.CartResponse> addToCart(@RequestBody CartDto.AddItemRequest request) {
        return ResponseEntity.ok(cartService.addToCart(request));
    }

    @Operation(summary = "ChoiceFit (VIP 피팅 신청 여부) 상태 업데이트", description = "장바구니 담기 후 피팅 신청 여부(true/false) 상태를 업데이트합니다.")
    @PutMapping("/choice-fit")
    public ResponseEntity<CartDto.CartResponse> updateChoiceFit(@RequestBody CartDto.ChoiceFitRequest request) {
        return ResponseEntity.ok(cartService.updateChoiceFit(request));
    }

    @Operation(summary = "내 스마트 장바구니 조회", description = "현재 회원의 활성화된 스마트 장바구니 및 품목 목록을 조회합니다.")
    @GetMapping("/my")
    public ResponseEntity<CartDto.CartResponse> getMyCart(@RequestParam Long memberId) {
        return ResponseEntity.ok(cartService.getMyCart(memberId));
    }
}
