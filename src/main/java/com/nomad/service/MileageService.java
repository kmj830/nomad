package com.nomad.service;

import com.nomad.domain.member.Member;
import com.nomad.domain.member.MemberRepository;
import com.nomad.domain.mileage.MileageHistory;
import com.nomad.domain.mileage.MileageHistoryRepository;
import com.nomad.domain.mileage.MileageType;
import com.nomad.dto.MileageDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MileageService {

    private final MemberRepository memberRepository;
    private final MileageHistoryRepository mileageHistoryRepository;
    private final com.nomad.domain.coupon.CouponRepository couponRepository;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy.MM.dd");

    public MileageDto.BalanceResponse getBalance(Long memberId) {
        Member member = getMember(memberId);
        return MileageDto.BalanceResponse.builder()
                .memberId(member.getId())
                .memberName(member.getName())
                .vipTier(member.getVipTier().name())
                .totalMiles(member.getNomadMiles())
                .expiringMiles(Math.min(member.getNomadMiles(), 3200L))
                .expiringDate("2026.12.31 만료 예정")
                .build();
    }

    public MileageDto.HistoryResponse getHistory(Long memberId) {
        Member member = getMember(memberId);
        List<MileageHistory> histories = mileageHistoryRepository.findByMemberIdOrderByCreatedAtDesc(memberId);

        List<MileageDto.HistoryItem> items = histories.stream()
                .map(h -> MileageDto.HistoryItem.builder()
                        .historyId(h.getId())
                        .title(h.getTitle())
                        .amount(h.getAmount())
                        .type(h.getType())
                        .balanceAfter(h.getBalanceAfter())
                        .createdAt(h.getCreatedAt())
                        .formattedDate(h.getCreatedAt().format(DATE_FORMATTER))
                        .build())
                .toList();

        return MileageDto.HistoryResponse.builder()
                .memberId(member.getId())
                .memberName(member.getName())
                .vipTier(member.getVipTier().name())
                .totalMiles(member.getNomadMiles())
                .expiringMiles(Math.min(member.getNomadMiles(), 3200L))
                .expiringDate("2026.12.31 만료 예정")
                .items(items)
                .build();
    }

    @Transactional
    public MileageDto.UseMilesResponse useMiles(MileageDto.UseMilesRequest request) {
        Member member = getMember(request.getMemberId());
        long amount = request.getAmount();

        if (amount <= 0) {
            throw new IllegalArgumentException("사용할 마일리지는 1 이상이어야 합니다.");
        }

        if (member.getNomadMiles() < amount) {
            throw new IllegalArgumentException("보유 마일리지가 부족합니다. 현재 잔액: " + member.getNomadMiles() + " 마일");
        }

        member.setNomadMiles(member.getNomadMiles() - amount);
        memberRepository.save(member);

        String title = request.getTitle() != null && !request.getTitle().isBlank() 
                ? request.getTitle() 
                : "마일리지 사용";

        MileageHistory history = MileageHistory.builder()
                .member(member)
                .title(title)
                .amount(-amount)
                .type(MileageType.USED_BENEFIT)
                .balanceAfter(member.getNomadMiles())
                .description(request.getDescription())
                .build();
        mileageHistoryRepository.save(history);

        return MileageDto.UseMilesResponse.builder()
                .memberId(member.getId())
                .usedMiles(amount)
                .remainingMiles(member.getNomadMiles())
                .title(title)
                .message("마일리지 " + amount + "M 사용이 성공적으로 완료되었습니다.")
                .build();
    }

    @Transactional
    public MileageDto.TransferResponse transferMiles(MileageDto.TransferRequest request) {
        Member fromMember = getMember(request.getFromMemberId());
        long amount = request.getAmount();

        if (amount <= 0) {
            throw new IllegalArgumentException("양도할 마일리지는 1 이상이어야 합니다.");
        }

        if (fromMember.getNomadMiles() < amount) {
            throw new IllegalArgumentException("보유 마일리지가 부족합니다. 현재 잔액: " + fromMember.getNomadMiles() + " 마일");
        }

        Member toMember;
        if (request.getToEmail() != null && !request.getToEmail().isBlank()) {
            toMember = memberRepository.findByEmail(request.getToEmail().trim())
                    .orElseThrow(() -> new IllegalArgumentException("양도받을 회원을 찾을 수 없습니다. 이메일: " + request.getToEmail()));
        } else if (request.getToMemberId() != null) {
            toMember = getMember(request.getToMemberId());
        } else {
            throw new IllegalArgumentException("양도받을 회원의 이메일 또는 회원 ID를 입력해주세요.");
        }

        if (fromMember.getId().equals(toMember.getId())) {
            throw new IllegalArgumentException("본인에게는 마일리지를 양도할 수 없습니다.");
        }

        // 차감 (From)
        fromMember.setNomadMiles(fromMember.getNomadMiles() - amount);
        memberRepository.save(fromMember);

        MileageHistory fromHistory = MileageHistory.builder()
                .member(fromMember)
                .title("마일리지 양도 (to: " + toMember.getName() + ")")
                .amount(-amount)
                .type(MileageType.TRANSFERRED_OUT)
                .balanceAfter(fromMember.getNomadMiles())
                .description("회원 [" + toMember.getEmail() + "]님에게 마일리지 양도 완료")
                .build();
        mileageHistoryRepository.save(fromHistory);

        // 적립 (To)
        toMember.setNomadMiles(toMember.getNomadMiles() + amount);
        memberRepository.save(toMember);

        MileageHistory toHistory = MileageHistory.builder()
                .member(toMember)
                .title("마일리지 양도 수신 (from: " + fromMember.getName() + ")")
                .amount(amount)
                .type(MileageType.TRANSFERRED_IN)
                .balanceAfter(toMember.getNomadMiles())
                .description("회원 [" + fromMember.getName() + "]님으로부터 마일리지 선물 수신")
                .build();
        mileageHistoryRepository.save(toHistory);

        return MileageDto.TransferResponse.builder()
                .fromMemberId(fromMember.getId())
                .fromMemberName(fromMember.getName())
                .toMemberId(toMember.getId())
                .toMemberName(toMember.getName())
                .transferredMiles(amount)
                .remainingMiles(fromMember.getNomadMiles())
                .message("[" + toMember.getName() + "]님에게 " + amount + " Nomad Miles 양도가 성공적으로 완료되었습니다.")
                .build();
    }

    @Transactional
    public MileageDto.RedeemBenefitResponse redeemBenefit(MileageDto.RedeemBenefitRequest request) {
        Member member = getMember(request.getMemberId());

        String benefitName;
        long requiredMiles;

        String code = request.getBenefitCode() != null ? request.getBenefitCode().toUpperCase() : "LOUNGE_PASS";
        switch (code) {
            case "LOUNGE_PASS" -> {
                benefitName = "인천공항 T1 프리미엄 VIP 라운지 1회 무료 이용권";
                requiredMiles = request.getRequiredMiles() != null ? request.getRequiredMiles() : 3000L;
            }
            case "VIP_FITTING" -> {
                benefitName = "글로벌 부티크 VIP 프라이빗 피팅룸 1:1 의전 서비스";
                requiredMiles = request.getRequiredMiles() != null ? request.getRequiredMiles() : 5000L;
            }
            case "LEATHER_CARE_KIT" -> {
                benefitName = "Herstory 시그니처 럭셔리 레더 케어 에센셜 키트 교환권";
                requiredMiles = request.getRequiredMiles() != null ? request.getRequiredMiles() : 4000L;
            }
            case "AIRPORT_PICKUP" -> {
                benefitName = "도착지 공항 프리미엄 리무진 픽업/샌딩 혜택";
                requiredMiles = request.getRequiredMiles() != null ? request.getRequiredMiles() : 8000L;
            }
            default -> {
                benefitName = "VIP 스페셜 혜택 교환";
                requiredMiles = request.getRequiredMiles() != null ? request.getRequiredMiles() : 3000L;
            }
        }

        if (member.getNomadMiles() < requiredMiles) {
            throw new IllegalArgumentException("보유 마일리지가 부족합니다. 필요 마일리지: " + requiredMiles + "M (현재 잔액: " + member.getNomadMiles() + "M)");
        }

        member.setNomadMiles(member.getNomadMiles() - requiredMiles);
        memberRepository.save(member);

        String couponCode = "HST-CPN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        MileageHistory history = MileageHistory.builder()
                .member(member)
                .title(benefitName + " 교환")
                .amount(-requiredMiles)
                .type(MileageType.USED_BENEFIT)
                .balanceAfter(member.getNomadMiles())
                .description("쿠폰코드: " + couponCode)
                .build();
        mileageHistoryRepository.save(history);

        // Save to Coupon table for MyPage Coupon Wallet
        com.nomad.domain.coupon.CouponCategory couponCategory = switch (code) {
            case "LOUNGE_PASS" -> com.nomad.domain.coupon.CouponCategory.LOUNGE;
            case "VIP_FITTING" -> com.nomad.domain.coupon.CouponCategory.VIP_FITTING;
            case "LEATHER_CARE_KIT" -> com.nomad.domain.coupon.CouponCategory.LEATHER_CARE;
            case "AIRPORT_PICKUP" -> com.nomad.domain.coupon.CouponCategory.AIRPORT_PICKUP;
            default -> com.nomad.domain.coupon.CouponCategory.DISCOUNT;
        };

        couponRepository.save(com.nomad.domain.coupon.Coupon.builder()
                .member(member)
                .couponCode(couponCode)
                .title(benefitName)
                .subtitle("마일리지 교환 혜택 · 유효기간 30일")
                .category(couponCategory)
                .status(com.nomad.domain.coupon.CouponStatus.AVAILABLE)
                .validUntil(java.time.LocalDateTime.now().plusDays(30))
                .isUrgent(false)
                .build());

        return MileageDto.RedeemBenefitResponse.builder()
                .memberId(member.getId())
                .benefitCode(code)
                .benefitName(benefitName)
                .couponCode(couponCode)
                .usedMiles(requiredMiles)
                .remainingMiles(member.getNomadMiles())
                .message("🎉 [" + benefitName + "] 혜택 신청이 완료되었습니다! (발급 쿠폰: " + couponCode + ")")
                .build();
    }

    private Member getMember(Long memberId) {
        if (memberId != null) {
            return memberRepository.findById(memberId)
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다. ID: " + memberId));
        }
        return memberRepository.findByEmail("vip@herstory.com")
                .orElseGet(() -> memberRepository.findAll().stream().findFirst()
                        .orElseThrow(() -> new IllegalArgumentException("존재하는 회원이 없습니다.")));
    }
}
