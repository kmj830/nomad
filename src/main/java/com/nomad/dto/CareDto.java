package com.nomad.dto;

import lombok.*;
import java.util.List;

public class CareDto {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class VisetosSpot {
        private String spotName;
        private String brand;
        private String address;
        private String locationType; // e.g. Luxury Flagship, Duty Free Boutique, Airport Care Desk
        private Double latitude;
        private Double longitude;
        private Integer walkingMinutes; // 도보 소요 시간 (분)
        private String careServiceAvailable;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CareResponse {
        private String destination;
        private String pushNotificationMessage;
        private List<VisetosSpot> visetosSpots;
        private List<com.nomad.domain.product.Product> recommendedItems;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StampRequest {
        private Long memberId;
        private String spotName;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class StampResponse {
        private Long memberId;
        private String spotName;
        private String cityName;
        private int earnedMiles;
        private Long totalMiles;
        private String message;
    }
}

