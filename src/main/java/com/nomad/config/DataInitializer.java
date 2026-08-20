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
    private final com.nomad.domain.coupon.CouponRepository couponRepository;
    private final com.nomad.domain.member.PaymentMethodRepository paymentMethodRepository;

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
                    .englishName("KIM NOMAD")
                    .birthDate("1994-03-08")
                    .passportNumber("M1234567")
                    .passportExpiryDate("2031.04")
                    .autoFillPassport(true)
                    .phone("010-2456-8890")
                    .vipTier(VipTier.VIP)
                    .nomadMiles(124500L)
                    .milesAlert(true)
                    .journeyAlert(true)
                    .marketingOptIn(false)
                    .build());

            goldMember = memberRepository.save(Member.builder()
                    .email("gold@herstory.com")
                    .password("Test1234!")
                    .name("이여행 (Gold)")
                    .englishName("LEE TRAVEL")
                    .birthDate("1996-07-15")
                    .passportNumber("M7654321")
                    .passportExpiryDate("2030.11")
                    .autoFillPassport(true)
                    .phone("010-9876-5432")
                    .vipTier(VipTier.GOLD)
                    .nomadMiles(4500L)
                    .milesAlert(true)
                    .journeyAlert(true)
                    .marketingOptIn(true)
                    .build());

            platinumMember = memberRepository.save(Member.builder()
                    .email("platinum@herstory.com")
                    .password("Test1234!")
                    .name("박스타 (Platinum)")
                    .englishName("PARK STAR")
                    .birthDate("1992-12-01")
                    .passportNumber("M9988776")
                    .passportExpiryDate("2032.08")
                    .autoFillPassport(true)
                    .phone("010-5555-7777")
                    .vipTier(VipTier.PLATINUM)
                    .nomadMiles(9800L)
                    .milesAlert(true)
                    .journeyAlert(true)
                    .marketingOptIn(false)
                    .build());
        } else {
            vipMember = memberRepository.findAll().stream().findFirst().orElse(null);
        }

        // 1. Force update all existing products to verified Unsplash high-res image URLs
        List<Product> existingProducts = productRepository.findAll();
        for (Product p : existingProducts) {
            if (p.getName().contains("판초") || p.getName().contains("커버") || p.getName().contains("트렌치코트")) {
                p.setImageUrl("https://images.unsplash.com/photo-1544441893-675973e31985?auto=format&fit=crop&w=800&q=80");
            } else if (p.getName().contains("솔루션") || p.getName().contains("케어") || p.getName().contains("실드")) {
                p.setImageUrl("https://images.unsplash.com/photo-1556228720-195a672e8a03?auto=format&fit=crop&w=800&q=80");
            } else if (p.getName().contains("슬링백") || p.getName().contains("크로스바디")) {
                p.setImageUrl("https://images.unsplash.com/photo-1553062407-98eeb64c6a62?auto=format&fit=crop&w=800&q=80");
            } else if (p.getName().contains("백팩")) {
                p.setImageUrl("https://images.unsplash.com/photo-1553062407-98eeb64c6a62?auto=format&fit=crop&w=800&q=80");
            } else if (p.getName().contains("패스포트") || p.getName().contains("여권") || p.getName().contains("월렛") || p.getName().contains("홀더")) {
                p.setImageUrl("https://images.unsplash.com/photo-1544816155-12df9643f363?auto=format&fit=crop&w=800&q=80");
            } else if (p.getName().contains("파우치") || p.getName().contains("워시백")) {
                p.setImageUrl("https://images.unsplash.com/photo-1577733966973-d680bffd2e80?auto=format&fit=crop&w=800&q=80");
            } else if (p.getName().contains("키폴") || p.getName().contains("더플백") || p.getName().contains("위켄더") || p.getName().contains("보스턴백") || p.getName().contains("카빈백") || p.getName().contains("캐리어")) {
                p.setImageUrl("https://images.unsplash.com/photo-1548036328-c9fa89d128fa?auto=format&fit=crop&w=800&q=80");
            } else if (p.getName().contains("샤넬") || p.getName().contains("플랩백") || p.getName().contains("22")) {
                p.setImageUrl("https://images.unsplash.com/photo-1584917865442-de89df76afd3?auto=format&fit=crop&w=800&q=80");
            } else if (p.getName().contains("에르메스") || p.getName().contains("타막")) {
                p.setImageUrl("https://images.unsplash.com/photo-1627123424574-724758594e93?auto=format&fit=crop&w=800&q=80");
            } else if (p.getName().contains("구찌")) {
                p.setImageUrl("https://images.unsplash.com/photo-1553062407-98eeb64c6a62?auto=format&fit=crop&w=800&q=80");
            } else if (p.getName().contains("프라다")) {
                p.setImageUrl("https://images.unsplash.com/photo-1577733966973-d680bffd2e80?auto=format&fit=crop&w=800&q=80");
            } else if (p.getName().contains("보테가") || p.getName().contains("카세트")) {
                p.setImageUrl("https://images.unsplash.com/photo-1607604276583-eef5d076aa5f?auto=format&fit=crop&w=800&q=80");
            } else if (p.getName().contains("블레이저")) {
                p.setImageUrl("https://images.unsplash.com/photo-1594938298603-c8148c4dae35?auto=format&fit=crop&w=800&q=80");
            } else if (p.getName().contains("트렉 팬츠") || p.getName().contains("바지")) {
                p.setImageUrl("https://images.unsplash.com/photo-1624378439575-d8705ad7ae80?auto=format&fit=crop&w=800&q=80");
            } else if (p.getName().contains("로퍼")) {
                p.setImageUrl("https://images.unsplash.com/photo-1533867617858-e7b97e060509?auto=format&fit=crop&w=800&q=80");
            } else if (p.getName().contains("헤드폰") || p.getName().contains("오버이어")) {
                p.setImageUrl("https://images.unsplash.com/photo-1505740420928-5e560c06d30e?auto=format&fit=crop&w=800&q=80");
            } else if (p.getName().contains("스니커즈")) {
                p.setImageUrl("https://images.unsplash.com/photo-1552346154-21d32810aba3?auto=format&fit=crop&w=800&q=80");
            } else if (p.getName().contains("키링")) {
                p.setImageUrl("https://images.unsplash.com/photo-1618221195710-dd6b41faaea6?auto=format&fit=crop&w=800&q=80");
            } else {
                p.setImageUrl("https://images.unsplash.com/photo-1553062407-98eeb64c6a62?auto=format&fit=crop&w=800&q=80");
            }
            productRepository.save(p);
        }

        // 2. Ensure 20 diverse luxury brand products exist
        ensureProduct("MCM 스타크 비세토스 방수 백팩 32", "MCM", ProductCategory.WATERPROOF,
                new BigDecimal("1250000.00"), 50,
                "https://images.unsplash.com/photo-1553062407-98eeb64c6a62?auto=format&fit=crop&w=800&q=80",
                "열대성 스콜 및 우천 시 소지품을 완벽 보호하는 하이테크 비세토스 방수 백팩", true);

        ensureProduct("MCM 비세토스 모노그램 위켄더 더플백 45", "MCM", ProductCategory.TRAVEL_BAG,
                new BigDecimal("1980000.00"), 30,
                "https://images.unsplash.com/photo-1548036328-c9fa89d128fa?auto=format&fit=crop&w=800&q=80",
                "아이코닉 코냑 비세토스 캔버스와 24K 도금 하드웨어로 완성된 클래식 여행 가방", true);

        ensureProduct("MCM 스타크 비세토스 에어포트 리미티드 백팩", "MCM", ProductCategory.LIMITED_EDITION,
                new BigDecimal("1350000.00"), 15,
                "https://images.unsplash.com/photo-1553062407-98eeb64c6a62?auto=format&fit=crop&w=800&q=80",
                "인천국제공항 T1 한정 팝업 익스클루시브 비세토스 레더 백팩", true);

        ensureProduct("프라다 리나일론 테크니컬 방수 레인 판초", "PRADA", ProductCategory.WATERPROOF,
                new BigDecimal("1980000.00"), 40,
                "https://images.unsplash.com/photo-1544441893-675973e31985?auto=format&fit=crop&w=800&q=80",
                "열대성 스콜과 우천 시 완벽한 방수를 자랑하는 프라다 시그니처 친환경 리나일론 케이프", true);

        ensureProduct("프라다 사피아노 레더 트래블 토일레트리 파우치", "PRADA", ProductCategory.ACCESSORY,
                new BigDecimal("1150000.00"), 45,
                "https://images.unsplash.com/photo-1577733966973-d680bffd2e80?auto=format&fit=crop&w=800&q=80",
                "스크래치와 수분에 강한 사피아노 가죽으로 제작된 기내 및 호텔 전용 럭셔리 워시백 파우치", false);

        ensureProduct("보테가 베네타 인트레치아토 레더 실드 케어 키트", "BOTTEGA VENETA", ProductCategory.LEATHER_CARE,
                new BigDecimal("180000.00"), 100,
                "https://images.unsplash.com/photo-1556228720-195a672e8a03?auto=format&fit=crop&w=800&q=80",
                "고습도 환경에서 가죽의 수분 침투를 막고 은은한 광택을 유지하는 프리미엄 나노 코팅 에센스 세트", false);

        ensureProduct("보테가 베네타 인트레치아토 카세트 크로스바디백", "BOTTEGA VENETA", ProductCategory.ACCESSORY,
                new BigDecimal("3250000.00"), 25,
                "https://images.unsplash.com/photo-1607604276583-eef5d076aa5f?auto=format&fit=crop&w=800&q=80",
                "부드러운 나파 가죽을 엮어 완성한 시그니처 카세트백으로 공항과 도심 이동 시 최적의 수납감 제공", true);

        ensureProduct("구찌 GG 방수 코팅 캔버스 슬링백", "GUCCI", ProductCategory.WATERPROOF,
                new BigDecimal("1420000.00"), 35,
                "https://images.unsplash.com/photo-1553062407-98eeb64c6a62?auto=format&fit=crop&w=800&q=80",
                "열대 스콜 속에서도 소지품과 여권을 쾌적하게 보호하는 GG 수프림 발수 코팅 슬링백", false);

        ensureProduct("구찌 사보이 모노그램 미디엄 더플백", "GUCCI", ProductCategory.TRAVEL_BAG,
                new BigDecimal("2890000.00"), 20,
                "https://images.unsplash.com/photo-1548036328-c9fa89d128fa?auto=format&fit=crop&w=800&q=80",
                "헤리티지 웹 스트라이프와 GG 수프림 캔버스가 돋보이는 럭셔리 주말 트래블 더플백", true);

        ensureProduct("루이비통 키폴 반둘리에 50 워터프루프 트래블백", "LOUIS VUITTON", ProductCategory.WATERPROOF,
                new BigDecimal("3580000.00"), 20,
                "https://images.unsplash.com/photo-1548036328-c9fa89d128fa?auto=format&fit=crop&w=800&q=80",
                "방수 지퍼와 심실링 테이핑이 적용되어 악천후에도 안전한 모노그램 캔버스 더플 트래블백", true);

        ensureProduct("루이비통 크리스토퍼 PM 모노그램 백팩", "LOUIS VUITTON", ProductCategory.BACKPACK,
                new BigDecimal("4450000.00"), 15,
                "https://images.unsplash.com/photo-1553062407-98eeb64c6a62?auto=format&fit=crop&w=800&q=80",
                "고급스러운 모노그램 캔버스와 넉넉한 수납공간을 갖춘 하이엔드 여행용 백팩", true);

        ensureProduct("루이비통 호라이즌 55 모노그램 캐리어", "LOUIS VUITTON", ProductCategory.LIMITED_EDITION,
                new BigDecimal("4200000.00"), 8,
                "https://images.unsplash.com/photo-1548036328-c9fa89d128fa?auto=format&fit=crop&w=800&q=80",
                "공항 VIP 라운지 팝업 익스클루시브 초경량 럭셔리 롤링 러기지", true);

        ensureProduct("에르메스 타막 카프스킨 트래블 패스포트 홀더", "HERMÈS", ProductCategory.ACCESSORY,
                new BigDecimal("720000.00"), 30,
                "https://images.unsplash.com/photo-1627123424574-724758594e93?auto=format&fit=crop&w=800&q=80",
                "최고급 엡송 송아지 가죽과 방수 안감이 적용되어 보딩패스와 여권을 품격 있게 보호", false);

        ensureProduct("에르메스 에르백 집 50 방수 캔버스 카빈백", "HERMÈS", ProductCategory.TRAVEL_BAG,
                new BigDecimal("4650000.00"), 10,
                "https://images.unsplash.com/photo-1627123424574-724758594e93?auto=format&fit=crop&w=800&q=80",
                "발수 가공 오피서 캔버스와 바레니아 레더 플랩이 결합된 기내 반입용 프리미엄 트래블백", true);

        ensureProduct("디올 새들 테크니컬 오블리크 트래블 백팩", "DIOR", ProductCategory.BACKPACK,
                new BigDecimal("4100000.00"), 25,
                "https://images.unsplash.com/photo-1553062407-98eeb64c6a62?auto=format&fit=crop&w=800&q=80",
                "발수 가공 오블리크 자카드와 알루미늄 버클이 돋보이는 럭셔리 트래블 백팩", true);

        ensureProduct("디올 오블리크 자카드 레더 케어 에센스 세트", "DIOR", ProductCategory.LEATHER_CARE,
                new BigDecimal("210000.00"), 60,
                "https://images.unsplash.com/photo-1556228720-195a672e8a03?auto=format&fit=crop&w=800&q=80",
                "디올 아틀리에 특화 가죽 클리닝 솔루션과 나노 발수 프로텍트 밤 세트", false);

        ensureProduct("샤넬 클래식 플랩백 에어포트 에디션", "CHANEL", ProductCategory.LIMITED_EDITION,
                new BigDecimal("14500000.00"), 5,
                "https://images.unsplash.com/photo-1584917865442-de89df76afd3?auto=format&fit=crop&w=800&q=80",
                "공항 면세 부티크 전용 익스클루시브 캐비어 스킨 플랩백", true);

        ensureProduct("샤넬 22 카프스킨 스몰 트래블 백", "CHANEL", ProductCategory.READY_TO_WEAR,
                new BigDecimal("8220000.00"), 12,
                "https://images.unsplash.com/photo-1584917865442-de89df76afd3?auto=format&fit=crop&w=800&q=80",
                "유연한 유광 카프스킨과 골드 메탈 참 장식이 돋보이는 모던 럭셔리 트래블 숄더백", true);

        ensureProduct("버버리 켄싱턴 헤리티지 방수 트렌치코트", "BURBERRY", ProductCategory.WATERPROOF,
                new BigDecimal("3390000.00"), 20,
                "https://images.unsplash.com/photo-1544441893-675973e31985?auto=format&fit=crop&w=800&q=80",
                "비바람을 완벽 차단하는 토마스 버버리 개버딘 코튼과 빈티지 체크 안감의 시그니처 레인코트", true);

        ensureProduct("고야드 보잉 45 방수 고야딘 캔버스 더플백", "GOYARD", ProductCategory.TRAVEL_BAG,
                new BigDecimal("4850000.00"), 15,
                "https://images.unsplash.com/photo-1548036328-c9fa89d128fa?auto=format&fit=crop&w=800&q=80",
                "천연 고무 코팅 처리된 고야딘 캔버스로 탁월한 경량성과 방수 기능을 제공하는 아이코닉 더플백", true);

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

        // 6. Initial Coupons Seed (MyPage 프로토타입 100% 매칭)
        if (couponRepository.count() == 0 && vipMember != null) {
            couponRepository.save(com.nomad.domain.coupon.Coupon.builder()
                    .member(vipMember)
                    .couponCode("DUTYFREE-10PCT")
                    .title("면세점 10% 할인")
                    .subtitle("12월 31일까지 · 인천 T1/T2")
                    .category(com.nomad.domain.coupon.CouponCategory.DISCOUNT)
                    .status(com.nomad.domain.coupon.CouponStatus.AVAILABLE)
                    .validUntil(LocalDateTime.now().plusMonths(4))
                    .isUrgent(false)
                    .discountRate(10)
                    .build());

            couponRepository.save(com.nomad.domain.coupon.Coupon.builder()
                    .member(vipMember)
                    .couponCode("LOUNGE-FREE-PASS")
                    .title("라운지 1회 무료")
                    .subtitle("3월 15일까지")
                    .category(com.nomad.domain.coupon.CouponCategory.LOUNGE)
                    .status(com.nomad.domain.coupon.CouponStatus.AVAILABLE)
                    .validUntil(LocalDateTime.now().plusMonths(7))
                    .isUrgent(false)
                    .build());

            couponRepository.save(com.nomad.domain.coupon.Coupon.builder()
                    .member(vipMember)
                    .couponCode("VIP-FITTING-PRIORITY")
                    .title("VIP 피팅 우선 예약")
                    .subtitle("만료 임박 · 8월 31일까지")
                    .category(com.nomad.domain.coupon.CouponCategory.VIP_FITTING)
                    .status(com.nomad.domain.coupon.CouponStatus.AVAILABLE)
                    .validUntil(LocalDateTime.now().plusDays(11))
                    .isUrgent(true)
                    .build());
        }

        // 7. Initial Payment Methods Seed (MyPage 프로토타입 100% 매칭)
        if (paymentMethodRepository.count() == 0 && vipMember != null) {
            paymentMethodRepository.save(com.nomad.domain.member.PaymentMethod.builder()
                    .member(vipMember)
                    .cardName("HER-STORY 카드")
                    .cardNumberMasked("•••• 4412")
                    .subtitle("•••• 4412 · 기본 결제")
                    .isDefault(true)
                    .build());

            paymentMethodRepository.save(com.nomad.domain.member.PaymentMethod.builder()
                    .member(vipMember)
                    .cardName("신한카드")
                    .cardNumberMasked("•••• 8890")
                    .subtitle("•••• 8890")
                    .isDefault(false)
                    .build());
        }

        // 8. Additional Journey History Seed
        if (journeyRepository.count() <= 1 && vipMember != null) {
            journeyRepository.save(Journey.builder()
                    .member(vipMember)
                    .pnr("HST888")
                    .origin("ICN (인천국제공항)")
                    .destination("HND (도쿄 하네다)")
                    .departureDateTime(LocalDateTime.now().minusDays(7))
                    .flightStatus(FlightStatus.COMPLETED)
                    .destinationWeather("맑음 (기온 26°C, 습도 45%)")
                    .recommendationReason("도쿄 쾌적한 도시 여행을 위한 럭셔리 레더 컬렉션")
                    .build());

            journeyRepository.save(Journey.builder()
                    .member(vipMember)
                    .pnr("HST999")
                    .origin("ICN (인천국제공항)")
                    .destination("CDG (파리 샤를 드골)")
                    .departureDateTime(LocalDateTime.now().minusMonths(1))
                    .flightStatus(FlightStatus.COMPLETED)
                    .destinationWeather("선선함 (기온 21°C, 습도 50%)")
                    .recommendationReason("파리 패션위크 전용 트래블 럭셔리 라인업")
                    .build());
        }
    }

    private void ensureProduct(String name, String brand, ProductCategory category,
                               BigDecimal price, Integer stock, String imageUrl,
                               String description, Boolean isVipExclusive) {
        if (!productRepository.existsByName(name)) {
            productRepository.save(Product.builder()
                    .name(name)
                    .brand(brand)
                    .category(category)
                    .price(price)
                    .stock(stock)
                    .imageUrl(imageUrl)
                    .description(description)
                    .isVipExclusive(isVipExclusive)
                    .build());
        }
    }
}


