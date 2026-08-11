package com.nomad.domain.member;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "members")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String name;

    private String phone;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private VipTier vipTier = VipTier.SILVER;

    @Builder.Default
    private Long nomadMiles = 0L;

    public void addMiles(long miles) {
        this.nomadMiles += miles;
    }
}
