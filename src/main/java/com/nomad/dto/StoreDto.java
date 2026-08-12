package com.nomad.dto;

import com.nomad.domain.store.CheckInStatus;
import com.nomad.domain.store.CheckInType;
import com.nomad.domain.store.PurchaseStatus;
import lombok.*;
import java.time.LocalDateTime;

public class StoreDto {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CheckInRequest {
        private Long memberId;
        private CheckInType checkInType;
        private String qrCode;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CheckInResponse {
        private Long visitId;
        private Long memberId;
        private String memberName;
        private String vipTier;
        private CheckInType checkInType;
        private CheckInStatus checkInStatus;
        private Boolean assistantNotified;
        private Boolean choiceFitRequested;
        private String welcomeCouponMessage;
        private PurchaseStatus purchaseStatus;
        private LocalDateTime visitedAt;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ReEntryResponse {
        private Long memberId;
        private String memberName;
        private PurchaseStatus purchaseStatus;
        private Boolean hasPendingCart;
        private int pendingCartItemCount;
        private String recommendedAction;
        private java.util.List<String> availableOptions;
    }
}

