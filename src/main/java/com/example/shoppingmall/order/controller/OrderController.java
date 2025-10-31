package com.example.shoppingmall.order.controller;

import com.example.shoppingmall.order.dto.OrderRequest;
import com.example.shoppingmall.order.dto.OrderResponse;
import com.example.shoppingmall.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "주문 API", description = "주문 관련 API")
@RestController
@RequestMapping("/api/v1/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @Operation(summary = "주문 생성 및 결제 요청", description = "주문 요청 데이터를 통해 주문 생성 및 결제 요청")
    @PostMapping
    public OrderResponse createOrder(@RequestBody OrderRequest request) {
        return orderService.createOrder(request);
    }

}
