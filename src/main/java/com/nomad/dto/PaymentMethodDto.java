package com.nomad.dto;

import lombok.*;

public class PaymentMethodDto {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PaymentMethodItem {
        private Long cardId;
        private String cardName;
        private String cardNumberMasked;
        private String subtitle;
        private Boolean isDefault;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AddCardRequest {
        private String cardName;
        private String cardNumber;
        private Boolean isDefault;
    }
}
