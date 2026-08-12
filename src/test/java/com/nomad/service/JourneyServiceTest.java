package com.nomad.service;

import com.nomad.domain.journey.FlightStatus;
import com.nomad.domain.journey.Journey;
import com.nomad.domain.journey.JourneyRepository;
import com.nomad.domain.member.Member;
import com.nomad.domain.member.MemberRepository;
import com.nomad.domain.member.VipTier;
import com.nomad.domain.product.Product;
import com.nomad.domain.product.ProductCategory;
import com.nomad.domain.product.ProductRepository;
import com.nomad.dto.JourneyDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JourneyServiceTest {

    @Mock
    private JourneyRepository journeyRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private WeatherService weatherService;

    @InjectMocks
    private JourneyService journeyService;


    @Test
    @DisplayName("보딩패스 OCR 스캔 및 여정 정상 등록")
    void scanBoardingPass_Success() {
        Member member = Member.builder().id(1L).email("test@mcm.com").name("테스트").vipTier(VipTier.VIP).build();
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(weatherService.fetchDestinationWeather(any())).thenReturn(WeatherService.WeatherData.builder()
                .cityName("Bangkok").temperature(30.0).humidity(80.0).isRainy(true).weatherDescription("Bangkok 현지 기후").build());

        Journey savedJourney = Journey.builder()
                .id(10L)
                .member(member)
                .pnr("MCM123")
                .origin("ICN (인천국제공항)")
                .destination("BKK (방콕 수완나품)")
                .departureDateTime(LocalDateTime.now().plusDays(2))
                .flightStatus(FlightStatus.SCHEDULED)
                .build();

        when(journeyRepository.save(any(Journey.class))).thenReturn(savedJourney);

        JourneyDto.ScanRequest req = new JourneyDto.ScanRequest(1L, "MCM123", "BOARDING PASS PNR MCM123", "ICN", "BKK");
        JourneyDto.ScanResponse res = journeyService.scanBoardingPass(req);

        assertThat(res.getJourneyId()).isEqualTo(10L);
        assertThat(res.getPnr()).isEqualTo("MCM123");
        assertThat(res.getOrigin()).contains("ICN");
    }

    @Test
    @DisplayName("존재하지 않는 회원의 경우 스캔 실패 예외 발생")
    void scanBoardingPass_MemberNotFound() {
        when(memberRepository.findById(99L)).thenReturn(Optional.empty());

        JourneyDto.ScanRequest req = new JourneyDto.ScanRequest(99L, "MCM123", "RAW", "ICN", "BKK");
        assertThatThrownBy(() -> journeyService.scanBoardingPass(req))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("존재하지 않는 회원입니다");
    }

    @Test
    @DisplayName("여정 분석 및 기후 추천 데이터 반환")
    void analyzeJourney_Success() {
        Journey journey = Journey.builder()
                .id(10L)
                .destination("BKK (방콕 수완나품)")
                .destinationWeather("Tropical Wet Season")
                .recommendationReason("방수 전용 제품 추천")
                .build();

        Product product = Product.builder()
                .id(1L)
                .name("MCM Visetos 방수 백팩")
                .category(ProductCategory.WATERPROOF)
                .price(new BigDecimal("1250000.00"))
                .build();

        when(journeyRepository.findById(10L)).thenReturn(Optional.of(journey));
        when(productRepository.findAll()).thenReturn(List.of(product));
        when(weatherService.fetchDestinationWeather(any())).thenReturn(WeatherService.WeatherData.builder()
                .cityName("Bangkok").temperature(30.0).humidity(80.0).isRainy(true).weatherDescription("Tropical Wet Season").build());

        JourneyDto.JourneyAnalysisResponse response = journeyService.analyzeJourney(10L);

        assertThat(response.getJourneyId()).isEqualTo(10L);
        assertThat(response.getRecommendedProducts()).hasSize(1);
        assertThat(response.getWeatherInfo()).isEqualTo("Tropical Wet Season");
    }
}

