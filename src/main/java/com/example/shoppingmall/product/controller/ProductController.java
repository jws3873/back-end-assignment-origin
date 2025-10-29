package com.example.shoppingmall.product.controller;

import com.example.shoppingmall.product.dto.ProductResponse;
import com.example.shoppingmall.product.dto.ProductSearchRequest;
import com.example.shoppingmall.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    /**
     * 상품 목록 조회 API
     * 검색 조건과 페이징 조건을 기반으로 상품을 조회한다.
     *
     * @param request 상품 조회용 DTO
     * @return 조건에 맞는 상품 목록 (페이징 형태)
     */
    @GetMapping
    public Page<ProductResponse> getProducts(ProductSearchRequest request) {
        return productService.getProducts(request);
    }

}
