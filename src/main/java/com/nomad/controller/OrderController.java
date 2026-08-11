package com.nomad.controller;

import com.nomad.dto.OrderDto;
import com.nomad.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/checkout")
    public ResponseEntity<OrderDto.OrderResponse> checkout(@RequestBody OrderDto.CheckoutRequest request) {
        return ResponseEntity.ok(orderService.checkout(request));
    }
}
