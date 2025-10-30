package com.example.shoppingmall.order.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * 주문 응답 DTO
 */
@Getter
@Setter
@Builder
public class OrderResponse {

    private Long orderId;          // 주문 번호
    private String status;         // 주문 상태 (결제대기, 결제완료, 실패 등)
    private int totalAmount;       // 총 결제 금액
    private String transactionId;  // 결제 트랜잭션 ID
    private String message;        // 응답 메시지
    
}
