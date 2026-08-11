package com.nomad.domain.journey;

import com.nomad.domain.member.Member;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "journeys")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Journey {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false, length = 10)
    private String pnr;

    @Column(nullable = false)
    private String origin;

    @Column(nullable = false)
    private String destination;

    private LocalDateTime departureDateTime;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private FlightStatus flightStatus = FlightStatus.SCHEDULED;

    private String destinationWeather;

    private String recommendationReason;
}
