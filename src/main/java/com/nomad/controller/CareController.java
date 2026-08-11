package com.nomad.controller;

import com.nomad.dto.CareDto;
import com.nomad.service.CareService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/care")
@RequiredArgsConstructor
public class CareController {

    private final CareService careService;

    @GetMapping("/visetos-spots")
    public ResponseEntity<CareDto.CareResponse> getVisetosSpots(@RequestParam Long memberId) {
        return ResponseEntity.ok(careService.getVisetosSpots(memberId));
    }
}
