package com.nomad.service;

import com.nomad.domain.cart.CartStatus;
import com.nomad.domain.cart.SmartCart;
import com.nomad.domain.cart.SmartCartRepository;
import com.nomad.domain.member.Member;
import com.nomad.domain.member.MemberRepository;
import com.nomad.domain.member.VipTier;
import com.nomad.domain.store.*;
import com.nomad.dto.StoreDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StoreServiceTest {

    @Mock
    private StoreVisitRepository storeVisitRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private SmartCartRepository smartCartRepository;

    @Mock
    private SseService sseService;

    @InjectMocks
    private StoreService storeService;

    @Test
    @DisplayName("공항 면세점 BLE/NFC 자동 체크인 처리 및 태블릿 알림")
    void checkIn_Success() {
        Member member = Member.builder().id(1L).name("김노마드").vipTier(VipTier.VIP).build();
        SmartCart cart = SmartCart.builder().id(10L).choiceFit(true).status(CartStatus.IN_CART).items(new ArrayList<>()).build();

        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(smartCartRepository.findByMemberIdAndStatus(1L, CartStatus.IN_CART)).thenReturn(Optional.of(cart));

        StoreVisit savedVisit = StoreVisit.builder()
                .id(100L)
                .member(member)
                .checkInType(CheckInType.BLE)
                .checkInStatus(CheckInStatus.COMPLETED)
                .assistantNotified(true)
                .purchaseStatus(PurchaseStatus.PENDING_REENTRY)
                .visitedAt(LocalDateTime.now())
                .build();

        when(storeVisitRepository.save(any(StoreVisit.class))).thenReturn(savedVisit);

        StoreDto.CheckInRequest req = new StoreDto.CheckInRequest(1L, CheckInType.BLE, null);
        StoreDto.CheckInResponse res = storeService.checkIn(req);

        assertThat(res.getVisitId()).isEqualTo(100L);
        assertThat(res.getCheckInType()).isEqualTo(CheckInType.BLE);
        assertThat(res.getChoiceFitRequested()).isTrue();
        assertThat(res.getWelcomeCouponMessage()).contains("VIP 피팅 고객입니다!");
    }

    @Test
    @DisplayName("재방문 (Re-entry Flow) 선택 분기 조회 성공")
    void getReEntryOptions_Success() {
        Member member = Member.builder().id(1L).name("김노마드").vipTier(VipTier.VIP).build();
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(smartCartRepository.findByMemberIdAndStatus(1L, CartStatus.IN_CART)).thenReturn(Optional.empty());
        when(storeVisitRepository.findTopByMemberIdOrderByVisitedAtDesc(1L)).thenReturn(Optional.empty());

        StoreDto.ReEntryResponse response = storeService.getReEntryOptions(1L);

        assertThat(response.getMemberId()).isEqualTo(1L);
        assertThat(response.getAvailableOptions()).hasSize(3);
        assertThat(response.getAvailableOptions()).contains("바로 결제 (DIRECT_CHECKOUT)", "다시 피팅 (RE_FITTING)", "새 상품 보기 (BROWSE_NEW_PRODUCTS)");
    }
}
