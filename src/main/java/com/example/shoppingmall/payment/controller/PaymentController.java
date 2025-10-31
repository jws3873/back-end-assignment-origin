package com.example.shoppingmall.payment.controller;

import com.example.shoppingmall.payment.dto.PaymentResponse;
import com.example.shoppingmall.payment.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @Operation(summary = "특정 주문의 결제 이력 조회", description = "주문 번호를 통해 해당 주문의 결제 이력을 조회")
    @GetMapping("/{orderId}")
    public List<PaymentResponse> getPaymentsByOrder(@PathVariable Long orderId) {
        return paymentService.getPaymentsByOrder(orderId);
    }

    @Operation(summary = "사용자 전체 결제 이력 조회", description = "사용자 번호를 통해 해당 사용자의 전체 결제 이력을 조회")
    @GetMapping("/users/{userId}")
    public List<PaymentResponse> getPaymentsByUser(@PathVariable Long userId) {
        return paymentService.getPaymentsByUser(userId);
    }
}
