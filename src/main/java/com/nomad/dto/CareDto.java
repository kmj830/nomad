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

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CollectionItem {
        private Long itemId;
        private Long productId;
        private String name;
        private String brand;
        private String category;
        private String imageUrl;
        private String purchaseDate;
        private String lastCareDate;
        private int lastCaredDaysAgo;
        private String careStatus;       // OPTIMAL, CONDITIONING_NEEDED, CARE_RECOMMENDED
        private String careStatusLabel;  // 최적, 컨디셔닝 필요, 전문 케어 권장
        private String careStatusColor;  // #44C67C, #C64F44, #F59E0B
        private String careTip;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MyCollectionResponse {
        private Long memberId;
        private String memberName;
        private int totalCount;
        private CollectionItem featuredItem;
        private List<CollectionItem> items;
    }
}

