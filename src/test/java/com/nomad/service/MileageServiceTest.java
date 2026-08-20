package com.nomad.service;

import com.nomad.domain.member.Member;
import com.nomad.domain.member.MemberRepository;
import com.nomad.domain.member.VipTier;
import com.nomad.domain.mileage.MileageHistory;
import com.nomad.domain.mileage.MileageHistoryRepository;
import com.nomad.domain.mileage.MileageType;
import com.nomad.dto.MileageDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MileageServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private MileageHistoryRepository mileageHistoryRepository;

    @Mock
    private com.nomad.domain.coupon.CouponRepository couponRepository;

    @InjectMocks
    private MileageService mileageService;

    @Test
    @DisplayName("마일리지 잔액 및 소멸 예정 정보 조회 정상 동작")
    void getBalance_Success() {
        Member member = Member.builder().id(1L).name("김노마드").vipTier(VipTier.VIP).nomadMiles(15000L).build();
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));

        MileageDto.BalanceResponse res = mileageService.getBalance(1L);

        assertThat(res.getMemberId()).isEqualTo(1L);
        assertThat(res.getTotalMiles()).isEqualTo(15000L);
        assertThat(res.getExpiringMiles()).isEqualTo(3200L);
    }

    @Test
    @DisplayName("마일리지 상세 히스토리 내역 조회")
    void getHistory_Success() {
        Member member = Member.builder().id(1L).name("김노마드").vipTier(VipTier.VIP).nomadMiles(15000L).build();
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));

        MileageHistory h1 = MileageHistory.builder()
                .id(10L)
                .member(member)
                .title("면세 구매 적립")
                .amount(4200L)
                .type(MileageType.EARNED_PURCHASE)
                .balanceAfter(15000L)
                .createdAt(LocalDateTime.now())
                .build();

        when(mileageHistoryRepository.findByMemberIdOrderByCreatedAtDesc(1L)).thenReturn(List.of(h1));

        MileageDto.HistoryResponse res = mileageService.getHistory(1L);

        assertThat(res.getTotalMiles()).isEqualTo(15000L);
        assertThat(res.getItems()).hasSize(1);
        assertThat(res.getItems().get(0).getTitle()).isEqualTo("면세 구매 적립");
        assertThat(res.getItems().get(0).getAmount()).isEqualTo(4200L);
    }

    @Test
    @DisplayName("마일리지 직접 사용 - 잔액 차감 및 히스토리 기록")
    void useMiles_Success() {
        Member member = Member.builder().id(1L).name("김노마드").vipTier(VipTier.VIP).nomadMiles(10000L).build();
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));

        MileageDto.UseMilesRequest req = new MileageDto.UseMilesRequest(1L, 3000L, "프리미엄 라운지 이용", "T1 마티나 라운지");
        MileageDto.UseMilesResponse res = mileageService.useMiles(req);

        assertThat(res.getUsedMiles()).isEqualTo(3000L);
        assertThat(res.getRemainingMiles()).isEqualTo(7000L);
        assertThat(member.getNomadMiles()).isEqualTo(7000L);
        verify(mileageHistoryRepository).save(any(MileageHistory.class));
    }

    @Test
    @DisplayName("마일리지 타 회원 양도 - 송신자 차감 & 수신자 적립")
    void transferMiles_Success() {
        Member from = Member.builder().id(1L).name("김노마드").email("vip@herstory.com").vipTier(VipTier.VIP).nomadMiles(10000L).build();
        Member to = Member.builder().id(2L).name("이여행").email("gold@herstory.com").vipTier(VipTier.GOLD).nomadMiles(5000L).build();

        when(memberRepository.findById(1L)).thenReturn(Optional.of(from));
        when(memberRepository.findByEmail("gold@herstory.com")).thenReturn(Optional.of(to));

        MileageDto.TransferRequest req = new MileageDto.TransferRequest(1L, "gold@herstory.com", null, 2000L);
        MileageDto.TransferResponse res = mileageService.transferMiles(req);

        assertThat(res.getTransferredMiles()).isEqualTo(2000L);
        assertThat(from.getNomadMiles()).isEqualTo(8000L);
        assertThat(to.getNomadMiles()).isEqualTo(7000L);
    }

    @Test
    @DisplayName("마일리지 VIP 혜택 교환 - 라운지 이용권 쿠폰 발급")
    void redeemBenefit_LoungePass() {
        Member member = Member.builder().id(1L).name("김노마드").vipTier(VipTier.VIP).nomadMiles(10000L).build();
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));

        MileageDto.RedeemBenefitRequest req = new MileageDto.RedeemBenefitRequest(1L, "LOUNGE_PASS", 3000L);
        MileageDto.RedeemBenefitResponse res = mileageService.redeemBenefit(req);

        assertThat(res.getBenefitCode()).isEqualTo("LOUNGE_PASS");
        assertThat(res.getCouponCode()).startsWith("HST-CPN-");
        assertThat(res.getUsedMiles()).isEqualTo(3000L);
        assertThat(res.getRemainingMiles()).isEqualTo(7000L);
    }
}
