package com.nomad.service;

import com.nomad.domain.journey.FlightStatus;
import com.nomad.domain.journey.Journey;
import com.nomad.domain.journey.JourneyRepository;
import com.nomad.domain.member.Member;
import com.nomad.domain.member.MemberRepository;
import com.nomad.domain.product.Product;
import com.nomad.domain.product.ProductCategory;
import com.nomad.domain.product.ProductRepository;
import com.nomad.dto.JourneyDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JourneyService {

    private final JourneyRepository journeyRepository;
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;
    private final WeatherService weatherService;

    @Transactional
    public JourneyDto.ScanResponse scanBoardingPass(JourneyDto.ScanRequest request) {
        Member member = memberRepository.findById(request.getMemberId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다. ID: " + request.getMemberId()));

        String pnr = request.getPnr();
        if (pnr == null || pnr.isBlank()) {
            pnr = extractPnrFromOcr(request.getRawOcrText());
        }

        String origin = request.getOrigin() != null ? request.getOrigin() : "ICN (인천국제공항)";
        String destination = request.getDestination() != null ? request.getDestination() : "BKK (방콕 수완나품)";

        WeatherService.WeatherData weatherData = weatherService.fetchDestinationWeather(destination);

        Journey journey = Journey.builder()
                .member(member)
                .pnr(pnr)
                .origin(origin)
                .destination(destination)
                .departureDateTime(LocalDateTime.now().plusDays(2))
                .flightStatus(FlightStatus.SCHEDULED)
                .destinationWeather(weatherData.getWeatherDescription())
                .recommendationReason(weatherData.isRainy()
                        ? "목적지 비/습도 기후에 적합한 MCM 방수 비세토스 컬렉션 맞춤 제안"
                        : "목적지 여정 전용 MCM 경량 라이트웨이트 더플백 컬렉션 제안")
                .build();

        Journey saved = journeyRepository.save(journey);

        return JourneyDto.ScanResponse.builder()
                .journeyId(saved.getId())
                .pnr(saved.getPnr())
                .origin(saved.getOrigin())
                .destination(saved.getDestination())
                .departureDateTime(saved.getDepartureDateTime())
                .flightStatus(saved.getFlightStatus())
                .message("보딩패스 OCR 스캔 완료! PNR [" + saved.getPnr() + "] 여정이 등록되었습니다.")
                .build();
    }

    public JourneyDto.JourneyAnalysisResponse analyzeJourney(Long journeyId) {
        Journey journey = journeyRepository.findById(journeyId)
                .orElseThrow(() -> new IllegalArgumentException("여정을 찾을 수 없습니다. ID: " + journeyId));

        WeatherService.WeatherData weather = weatherService.fetchDestinationWeather(journey.getDestination());
        List<Product> allProducts = productRepository.findAll();

        // Smart curation filtering based on weather
        List<Product> recommended;
        if (weather.isRainy()) {
            recommended = allProducts.stream()
                    .filter(p -> p.getCategory() == ProductCategory.WATERPROOF || p.getCategory() == ProductCategory.LEATHER_CARE)
                    .collect(Collectors.toList());
        } else {
            recommended = allProducts;
        }
        if (recommended.isEmpty()) {
            recommended = allProducts;
        }

        return JourneyDto.JourneyAnalysisResponse.builder()
                .journeyId(journey.getId())
                .destination(journey.getDestination())
                .weatherInfo(weather.getWeatherDescription())
                .climateSummary("실시간 글로벌 기상 API 조회 결과: " + weather.getWeatherDescription())
                .recommendationReason(journey.getRecommendationReason() != null ? journey.getRecommendationReason() : "AI 기반 기후 맞춤 큐레이션")
                .recommendedProducts(recommended)
                .build();
    }

    public JourneyDto.LiveCardResponse getLiveCard(Long journeyId) {
        Journey journey = journeyRepository.findById(journeyId)
                .orElseThrow(() -> new IllegalArgumentException("여정을 찾을 수 없습니다. ID: " + journeyId));

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime departure = journey.getDepartureDateTime() != null ? journey.getDepartureDateTime() : now.plusHours(3);
        long remainingMinutes = Math.max(0, Duration.between(now, departure).toMinutes());

        return JourneyDto.LiveCardResponse.builder()
                .journeyId(journey.getId())
                .pnr(journey.getPnr())
                .destination(journey.getDestination())
                .departureDateTime(departure)
                .remainingMinutesToDeparture(remainingMinutes)
                .gate("Gate 24 (T1)")
                .flightStatus(journey.getFlightStatus())
                .loungeLocation("MCM VIP 노마드 라운지 (탑승동 D구역)")
                .loungeWaitTime("대기 시간 5분 미만 (원활)")
                .liveGuideMessage("탑승까지 약 " + remainingMinutes + "분 남아있습니다. 면세점 MCM 부티크에서 사전 신청하신 VIP 피팅을 받아보세요!")
                .build();
    }

    private String extractPnrFromOcr(String rawText) {
        if (rawText != null && rawText.length() >= 6) {
            return rawText.substring(0, 6).toUpperCase();
        }
        return "MCM888";
    }
}

