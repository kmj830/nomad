package com.nomad.domain.cart;

import com.nomad.domain.member.Member;
import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "smart_carts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SmartCart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Builder.Default
    private Boolean choiceFit = false;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private CartStatus status = CartStatus.IN_CART;

    @OneToMany(mappedBy = "smartCart", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<CartItem> items = new ArrayList<>();

    public void addItem(CartItem item) {
        items.add(item);
        item.setSmartCart(this);
    }
}
