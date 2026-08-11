package com.nomad.service;

import com.nomad.domain.journey.Journey;
import com.nomad.domain.journey.JourneyRepository;
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

    public CareDto.CareResponse getVisetosSpots(Long memberId) {
        String destination = journeyRepository.findTopByMemberIdOrderByDepartureDateTimeDesc(memberId)
                .map(Journey::getDestination)
                .orElse("BKK (방콕 수완나품)");

        boolean hasPurchased = !orderRepository.findByMemberIdOrderByCreatedAtDesc(memberId).isEmpty();

        String pushMessage = hasPurchased
                ? "[" + destination + " 도착 알림] MCM 제품을 구매해주셔서 감사합니다! 목적지의 고온다습 기후 조건에 맞는 비세토스 가죽 케어 가이드와 현지 Care Desk 위치를 확인하세요."
                : "[" + destination + " 도착 알림] 목적지 현지 MCM 플래그십 스토어 및 Care Desk 안내입니다.";

        List<CareDto.VisetosSpot> spots = List.of(
                CareDto.VisetosSpot.builder()
                        .spotName("MCM 방콕 시암파라곤 플래그십 스토어")
                        .address("Siam Paragon, M Floor, Bangkok 10330")
                        .locationType("Flagship Store & VIP Lounge")
                        .latitude(13.7460)
                        .longitude(100.5348)
                        .careServiceAvailable("가죽 스팀 케어, 워터프루프 코팅 케어, 방수 커버 제공")
                        .build(),
                CareDto.VisetosSpot.builder()
                        .spotName("MCM 수완나품 공항 면세 Care Desk")
                        .address("Suvarnabhumi Airport Departure Hall, Concourse D")
                        .locationType("Airport Care Desk")
                        .latitude(13.6900)
                        .longitude(100.7501)
                        .careServiceAvailable("긴급 가죽 왁싱, 여권지갑 리페어")
                        .build()
        );

        return CareDto.CareResponse.builder()
                .destination(destination)
                .pushNotificationMessage(pushMessage)
                .visetosSpots(spots)
                .build();
    }
}
