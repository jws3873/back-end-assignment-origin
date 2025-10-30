package com.example.shoppingmall.order.entity;

public enum OrderStatus {
    CREATED,        // 주문 생성 (사용자가 결제 요청 전)
    PAYMENT_PENDING,// 결제 요청 중
    PAYMENT_COMPLETED, // 결제 완료
    PAYMENT_FAILED, // 결제 실패
    CANCELLED       // 주문 취소
}
