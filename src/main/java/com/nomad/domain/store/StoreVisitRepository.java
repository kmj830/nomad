package com.nomad.domain.store;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface StoreVisitRepository extends JpaRepository<StoreVisit, Long> {
    List<StoreVisit> findByMemberIdOrderByVisitedAtDesc(Long memberId);
    Optional<StoreVisit> findTopByMemberIdOrderByVisitedAtDesc(Long memberId);
}
