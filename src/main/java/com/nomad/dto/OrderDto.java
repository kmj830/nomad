package com.nomad.dto;

import com.nomad.domain.order.OrderStatus;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class OrderDto {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CheckoutRequest {
        private Long memberId;
        private Long journeyId;
        private String pickupMonth;
        private String pickupDay;
        private String pickupTime;
        private String pickupLocation;

        public CheckoutRequest(Long memberId, Long journeyId) {
            this.memberId = memberId;
            this.journeyId = journeyId;
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OrderItemDetail {
        private Long productId;
        private String productName;
        private Integer quantity;
        private BigDecimal price;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OrderResponse {
        private Long orderId;
        private Long memberId;
        private Long journeyId;
        private BigDecimal totalAmount;
        private BigDecimal dutyFreeDiscount;
        private BigDecimal finalAmount;
        private Integer earnedMiles;
        private OrderStatus orderStatus;
        private String pickupDate;
        private String pickupTime;
        private String pickupLocation;
        private List<OrderItemDetail> items;
        private LocalDateTime createdAt;
    }
}
