package com.nomad;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nomad.dto.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class NomadApiIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("전체 MCM Nomad Passport AI 핵심 비즈니스 흐름 통합 테스트")
    void fullNomadWorkflowTest() throws Exception {
        // Phase 1: Auth Login
        AuthDto.LoginRequest loginReq = new AuthDto.LoginRequest("vip@mcmworldwide.com", "김노마드 (VIP)");
        MvcResult loginResult = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginReq)))
                .andExpect(status().isOk())
                .andReturn();

        AuthDto.LoginResponse loginRes = objectMapper.readValue(loginResult.getResponse().getContentAsString(), AuthDto.LoginResponse.class);
        assertThat(loginRes.getMemberId()).isNotNull();
        Long memberId = loginRes.getMemberId();

        // Phase 1: Boarding Pass OCR Scan
        JourneyDto.ScanRequest scanReq = new JourneyDto.ScanRequest(memberId, "MCM999", "BOARDING PASS PNR MCM999", "ICN", "BKK");
        MvcResult scanResult = mockMvc.perform(post("/api/v1/journey/scan")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(scanReq)))
                .andExpect(status().isOk())
                .andReturn();

        JourneyDto.ScanResponse scanRes = objectMapper.readValue(scanResult.getResponse().getContentAsString(), JourneyDto.ScanResponse.class);
        assertThat(scanRes.getJourneyId()).isNotNull();
        Long journeyId = scanRes.getJourneyId();

        // Phase 1: AI Live Card Widget Check
        mockMvc.perform(get("/api/v1/journey/live-card/" + journeyId))
                .andExpect(status().isOk());

        // Phase 1: Destination Weather & Climate Analysis
        mockMvc.perform(get("/api/v1/journey/analysis/" + journeyId))
                .andExpect(status().isOk());

        // Phase 1: Add Item to SmartCart & ChoiceFit Flag Update
        CartDto.AddItemRequest cartAddReq = new CartDto.AddItemRequest(memberId, 1L, 1);
        mockMvc.perform(post("/api/v1/cart/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cartAddReq)))
                .andExpect(status().isOk());

        CartDto.ChoiceFitRequest choiceFitReq = new CartDto.ChoiceFitRequest(memberId, true);
        mockMvc.perform(put("/api/v1/cart/choice-fit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(choiceFitReq)))
                .andExpect(status().isOk());

        // Phase 2: Airport Store Auto Check-in
        StoreDto.CheckInRequest checkInReq = new StoreDto.CheckInRequest(memberId, com.nomad.domain.store.CheckInType.BLE, null);
        mockMvc.perform(post("/api/v1/store/check-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(checkInReq)))
                .andExpect(status().isOk());

        // Phase 2: Store Re-entry Options Check
        mockMvc.perform(get("/api/v1/store/re-entry-options/" + memberId))
                .andExpect(status().isOk());

        // Phase 2: Duty-Free Checkout & Miles Accrual
        OrderDto.CheckoutRequest checkoutReq = new OrderDto.CheckoutRequest(memberId, journeyId);
        mockMvc.perform(post("/api/v1/order/checkout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(checkoutReq)))
                .andExpect(status().isOk());

        // Phase 1: Apple Wallet Pass Generation Check
        mockMvc.perform(get("/api/v1/journey/apple-wallet-pass/" + journeyId))
                .andExpect(status().isOk());

        // Phase 3: Post-flight Visetos Spots & Care Message
        mockMvc.perform(get("/api/v1/care/visetos-spots").param("memberId", memberId.toString()))
                .andExpect(status().isOk());

        // Phase 3: Google Maps Integration Search
        mockMvc.perform(get("/api/v1/care/google-maps").param("destination", "Bangkok"))
                .andExpect(status().isOk());

        // Phase 3: OpenAI AI Care Tip Generation
        mockMvc.perform(get("/api/v1/care/ai-care-tip"))
                .andExpect(status().isOk());

        // Phase 3: FCM Push Test
        mockMvc.perform(post("/api/v1/care/push-test")
                        .param("title", "테스트 푸시")
                        .param("body", "MCM 가죽 케어 가이드"))
                .andExpect(status().isOk());

        // Phase 1: Real-time Flight Lookup Test
        mockMvc.perform(get("/api/v1/flight/lookup").param("flightNumber", "KE651"))
                .andExpect(status().isOk());

        // System Monitoring Health Check Test
        mockMvc.perform(get("/api/v1/health"))
                .andExpect(status().isOk());

        // Phase 3: City Passport Stamp Check-in & Bonus Miles
        CareDto.StampRequest stampReq = new CareDto.StampRequest(memberId, "MCM 방콕 시암파라곤");
        mockMvc.perform(post("/api/v1/care/stamp-checkin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(stampReq)))
                .andExpect(status().isOk());
    }



    @Test
    @DisplayName("예외 발생 시 GlobalExceptionHandler를 통한 표준 ErrorResponse 반환 검증")
    void globalExceptionHandlerTest() throws Exception {
        // 존재하지 않는 회원 ID로 장바구니 상품 추가 시 400 Bad Request
        CartDto.AddItemRequest invalidReq = new CartDto.AddItemRequest(99999L, 1L, 1);
        mockMvc.perform(post("/api/v1/cart/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidReq)))
                .andExpect(status().isBadRequest());
    }
}


