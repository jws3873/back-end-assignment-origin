package com.example.shoppingmall.payment.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentRequest {
    private Long userId;   // 결제 요청자 ID
    private Long orderId;  // 결제할 주문 ID
}
