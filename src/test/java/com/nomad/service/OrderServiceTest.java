package com.nomad.service;

import com.nomad.domain.cart.CartItem;
import com.nomad.domain.cart.CartStatus;
import com.nomad.domain.cart.SmartCart;
import com.nomad.domain.cart.SmartCartRepository;
import com.nomad.domain.journey.Journey;
import com.nomad.domain.journey.JourneyRepository;
import com.nomad.domain.member.Member;
import com.nomad.domain.member.MemberRepository;
import com.nomad.domain.member.VipTier;
import com.nomad.domain.order.Order;
import com.nomad.domain.order.OrderRepository;
import com.nomad.domain.order.OrderStatus;
import com.nomad.domain.product.Product;
import com.nomad.domain.product.ProductCategory;
import com.nomad.domain.store.StoreVisitRepository;
import com.nomad.dto.OrderDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private SmartCartRepository smartCartRepository;

    @Mock
    private JourneyRepository journeyRepository;

    @Mock
    private StoreVisitRepository storeVisitRepository;

    @InjectMocks
    private OrderService orderService;

    @Test
    @DisplayName("면세 결제 처리 및 VIP 15% 할인, 마일리지 적립 적용")
    void checkout_Success() {
        Member member = Member.builder().id(1L).email("vip@mcm.com").name("VIP").vipTier(VipTier.VIP).nomadMiles(0L).build();
        Product product = Product.builder().id(10L).name("MCM 백팩").price(new BigDecimal("1000000.00")).category(ProductCategory.WATERPROOF).build();

        SmartCart cart = SmartCart.builder().id(2L).member(member).status(CartStatus.IN_CART).items(new ArrayList<>()).build();
        CartItem cartItem = CartItem.builder().id(100L).product(product).quantity(1).build();
        cart.addItem(cartItem);

        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(smartCartRepository.findByMemberIdAndStatus(1L, CartStatus.IN_CART)).thenReturn(Optional.of(cart));

        Order savedOrder = Order.builder()
                .id(999L)
                .member(member)
                .totalAmount(new BigDecimal("1000000.00"))
                .dutyFreeDiscount(new BigDecimal("150000.00"))
                .finalAmount(new BigDecimal("850000.00"))
                .earnedMiles(42500)
                .orderStatus(OrderStatus.PAID)
                .createdAt(LocalDateTime.now())
                .orderItems(new ArrayList<>())
                .build();

        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);
        when(storeVisitRepository.findTopByMemberIdOrderByVisitedAtDesc(1L)).thenReturn(Optional.empty());

        OrderDto.CheckoutRequest req = new OrderDto.CheckoutRequest(1L, null);
        OrderDto.OrderResponse res = orderService.checkout(req);

        assertThat(res.getOrderId()).isEqualTo(999L);
        assertThat(res.getDutyFreeDiscount()).isEqualTo(new BigDecimal("150000.00"));
        assertThat(res.getFinalAmount()).isEqualTo(new BigDecimal("850000.00"));
        assertThat(res.getEarnedMiles()).isEqualTo(42500);
        assertThat(cart.getStatus()).isEqualTo(CartStatus.CHECKED_OUT);
    }
}
