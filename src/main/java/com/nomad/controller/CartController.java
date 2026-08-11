package com.nomad.controller;

import com.nomad.dto.CartDto;
import com.nomad.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping("/add")
    public ResponseEntity<CartDto.CartResponse> addToCart(@RequestBody CartDto.AddItemRequest request) {
        return ResponseEntity.ok(cartService.addToCart(request));
    }

    @PutMapping("/choice-fit")
    public ResponseEntity<CartDto.CartResponse> updateChoiceFit(@RequestBody CartDto.ChoiceFitRequest request) {
        return ResponseEntity.ok(cartService.updateChoiceFit(request));
    }

    @GetMapping("/my")
    public ResponseEntity<CartDto.CartResponse> getMyCart(@RequestParam Long memberId) {
        return ResponseEntity.ok(cartService.getMyCart(memberId));
    }
}
