package com.nomad.dto;

import com.nomad.domain.member.VipTier;
import lombok.*;

public class MyPageDto {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SummaryResponse {
        private Long memberId;
        private String name;
        private String initial;
        private String email;
        private VipTier vipTier;
        private Long miles;
        private Long couponCount;
        private Long journeyCount;
        private String nextTier;
        private Long milesToNextTier;
        private Integer tierProgressPercent;
        private NotificationSettings settings;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class NotificationSettings {
        private Boolean milesAlert;
        private Boolean journeyAlert;
        private Boolean marketingOptIn;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ProfileResponse {
        private Long memberId;
        private String name;
        private String englishName;
        private String email;
        private String phone;
        private String birthDate;
        private VipTier vipTier;
        private Long nomadMiles;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateProfileRequest {
        private String englishName;
        private String email;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateSettingsRequest {
        private Boolean milesAlert;
        private Boolean journeyAlert;
        private Boolean marketingOptIn;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChangePasswordRequest {
        private String currentPassword;
        private String newPassword;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PassportResponse {
        private Long memberId;
        private String name;
        private String englishName;
        private String passportNumber;
        private String maskedPassportNumber;
        private String expiryDate;
        private String formattedDetail;
        private Boolean autoFill;
        private Integer companionCount;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdatePassportRequest {
        private String passportNumber;
        private String expiryDate;
        private Boolean autoFill;
    }
}
