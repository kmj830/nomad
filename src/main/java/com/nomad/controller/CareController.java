package com.nomad.controller;

import com.nomad.dto.CareDto;
import com.nomad.service.CareService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "6. Care API (현지 비세토스 스팟 & 케어)", description = "목적지 현지 MCM 스팟(플래그십/Care Desk) 탐색 및 비세토스 가죽 케어 푸시 메시지 API")
@RestController
@RequestMapping("/api/v1/care")
@RequiredArgsConstructor
public class CareController {

    private final CareService careService;

    @Operation(summary = "현지 비세토스 스팟 탐색 및 가죽 케어 메시지 조회", description = "목적지의 현지 MCM 플래그십 스토어 및 Care Desk 정보를 조회하고 맞춤형 케어 가이드를 반환합니다.")
    @GetMapping("/visetos-spots")
    public ResponseEntity<CareDto.CareResponse> getVisetosSpots(@RequestParam Long memberId) {
        return ResponseEntity.ok(careService.getVisetosSpots(memberId));
    }
}
