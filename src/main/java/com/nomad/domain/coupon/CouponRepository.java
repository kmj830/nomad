package com.nomad.domain.coupon;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, Long> {
    List<Coupon> findByMemberIdOrderByValidUntilAsc(Long memberId);
    List<Coupon> findByMemberIdAndStatus(Long memberId, CouponStatus status);
    long countByMemberIdAndStatus(Long memberId, CouponStatus status);
}
