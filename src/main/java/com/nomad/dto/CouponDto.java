package com.nomad.dto;

import com.nomad.domain.coupon.CouponCategory;
import com.nomad.domain.coupon.CouponStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

public class CouponDto {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CouponItem {
        private Long couponId;
        private String couponCode;
        private String title;
        private String subtitle;
        private CouponCategory category;
        private CouponStatus status;
        private LocalDateTime validUntil;
        private Boolean urgent;
        private Integer discountRate;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CouponListResponse {
        private Long memberId;
        private Integer totalCoupons;
        private List<CouponItem> items;
    }
}
