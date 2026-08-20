package com.nomad.service;

import com.nomad.domain.journey.FlightStatus;
import com.nomad.domain.journey.Journey;
import com.nomad.domain.journey.JourneyRepository;
import com.nomad.domain.member.Member;
import com.nomad.domain.member.MemberRepository;
import com.nomad.domain.product.Product;
import com.nomad.domain.product.ProductCategory;
import com.nomad.domain.product.ProductRepository;
import com.nomad.dto.FlightDto;
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
    private final OpenAiService openAiService;
    private final VisionOcrService visionOcrService;
    private final FlightService flightService;

    @Transactional
    public JourneyDto.ScanResponse scanBoardingPass(JourneyDto.ScanRequest request) {
        Member member = memberRepository.findById(request.getMemberId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다. ID: " + request.getMemberId()));

        String pnr = request.getPnr();
        if (pnr == null || pnr.isBlank()) {
            var ocrResult = visionOcrService.processBoardingPassOcr(request.getRawOcrText());
            pnr = ocrResult.getOrDefault("pnr", extractPnrFromOcr(request.getRawOcrText()));
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
                        ? "목적지 비/습도 기후에 적합한 럭셔리 방수 레더 컬렉션 맞춤 제안"
                        : "목적지 여정 전용 럭셔리 경량 라이트웨이트 더플백 컬렉션 제안")
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

        String topProductName = recommended.isEmpty() ? "럭셔리 레더 백팩" : recommended.get(0).getName();
        String aiAdvice = openAiService.generatePersonalizedStylingAdvice(
                journey.getDestination(),
                weather.getWeatherDescription(),
                journey.getMember().getVipTier().name(),
                topProductName
        );

        String rainProb = weather.isRainy() ? "76%" : "20%";

        // Fetch official schedule from FlightService
        FlightDto.FlightInfoResponse flightInfo = flightService.getFlightInfo(journey.getPnr());

        List<JourneyDto.TimelineItem> timeline = List.of(
                JourneyDto.TimelineItem.builder()
                        .stepType("DEPARTURE")
                        .title("인천국제공항 (" + flightInfo.getOriginCode() + ") " + flightInfo.getOriginTerminal() + " 탑승구")
                        .time(flightInfo.getScheduledDepartureFormatted() + " 출발")
                        .description("탑승 전, 2층 면세구역에서 50분 여유가 있습니다.")
                        .tipMessage("면세점 방문 추천: 사전 신청하신 ChoiceFit VIP 피팅 룸을 이용해보세요.")
                        .iconType("AIRPLANE_DEPARTURE")
                        .build(),
                JourneyDto.TimelineItem.builder()
                        .stepType("IN_FLIGHT")
                        .title("비행중 (" + flightInfo.getFlightNumber() + " / " + flightInfo.getAirlineName() + ")")
                        .time(flightInfo.getFlightDuration() + " 비행")
                        .description("목적지 상공 기류 안정, 편안한 비행 되세요.")
                        .tipMessage("기내 좌석에서 면세 상품 추가 주문 및 도착지 가죽 케어 예약 가능")
                        .iconType("AIRPLANE_IN_FLIGHT")
                        .build(),
                JourneyDto.TimelineItem.builder()
                        .stepType("ARRIVAL")
                        .title(journey.getDestination() + " 도착")
                        .time(flightInfo.getScheduledArrivalFormatted() + " 도착 예정")
                        .description("우천 대비 방수 트렌치 코트 및 픽업 상품을 확인하세요.")
                        .tipMessage("목적지 현지 럭셔리 Care Desk 위치가 지도에 표시됩니다.")
                        .iconType("AIRPLANE_ARRIVAL")
                        .build()
        );

        return JourneyDto.JourneyAnalysisResponse.builder()
                .journeyId(journey.getId())
                .destination(journey.getDestination())
                .weatherInfo(weather.getWeatherDescription())
                .rainProbability(rainProb)
                .climateSummary("실시간 글로벌 기상 위성 관측 결과: " + weather.getWeatherDescription())
                .recommendationReason(aiAdvice)
                .timeline(timeline)
                .recommendedProducts(recommended)
                .build();
    }

    public JourneyDto.LiveCardResponse getLiveCard(Long journeyId) {
        Journey journey = journeyRepository.findById(journeyId)
                .orElseThrow(() -> new IllegalArgumentException("여정을 찾을 수 없습니다. ID: " + journeyId));

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime departure = journey.getDepartureDateTime() != null ? journey.getDepartureDateTime() : now.plusHours(3);
        long remainingMinutes = Math.max(0, Duration.between(now, departure).toMinutes());

        // Dynamic step calculation based on remaining time
        String step = "CHECK_IN";
        String stepLabel = "체크인 진행 중";
        int estSecurity = 25;

        if (remainingMinutes <= 30) {
            step = "BOARDING";
            stepLabel = "탑승 중 (Gate 마감 임박)";
        } else if (remainingMinutes <= 90) {
            step = "SECURITY_CHECK";
            stepLabel = "게이트 이동 중";
        } else if (remainingMinutes <= 180) {
            step = "SECURITY_CHECK";
            stepLabel = "보안검색 대기 중";
        }

        return JourneyDto.LiveCardResponse.builder()
                .journeyId(journey.getId())
                .pnr(journey.getPnr())
                .flightNumber(journey.getPnr() != null ? journey.getPnr() : "JL92")
                .origin(journey.getOrigin() != null ? journey.getOrigin() : "ICN")
                .destination(journey.getDestination())
                .departureDateTime(departure)
                .remainingMinutesToDeparture(remainingMinutes)
                .gate("G-12")
                .flightStatus(journey.getFlightStatus())
                .currentStep(step)
                .currentStepLabel(stepLabel)
                .estimatedSecurityMinutes(estSecurity)
                .loungeLocation("인천공항 라운지")
                .loungeGateLocation("터미널 2, 게이트 16번 맞은편")
                .loungeWalkingMinutes(15)
                .loungeWaitMinutes(3)
                .loungeWaitTime("라운지 대기시간 3분")
                .liveGuideMessage("탑승까지 약 " + remainingMinutes + "분 남아있습니다. 공항 면세 부티크에서 사전 신청하신 ChoiceFit VIP 피팅을 받아보세요!")
                .build();
    }

    public JourneyDto.JourneyResponse getJourney(Long journeyId) {
        Journey journey = journeyRepository.findById(journeyId)
                .orElseThrow(() -> new IllegalArgumentException("여정을 찾을 수 없습니다. ID: " + journeyId));

        return JourneyDto.JourneyResponse.builder()
                .journeyId(journey.getId())
                .memberId(journey.getMember().getId())
                .memberName(journey.getMember().getName())
                .pnr(journey.getPnr())
                .origin(journey.getOrigin())
                .destination(journey.getDestination())
                .departureDateTime(journey.getDepartureDateTime())
                .flightStatus(journey.getFlightStatus())
                .destinationWeather(journey.getDestinationWeather())
                .recommendationReason(journey.getRecommendationReason())
                .build();
    }

    public JourneyDto.MyJourneysResponse getMyJourneys(Long memberId) {
        List<Journey> journeys = journeyRepository.findByMemberIdOrderByDepartureDateTimeDesc(memberId);

        List<JourneyDto.JourneySummaryItem> items = journeys.stream()
                .map(j -> JourneyDto.JourneySummaryItem.builder()
                        .journeyId(j.getId())
                        .pnr(j.getPnr())
                        .origin(j.getOrigin())
                        .destination(j.getDestination())
                        .departureDateTime(j.getDepartureDateTime())
                        .flightStatus(j.getFlightStatus())
                        .destinationWeather(j.getDestinationWeather())
                        .build())
                .collect(Collectors.toList());

        return JourneyDto.MyJourneysResponse.builder()
                .memberId(memberId)
                .totalJourneys(items.size())
                .journeys(items)
                .build();
    }

    @Transactional
    public JourneyDto.DeleteResponse deleteJourney(Long journeyId) {
        Journey journey = journeyRepository.findById(journeyId)
                .orElseThrow(() -> new IllegalArgumentException("여정을 찾을 수 없습니다. ID: " + journeyId));

        journeyRepository.delete(journey);

        return JourneyDto.DeleteResponse.builder()
                .journeyId(journeyId)
                .success(true)
                .message("여정(PNR: " + journey.getPnr() + ")이 성공적으로 취소/삭제되었습니다.")
                .build();
    }

    private String extractPnrFromOcr(String rawText) {
        if (rawText != null && rawText.length() >= 6) {
            return rawText.substring(0, 6).toUpperCase();
        }
        return "HST888";
    }
}

