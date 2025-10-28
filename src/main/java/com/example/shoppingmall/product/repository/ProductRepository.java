package com.example.shoppingmall.product.repository;

import com.example.shoppingmall.product.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Page<Product> findByCategoryContainingAndNameContainingAndPriceBetween(
            String category, String name, int minPrice, int maxPrice, Pageable pageable
    );
}
