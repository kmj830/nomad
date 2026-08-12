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
        private int delayMinutes;
        private String dataSource;
    }
}
