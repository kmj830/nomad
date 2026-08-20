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
    private final com.nomad.domain.mileage.MileageHistoryRepository mileageHistoryRepository;

    @Transactional
    public OrderDto.OrderResponse checkout(OrderDto.CheckoutRequest request) {
        Member member = findMemberOrFallback(request.getMemberId());

        SmartCart cart = smartCartRepository.findByMemberIdAndStatus(member.getId(), CartStatus.IN_CART)
                .orElseThrow(() -> new IllegalArgumentException("결제할 장바구니 상품이 없습니다."));

        if (cart.getItems().isEmpty()) {
            throw new IllegalArgumentException("장바구니가 비어 있습니다.");
        }

        Journey journey = null;
        if (request.getJourneyId() != null) {
            journey = journeyRepository.findById(request.getJourneyId()).orElse(null);
        }
        if (journey == null) {
            journey = journeyRepository.findTopByMemberIdOrderByDepartureDateTimeDesc(member.getId())
                    .orElseGet(() -> journeyRepository.findAll().stream().findFirst().orElse(null));
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

        if (earnedMiles > 0) {
            mileageHistoryRepository.save(com.nomad.domain.mileage.MileageHistory.builder()
                    .member(member)
                    .title("인천 T1 부티크 면세 구매 적립")
                    .amount((long) earnedMiles)
                    .type(com.nomad.domain.mileage.MileageType.EARNED_PURCHASE)
                    .balanceAfter(member.getNomadMiles())
                    .description("면세 패스트 체크아웃 결제 완료 적립 (5%)")
                    .build());
        }

        // Calculate or assign pickup schedule
        String pickupDate = (request.getPickupMonth() != null && request.getPickupDay() != null)
                ? (request.getPickupMonth() + " " + request.getPickupDay())
                : (journey != null && journey.getDepartureDateTime() != null
                        ? (journey.getDepartureDateTime().getMonthValue() + "월 " + journey.getDepartureDateTime().getDayOfMonth() + "일")
                        : "8월 22일");

        String pickupTime = (request.getPickupTime() != null && !request.getPickupTime().isBlank())
                ? request.getPickupTime()
                : "5:30 PM";

        String pickupLocation = (request.getPickupLocation() != null && !request.getPickupLocation().isBlank())
                ? request.getPickupLocation()
                : "인천국제공항 제2여객터미널 3층 면세구역 250번 게이트 앞 Herstory VIP Care & Pick-up Desk";

        Order order = Order.builder()
                .member(member)
                .journey(journey)
                .totalAmount(totalAmount)
                .dutyFreeDiscount(dutyFreeDiscount)
                .finalAmount(finalAmount)
                .earnedMiles(earnedMiles)
                .pickupDate(pickupDate)
                .pickupTime(pickupTime)
                .pickupLocation(pickupLocation)
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
                .pickupDate(savedOrder.getPickupDate())
                .pickupTime(savedOrder.getPickupTime())
                .pickupLocation(savedOrder.getPickupLocation())
                .orderStatus(savedOrder.getOrderStatus())
                .items(itemDetails)
                .createdAt(savedOrder.getCreatedAt())
                .build();
    }

    private Member findMemberOrFallback(Long memberId) {
        if (memberId != null) {
            return memberRepository.findById(memberId)
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다. ID: " + memberId));
        }
        return memberRepository.findByEmail("vip@herstory.com")
                .orElseGet(() -> memberRepository.findAll().stream().findFirst()
                        .orElseThrow(() -> new IllegalArgumentException("존재하는 회원이 없습니다.")));
    }
}
