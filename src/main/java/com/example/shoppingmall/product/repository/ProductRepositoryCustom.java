package com.example.shoppingmall.product.repository;

import com.example.shoppingmall.product.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductRepositoryCustom {

    Page<Product> searchProducts(String category, String name, Integer minPrice, Integer maxPrice, Pageable pageable);

}
