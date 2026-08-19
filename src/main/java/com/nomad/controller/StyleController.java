package com.nomad.controller;

import com.nomad.domain.product.Product;
import com.nomad.domain.product.ProductRepository;
import com.nomad.dto.CareDto;
import com.nomad.dto.JourneyDto;
import com.nomad.service.CareService;
import com.nomad.service.JourneyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "2-2. Style API (프론트엔드 스타일 엔진 & 팝업 스팟 연동)", description = "프론트엔드 styleApi 전용 큐레이션 추천 및 공항/현지 팝업 스팟 엔드포인트")
@RestController
@RequestMapping("/api/v1/style")
@RequiredArgsConstructor
public class StyleController {

    private final ProductRepository productRepository;
    private final CareService careService;
    private final JourneyService journeyService;

    @Operation(summary = "공항 및 목적지 팝업 스팟 목록 조회", description = "럭셔리 플래그십 스토어, 공항 면세 부티크 및 Care Desk 스팟 목록을 반환합니다.")
    @GetMapping({"/popup-spots", "/popup-spot"})
    public ResponseEntity<CareDto.CareResponse> getPopupSpots(@RequestParam(required = false) Long memberId) {
        Long targetMemberId = memberId != null ? memberId : 1L;
        return ResponseEntity.ok(careService.getVisetosSpots(targetMemberId));
    }

    @Operation(summary = "목적지 맞춤 스타일 엔진 상품 추천 목록", description = "여정 기후에 맞춘 추천 상품 리스트를 반환합니다.")
    @GetMapping({"/recommendations", "/{journeyId}/recommendations"})
    public ResponseEntity<List<Product>> getRecommendations(@PathVariable(required = false) Long journeyId) {
        Long targetJourneyId = journeyId != null ? journeyId : 1L;
        JourneyDto.JourneyAnalysisResponse analysis = journeyService.analyzeJourney(targetJourneyId);
        return ResponseEntity.ok(analysis.getRecommendedProducts());
    }

    @Operation(summary = "공항 팝업 스팟 추천 상품 3종 목록 (배열)", description = "공항 팝업 스팟 화면에서 추천할 LIMITED_EDITION 한정판 상품 3종 리스트를 배열로 반환합니다.")
    @GetMapping({"/popup-items", "/popup-products", "/popup-spots/items", "/popup-spots/products", "/airport-items"})
    public ResponseEntity<List<Product>> getPopupItems() {
        List<Product> limited = productRepository.findByCategory(com.nomad.domain.product.ProductCategory.LIMITED_EDITION);
        if (limited.isEmpty()) {
            limited = productRepository.findAll().stream().limit(3).toList();
        } else if (limited.size() > 3) {
            limited = limited.subList(0, 3);
        }
        return ResponseEntity.ok(limited);
    }
}
