package com.nomad.domain.store;

import com.nomad.domain.member.Member;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "store_visits")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoreVisit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CheckInType checkInType;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private CheckInStatus checkInStatus = CheckInStatus.COMPLETED;

    @Builder.Default
    private Boolean assistantNotified = false;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private PurchaseStatus purchaseStatus = PurchaseStatus.PENDING_REENTRY;

    @Builder.Default
    private LocalDateTime visitedAt = LocalDateTime.now();
}
