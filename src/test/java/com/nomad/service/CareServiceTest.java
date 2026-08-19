package com.nomad.service;

import com.nomad.domain.journey.Journey;
import com.nomad.domain.journey.JourneyRepository;
import com.nomad.domain.order.Order;
import com.nomad.domain.order.OrderRepository;
import com.nomad.dto.CareDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CareServiceTest {

    @Mock
    private JourneyRepository journeyRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private com.nomad.domain.member.MemberRepository memberRepository;

    @Mock
    private GoogleMapsService googleMapsService;

    @Mock
    private com.nomad.domain.product.ProductRepository productRepository;

    @InjectMocks
    private CareService careService;

    @Test
    @DisplayName("귀국/목적지 도착 후 비세토스 스팟 및 가죽 케어 푸시 메시지 제공")
    void getVisetosSpots_PurchasedUser() {
        Journey journey = Journey.builder().id(1L).destination("BKK (방콕 수완나품)").build();

        when(journeyRepository.findTopByMemberIdOrderByDepartureDateTimeDesc(1L)).thenReturn(Optional.of(journey));
        when(orderRepository.findByMemberIdOrderByCreatedAtDesc(1L)).thenReturn(List.of(Order.builder().id(10L).build()));
        when(googleMapsService.findSpotsWithMaps(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.anyString()))
                .thenReturn(List.of(
                        CareDto.VisetosSpot.builder().spotName("CHANEL Siam Paragon").brand("CHANEL").build(),
                        CareDto.VisetosSpot.builder().spotName("LV Gaysorn Amarin").brand("LOUIS VUITTON").build()
                ));

        CareDto.CareResponse response = careService.getVisetosSpots(1L);

        assertThat(response.getDestination()).isEqualTo("BKK (방콕 수완나품)");
        assertThat(response.getPushNotificationMessage()).contains("구매해주셔서 감사합니다");
        assertThat(response.getVisetosSpots()).hasSize(2);
    }

    @Test
    @DisplayName("시티 패스포트 스탬프 획득 및 보너스 마일리지 적립")
    void checkInCityStamp_Success() {
        com.nomad.domain.member.Member member = com.nomad.domain.member.Member.builder()
                .id(1L).name("김노마드").nomadMiles(5000L).build();

        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));

        CareDto.StampRequest req = new CareDto.StampRequest(1L, "Herstory 방콕 시암파라곤");
        CareDto.StampResponse res = careService.checkInCityStamp(req);

        assertThat(res.getMemberId()).isEqualTo(1L);
        assertThat(res.getEarnedMiles()).isEqualTo(1000);
        assertThat(res.getTotalMiles()).isEqualTo(6000L);
        assertThat(res.getMessage()).contains("시티 패스포트 스탬프 획득!");
    }
}

