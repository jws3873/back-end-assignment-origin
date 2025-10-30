package com.example.shoppingmall.cart.dto;

import lombok.Builder;
import lombok.Getter;

/**
 * 장바구니 API 응답 DTO
 * 장바구니 조회 시 반환
 */
@Getter
@Builder
public class CartResponse {

    private Long cartId;
    private Long productId;
    private String productName;
    private int price;
    private int quantity;
    private boolean soldOut;

}
