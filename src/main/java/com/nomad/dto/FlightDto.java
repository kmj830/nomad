package com.nomad.dto;

import com.nomad.domain.journey.FlightStatus;
import lombok.*;

import java.time.LocalDateTime;

public class FlightDto {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class FlightInfoResponse {
        private String flightNumber;
        private String airlineName;
        private String originCode;
        private String originName;
        private String originTerminal;
        private String destinationCode;
        private String destinationName;
        private String gate;
        private FlightStatus flightStatus;
        private LocalDateTime scheduledDepartureTime;
        private LocalDateTime estimatedDepartureTime;
        private LocalDateTime scheduledArrivalTime;
        private String scheduledDepartureFormatted; // e.g. "오후 7:35"
        private String scheduledArrivalFormatted;   // e.g. "오후 11:35"
        private String flightDuration;              // e.g. "6시간 0분"
        private String checkinCounter;              // e.g. "G17-J34"
        private String remark;                      // e.g. "출발", "지연", "탑승중", "마감"
        private int delayMinutes;
        private String dataSource;
    }
}
