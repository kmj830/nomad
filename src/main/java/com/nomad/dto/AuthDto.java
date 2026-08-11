package com.nomad.dto;

import com.nomad.domain.member.VipTier;
import lombok.*;

public class AuthDto {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginRequest {
        private String email;
        private String name;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class LoginResponse {
        private Long memberId;
        private String email;
        private String name;
        private VipTier vipTier;
        private Long nomadMiles;
        private String message;
    }
}
