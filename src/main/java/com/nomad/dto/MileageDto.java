package com.nomad.dto;

import com.nomad.domain.mileage.MileageType;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

public class MileageDto {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class BalanceResponse {
        private Long memberId;
        private String memberName;
        private String vipTier;
        private Long totalMiles;
        private Long expiringMiles;
        private String expiringDate;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class HistoryItem {
        private Long historyId;
        private String title;
        private Long amount;
        private MileageType type;
        private Long balanceAfter;
        private LocalDateTime createdAt;
        private String formattedDate;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class HistoryResponse {
        private Long memberId;
        private String memberName;
        private String vipTier;
        private Long totalMiles;
        private Long expiringMiles;
        private String expiringDate;
        private List<HistoryItem> items;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UseMilesRequest {
        private Long memberId;
        private Long amount;
        private String title; // 사용처 (예: "프리미엄 라운지 이용권 교환", "공항 의전 패스트트랙")
        private String description;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UseMilesResponse {
        private Long memberId;
        private Long usedMiles;
        private Long remainingMiles;
        private String title;
        private String message;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TransferRequest {
        private Long fromMemberId;
        private String toEmail;     // 양도받을 대상 회원 이메일 (예: gold@herstory.com)
        private Long toMemberId;    // 또는 대상 회원 ID
        private Long amount;        // 양도할 마일리지 금액
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TransferResponse {
        private Long fromMemberId;
        private String fromMemberName;
        private Long toMemberId;
        private String toMemberName;
        private Long transferredMiles;
        private Long remainingMiles;
        private String message;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RedeemBenefitRequest {
        private Long memberId;
        private String benefitCode; // "LOUNGE_PASS", "VIP_FITTING", "LEATHER_CARE_KIT", "AIRPORT_PICKUP"
        private Long requiredMiles;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RedeemBenefitResponse {
        private Long memberId;
        private String benefitCode;
        private String benefitName;
        private String couponCode;
        private Long usedMiles;
        private Long remainingMiles;
        private String message;
    }
}
