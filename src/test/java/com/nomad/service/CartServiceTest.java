package com.nomad.service;

import com.nomad.domain.cart.*;
import com.nomad.domain.member.Member;
import com.nomad.domain.member.MemberRepository;
import com.nomad.domain.member.VipTier;
import com.nomad.domain.product.Product;
import com.nomad.domain.product.ProductCategory;
import com.nomad.domain.product.ProductRepository;
import com.nomad.dto.CartDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private SmartCartRepository smartCartRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private CartService cartService;

    @Test
    @DisplayName("장바구니에 상품 추가 성공")
    void addToCart_Success() {
        Member member = Member.builder().id(1L).email("vip@herstory.com").name("VIP고객").vipTier(VipTier.VIP).build();
        Product product = Product.builder().id(100L).name("럭셔리 백팩").category(ProductCategory.WATERPROOF).price(new BigDecimal("100000.00")).build();

        SmartCart cart = SmartCart.builder().id(5L).member(member).choiceFit(false).status(CartStatus.IN_CART).items(new ArrayList<>()).build();

        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(productRepository.findById(100L)).thenReturn(Optional.of(product));
        when(smartCartRepository.findByMemberIdAndStatus(1L, CartStatus.IN_CART)).thenReturn(Optional.of(cart));

        CartDto.AddItemRequest req = new CartDto.AddItemRequest(1L, 100L, 2);
        CartDto.CartResponse res = cartService.addToCart(req);

        assertThat(res.getCartId()).isEqualTo(5L);
        assertThat(res.getItems()).hasSize(1);
        assertThat(res.getTotalPrice()).isEqualTo(new BigDecimal("200000.00"));
    }

    @Test
    @DisplayName("ChoiceFit 피팅 신청 여부 상태 변경 성공")
    void updateChoiceFit_Success() {
        Member member = Member.builder().id(1L).build();
        SmartCart cart = SmartCart.builder().id(5L).member(member).choiceFit(false).status(CartStatus.IN_CART).items(new ArrayList<>()).build();

        when(smartCartRepository.findByMemberIdAndStatus(1L, CartStatus.IN_CART)).thenReturn(Optional.of(cart));

        CartDto.ChoiceFitRequest req = new CartDto.ChoiceFitRequest(1L, true);
        CartDto.CartResponse res = cartService.updateChoiceFit(req);

        assertThat(res.getChoiceFit()).isTrue();
    }
}
