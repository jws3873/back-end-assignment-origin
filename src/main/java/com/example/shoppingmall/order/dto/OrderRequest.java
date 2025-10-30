package com.example.shoppingmall.order.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 주문 요청 DTO
 */
@Getter
@Setter
public class OrderRequest {

    private Long userId; // 주문자 번호
    private List<Long> cartIds; // 장바구니 항목 ID 리스트

}
