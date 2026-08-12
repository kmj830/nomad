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
                    .name("김노마드 (VIP)")
                    .phone("010-1234-5678")
                    .vipTier(VipTier.VIP)
                    .nomadMiles(15000L)
                    .build());

            memberRepository.save(Member.builder()
                    .email("gold@mcmworldwide.com")
                    .name("이여행 (Gold)")
                    .phone("010-9876-5432")
                    .vipTier(VipTier.GOLD)
                    .nomadMiles(4500L)
                    .build());

            memberRepository.save(Member.builder()
                    .email("platinum@mcmworldwide.com")
                    .name("박스타 (Platinum)")
                    .phone("010-5555-7777")
                    .vipTier(VipTier.PLATINUM)
                    .nomadMiles(9800L)
                    .build());
        }

        if (productRepository.count() == 0) {
            productRepository.save(Product.builder()
                    .name("MCM Visetos 방수 트래블 백팩")
                    .category(ProductCategory.WATERPROOF)
                    .price(new BigDecimal("1250000.00"))
                    .stock(50)
                    .imageUrl("https://images.mcmworldwide.com/products/backpack_visetos.jpg")
                    .description("우천 및 수상 활동 시 소지품을 보호해주는 하이테크 비세토스 방수 백팩")
                    .isVipExclusive(true)
                    .build());

            productRepository.save(Product.builder()
                    .name("MCM 스카이 트래블러 더플백")
                    .category(ProductCategory.TRAVEL_BAG)
                    .price(new BigDecimal("1680000.00"))
                    .stock(30)
                    .imageUrl("https://images.mcmworldwide.com/products/duffle_sky.jpg")
                    .description("경량 소재와 고급 가죽 핸들이 조화된 비행 전용 라이트웨이트 더플백")
                    .isVipExclusive(false)
                    .build());

            productRepository.save(Product.builder()
                    .name("MCM 비세토스 스마트 패스포트 지갑")
                    .category(ProductCategory.ACCESSORY)
                    .price(new BigDecimal("420000.00"))
                    .stock(100)
                    .imageUrl("https://images.mcmworldwide.com/products/passport_holder.jpg")
                    .description("NFC/BLE 칩 내장 오토 체크인 지원 프리미엄 여권 케이스")
                    .isVipExclusive(false)
                    .build());

            productRepository.save(Product.builder()
                    .name("MCM 베를린 골드 한정판 클러치백")
                    .category(ProductCategory.ACCESSORY)
                    .price(new BigDecimal("1850000.00"))
                    .stock(10)
                    .imageUrl("https://images.mcmworldwide.com/products/clutch_gold.jpg")
                    .description("독일 베를린 에디션 24K 골드 아키텍처 하드웨어 한정판 미니 클러치")
                    .isVipExclusive(true)
                    .build());

            productRepository.save(Product.builder()
                    .name("MCM 프리미엄 가죽 케어 솔루션 세트")
                    .category(ProductCategory.LEATHER_CARE)
                    .price(new BigDecimal("150000.00"))
                    .stock(200)
                    .imageUrl("https://images.mcmworldwide.com/products/leather_care.jpg")
                    .description("해외 기후 변화로부터 고급 비세토스 가죽을 보호하는 케어 킷")
                    .isVipExclusive(false)
                    .build());
        }
    }
}
