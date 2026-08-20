package com.nomad.service;

import com.nomad.domain.coupon.CouponRepository;
import com.nomad.domain.coupon.CouponStatus;
import com.nomad.domain.journey.JourneyRepository;
import com.nomad.domain.member.Member;
import com.nomad.domain.member.MemberRepository;
import com.nomad.domain.member.PaymentMethod;
import com.nomad.domain.member.PaymentMethodRepository;
import com.nomad.domain.member.VipTier;
import com.nomad.dto.MyPageDto;
import com.nomad.dto.PaymentMethodDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MyPageServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private CouponRepository couponRepository;

    @Mock
    private JourneyRepository journeyRepository;

    @Mock
    private PaymentMethodRepository paymentMethodRepository;

    @InjectMocks
    private MyPageService myPageService;

    @Test
    @DisplayName("마이페이지 대시보드 요약 조회 정상 동작")
    void getSummary_Success() {
        Member member = Member.builder()
                .id(1L)
                .name("김노마드")
                .email("vip@herstory.com")
                .vipTier(VipTier.GOLD)
                .nomadMiles(124500L)
                .milesAlert(true)
                .journeyAlert(true)
                .marketingOptIn(false)
                .build();

        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(couponRepository.countByMemberIdAndStatus(1L, CouponStatus.AVAILABLE)).thenReturn(3L);
        when(journeyRepository.countByMemberId(1L)).thenReturn(14L);

        MyPageDto.SummaryResponse res = myPageService.getSummary(1L);

        assertThat(res.getMemberId()).isEqualTo(1L);
        assertThat(res.getName()).isEqualTo("김노마드");
        assertThat(res.getInitial()).isEqualTo("김");
        assertThat(res.getVipTier()).isEqualTo(VipTier.GOLD);
        assertThat(res.getMiles()).isEqualTo(124500L);
        assertThat(res.getCouponCount()).isEqualTo(3L);
        assertThat(res.getJourneyCount()).isEqualTo(14L);
        assertThat(res.getNextTier()).isEqualTo("PLATINUM");
        assertThat(res.getSettings().getMilesAlert()).isTrue();
    }

    @Test
    @DisplayName("회원 프로필 수정 정상 동작")
    void updateProfile_Success() {
        Member member = Member.builder()
                .id(1L)
                .name("김노마드")
                .englishName("KIM NOMAD")
                .email("vip@herstory.com")
                .phone("010-2456-8890")
                .birthDate("1994-03-08")
                .vipTier(VipTier.VIP)
                .nomadMiles(15000L)
                .build();

        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));

        MyPageDto.UpdateProfileRequest req = new MyPageDto.UpdateProfileRequest("KIM NEW", "new@herstory.com");
        MyPageDto.ProfileResponse res = myPageService.updateProfile(1L, req);

        assertThat(res.getEnglishName()).isEqualTo("KIM NEW");
        assertThat(res.getEmail()).isEqualTo("new@herstory.com");
    }

    @Test
    @DisplayName("결제 수단 등록 정상 동작")
    void addPaymentMethod_Success() {
        Member member = Member.builder().id(1L).name("김노마드").build();
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));

        PaymentMethod saved = PaymentMethod.builder()
                .id(10L)
                .member(member)
                .cardName("HER-STORY 카드")
                .cardNumberMasked("•••• 4412")
                .subtitle("•••• 4412 · 기본 결제")
                .isDefault(true)
                .build();

        when(paymentMethodRepository.save(any(PaymentMethod.class))).thenReturn(saved);

        PaymentMethodDto.AddCardRequest req = new PaymentMethodDto.AddCardRequest("HER-STORY 카드", "1234567812344412", true);
        PaymentMethodDto.PaymentMethodItem res = myPageService.addPaymentMethod(1L, req);

        assertThat(res.getCardId()).isEqualTo(10L);
        assertThat(res.getCardName()).isEqualTo("HER-STORY 카드");
        assertThat(res.getCardNumberMasked()).isEqualTo("•••• 4412");
        assertThat(res.getIsDefault()).isTrue();
    }
}
