package com.nomad.domain.coupon;

import com.nomad.domain.member.Member;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "coupons")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false)
    private String couponCode;

    @Column(nullable = false)
    private String title;

    private String subtitle;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CouponCategory category;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private CouponStatus status = CouponStatus.AVAILABLE;

    private LocalDateTime validUntil;

    @Builder.Default
    private Boolean isUrgent = false;

    private Integer discountRate; // e.g. 10 for 10%

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    public boolean isExpired() {
        return validUntil != null && LocalDateTime.now().isAfter(validUntil);
    }
}
