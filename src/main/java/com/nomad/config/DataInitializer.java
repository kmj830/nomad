package com.nomad.config;

import com.nomad.domain.member.Member;
import com.nomad.domain.member.MemberRepository;
import com.nomad.domain.member.VipTier;
import com.nomad.domain.product.Product;
import com.nomad.domain.product.ProductCategory;
import com.nomad.domain.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;

    @Override
    public void run(String... args) {
        if (memberRepository.count() == 0) {
            memberRepository.save(Member.builder()
                    .email("vip@mcmworldwide.com")
                    .password("1234")
                    .name("김노마드 (VIP)")
                    .phone("010-1234-5678")
                    .vipTier(VipTier.VIP)
                    .nomadMiles(15000L)
                    .build());

            memberRepository.save(Member.builder()
                    .email("gold@mcmworldwide.com")
                    .password("1234")
                    .name("이여행 (Gold)")
                    .phone("010-9876-5432")
                    .vipTier(VipTier.GOLD)
                    .nomadMiles(4500L)
                    .build());

            memberRepository.save(Member.builder()
                    .email("platinum@mcmworldwide.com")
                    .password("1234")
                    .name("박스타 (Platinum)")
                    .phone("010-5555-7777")
                    .vipTier(VipTier.PLATINUM)
                    .nomadMiles(9800L)
                    .build());
        }

        if (productRepository.count() == 0) {
            // MCM Brand
            productRepository.save(Product.builder()
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
            productRepository.save(Product.builder()
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
    }
}
