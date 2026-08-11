package com.nomad.domain.cart;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface SmartCartRepository extends JpaRepository<SmartCart, Long> {
    Optional<SmartCart> findByMemberIdAndStatus(Long memberId, CartStatus status);
}
