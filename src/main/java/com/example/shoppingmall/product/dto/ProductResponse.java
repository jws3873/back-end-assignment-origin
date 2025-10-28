package com.example.shoppingmall.product.dto;

import com.example.shoppingmall.product.entity.Product;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProductResponse {

    private Long id;          // 상품 ID
    private String name;      // 상품명
    private String category;  // 카테고리
    private int price;        // 가격
    private int stock;        // 재고 수량
    private boolean soldOut;  // 품절 여부

    // Entity -> Response DTO 변환용 메서드
    public static ProductResponse fromEntity(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .category(product.getCategory())
                .price(product.getPrice())
                .stock(product.getStock())
                .soldOut(product.isSoldOut())
                .build();
    }
}
