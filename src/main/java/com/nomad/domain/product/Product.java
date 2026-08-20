package com.nomad.domain.product;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Builder.Default
    @Column(nullable = false)
    private String brand = "HERSTORY";

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductCategory category;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    @Builder.Default
    private Integer stock = 100;

    private String imageUrl;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Builder.Default
    private Boolean isVipExclusive = false;

    public String getImageUrl() {
        if (this.imageUrl == null) {
            return "https://images.unsplash.com/photo-1553062407-98eeb64c6a62?auto=format&fit=crop&w=800&q=80";
        }
        if (this.imageUrl.startsWith("/")) {
            return "https://mcm-nomad-backend.onrender.com" + this.imageUrl;
        }
        return this.imageUrl;
    }

    // Frontend Next.js ProductRow compatibility
    public String getThumbnailUrl() {
        return getImageUrl();
    }

    public java.math.BigDecimal getPriceKrw() {
        return this.price != null ? this.price : java.math.BigDecimal.ZERO;
    }
}

