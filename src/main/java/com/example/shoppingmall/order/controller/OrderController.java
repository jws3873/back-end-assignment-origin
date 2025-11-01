package com.example.shoppingmall.order.controller;

import com.example.shoppingmall.order.dto.OrderRequest;
import com.example.shoppingmall.order.dto.OrderResponse;
import com.example.shoppingmall.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "주문 API", description = "주문 관련 API")
@RestController
@RequestMapping("/api/v1/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @Operation(summary = "사용자 주문 조회", description = "사용자 번호를 기반으로 주문 목록을 조회합니다.")
    @GetMapping("/users/{userId}")
    public List<OrderResponse> getOrdersByUser(@PathVariable Long userId) {
        return orderService.getOrdersByUser(userId);
    }

    @Operation(summary = "주문 생성 및 결제 요청", description = "주문 요청 데이터를 통해 주문 생성 및 결제 요청")
    @PostMapping
    public OrderResponse createOrder(@RequestBody OrderRequest request) {
        return orderService.createOrder(request);
    }

    @Operation(summary = "주문 취소", description = "결제 완료된 주문만 취소할 수 있습니다.")
    @DeleteMapping("/{orderId}/cancel")
    public ResponseEntity<String> cancelOrder(@PathVariable Long orderId) {
        orderService.cancelOrder(orderId);
        return ResponseEntity.ok("주문이 정상적으로 취소되었습니다.");
    }
}
