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
        private String address;
        private String locationType; // e.g. MCM Flagship, Duty Free Care Desk
        private Double latitude;
        private Double longitude;
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
    }
}
