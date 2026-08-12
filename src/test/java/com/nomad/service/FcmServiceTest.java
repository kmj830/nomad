package com.nomad.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FcmServiceTest {

    private final FcmService fcmService = new FcmService();

    @Test
    @DisplayName("FCM 푸시 알림 Payload 생성 및 발송 테스트")
    void sendPushNotification_Success() {
        FcmService.PushResponse res = fcmService.sendPushNotification("TOKEN123", "웰컴 알림", "방콕 도착을 환영합니다!");

        assertThat(res.isSuccess()).isTrue();
        assertThat(res.getTitle()).isEqualTo("웰컴 알림");
        assertThat(res.getMessageId()).isNotNull();
    }
}
