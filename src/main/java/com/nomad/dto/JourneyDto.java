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
    public static class TimelineItem {
        private String stepType; // e.g. "DEPARTURE", "IN_FLIGHT", "ARRIVAL"
        private String title;
        private String time;
        private String description;
        private String tipMessage;
        private String iconType;
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
        private String rainProbability;
        private String climateSummary;
        private String recommendationReason;
        private List<TimelineItem> timeline;
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
        private String flightNumber;
        private String origin;
        private String destination;
        private LocalDateTime departureDateTime;
        private long remainingMinutesToDeparture;
        private String gate;
        private FlightStatus flightStatus;
        private String currentStep; // e.g. "CHECK_IN", "SECURITY_CHECK", "BOARDING", "ARRIVAL"
        private String currentStepLabel; // e.g. "게이트 이동 중"
        private Integer estimatedSecurityMinutes; // e.g. 25
        private String loungeLocation; // e.g. "인천공항 라운지"
        private String loungeGateLocation; // e.g. "터미널 2, 게이트 16번 맞은편"
        private Integer loungeWalkingMinutes; // e.g. 15
        private Integer loungeWaitMinutes; // e.g. 3
        private String loungeWaitTime; // e.g. "라운지 대기시간 3분"
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

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DeleteResponse {
        private Long journeyId;
        private boolean success;
        private String message;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class JourneySummaryItem {
        private Long journeyId;
        private String pnr;
        private String origin;
        private String destination;
        private LocalDateTime departureDateTime;
        private FlightStatus flightStatus;
        private String destinationWeather;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MyJourneysResponse {
        private Long memberId;
        private Integer totalJourneys;
        private List<JourneySummaryItem> journeys;
    }
}
