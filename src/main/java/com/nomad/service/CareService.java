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
    private final com.nomad.domain.product.ProductRepository productRepository;
    private final com.nomad.domain.mileage.MileageHistoryRepository mileageHistoryRepository;

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

        List<com.nomad.domain.product.Product> limited = productRepository.findByCategory(com.nomad.domain.product.ProductCategory.LIMITED_EDITION);
        if (limited.isEmpty()) {
            limited = productRepository.findAll().stream().limit(3).toList();
        } else if (limited.size() > 3) {
            limited = limited.subList(0, 3);
        }

        return CareDto.CareResponse.builder()
                .destination(destination)
                .pushNotificationMessage(pushMessage)
                .visetosSpots(spots)
                .recommendedItems(limited)
                .build();
    }

    @Transactional
    public CareDto.StampResponse checkInCityStamp(CareDto.StampRequest request) {
        Member member = memberRepository.findById(request.getMemberId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다. ID: " + request.getMemberId()));

        int earnedMiles = 1000;
        member.addMiles(earnedMiles);

        String spotName = request.getSpotName() != null ? request.getSpotName() : "Herstory 시암파라곤 럭셔리 부티크";

        mileageHistoryRepository.save(com.nomad.domain.mileage.MileageHistory.builder()
                .member(member)
                .title("시티 스탬프 획득 (" + spotName + ")")
                .amount((long) earnedMiles)
                .type(com.nomad.domain.mileage.MileageType.EARNED_STAMP)
                .balanceAfter(member.getNomadMiles())
                .description("도시 부티크 방문 스탬프 리워드")
                .build());

        return CareDto.StampResponse.builder()
                .memberId(member.getId())
                .spotName(spotName)
                .cityName("Bangkok (방콕)")
                .earnedMiles(earnedMiles)
                .totalMiles(member.getNomadMiles())
                .message("🎉 ['" + spotName + "'] 시티 패스포트 스탬프 획득! 보상 보너스 +" + earnedMiles + " Nomad Miles가 적립되었습니다.")
                .build();
    }

    public CareDto.MyCollectionResponse getMyCollection(Long memberId) {
        Member member = null;
        if (memberId != null) {
            member = memberRepository.findById(memberId).orElse(null);
        }
        if (member == null) {
            member = memberRepository.findByEmail("vip@herstory.com")
                    .orElseGet(() -> memberRepository.findAll().stream().findFirst().orElse(null));
        }

        Long targetMemberId = member != null ? member.getId() : 1L;
        String memberName = member != null ? member.getName() : "김노마드 (VIP)";

        List<CareDto.CollectionItem> items = new java.util.ArrayList<>();

        if (member != null) {
            List<com.nomad.domain.order.Order> orders = orderRepository.findByMemberIdOrderByCreatedAtDesc(member.getId());
            long itemIdSeq = 100L;
            for (com.nomad.domain.order.Order order : orders) {
                if (order.getOrderItems() != null) {
                    for (com.nomad.domain.order.OrderItem orderItem : order.getOrderItems()) {
                        com.nomad.domain.product.Product p = orderItem.getProduct();
                        if (p != null) {
                            items.add(CareDto.CollectionItem.builder()
                                    .itemId(itemIdSeq++)
                                    .productId(p.getId())
                                    .name(p.getName())
                                    .brand(p.getBrand() != null ? p.getBrand() : "HERSTORY")
                                    .category(p.getCategory() != null ? p.getCategory().name() : "LEATHER_GOODS")
                                    .imageUrl(p.getImageUrl())
                                    .purchaseDate(order.getCreatedAt() != null ? order.getCreatedAt().toLocalDate().toString() : "2026.08.10")
                                    .lastCareDate("2026.08.15")
                                    .lastCaredDaysAgo(6)
                                    .careStatus("OPTIMAL")
                                    .careStatusLabel("최적")
                                    .careStatusColor("#44C67C")
                                    .careTip("공항 면세 수령 신규 제품으로 가죽 컨디션이 최상입니다.")
                                    .build());
                        }
                    }
                }
            }
        }

        CareDto.CollectionItem defaultItem1 = CareDto.CollectionItem.builder()
                .itemId(1L)
                .productId(101L)
                .name("비세토스 뮌헨 토트")
                .brand("MCM")
                .category("LEATHER_TOTE")
                .imageUrl("/care/bag-tote.png")
                .purchaseDate("2025.10.14")
                .lastCareDate("2026.08.09")
                .lastCaredDaysAgo(12)
                .careStatus("OPTIMAL")
                .careStatusLabel("최적")
                .careStatusColor("#44C67C")
                .careTip("가죽 밸런스가 매우 양호합니다. 습기 노출만 주의해주세요.")
                .build();

        CareDto.CollectionItem defaultItem2 = CareDto.CollectionItem.builder()
                .itemId(2L)
                .productId(102L)
                .name("아렌 크로스바디")
                .brand("MCM")
                .category("CROSSBODY")
                .imageUrl("/care/bag-crossbody.png")
                .purchaseDate("2025.04.10")
                .lastCareDate("2026.05.28")
                .lastCaredDaysAgo(84)
                .careStatus("CONDITIONING_NEEDED")
                .careStatusLabel("컨디셔닝 필요")
                .careStatusColor("#C64F44")
                .careTip("가죽 표면 유분 감소가 감지되어 가죽 전용 밤 컨디셔닝이 필요합니다.")
                .build();

        CareDto.CollectionItem defaultItem3 = CareDto.CollectionItem.builder()
                .itemId(3L)
                .productId(17L)
                .name("샤넬 클래식 캐비어 플랩백")
                .brand("CHANEL")
                .category("SHOULDER_BAG")
                .imageUrl("https://mcm-nomad-backend.onrender.com/images/products/17_chanel_classic_flap.jpg")
                .purchaseDate("2024.12.24")
                .lastCareDate("2026.07.20")
                .lastCaredDaysAgo(32)
                .careStatus("OPTIMAL")
                .careStatusLabel("최적")
                .careStatusColor("#44C67C")
                .careTip("캐비어 스킨의 엠보싱 상태가 우수합니다.")
                .build();

        CareDto.CollectionItem defaultItem4 = CareDto.CollectionItem.builder()
                .itemId(4L)
                .productId(10L)
                .name("루이비통 모노그램 키폴 50")
                .brand("LOUIS VUITTON")
                .category("TRAVEL_BAG")
                .imageUrl("https://mcm-nomad-backend.onrender.com/images/products/10_lv_keepall_50.jpg")
                .purchaseDate("2024.08.18")
                .lastCareDate("2026.06.15")
                .lastCaredDaysAgo(67)
                .careStatus("CARE_RECOMMENDED")
                .careStatusLabel("전문 케어 권장")
                .careStatusColor("#F59E0B")
                .careTip("핸들 카우하이드 가죽 태닝 및 방수 코팅 점검을 권장합니다.")
                .build();

        if (items.isEmpty()) {
            items.add(defaultItem1);
            items.add(defaultItem2);
            items.add(defaultItem3);
            items.add(defaultItem4);
        } else {
            items.add(defaultItem1);
            items.add(defaultItem2);
        }

        CareDto.CollectionItem featured = CareDto.CollectionItem.builder()
                .itemId(99L)
                .productId(1L)
                .name("비세토스 스타크 백팩")
                .brand("MCM")
                .category("TRAVEL_BACKPACK")
                .imageUrl("https://mcm-nomad-backend.onrender.com/images/products/01_mcm_stark_backpack.jpg")
                .purchaseDate("2026.01.10")
                .lastCareDate("2026.08.18")
                .lastCaredDaysAgo(3)
                .careStatus("OPTIMAL")
                .careStatusLabel("최적")
                .careStatusColor("#44C67C")
                .careTip("열대성 고온다습 기후 대응 방수 왁싱 가공 상태 우수")
                .build();

        return CareDto.MyCollectionResponse.builder()
                .memberId(targetMemberId)
                .memberName(memberName)
                .totalCount(items.size())
                .featuredItem(featured)
                .items(items)
                .build();
    }
}

