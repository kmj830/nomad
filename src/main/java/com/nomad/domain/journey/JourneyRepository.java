package com.nomad.domain.journey;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface JourneyRepository extends JpaRepository<Journey, Long> {
    List<Journey> findByMemberIdOrderByDepartureDateTimeDesc(Long memberId);
    long countByMemberId(Long memberId);
    Optional<Journey> findTopByMemberIdOrderByDepartureDateTimeDesc(Long memberId);
    Optional<Journey> findByPnr(String pnr);
}
