package com.nomad.service;

import com.nomad.domain.journey.FlightStatus;
import com.nomad.domain.journey.Journey;
import com.nomad.domain.journey.JourneyRepository;
import com.nomad.domain.member.Member;
import com.nomad.domain.member.MemberRepository;
import com.nomad.domain.product.Product;
import com.nomad.domain.product.ProductRepository;
import com.nomad.dto.JourneyDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JourneyService {

    private final JourneyRepository journeyRepository;
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;

    @Transactional
    public JourneyDto.ScanResponse scanBoardingPass(JourneyDto.ScanRequest request) {
        Member member = memberRepository.findById(request.getMemberId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다. ID: " + request.getMemberId()));

        String pnr = request.getPnr();
        if (pnr == null || pnr.isBlank()) {
            // OCR PNR extraction simulation
            pnr = extractPnrFromOcr(request.getRawOcrText());
        }

        String origin = request.getOrigin() != null ? request.getOrigin() : "ICN (인천국제공항)";
        String destination = request.getDestination() != null ? request.getDestination() : "BKK (방콕 수완나품)";

        Journey journey = Journey.builder()
                .member(member)
                .pnr(pnr)
                .origin(origin)
                .destination(destination)
                .departureDateTime(LocalDateTime.now().plusDays(2))
                .flightStatus(FlightStatus.SCHEDULED)
                .destinationWeather("Tropical Wet Season (강수확률 85%, 습도 88%)")
                .recommendationReason("목적지 방콕의 스콜 및 높은 습도 기후 조건 고려 분석됨")
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

        List<Product> recommendedProducts = productRepository.findAll();

        return JourneyDto.JourneyAnalysisResponse.builder()
                .journeyId(journey.getId())
                .destination(journey.getDestination())
                .weatherInfo(journey.getDestinationWeather())
                .climateSummary("목적지 기후 분석 결과: 열대성 스콜 및 습함. 방수 제품 및 원터치 큐레이션 추천.")
                .recommendationReason(journey.getRecommendationReason())
                .recommendedProducts(recommendedProducts)
                .build();
    }

    private String extractPnrFromOcr(String rawText) {
        if (rawText != null && rawText.length() >= 6) {
            return rawText.substring(0, 6).toUpperCase();
        }
        return "MCM888";
    }
}
