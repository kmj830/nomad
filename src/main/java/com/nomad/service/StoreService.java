package com.nomad.service;

import com.nomad.domain.cart.CartStatus;
import com.nomad.domain.cart.SmartCart;
import com.nomad.domain.cart.SmartCartRepository;
import com.nomad.domain.member.Member;
import com.nomad.domain.member.MemberRepository;
import com.nomad.domain.store.*;
import com.nomad.dto.StoreDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StoreService {

    private final StoreVisitRepository storeVisitRepository;
    private final MemberRepository memberRepository;
    private final SmartCartRepository smartCartRepository;

    @Transactional
    public StoreDto.CheckInResponse checkIn(StoreDto.CheckInRequest request) {
        Member member = memberRepository.findById(request.getMemberId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다. ID: " + request.getMemberId()));

        Optional<SmartCart> cartOpt = smartCartRepository.findByMemberIdAndStatus(member.getId(), CartStatus.IN_CART);
        boolean choiceFitRequested = cartOpt.map(SmartCart::getChoiceFit).orElse(false);

        CheckInType checkInType = request.getCheckInType();
        if (checkInType == null) {
            checkInType = (request.getQrCode() != null && !request.getQrCode().isBlank()) ? CheckInType.QR : CheckInType.MANUAL;
        }


        // Staff tablet notification status
        boolean assistantNotified = true;

        String welcomeMessage = null;
        if (choiceFitRequested) {
            welcomeMessage = "VIP 피팅 고객입니다! 담당 MCM 매장 어시스턴트 태블릿에 피팅 신청 상품 목록이 전송되었습니다.";
        } else {
            welcomeMessage = "MCM 공항 면세점 방문을 환영합니다! [웰컴 VIP 10% 면세 특별 할인 쿠폰]이 발급되었습니다.";
        }

        StoreVisit visit = StoreVisit.builder()
                .member(member)
                .checkInType(checkInType)
                .checkInStatus(CheckInStatus.COMPLETED)
                .assistantNotified(assistantNotified)
                .purchaseStatus(PurchaseStatus.PENDING_REENTRY)
                .visitedAt(LocalDateTime.now())
                .build();

        StoreVisit savedVisit = storeVisitRepository.save(visit);

        return StoreDto.CheckInResponse.builder()
                .visitId(savedVisit.getId())
                .memberId(member.getId())
                .memberName(member.getName())
                .vipTier(member.getVipTier().name())
                .checkInType(savedVisit.getCheckInType())
                .checkInStatus(savedVisit.getCheckInStatus())
                .assistantNotified(savedVisit.getAssistantNotified())
                .choiceFitRequested(choiceFitRequested)
                .welcomeCouponMessage(welcomeMessage)
                .purchaseStatus(savedVisit.getPurchaseStatus())
                .visitedAt(savedVisit.getVisitedAt())
                .build();
    }

    // Helper for fallback check-in logic
    private CheckInType getCheckInTypeFallback(StoreDto.CheckInRequest request) {
        if (request.getQrCode() != null && !request.getQrCode().isBlank()) {
            return CheckInType.QR;
        }
        return CheckInType.MANUAL;
    }
}
