package com.example.shoppingmall.product.service;

import com.example.shoppingmall.product.dto.ProductResponse;
import com.example.shoppingmall.product.dto.ProductRequest;
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

    /**
     * 상품 조회 서비스
     * 검색 조건과 페이징 조건을 기반으로 상품 목록을 반환한다.
     *
     * @param request 상품 조회용 DTO
     * @return 상품 목록 페이지
     */
    public Page<ProductResponse> getProducts(ProductRequest request) {

        Page<Product> products = productRepository.findByCategoryContainingAndNameContainingAndPriceBetween(
                request.getCategory(),
                request.getName(),
                request.getMinPrice(),
                request.getMaxPrice(),
                PageRequest.of(request.getPage(), request.getSize())
        );
        return products.map(ProductResponse::fromEntity);
    }

}
