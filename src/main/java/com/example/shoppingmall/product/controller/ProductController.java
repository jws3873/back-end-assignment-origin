package com.example.shoppingmall.product.controller;

import com.example.shoppingmall.product.entity.Product;
import com.example.shoppingmall.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public Page<Product> getProducts(
            @RequestParam(defaultValue = "") String category,
            @RequestParam(defaultValue = "") String name,
            @RequestParam(defaultValue = "0") int minPrice,
            @RequestParam(defaultValue = "9999999") int maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return productService.getProducts(category, name, minPrice, maxPrice, page, size);
    }

}
