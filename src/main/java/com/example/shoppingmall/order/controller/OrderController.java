package com.example.shoppingmall.order.controller;

import com.example.shoppingmall.order.dto.OrderRequest;
import com.example.shoppingmall.order.dto.OrderResponse;
import com.example.shoppingmall.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    /**
     * 주문 생성 및 결제 요청 API
     *
     * @param request 주문 요청 데이터 (userId, cartIds)
     * @return 주문 결과 (주문번호, 결제상태, 결제금액 등)
     */
    @PostMapping
    public OrderResponse createOrder(@RequestBody OrderRequest request) {
        return orderService.createOrder(request);
    }
}
