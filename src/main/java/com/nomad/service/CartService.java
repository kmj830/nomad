package com.nomad.service;

import com.nomad.domain.cart.*;
import com.nomad.domain.member.Member;
import com.nomad.domain.member.MemberRepository;
import com.nomad.domain.product.Product;
import com.nomad.domain.product.ProductRepository;
import com.nomad.dto.CartDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CartService {

    private final SmartCartRepository smartCartRepository;
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;

    @Transactional
    public CartDto.CartResponse addToCart(CartDto.AddItemRequest request) {
        Member member = memberRepository.findById(request.getMemberId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다. ID: " + request.getMemberId()));

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다. ID: " + request.getProductId()));

        SmartCart cart = smartCartRepository.findByMemberIdAndStatus(member.getId(), CartStatus.IN_CART)
                .orElseGet(() -> smartCartRepository.save(
                        SmartCart.builder()
                                .member(member)
                                .choiceFit(false)
                                .status(CartStatus.IN_CART)
                                .build()
                ));

        CartItem existingItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(product.getId()))
                .findFirst()
                .orElse(null);

        int addedQty = request.getQuantity() != null ? request.getQuantity() : 1;

        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + addedQty);
        } else {
            CartItem newItem = CartItem.builder()
                    .product(product)
                    .quantity(addedQty)
                    .build();
            cart.addItem(newItem);
        }

        return convertToResponse(cart);
    }

    @Transactional
    public CartDto.CartResponse updateChoiceFit(CartDto.ChoiceFitRequest request) {
        SmartCart cart = smartCartRepository.findByMemberIdAndStatus(request.getMemberId(), CartStatus.IN_CART)
                .orElseThrow(() -> new IllegalArgumentException("활성화된 장바구니가 없습니다."));

        cart.setChoiceFit(request.getChoiceFit());
        return convertToResponse(cart);
    }

    public CartDto.CartResponse getMyCart(Long memberId) {
        SmartCart cart = smartCartRepository.findByMemberIdAndStatus(memberId, CartStatus.IN_CART)
                .orElseGet(() -> {
                    Member member = memberRepository.findById(memberId)
                            .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));
                    return smartCartRepository.save(SmartCart.builder().member(member).status(CartStatus.IN_CART).build());
                });

        return convertToResponse(cart);
    }

    private CartDto.CartResponse convertToResponse(SmartCart cart) {
        List<CartDto.ItemDetail> itemDetails = cart.getItems().stream()
                .map(item -> CartDto.ItemDetail.builder()
                        .cartItemId(item.getId())
                        .productId(item.getProduct().getId())
                        .productName(item.getProduct().getName())
                        .brand(item.getProduct().getBrand())
                        .category(item.getProduct().getCategory().name())
                        .price(item.getProduct().getPrice())
                        .quantity(item.getQuantity())
                        .imageUrl(item.getProduct().getImageUrl())
                        .build())
                .collect(Collectors.toList());

        BigDecimal totalPrice = itemDetails.stream()
                .map(i -> i.getPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return CartDto.CartResponse.builder()
                .cartId(cart.getId())
                .memberId(cart.getMember().getId())
                .choiceFit(cart.getChoiceFit())
                .status(cart.getStatus())
                .items(itemDetails)
                .totalPrice(totalPrice)
                .build();
    }
}
