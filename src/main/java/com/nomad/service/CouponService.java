package com.nomad.service;

import com.nomad.domain.coupon.*;
import com.nomad.domain.member.Member;
import com.nomad.domain.member.MemberRepository;
import com.nomad.dto.CouponDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository couponRepository;
    private final MemberRepository memberRepository;

    @Transactional(readOnly = true)
    public CouponDto.CouponListResponse getMyCoupons(Long memberId) {
        List<Coupon> coupons = couponRepository.findByMemberIdOrderByValidUntilAsc(memberId);

        List<CouponDto.CouponItem> items = coupons.stream()
                .map(c -> CouponDto.CouponItem.builder()
                        .couponId(c.getId())
                        .couponCode(c.getCouponCode())
                        .title(c.getTitle())
                        .subtitle(c.getSubtitle())
                        .category(c.getCategory())
                        .status(c.getStatus())
                        .validUntil(c.getValidUntil())
                        .urgent(c.getIsUrgent() != null ? c.getIsUrgent() : false)
                        .discountRate(c.getDiscountRate())
                        .build())
                .collect(Collectors.toList());

        return CouponDto.CouponListResponse.builder()
                .memberId(memberId)
                .totalCoupons(items.size())
                .items(items)
                .build();
    }

    @Transactional
    public Coupon issueCoupon(Long memberId, String title, String subtitle, CouponCategory category, int validDays, Integer discountRate) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다. ID: " + memberId));

        String code = "CPN-" + System.currentTimeMillis() % 1000000;
        LocalDateTime validUntil = LocalDateTime.now().plusDays(validDays);

        Coupon coupon = Coupon.builder()
                .member(member)
                .couponCode(code)
                .title(title)
                .subtitle(subtitle)
                .category(category)
                .status(CouponStatus.AVAILABLE)
                .validUntil(validUntil)
                .isUrgent(validDays <= 7)
                .discountRate(discountRate)
                .build();

        return couponRepository.save(coupon);
    }
}
