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

    @Override
    public void run(String... args) {
        Member vipMember = null;
        Member goldMember = null;
        Member platinumMember = null;

        if (memberRepository.count() == 0) {
            vipMember = memberRepository.save(Member.builder()
                    .email("vip@mcmworldwide.com")
                    .password("1234")
                    .name("김노마드 (VIP)")
                    .phone("010-1234-5678")
                    .vipTier(VipTier.VIP)
                    .nomadMiles(15000L)
                    .build());

            goldMember = memberRepository.save(Member.builder()
                    .email("gold@mcmworldwide.com")
                    .password("1234")
                    .name("이여행 (Gold)")
                    .phone("010-9876-5432")
                    .vipTier(VipTier.GOLD)
                    .nomadMiles(4500L)
                    .build());

            platinumMember = memberRepository.save(Member.builder()
                    .email("platinum@mcmworldwide.com")
                    .password("1234")
                    .name("박스타 (Platinum)")
                    .phone("010-5555-7777")
                    .vipTier(VipTier.PLATINUM)
                    .nomadMiles(9800L)
                    .build());
        } else {
            vipMember = memberRepository.findAll().stream().findFirst().orElse(null);
        }

        Product pMcmBackpack = null;
        Product pLvKeepall = null;

        if (productRepository.count() == 0) {
            // MCM Brand
            pMcmBackpack = productRepository.save(Product.builder()
                    .name("MCM 스타크 비세토스 방수 백팩 32")
                    .brand("MCM")
                    .category(ProductCategory.WATERPROOF)
                    .price(new BigDecimal("1250000.00"))
                    .stock(50)
                    .imageUrl("https://images.mcmworldwide.com/products/backpack_visetos.jpg")
                    .description("열대성 스콜 및 우천 시 소지품을 완벽 보호하는 하이테크 비세토스 방수 백팩")
                    .isVipExclusive(true)
                    .build());

            productRepository.save(Product.builder()
                    .name("MCM 비세토스 스마트 패스포트 홀더")
                    .brand("MCM")
                    .category(ProductCategory.ACCESSORY)
                    .price(new BigDecimal("420000.00"))
                    .stock(100)
                    .imageUrl("https://images.mcmworldwide.com/products/passport_holder.jpg")
                    .description("NFC/BLE 칩 내장 오토 체크인 지원 프리미엄 여권 지갑")
                    .isVipExclusive(false)
                    .build());

            // LOUIS VUITTON Brand
            pLvKeepall = productRepository.save(Product.builder()
                    .name("루이비통 키폴 반둘리에 50 모노그램")
                    .brand("LOUIS VUITTON")
                    .category(ProductCategory.TRAVEL_BAG)
                    .price(new BigDecimal("3360000.00"))
                    .stock(20)
                    .imageUrl("https://images.louisvuitton.com/products/keepall_50.jpg")
                    .description("기내 반입이 가능한 럭셔리 여행의 아이콘, 모노그램 캔버스 더플 트래블백")
                    .isVipExclusive(true)
                    .build());

            // CHANEL Brand
            productRepository.save(Product.builder()
                    .name("샤넬 클래식 플랩백 미디엄 카프스킨")
                    .brand("CHANEL")
                    .category(ProductCategory.ACCESSORY)
                    .price(new BigDecimal("15500000.00"))
                    .stock(5)
                    .imageUrl("https://images.chanel.com/products/classic_flap.jpg")
                    .description("시대를 초월한 우아함, 골드 메탈 체인의 타임리스 숄더백")
                    .isVipExclusive(true)
                    .build());

            // HERMES Brand
            productRepository.save(Product.builder()
                    .name("에르메스 타막 Epsom 가죽 패스포트 케이스")
                    .brand("HERMES")
                    .category(ProductCategory.ACCESSORY)
                    .price(new BigDecimal("1150000.00"))
                    .stock(15)
                    .imageUrl("https://images.hermes.com/products/tarmac_passport.jpg")
                    .description("최상급 엡솜 송아지 가죽으로 제작된 프렌치 아티잔 트래블 여권 케이스")
                    .isVipExclusive(true)
                    .build());

            // GUCCI Brand
            productRepository.save(Product.builder()
                    .name("구찌 GG 수프림 오피디아 더플 트래블백")
                    .brand("GUCCI")
                    .category(ProductCategory.TRAVEL_BAG)
                    .price(new BigDecimal("2850000.00"))
                    .stock(25)
                    .imageUrl("https://images.gucci.com/products/ophidia_duffle.jpg")
                    .description("시그니처 그린/레드 웹 스트라이프와 더블 G 하드웨어 트래블백")
                    .isVipExclusive(false)
                    .build());

            // PRADA Brand
            productRepository.save(Product.builder()
                    .name("프라다 리나일론 방수 테크 백팩")
                    .brand("PRADA")
                    .category(ProductCategory.WATERPROOF)
                    .price(new BigDecimal("2750000.00"))
                    .stock(30)
                    .imageUrl("https://images.prada.com/products/renylon_backpack.jpg")
                    .description("해양 재생 나일론과 사피아노 가죽 디테일의 하이테크 방수 백팩")
                    .isVipExclusive(false)
                    .build());

            // BOTTEGA VENETA Brand
            productRepository.save(Product.builder()
                    .name("보테가 베네타 인트레치아토 가죽 지갑")
                    .brand("BOTTEGA VENETA")
                    .category(ProductCategory.ACCESSORY)
                    .price(new BigDecimal("890000.00"))
                    .stock(40)
                    .imageUrl("https://images.bottegaveneta.com/products/intrecciato_wallet.jpg")
                    .description("이탈리아 장인의 핸드메이드 인트레치아토 위빙 가죽 지갑")
                    .isVipExclusive(false)
                    .build());

            // PROTOTYPE FEATURED AIRPORT & READY-TO-WEAR COLLECTIONS
            productRepository.save(Product.builder()
                    .name("미쓰나잇 위캔더 (공항 한정)")
                    .brand("HERSTORY")
                    .category(ProductCategory.TRAVEL_BAG)
                    .price(new BigDecimal("1980000.00"))
                    .stock(15)
                    .imageUrl("https://images.herstory.ai/products/midnight_weekender.jpg")
                    .description("시그니처 코딩 캔버스 소재로 구조적인 실루엣을 완성한 공항 한정 럭셔리 위캔더")
                    .isVipExclusive(true)
                    .build());

            productRepository.save(Product.builder()
                    .name("에어로 트래블 블레이저")
                    .brand("HERSTORY")
                    .category(ProductCategory.READY_TO_WEAR)
                    .price(new BigDecimal("890000.00"))
                    .stock(25)
                    .imageUrl("https://images.herstory.ai/products/aero_blazer.jpg")
                    .description("장시간 비행에도 구김 없는 주름 방지 프리미엄 울 혼방 테일러드 블레이저")
                    .isVipExclusive(false)
                    .build());

            productRepository.save(Product.builder()
                    .name("에어로 트래블 트렉 팬츠")
                    .brand("HERSTORY")
                    .category(ProductCategory.READY_TO_WEAR)
                    .price(new BigDecimal("450000.00"))
                    .stock(30)
                    .imageUrl("https://images.herstory.ai/products/aero_pants.jpg")
                    .description("장시간 착용해도 피로감 없는 초경량 스트레치 트렉 팬츠")
                    .isVipExclusive(false)
                    .build());

            productRepository.save(Product.builder()
                    .name("트래블레더 슬립 로퍼")
                    .brand("HERSTORY")
                    .category(ProductCategory.READY_TO_WEAR)
                    .price(new BigDecimal("620000.00"))
                    .stock(20)
                    .imageUrl("https://images.herstory.ai/products/travel_loafer.jpg")
                    .description("기내 및 도심 도보 시 최상의 편안함을 선사하는 유연한 송아지 가죽 로퍼")
                    .isVipExclusive(false)
                    .build());

            productRepository.save(Product.builder()
                    .name("어쿠스틱 오버이어 프리미엄 헤드폰")
                    .brand("HERSTORY")
                    .category(ProductCategory.ACCESSORY)
                    .price(new BigDecimal("540000.00"))
                    .stock(35)
                    .imageUrl("https://images.herstory.ai/products/acoustic_headphones.jpg")
                    .description("비행을 위한 최고급 액티브 노이즈 캔슬링과 가죽 이어패드 헤드폰")
                    .isVipExclusive(false)
                    .build());

            productRepository.save(Product.builder()
                    .name("공항 한정판 워터프루프 트렌치코트")
                    .brand("MCM")
                    .category(ProductCategory.LIMITED_EDITION)
                    .price(new BigDecimal("1750000.00"))
                    .stock(10)
                    .imageUrl("https://images.mcmworldwide.com/products/trenchcoat_limited.jpg")
                    .description("MCM 코냑 비세토스 패턴과 공항 테마 유니크 디테일의 한정판 방수 트렌치코트")
                    .isVipExclusive(true)
                    .build());

            productRepository.save(Product.builder()
                    .name("공항 한정판 하이탑 스니커즈")
                    .brand("MCM")
                    .category(ProductCategory.LIMITED_EDITION)
                    .price(new BigDecimal("850000.00"))
                    .stock(20)
                    .imageUrl("https://images.mcmworldwide.com/products/sneakers_limited.jpg")
                    .description("여행자의 편안함과 하이엔드 스트리트 감성을 결합한 공항 한정판 스니커즈")
                    .isVipExclusive(false)
                    .build());

            productRepository.save(Product.builder()
                    .name("공항 한정판 미니 백팩 키링")
                    .brand("MCM")
                    .category(ProductCategory.LIMITED_EDITION)
                    .price(new BigDecimal("320000.00"))
                    .stock(50)
                    .imageUrl("https://images.mcmworldwide.com/products/keyring_backpack.jpg")
                    .description("MCM 아이코닉 스타크 백팩을 미니멀하게 축소한 공항 한정 컬렉터스 키링")
                    .isVipExclusive(false)
                    .build());

            // LEATHER CARE SOLUTION
            productRepository.save(Product.builder()
                    .name("Herstory 프리미엄 럭셔리 레더 케어 & 방수 솔루션")
                    .brand("HERSTORY")
                    .category(ProductCategory.LEATHER_CARE)
                    .price(new BigDecimal("150000.00"))
                    .stock(200)
                    .imageUrl("https://images.herstory.ai/products/luxury_care_kit.jpg")
                    .description("전세계 기후 변화로부터 전 브랜드 명품 가죽(에르메스/샤넬/LV/MCM)을 보호하는 올인원 케어 킷")
                    .isVipExclusive(false)
                    .build());
        }

        // 1. Initial Journey Seed (여정 기본 데이터)
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

        // 2. Initial SmartCart & CartItems Seed (장바구니 & VIP 피팅 데이터)
        if (smartCartRepository.count() == 0 && vipMember != null) {
            SmartCart cart = smartCartRepository.save(SmartCart.builder()
                    .member(vipMember)
                    .choiceFit(true)
                    .status(CartStatus.IN_CART)
                    .build());

            if (pMcmBackpack != null) {
                cart.addItem(CartItem.builder().product(pMcmBackpack).quantity(1).build());
            }
            if (pLvKeepall != null) {
                cart.addItem(CartItem.builder().product(pLvKeepall).quantity(1).build());
            }
            smartCartRepository.save(cart);
        }

        // 3. Initial Order Seed (이전 출국 면세 구매 내역 1건)
        if (orderRepository.count() == 0 && vipMember != null) {
            Order order = Order.builder()
                    .member(vipMember)
                    .totalAmount(new BigDecimal("1250000.00"))
                    .dutyFreeDiscount(new BigDecimal("250000.00"))
                    .finalAmount(new BigDecimal("1000000.00"))
                    .earnedMiles(1000)
                    .orderStatus(OrderStatus.PAID)
                    .build();

            if (pMcmBackpack != null) {
                order.addOrderItem(OrderItem.builder()
                        .product(pMcmBackpack)
                        .price(new BigDecimal("1250000.00"))
                        .quantity(1)
                        .build());
            }
            orderRepository.save(order);
        }
    }
}
