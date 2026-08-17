package com.nomad.service;

import com.nomad.domain.journey.Journey;
import com.nomad.domain.journey.JourneyRepository;
import com.nomad.domain.member.Member;
import com.nomad.domain.member.MemberRepository;
import com.nomad.domain.order.OrderRepository;
import com.nomad.dto.CareDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CareService {

    private final JourneyRepository journeyRepository;
    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final GoogleMapsService googleMapsService;

    public CareDto.CareResponse getVisetosSpots(Long memberId) {
        return getVisetosSpots(memberId, "ALL");
    }

    public CareDto.CareResponse getVisetosSpots(Long memberId, String brand) {
        String destination = "BKK (방콕 수완나품)";
        if (memberId != null) {
            destination = journeyRepository.findTopByMemberIdOrderByDepartureDateTimeDesc(memberId)
                    .map(Journey::getDestination)
                    .orElse("BKK (방콕 수완나품)");
        }

        boolean hasPurchased = memberId != null && !orderRepository.findByMemberIdOrderByCreatedAtDesc(memberId).isEmpty();

        String pushMessage = hasPurchased
                ? "[" + destination + " 도착 알림] Herstory 럭셔리 제품을 구매해주셔서 감사합니다! 목적지 기후에 맞는 명품 가죽 케어 가이드와 현지 전 브랜드 Care Desk 위치를 확인하세요."
                : "[" + destination + " 도착 알림] 목적지 현지 글로벌 럭셔리 부티크(샤넬, LV, 구찌, MCM, 에르메스 등) 및 공항 Care Desk 안내입니다.";

        List<CareDto.VisetosSpot> spots = googleMapsService.findSpotsWithMaps(destination, brand);

        return CareDto.CareResponse.builder()
                .destination(destination)
                .pushNotificationMessage(pushMessage)
                .visetosSpots(spots)
                .build();
    }

    @Transactional
    public CareDto.StampResponse checkInCityStamp(CareDto.StampRequest request) {
        Member member = memberRepository.findById(request.getMemberId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다. ID: " + request.getMemberId()));

        int earnedMiles = 1000;
        member.addMiles(earnedMiles);

        String spotName = request.getSpotName() != null ? request.getSpotName() : "MCM 시암파라곤 플래그십 스토어";

        return CareDto.StampResponse.builder()
                .memberId(member.getId())
                .spotName(spotName)
                .cityName("Bangkok (방콕)")
                .earnedMiles(earnedMiles)
                .totalMiles(member.getNomadMiles())
                .message("🎉 ['" + spotName + "'] 시티 패스포트 스탬프 획득! 보상 보너스 +" + earnedMiles + " Nomad Miles가 적립되었습니다.")
                .build();
    }
}

