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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MyPageService {

    private final MemberRepository memberRepository;
    private final CouponRepository couponRepository;
    private final JourneyRepository journeyRepository;
    private final PaymentMethodRepository paymentMethodRepository;

    @Transactional(readOnly = true)
    public MyPageDto.SummaryResponse getSummary(Long memberId) {
        Member member = findMemberOrFallback(memberId);

        long couponCount = couponRepository.countByMemberIdAndStatus(member.getId(), CouponStatus.AVAILABLE);
        long journeyCount = journeyRepository.countByMemberId(member.getId());

        // Tier progress calculation
        String nextTier = "PLATINUM";
        long milesToNextTier = 0L;
        int tierProgressPercent = 50;

        VipTier currentTier = member.getVipTier();
        long miles = member.getNomadMiles() != null ? member.getNomadMiles() : 0L;

        if (currentTier == VipTier.SILVER) {
            nextTier = "GOLD";
            long target = 5000L;
            milesToNextTier = Math.max(0, target - miles);
            tierProgressPercent = (int) Math.min(100, (miles * 100) / target);
        } else if (currentTier == VipTier.GOLD) {
            nextTier = "PLATINUM";
            long target = 150000L;
            milesToNextTier = Math.max(0, target - miles);
            tierProgressPercent = (int) Math.min(100, Math.max(10, (miles * 100) / target));
        } else if (currentTier == VipTier.PLATINUM) {
            nextTier = "VIP";
            long target = 300000L;
            milesToNextTier = Math.max(0, target - miles);
            tierProgressPercent = (int) Math.min(100, Math.max(20, (miles * 100) / target));
        } else {
            nextTier = "VIP EXCLUSIVE";
            milesToNextTier = 0L;
            tierProgressPercent = 100;
        }

        String initial = (member.getName() != null && !member.getName().isEmpty())
                ? member.getName().substring(0, 1)
                : "H";

        return MyPageDto.SummaryResponse.builder()
                .memberId(member.getId())
                .name(member.getName())
                .initial(initial)
                .email(member.getEmail())
                .vipTier(member.getVipTier())
                .miles(miles)
                .couponCount(couponCount)
                .journeyCount(journeyCount)
                .nextTier(nextTier)
                .milesToNextTier(milesToNextTier)
                .tierProgressPercent(tierProgressPercent)
                .settings(MyPageDto.NotificationSettings.builder()
                        .milesAlert(member.getMilesAlert() != null ? member.getMilesAlert() : true)
                        .journeyAlert(member.getJourneyAlert() != null ? member.getJourneyAlert() : true)
                        .marketingOptIn(member.getMarketingOptIn() != null ? member.getMarketingOptIn() : false)
                        .build())
                .build();
    }

    @Transactional(readOnly = true)
    public MyPageDto.ProfileResponse getProfile(Long memberId) {
        Member member = findMemberOrFallback(memberId);

        return MyPageDto.ProfileResponse.builder()
                .memberId(member.getId())
                .name(member.getName())
                .englishName(member.getEnglishName() != null ? member.getEnglishName() : "KIM NOMAD")
                .email(member.getEmail())
                .phone(member.getPhone() != null ? member.getPhone() : "010-1234-5678")
                .birthDate(member.getBirthDate() != null ? member.getBirthDate() : "1994-03-08")
                .vipTier(member.getVipTier())
                .nomadMiles(member.getNomadMiles())
                .build();
    }

    @Transactional
    public MyPageDto.ProfileResponse updateProfile(Long memberId, MyPageDto.UpdateProfileRequest request) {
        Member member = findMemberOrFallback(memberId);

        if (request.getEnglishName() != null && !request.getEnglishName().isBlank()) {
            member.setEnglishName(request.getEnglishName().trim());
        }
        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            member.setEmail(request.getEmail().trim());
        }

        memberRepository.save(member);
        return getProfile(member.getId());
    }

    @Transactional
    public MyPageDto.NotificationSettings updateSettings(Long memberId, MyPageDto.UpdateSettingsRequest request) {
        Member member = findMemberOrFallback(memberId);

        if (request.getMilesAlert() != null) {
            member.setMilesAlert(request.getMilesAlert());
        }
        if (request.getJourneyAlert() != null) {
            member.setJourneyAlert(request.getJourneyAlert());
        }
        if (request.getMarketingOptIn() != null) {
            member.setMarketingOptIn(request.getMarketingOptIn());
        }

        memberRepository.save(member);
        return MyPageDto.NotificationSettings.builder()
                .milesAlert(member.getMilesAlert())
                .journeyAlert(member.getJourneyAlert())
                .marketingOptIn(member.getMarketingOptIn())
                .build();
    }

    @Transactional
    public boolean changePassword(Long memberId, MyPageDto.ChangePasswordRequest request) {
        Member member = findMemberOrFallback(memberId);

        if (request.getCurrentPassword() == null || !request.getCurrentPassword().equals(member.getPassword())) {
            throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
        }
        if (request.getNewPassword() == null || request.getNewPassword().length() < 8) {
            throw new IllegalArgumentException("새 비밀번호는 8자 이상이어야 합니다.");
        }

        member.setPassword(request.getNewPassword());
        memberRepository.save(member);
        return true;
    }

    @Transactional(readOnly = true)
    public MyPageDto.PassportResponse getPassport(Long memberId) {
        Member member = findMemberOrFallback(memberId);

        String pNum = member.getPassportNumber() != null ? member.getPassportNumber() : "M1234567";
        String pExp = member.getPassportExpiryDate() != null ? member.getPassportExpiryDate() : "2031.04";
        String engName = member.getEnglishName() != null ? member.getEnglishName() : "KIM NOMAD";

        String masked = pNum.length() > 4 ? pNum.substring(0, 4) + "****" : pNum;
        String formatted = "여권 " + pNum + " · " + pExp + " 만료";

        return MyPageDto.PassportResponse.builder()
                .memberId(member.getId())
                .name(member.getName() + " / " + engName)
                .englishName(engName)
                .passportNumber(pNum)
                .maskedPassportNumber(masked)
                .expiryDate(pExp)
                .formattedDetail(formatted)
                .autoFill(member.getAutoFillPassport() != null ? member.getAutoFillPassport() : true)
                .companionCount(0)
                .build();
    }

    @Transactional
    public MyPageDto.PassportResponse updatePassport(Long memberId, MyPageDto.UpdatePassportRequest request) {
        Member member = findMemberOrFallback(memberId);

        if (request.getPassportNumber() != null) {
            member.setPassportNumber(request.getPassportNumber());
        }
        if (request.getExpiryDate() != null) {
            member.setPassportExpiryDate(request.getExpiryDate());
        }
        if (request.getAutoFill() != null) {
            member.setAutoFillPassport(request.getAutoFill());
        }

        memberRepository.save(member);
        return getPassport(member.getId());
    }

    @Transactional(readOnly = true)
    public List<PaymentMethodDto.PaymentMethodItem> getPaymentMethods(Long memberId) {
        Member member = findMemberOrFallback(memberId);
        return paymentMethodRepository.findByMemberIdOrderByIsDefaultDescCreatedAtAsc(member.getId())
                .stream()
                .map(p -> PaymentMethodDto.PaymentMethodItem.builder()
                        .cardId(p.getId())
                        .cardName(p.getCardName())
                        .cardNumberMasked(p.getCardNumberMasked())
                        .subtitle(p.getSubtitle())
                        .isDefault(p.getIsDefault())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional
    public PaymentMethodDto.PaymentMethodItem addPaymentMethod(Long memberId, PaymentMethodDto.AddCardRequest request) {
        Member member = findMemberOrFallback(memberId);

        String rawNumber = request.getCardNumber() != null ? request.getCardNumber().replaceAll("[^0-9]", "") : "0000";
        String last4 = rawNumber.length() >= 4 ? rawNumber.substring(rawNumber.length() - 4) : "0000";
        String masked = "•••• " + last4;
        boolean isDefault = request.getIsDefault() != null && request.getIsDefault();
        String subtitle = isDefault ? masked + " · 기본 결제" : masked;

        PaymentMethod pm = PaymentMethod.builder()
                .member(member)
                .cardName(request.getCardName() != null ? request.getCardName() : "신규 등록 카드")
                .cardNumberMasked(masked)
                .subtitle(subtitle)
                .isDefault(isDefault)
                .build();

        PaymentMethod saved = paymentMethodRepository.save(pm);

        return PaymentMethodDto.PaymentMethodItem.builder()
                .cardId(saved.getId())
                .cardName(saved.getCardName())
                .cardNumberMasked(saved.getCardNumberMasked())
                .subtitle(saved.getSubtitle())
                .isDefault(saved.getIsDefault())
                .build();
    }

    @Transactional
    public boolean deletePaymentMethod(Long memberId, Long cardId) {
        PaymentMethod pm = paymentMethodRepository.findById(cardId)
                .orElseThrow(() -> new IllegalArgumentException("결제 수단을 찾을 수 없습니다. ID: " + cardId));
        if (!pm.getMember().getId().equals(memberId)) {
            throw new IllegalArgumentException("본인의 결제 수단만 삭제할 수 있습니다.");
        }
        paymentMethodRepository.delete(pm);
        return true;
    }

    private Member findMemberOrFallback(Long memberId) {
        if (memberId != null) {
            return memberRepository.findById(memberId)
                    .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다. ID: " + memberId));
        }
        return memberRepository.findByEmail("vip@herstory.com")
                .orElseGet(() -> memberRepository.findAll().stream().findFirst()
                        .orElseThrow(() -> new IllegalArgumentException("존재하는 회원이 없습니다.")));
    }
}
