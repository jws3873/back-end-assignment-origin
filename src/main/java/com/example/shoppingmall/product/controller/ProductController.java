package com.example.shoppingmall.product.controller;

import com.example.shoppingmall.product.dto.ProductResponse;
import com.example.shoppingmall.product.dto.ProductRequest;
import com.example.shoppingmall.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "상품 API", description = "상품 조회 및 검색 관련 API")
@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @Operation(
            summary = "상품 목록 조회",
            description = "카테고리, 키워드, 페이징 조건을 기반으로 상품 목록을 조회"
    )
    @GetMapping
    public Page<ProductResponse> getProducts(ProductRequest request) {
        return productService.getProducts(request);
    }

}
