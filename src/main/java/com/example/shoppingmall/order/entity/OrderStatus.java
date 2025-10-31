package com.example.shoppingmall.order.entity;

public enum OrderStatus {

    CREATED("주문 생성됨"),
    PENDING_PAYMENT("결제 대기"),
    PAYMENT_COMPLETED("결제 완료"),
    PAYMENT_FAILED("결제 실패"),
    CANCELED("주문 취소");

    private final String description;

    OrderStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
