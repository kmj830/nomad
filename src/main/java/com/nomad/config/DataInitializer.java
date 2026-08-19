package com.nomad.config;

import com.nomad.domain.cart.CartItem;
import com.nomad.domain.cart.CartStatus;
import com.nomad.domain.cart.SmartCart;
import com.nomad.domain.cart.SmartCartRepository;
import com.nomad.domain.journey.FlightStatus;
import com.nomad.domain.journey.Journey;
import com.nomad.domain.journey.JourneyRepository;
import com.nomad.domain.member.Member;
import com.nomad.domain.member.MemberRepository;
import com.nomad.domain.member.VipTier;
import com.nomad.domain.order.Order;
import com.nomad.domain.order.OrderItem;
import com.nomad.domain.order.OrderRepository;
import com.nomad.domain.order.OrderStatus;
import com.nomad.domain.product.Product;
import com.nomad.domain.product.ProductCategory;
import com.nomad.domain.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;
    private final JourneyRepository journeyRepository;
    private final SmartCartRepository smartCartRepository;
    private final OrderRepository orderRepository;
    private final com.nomad.domain.mileage.MileageHistoryRepository mileageHistoryRepository;

    @Override
    @Transactional
    public void run(String... args) {
        Member vipMember = null;
        Member goldMember = null;
        Member platinumMember = null;

        if (memberRepository.count() == 0) {
            vipMember = memberRepository.save(Member.builder()
                    .email("vip@herstory.com")
                    .password("Test1234!")
                    .name("김노마드 (VIP)")
                    .phone("010-1234-5678")
                    .vipTier(VipTier.VIP)
                    .nomadMiles(15000L)
                    .build());

            goldMember = memberRepository.save(Member.builder()
                    .email("gold@herstory.com")
                    .password("Test1234!")
                    .name("이여행 (Gold)")
                    .phone("010-9876-5432")
                    .vipTier(VipTier.GOLD)
                    .nomadMiles(4500L)
                    .build());

            platinumMember = memberRepository.save(Member.builder()
                    .email("platinum@herstory.com")
                    .password("Test1234!")
                    .name("박스타 (Platinum)")
                    .phone("010-5555-7777")
                    .vipTier(VipTier.PLATINUM)
                    .nomadMiles(9800L)
                    .build());
        } else {
            vipMember = memberRepository.findAll().stream().findFirst().orElse(null);
        }

        // 1. Force update all existing products to verified Unsplash high-res image URLs
        List<Product> existingProducts = productRepository.findAll();
        for (Product p : existingProducts) {
            if (p.getName().contains("백팩") && p.getBrand().equalsIgnoreCase("MCM")) {
                p.setImageUrl("https://images.unsplash.com/photo-1553062407-98eeb64c6a62?auto=format&fit=crop&w=800&q=80");
            } else if (p.getName().contains("패스포트") && p.getBrand().equalsIgnoreCase("MCM")) {
                p.setImageUrl("https://images.unsplash.com/photo-1544816155-12df9643f363?auto=format&fit=crop&w=800&q=80");
            } else if (p.getName().contains("키폴")) {
                p.setImageUrl("https://images.unsplash.com/photo-1548036328-c9fa89d128fa?auto=format&fit=crop&w=800&q=80");
            } else if (p.getName().contains("샤넬") || p.getName().contains("플랩백")) {
                p.setImageUrl("https://images.unsplash.com/photo-1584917865442-de89df76afd3?auto=format&fit=crop&w=800&q=80");
            } else if (p.getName().contains("에르메스")) {
                p.setImageUrl("https://images.unsplash.com/photo-1627123424574-724758594e93?auto=format&fit=crop&w=800&q=80");
            } else if (p.getName().contains("구찌")) {
                p.setImageUrl("https://images.unsplash.com/photo-1553062407-98eeb64c6a62?auto=format&fit=crop&w=800&q=80");
            } else if (p.getName().contains("프라다")) {
                p.setImageUrl("https://images.unsplash.com/photo-1577733966973-d680bffd2e80?auto=format&fit=crop&w=800&q=80");
            } else if (p.getName().contains("보테가")) {
                p.setImageUrl("https://images.unsplash.com/photo-1607604276583-eef5d076aa5f?auto=format&fit=crop&w=800&q=80");
            } else if (p.getName().contains("블레이저")) {
                p.setImageUrl("https://images.unsplash.com/photo-1594938298603-c8148c4dae35?auto=format&fit=crop&w=800&q=80");
            } else if (p.getName().contains("트렉 팬츠") || p.getName().contains("바지")) {
                p.setImageUrl("https://images.unsplash.com/photo-1624378439575-d8705ad7ae80?auto=format&fit=crop&w=800&q=80");
            } else if (p.getName().contains("로퍼")) {
                p.setImageUrl("https://images.unsplash.com/photo-1533867617858-e7b97e060509?auto=format&fit=crop&w=800&q=80");
            } else if (p.getName().contains("헤드폰") || p.getName().contains("오버이어")) {
                p.setImageUrl("https://images.unsplash.com/photo-1505740420928-5e560c06d30e?auto=format&fit=crop&w=800&q=80");
            } else if (p.getName().contains("트렌치코트")) {
                p.setImageUrl("https://images.unsplash.com/photo-1544441893-675973e31985?auto=format&fit=crop&w=800&q=80");
            } else if (p.getName().contains("스니커즈")) {
                p.setImageUrl("https://images.unsplash.com/photo-1552346154-21d32810aba3?auto=format&fit=crop&w=800&q=80");
            } else if (p.getName().contains("키링")) {
                p.setImageUrl("https://images.unsplash.com/photo-1618221195710-dd6b41faaea6?auto=format&fit=crop&w=800&q=80");
            } else if (p.getName().contains("솔루션") || p.getName().contains("케어")) {
                p.setImageUrl("https://images.unsplash.com/photo-1556228720-195a672e8a03?auto=format&fit=crop&w=800&q=80");
            } else {
                p.setImageUrl("https://images.unsplash.com/photo-1553062407-98eeb64c6a62?auto=format&fit=crop&w=800&q=80");
            }
            productRepository.save(p);
        }

        // 2. Ensure initial catalog if empty
        if (productRepository.count() == 0) {
            Product pMcmBackpack = productRepository.save(Product.builder()
                    .name("MCM 스타크 비세토스 방수 백팩 32")
                    .brand("MCM")
                    .category(ProductCategory.WATERPROOF)
                    .price(new BigDecimal("1250000.00"))
                    .stock(50)
                    .imageUrl("https://images.unsplash.com/photo-1553062407-98eeb64c6a62?auto=format&fit=crop&w=800&q=80")
                    .description("열대성 스콜 및 우천 시 소지품을 완벽 보호하는 하이테크 비세토스 방수 백팩")
                    .isVipExclusive(true)
                    .build());

            Product pLvKeepall = productRepository.save(Product.builder()
                    .name("루이비통 키폴 반둘리에 50 모노그램")
                    .brand("LOUIS VUITTON")
                    .category(ProductCategory.TRAVEL_BAG)
                    .price(new BigDecimal("3360000.00"))
                    .stock(20)
                    .imageUrl("https://images.unsplash.com/photo-1548036328-c9fa89d128fa?auto=format&fit=crop&w=800&q=80")
                    .description("기내 반입이 가능한 럭셔리 여행의 아이콘, 모노그램 캔버스 더플 트래블백")
                    .isVipExclusive(true)
                    .build());
        }

        // 2-1. Ensure 3 Limited Edition Airport Popup Products exist
        if (productRepository.findByCategory(ProductCategory.LIMITED_EDITION).size() < 3) {
            productRepository.save(Product.builder()
                    .name("MCM 스타크 비세토스 에어포트 리미티드 백팩")
                    .brand("MCM")
                    .category(ProductCategory.LIMITED_EDITION)
                    .price(new BigDecimal("1350000.00"))
                    .stock(15)
                    .imageUrl("https://images.unsplash.com/photo-1553062407-98eeb64c6a62?auto=format&fit=crop&w=800&q=80")
                    .description("인천국제공항 T1 한정 팝업 익스클루시브 비세토스 레더 백팩")
                    .isVipExclusive(true)
                    .build());

            productRepository.save(Product.builder()
                    .name("샤넬 클래식 플랩백 에어포트 에디션")
                    .brand("CHANEL")
                    .category(ProductCategory.LIMITED_EDITION)
                    .price(new BigDecimal("14500000.00"))
                    .stock(5)
                    .imageUrl("https://images.unsplash.com/photo-1584917865442-de89df76afd3?auto=format&fit=crop&w=800&q=80")
                    .description("공항 면세 부티크 전용 익스클루시브 캐비어 스킨 플랩백")
                    .isVipExclusive(true)
                    .build());

            productRepository.save(Product.builder()
                    .name("루이비통 호라이즌 55 모노그램 캐리어")
                    .brand("LOUIS VUITTON")
                    .category(ProductCategory.LIMITED_EDITION)
                    .price(new BigDecimal("4200000.00"))
                    .stock(8)
                    .imageUrl("https://images.unsplash.com/photo-1548036328-c9fa89d128fa?auto=format&fit=crop&w=800&q=80")
                    .description("공항 VIP 라운지 팝업 익스클루시브 초경량 럭셔리 롤링 러기지")
                    .isVipExclusive(true)
                    .build());
        }

        // 3. Initial Journey Seed (여정 기본 데이터)
        if (journeyRepository.count() == 0 && vipMember != null) {
            journeyRepository.save(Journey.builder()
                    .member(vipMember)
                    .pnr("HST777")
                    .origin("ICN (인천국제공항)")
                    .destination("BKK (방콕 수완나품)")
                    .departureDateTime(LocalDateTime.now().plusHours(3))
                    .flightStatus(FlightStatus.SCHEDULED)
                    .destinationWeather("열대성 스콜 (기온 32°C, 습도 85%)")
                    .recommendationReason("방콕 고온다습 기후를 위한 방수 럭셔리 컬렉션 추천")
                    .build());
        }

        // 4. Initial SmartCart Seed
        if (smartCartRepository.count() == 0 && vipMember != null) {
            Product p1 = productRepository.findAll().stream().findFirst().orElse(null);
            if (p1 != null) {
                SmartCart cart = smartCartRepository.save(SmartCart.builder()
                        .member(vipMember)
                        .choiceFit(true)
                        .status(CartStatus.IN_CART)
                        .build());

                cart.addItem(CartItem.builder().product(p1).quantity(1).build());
                smartCartRepository.save(cart);
            }
        }

        // 5. Initial Mileage History Seed (프로토타입 디자인 100% 매칭)
        if (mileageHistoryRepository.count() == 0 && vipMember != null) {
            mileageHistoryRepository.save(com.nomad.domain.mileage.MileageHistory.builder()
                    .member(vipMember)
                    .title("인천국제공항 제1여객터미널 부티크")
                    .amount(4200L)
                    .type(com.nomad.domain.mileage.MileageType.EARNED_PURCHASE)
                    .balanceAfter(15000L)
                    .description("면세점 구매 · 10월 14일")
                    .createdAt(LocalDateTime.now().minusDays(2))
                    .build());

            mileageHistoryRepository.save(com.nomad.domain.mileage.MileageHistory.builder()
                    .member(vipMember)
                    .title("항공편 등록 ICN → HND")
                    .amount(1500L)
                    .type(com.nomad.domain.mileage.MileageType.EARNED_FLIGHT)
                    .balanceAfter(10800L)
                    .description("HND에서 ICN · 10월 13일")
                    .createdAt(LocalDateTime.now().minusDays(3))
                    .build());

            mileageHistoryRepository.save(com.nomad.domain.mileage.MileageHistory.builder()
                    .member(vipMember)
                    .title("라운지 이용권 교환")
                    .amount(-3000L)
                    .type(com.nomad.domain.mileage.MileageType.USED_BENEFIT)
                    .balanceAfter(9300L)
                    .description("인천공항 T1 마티나 라운지 · 10월 9일")
                    .createdAt(LocalDateTime.now().minusDays(7))
                    .build());
        }
    }
}

