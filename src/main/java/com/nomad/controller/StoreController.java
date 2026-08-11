package com.nomad.controller;

import com.nomad.dto.StoreDto;
import com.nomad.service.StoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/store")
@RequiredArgsConstructor
public class StoreController {

    private final StoreService storeService;

    @PostMapping("/check-in")
    public ResponseEntity<StoreDto.CheckInResponse> checkIn(@RequestBody StoreDto.CheckInRequest request) {
        return ResponseEntity.ok(storeService.checkIn(request));
    }
}
