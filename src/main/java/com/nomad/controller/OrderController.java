package com.nomad.controller;

import com.nomad.dto.OrderDto;
import com.nomad.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "5. Order API (면세 결제 & 마일리지)", description = "선속 결제, VIP 티어별 면세 한도 할인 적용 및 Herstory Miles 마일리지 적립 API")
@RestController
@RequestMapping("/api/v1/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @Operation(summary = "면세점 주문 결제 및 마일리지 적립", description = "장바구니 품목을 결제하고 VIP 티어별 면세 할인을 적용한 후 Herstory Miles를 적립합니다.")
    @PostMapping("/checkout")
    public ResponseEntity<OrderDto.OrderResponse> checkout(@RequestBody OrderDto.CheckoutRequest request) {
        return ResponseEntity.ok(orderService.checkout(request));
    }
}
