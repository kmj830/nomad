package com.nomad.service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class PassKitService {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AppleWalletPassResponse {
        private String passTypeIdentifier;
        private String serialNumber;
        private String teamIdentifier;
        private String organizationName;
        private String description;
        private String logoText;
        private Map<String, Object> boardingPassDetails;
        private String pkpassDownloadUrl;
    }

    public AppleWalletPassResponse generateNomadPassportPass(Long memberId, String pnr, String destination) {
        String serial = "MCM-PASS-" + (pnr != null ? pnr : "MCM999") + "-" + memberId;

        Map<String, Object> details = Map.of(
                "transitType", "PKTransitTypeAir",
                "headerFields", Map.of("key", "gate", "label", "GATE", "value", "Gate 24 (T1)"),
                "primaryFields", Map.of("key", "destination", "label", "DESTINATION", "value", destination),
                "secondaryFields", Map.of("key", "pnr", "label", "PNR REQ", "value", pnr != null ? pnr : "MCM999"),
                "auxiliaryFields", Map.of("key", "tier", "label", "VIP MEMBERSHIP", "value", "MCM VIP NOMAD")
        );

        return AppleWalletPassResponse.builder()
                .passTypeIdentifier("pass.com.mcmworldwide.nomadpassport")
                .serialNumber(serial)
                .teamIdentifier("MCM99PASSKIT")
                .organizationName("MCM Worldwide")
                .description("MCM Nomad Passport VIP Flight Boarding & Fitting Pass")
                .logoText("MCM NOMAD PASSPORT AI")
                .boardingPassDetails(details)
                .pkpassDownloadUrl("https://mcm-nomad-backend.onrender.com/api/v1/journey/apple-wallet-pass/" + serial + ".pkpass")
                .build();
    }
}
