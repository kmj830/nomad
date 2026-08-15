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
        String serial = "HERSTORY-PASS-" + (pnr != null ? pnr : "HST999") + "-" + memberId;

        Map<String, Object> details = Map.of(
                "transitType", "PKTransitTypeAir",
                "headerFields", Map.of("key", "gate", "label", "GATE", "value", "Gate 24 (T1)"),
                "primaryFields", Map.of("key", "destination", "label", "DESTINATION", "value", destination),
                "secondaryFields", Map.of("key", "pnr", "label", "PNR REQ", "value", pnr != null ? pnr : "HST999"),
                "auxiliaryFields", Map.of("key", "tier", "label", "VIP MEMBERSHIP", "value", "VIP HERSTORY")
        );

        return AppleWalletPassResponse.builder()
                .passTypeIdentifier("pass.com.herstory.passport")
                .serialNumber(serial)
                .teamIdentifier("HST99PASSKIT")
                .organizationName("Herstory")
                .description("Herstory VIP Flight Boarding & Fitting Pass")
                .logoText("HERSTORY AI")
                .boardingPassDetails(details)
                .pkpassDownloadUrl("https://mcm-nomad-backend.onrender.com/api/v1/journey/apple-wallet-pass/download/" + (pnr != null ? pnr : "HST999") + ".pkpass")
                .build();
    }

    public byte[] generatePkpassZipBytes(Long memberId, String pnr, String destination) {
        try {
            java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
            java.util.zip.ZipOutputStream zos = new java.util.zip.ZipOutputStream(baos);

            // Add pass.json
            zos.putNextEntry(new java.util.zip.ZipEntry("pass.json"));
            String jsonContent = String.format("{\n" +
                    "  \"formatVersion\": 1,\n" +
                    "  \"passTypeIdentifier\": \"pass.com.herstory.passport\",\n" +
                    "  \"serialNumber\": \"HST-%s-%d\",\n" +
                    "  \"teamIdentifier\": \"HST99PASSKIT\",\n" +
                    "  \"organizationName\": \"Herstory\",\n" +
                    "  \"description\": \"Herstory VIP Pass\",\n" +
                    "  \"logoText\": \"HERSTORY\",\n" +
                    "  \"foregroundColor\": \"rgb(255, 255, 255)\",\n" +
                    "  \"backgroundColor\": \"rgb(17, 17, 17)\",\n" +
                    "  \"boardingPass\": {\n" +
                    "    \"transitType\": \"PKTransitTypeAir\"\n" +
                    "  }\n" +
                    "}", pnr != null ? pnr : "HST999", memberId != null ? memberId : 1);
            zos.write(jsonContent.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            zos.closeEntry();

            zos.finish();
            return baos.toByteArray();
        } catch (Exception e) {
            return "PKPASS_GEN_ERROR".getBytes(java.nio.charset.StandardCharsets.UTF_8);
        }
    }
}
