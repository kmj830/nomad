package com.nomad.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PassKitServiceTest {

    private final PassKitService passKitService = new PassKitService();

    @Test
    @DisplayName("Apple Wallet PassKit 패스 생성 데이터 검증")
    void generateNomadPassportPass_Success() {
        PassKitService.AppleWalletPassResponse pass = passKitService.generateNomadPassportPass(1L, "MCM999", "BKK");

        assertThat(pass.getPassTypeIdentifier()).isEqualTo("pass.com.mcmworldwide.nomadpassport");
        assertThat(pass.getSerialNumber()).contains("MCM999");
        assertThat(pass.getPkpassDownloadUrl()).contains(".pkpass");
    }
}
