package com.nomad.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

public class AirportDto {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class FittingResponse {
        private Long journeyId;
        private Long memberId;
        private boolean choiceFit;
        private String message;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PickupScheduleResponse {
        private Long journeyId;
        private String pnr;
        private String flightNumber;
        private String airportName;
        private String terminal;
        private LocalDateTime departureDateTime;
        private String departureDate;          // e.g. "2026-08-22"
        private String departureTime;          // e.g. "19:35"
        private String pickupDeskLocation;     // e.g. "인천국제공항 제2여객터미널 3층 면세구역 250번 게이트 앞 Herstory VIP Care & Pick-up Desk"
        private List<String> months;           // e.g. ["7월", "8월", "9월"]
        private List<String> days;             // e.g. ["21일", "22일", "23일"]
        private List<String> times;            // e.g. ["3:30 PM", "4:30 PM", "5:30 PM"]
        private String defaultMonth;           // e.g. "8월"
        private String defaultDay;             // e.g. "22일"
        private String defaultTime;            // e.g. "5:30 PM"
        private String recommendedNotice;      // e.g. "출국 2시간 전(5:30 PM) 수령 시 가장 여유롭게 탑승하실 수 있습니다."
    }
}
