package com.nomad.domain.mileage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MileageHistoryRepository extends JpaRepository<MileageHistory, Long> {
    List<MileageHistory> findByMemberIdOrderByCreatedAtDesc(Long memberId);
}
