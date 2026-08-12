package com.nomad.controller;

import com.nomad.dto.FlightDto;
import com.nomad.service.FlightService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "7. Flight API (실시간 항공편 & 운항 정보)", description = "편명(IATA Code) 기준 실시간 항공편 운항 상태, 터미널, 탑승구(Gate) 조회 API")
@RestController
@RequestMapping("/api/v1/flight")
@RequiredArgsConstructor
public class FlightController {

    private final FlightService flightService;

    @Operation(summary = "실시간 항공편 운항 정보 조회", description = "편명(예: KE651, OZ741, SQ607, LH713)을 입력받아 항공사, 출발 터미널, 게이트, 목적지 정보를 조회합니다.")
    @GetMapping("/lookup")
    public ResponseEntity<FlightDto.FlightInfoResponse> lookupFlight(@RequestParam(defaultValue = "KE651") String flightNumber) {
        return ResponseEntity.ok(flightService.getFlightInfo(flightNumber));
    }
}
