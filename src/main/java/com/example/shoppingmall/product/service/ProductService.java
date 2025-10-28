package com.example.shoppingmall.product.service;

import com.example.shoppingmall.product.entity.Product;
import com.example.shoppingmall.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public Page<Product> getProducts(String category, String name, int minPrice, int maxPrice, int page, int size) {
        return productRepository.findByCategoryContainingAndNameContainingAndPriceBetween(
                category, name, minPrice, maxPrice, PageRequest.of(page, size)
        );
    }

}
