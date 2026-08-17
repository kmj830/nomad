package com.nomad.dto;

import com.nomad.domain.member.VipTier;
import lombok.*;

public class AuthDto {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class LoginRequest {
        private String email;
        private String password;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RegisterRequest {
        private String email;
        private String password;
        private String name;
        private String phone;
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

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RegisterResponse {
        private Long memberId;
        private String email;
        private String name;
        private VipTier vipTier;
        private Long nomadMiles;
        private String message;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SendVerificationCodeRequest {
        private String phone;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SendVerificationCodeResponse {
        private String phone;
        private String verificationCode;
        private String message;
        private Integer expiresInSeconds;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class VerifyCodeRequest {
        private String phone;
        private String verificationCode;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class VerifyCodeResponse {
        private String phone;
        private boolean verified;
        private String message;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ResetPasswordRequest {
        private String email;
        private String phone;
        private String newPassword;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PasswordResponse {
        private boolean success;
        private String message;
    }
}
