package com.nomad.domain.member;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "payment_methods")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentMethod {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false)
    private String cardName; // 예: "HER-STORY 카드", "신한카드"

    @Column(nullable = false)
    private String cardNumberMasked; // 예: "•••• 4412"

    private String subtitle; // 예: "•••• 4412 · 기본 결제"

    @Builder.Default
    private Boolean isDefault = false;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
