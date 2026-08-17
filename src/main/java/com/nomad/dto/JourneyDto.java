package com.nomad.dto;

import com.nomad.domain.journey.FlightStatus;
import com.nomad.domain.product.Product;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

public class JourneyDto {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ScanRequest {
        private Long memberId;
        private String pnr;
        private String rawOcrText;
        private String origin;
        private String destination;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ScanResponse {
        private Long journeyId;
        private String pnr;
        private String origin;
        private String destination;
        private LocalDateTime departureDateTime;
        private FlightStatus flightStatus;
        private String message;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class JourneyAnalysisResponse {
        private Long journeyId;
        private String destination;
        private String weatherInfo;
        private String climateSummary;
        private String recommendationReason;
        private List<Product> recommendedProducts;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class LiveCardResponse {
        private Long journeyId;
        private String pnr;
        private String destination;
        private LocalDateTime departureDateTime;
        private long remainingMinutesToDeparture;
        private String gate;
        private FlightStatus flightStatus;
        private String loungeLocation;
        private String loungeWaitTime;
        private String liveGuideMessage;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class JourneyResponse {
        private Long journeyId;
        private Long memberId;
        private String memberName;
        private String pnr;
        private String origin;
        private String destination;
        private LocalDateTime departureDateTime;
        private FlightStatus flightStatus;
        private String destinationWeather;
        private String recommendationReason;
    }
}

