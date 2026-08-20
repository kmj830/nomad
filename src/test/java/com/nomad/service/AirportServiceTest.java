package com.nomad.service;

import com.nomad.domain.journey.Journey;
import com.nomad.domain.journey.JourneyRepository;
import com.nomad.dto.AirportDto;
import com.nomad.dto.FlightDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AirportServiceTest {

    @Mock
    private JourneyRepository journeyRepository;

    @Mock
    private FlightService flightService;

    @InjectMocks
    private AirportService airportService;

    @Test
    @DisplayName("비행 여정 탑승시간 기반 출국 당일 픽업 가능 일정 및 슬롯 자동 계산")
    void getPickupSchedule_Success() {
        LocalDateTime departure = LocalDateTime.of(2026, 8, 22, 19, 35);
        Journey journey = Journey.builder()
                .id(1L)
                .pnr("HST777")
                .origin("ICN")
                .destination("BKK")
                .departureDateTime(departure)
                .build();

        when(journeyRepository.findById(1L)).thenReturn(Optional.of(journey));
        when(flightService.getFlightInfo("HST777")).thenReturn(FlightDto.FlightInfoResponse.builder()
                .flightNumber("HST777")
                .originCode("ICN")
                .originTerminal("인천공항 제2여객터미널")
                .build());

        AirportDto.PickupScheduleResponse response = airportService.getPickupSchedule(1L);

        assertThat(response).isNotNull();
        assertThat(response.getJourneyId()).isEqualTo(1L);
        assertThat(response.getFlightNumber()).isEqualTo("HST777");
        assertThat(response.getDepartureDate()).isEqualTo("2026-08-22");
        assertThat(response.getDepartureTime()).isEqualTo("19:35");
        assertThat(response.getDefaultMonth()).isEqualTo("8월");
        assertThat(response.getDefaultDay()).isEqualTo("22일");
        assertThat(response.getMonths()).contains("7월", "8월", "9월");
        assertThat(response.getDays()).contains("21일", "22일", "23일");
        assertThat(response.getTimes()).hasSize(3);
        assertThat(response.getDefaultTime()).isEqualTo("5:35 PM");
        assertThat(response.getPickupDeskLocation()).contains("인천공항 제2여객터미널 3층 면세구역 250번 게이트 앞");
        assertThat(response.getRecommendedNotice()).contains("5:35 PM");
    }
}
