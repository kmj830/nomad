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
import com.nomad.dto.FlightDto;
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

    @Mock
    private OpenAiService openAiService;

    @Mock
    private VisionOcrService visionOcrService;

    @Mock
    private FlightService flightService;

    @InjectMocks
    private JourneyService journeyService;


    @Test
    @DisplayName("보딩패스 OCR 스캔 및 여정 정상 등록")
    void scanBoardingPass_Success() {
        Member member = Member.builder().id(1L).email("test@herstory.com").name("테스트").vipTier(VipTier.VIP).build();
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(weatherService.fetchDestinationWeather(any())).thenReturn(WeatherService.WeatherData.builder()
                .temperature(28.0)
                .weatherDescription("Sunny")
                .isRainy(false)
                .build());

        Journey savedJourney = Journey.builder()
                .id(10L)
                .member(member)
                .pnr("HST123")
                .origin("ICN (인천국제공항)")
                .destination("BKK (방콕 수완나품)")
                .flightStatus(FlightStatus.SCHEDULED)
                .build();

        when(journeyRepository.save(any(Journey.class))).thenReturn(savedJourney);

        JourneyDto.ScanRequest req = new JourneyDto.ScanRequest(1L, "HST123", "BOARDING PASS PNR HST123", "ICN", "BKK");
        JourneyDto.ScanResponse res = journeyService.scanBoardingPass(req);

        assertThat(res.getJourneyId()).isEqualTo(10L);
        assertThat(res.getPnr()).isEqualTo("HST123");
        assertThat(res.getOrigin()).contains("ICN");
    }

    @Test
    @DisplayName("존재하지 않는 회원의 경우 스캔 실패 예외 발생")
    void scanBoardingPass_MemberNotFound() {
        when(memberRepository.findById(99L)).thenReturn(Optional.empty());

        JourneyDto.ScanRequest req = new JourneyDto.ScanRequest(99L, "HST123", "RAW", "ICN", "BKK");
        assertThatThrownBy(() -> journeyService.scanBoardingPass(req))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("존재하지 않는 회원입니다");
    }

    @Test
    @DisplayName("여정 분석 및 기후 추천 데이터 반환")
    void analyzeJourney_Success() {
        Member member = Member.builder().id(1L).vipTier(VipTier.VIP).build();
        Journey journey = Journey.builder()
                .id(10L)
                .member(member)
                .pnr("OZ741")
                .destination("BKK (방콕 수완나품)")
                .destinationWeather("Tropical Wet Season")
                .recommendationReason("방수 전용 제품 추천")
                .build();

        Product product = Product.builder()
                .id(1L)
                .name("럭셔리 방수 백팩")
                .category(ProductCategory.WATERPROOF)
                .price(new BigDecimal("1250000.00"))
                .build();

        when(journeyRepository.findById(10L)).thenReturn(Optional.of(journey));
        when(weatherService.fetchDestinationWeather(any())).thenReturn(WeatherService.WeatherData.builder()
                .temperature(31.0)
                .weatherDescription("Rainy and humid")
                .isRainy(true)
                .build());
        when(productRepository.findAll()).thenReturn(List.of(product));
        when(openAiService.generatePersonalizedStylingAdvice(any(), any(), any(), any()))
                .thenReturn("방수 전용 비세토스 백팩 스타일링 제안");
        when(flightService.getFlightInfo(any())).thenReturn(FlightDto.FlightInfoResponse.builder()
                .flightNumber("OZ741")
                .airlineName("아시아나항공")
                .originCode("ICN")
                .originTerminal("제2여객터미널")
                .scheduledDepartureFormatted("오후 7:35")
                .scheduledArrivalFormatted("오후 11:35")
                .flightDuration("6시간 0분")
                .build());

        JourneyDto.JourneyAnalysisResponse response = journeyService.analyzeJourney(10L);

        assertThat(response.getJourneyId()).isEqualTo(10L);
        assertThat(response.getRecommendedProducts()).hasSize(1);
        assertThat(response.getWeatherInfo()).isEqualTo("Rainy and humid");
    }
}


