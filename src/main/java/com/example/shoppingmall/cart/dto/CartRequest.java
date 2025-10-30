package com.example.shoppingmall.cart.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * 장바구니 API 요청 DTO
 * 장바구니 상품 추가 및 수량 수정 시 사용
 */
@Getter
@Setter
public class CartRequest {

    private Long cartId;     // 장바구니 번호
    private Long userId;     // 사용자 번호
    private Long productId;  // 상품 번호
    private int quantity;    // 추가/수정할 수량

}
