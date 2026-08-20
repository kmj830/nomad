package com.nomad.domain.product;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategory(ProductCategory category);
    boolean existsByName(String name);
    java.util.Optional<Product> findByName(String name);
}

