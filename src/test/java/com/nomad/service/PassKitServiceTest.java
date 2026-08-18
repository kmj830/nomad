package com.nomad.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PassKitServiceTest {

    private final PassKitService passKitService = new PassKitService();

    @Test
    @DisplayName("Apple Wallet PassKit 패스 생성 데이터 검증")
    void generateNomadPassportPass_Success() {
        PassKitService.AppleWalletPassResponse pass = passKitService.generateNomadPassportPass(1L, "HST999", "BKK");

        assertThat(pass.getPassTypeIdentifier()).isEqualTo("pass.com.herstory.passport");
        assertThat(pass.getSerialNumber()).contains("HST999");
        assertThat(pass.getPkpassDownloadUrl()).contains(".pkpass");
    }
}
