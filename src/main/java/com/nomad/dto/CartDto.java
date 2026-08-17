package com.nomad.dto;

import com.nomad.domain.cart.CartStatus;
import lombok.*;
import java.math.BigDecimal;
import java.util.List;

public class CartDto {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AddItemRequest {
        private Long memberId;
        private Long productId;
        private Integer quantity;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChoiceFitRequest {
        private Long memberId;
        private Boolean choiceFit;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ItemDetail {
        private Long cartItemId;
        private Long productId;
        private String productName;
        private String brand;
        private String category;
        private BigDecimal price;
        private Integer quantity;
        private String imageUrl;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CartResponse {
        private Long cartId;
        private Long memberId;
        private Boolean choiceFit;
        private CartStatus status;
        private List<ItemDetail> items;
        private BigDecimal totalPrice;
    }
}
