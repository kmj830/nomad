package com.nomad.service;

import com.nomad.domain.cart.*;
import com.nomad.domain.journey.Journey;
import com.nomad.domain.journey.JourneyRepository;
import com.nomad.domain.member.Member;
import com.nomad.domain.member.MemberRepository;
import com.nomad.domain.order.*;
import com.nomad.domain.store.PurchaseStatus;
import com.nomad.domain.store.StoreVisitRepository;
import com.nomad.dto.OrderDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final SmartCartRepository smartCartRepository;
    private final JourneyRepository journeyRepository;
    private final StoreVisitRepository storeVisitRepository;

    @Transactional
    public OrderDto.OrderResponse checkout(OrderDto.CheckoutRequest request) {
        Member member = memberRepository.findById(request.getMemberId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다. ID: " + request.getMemberId()));

        SmartCart cart = smartCartRepository.findByMemberIdAndStatus(member.getId(), CartStatus.IN_CART)
                .orElseThrow(() -> new IllegalArgumentException("결제할 장바구니 상품이 없습니다."));

        if (cart.getItems().isEmpty()) {
            throw new IllegalArgumentException("장바구니가 비어 있습니다.");
        }

        Journey journey = null;
        if (request.getJourneyId() != null) {
            journey = journeyRepository.findById(request.getJourneyId()).orElse(null);
        }

        BigDecimal totalAmount = cart.getItems().stream()
                .map(item -> item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Duty-Free Discount rate based on VIP tier
        double discountRate = switch (member.getVipTier()) {
            case VIP, PLATINUM -> 0.15;
            case GOLD -> 0.10;
            default -> 0.05;
        };

        BigDecimal dutyFreeDiscount = totalAmount.multiply(BigDecimal.valueOf(discountRate))
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal finalAmount = totalAmount.subtract(dutyFreeDiscount);

        // Earn Nomad Miles (5% of final amount)
        int earnedMiles = finalAmount.multiply(BigDecimal.valueOf(0.05)).intValue();
        member.addMiles(earnedMiles);

        Order order = Order.builder()
                .member(member)
                .journey(journey)
                .totalAmount(totalAmount)
                .dutyFreeDiscount(dutyFreeDiscount)
                .finalAmount(finalAmount)
                .earnedMiles(earnedMiles)
                .orderStatus(OrderStatus.PAID)
                .createdAt(LocalDateTime.now())
                .build();

        for (CartItem cartItem : cart.getItems()) {
            OrderItem orderItem = OrderItem.builder()
                    .product(cartItem.getProduct())
                    .quantity(cartItem.getQuantity())
                    .price(cartItem.getProduct().getPrice())
                    .build();
            order.addOrderItem(orderItem);
        }

        Order savedOrder = orderRepository.save(order);

        // Update cart status
        cart.setStatus(CartStatus.CHECKED_OUT);

        // Update latest store visit status
        storeVisitRepository.findTopByMemberIdOrderByVisitedAtDesc(member.getId())
                .ifPresent(visit -> visit.setPurchaseStatus(PurchaseStatus.PURCHASED));

        List<OrderDto.OrderItemDetail> itemDetails = savedOrder.getOrderItems().stream()
                .map(i -> OrderDto.OrderItemDetail.builder()
                        .productId(i.getProduct().getId())
                        .productName(i.getProduct().getName())
                        .quantity(i.getQuantity())
                        .price(i.getPrice())
                        .build())
                .collect(Collectors.toList());

        return OrderDto.OrderResponse.builder()
                .orderId(savedOrder.getId())
                .memberId(member.getId())
                .journeyId(journey != null ? journey.getId() : null)
                .totalAmount(savedOrder.getTotalAmount())
                .dutyFreeDiscount(savedOrder.getDutyFreeDiscount())
                .finalAmount(savedOrder.getFinalAmount())
                .earnedMiles(savedOrder.getEarnedMiles())
                .orderStatus(savedOrder.getOrderStatus())
                .items(itemDetails)
                .createdAt(savedOrder.getCreatedAt())
                .build();
    }
}
