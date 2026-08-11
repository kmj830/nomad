package com.nomad.controller;

import com.nomad.dto.JourneyDto;
import com.nomad.service.JourneyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/journey")
@RequiredArgsConstructor
public class JourneyController {

    private final JourneyService journeyService;

    @PostMapping("/scan")
    public ResponseEntity<JourneyDto.ScanResponse> scanBoardingPass(@RequestBody JourneyDto.ScanRequest request) {
        return ResponseEntity.ok(journeyService.scanBoardingPass(request));
    }

    @GetMapping("/analysis/{journeyId}")
    public ResponseEntity<JourneyDto.JourneyAnalysisResponse> analyzeJourney(@PathVariable Long journeyId) {
        return ResponseEntity.ok(journeyService.analyzeJourney(journeyId));
    }
}
