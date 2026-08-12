package com.nomad.service;

import com.nomad.dto.FlightDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FlightServiceTest {

    private final FlightService flightService = new FlightService();

    @Test
    @DisplayName("항공편명 기준 운항 정보 조회 테스트 (KE651)")
    void getFlightInfo_KE651() {
        FlightDto.FlightInfoResponse response = flightService.getFlightInfo("KE651");

        assertThat(response.getFlightNumber()).isEqualTo("KE651");
        assertThat(response.getAirlineName()).contains("Korean Air");
        assertThat(response.getDestinationCode()).isEqualTo("BKK");
    }

    @Test
    @DisplayName("항공편명 기준 운항 정보 조회 테스트 (OZ741)")
    void getFlightInfo_OZ741() {
        FlightDto.FlightInfoResponse response = flightService.getFlightInfo("OZ741");

        assertThat(response.getFlightNumber()).isEqualTo("OZ741");
        assertThat(response.getAirlineName()).contains("Asiana");
        assertThat(response.getDestinationCode()).isEqualTo("NRT");
    }
}
