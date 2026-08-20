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

    @Column(nullable = false)
    private String password;

    private String phone;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private VipTier vipTier = VipTier.SILVER;

    @Builder.Default
    private Long nomadMiles = 0L;

    private String englishName;

    private String birthDate;

    private String passportNumber;

    private String passportExpiryDate;

    @Builder.Default
    private Boolean autoFillPassport = true;

    @Builder.Default
    private Boolean milesAlert = true;

    @Builder.Default
    private Boolean journeyAlert = true;

    @Builder.Default
    private Boolean marketingOptIn = false;

    public void addMiles(long miles) {
        this.nomadMiles += miles;
    }
}
