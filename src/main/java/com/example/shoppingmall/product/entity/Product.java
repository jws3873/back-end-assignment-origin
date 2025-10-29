package com.example.shoppingmall.product.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "product")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 상품 식별자 (PK)

    private String name; // 상품명

    private String category; // 상품 카테고리

    private int price; // 가격

    private int stock; // 재고 수량

    // 재고가 0 이하일 경우 품절 여부 반환
    public boolean isSoldOut() {
        return stock <= 0;
    }

}
