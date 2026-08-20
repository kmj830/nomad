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
        Member member = findMemberOrFallback(request.getMemberId());

        Product product = productRepository.findById(request.getProductId())
                .orElseGet(() -> productRepository.findAll().stream().findFirst()
                        .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다. ID: " + request.getProductId())));

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
        Member member = findMemberOrFallback(request.getMemberId());
        SmartCart cart = smartCartRepository.findByMemberIdAndStatus(member.getId(), CartStatus.IN_CART)
                .orElseGet(() -> smartCartRepository.save(
                        SmartCart.builder()
                                .member(member)
                                .choiceFit(request.getChoiceFit())
                                .status(CartStatus.IN_CART)
                                .build()
                ));

        cart.setChoiceFit(request.getChoiceFit());
        return convertToResponse(cart);
    }

    @Transactional
    public CartDto.CartResponse getMyCart(Long memberId) {
        Member member = findMemberOrFallback(memberId);
        SmartCart cart = smartCartRepository.findByMemberIdAndStatus(member.getId(), CartStatus.IN_CART)
                .orElseGet(() -> smartCartRepository.save(
                        SmartCart.builder()
                                .member(member)
                                .choiceFit(false)
                                .status(CartStatus.IN_CART)
                                .build()
                ));

        return convertToResponse(cart);
    }

    private Member findMemberOrFallback(Long memberId) {
        if (memberId != null) {
            var found = memberRepository.findById(memberId);
            if (found.isPresent()) return found.get();
        }
        return memberRepository.findByEmail("vip@herstory.com")
                .orElseGet(() -> memberRepository.findAll().stream().findFirst()
                        .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다. ID: " + memberId)));
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
