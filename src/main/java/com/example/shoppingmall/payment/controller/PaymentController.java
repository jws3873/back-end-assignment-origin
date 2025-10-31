package com.example.shoppingmall.payment.controller;

import com.example.shoppingmall.payment.dto.PaymentRequest;
import com.example.shoppingmall.payment.dto.PaymentResponse;
import com.example.shoppingmall.payment.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "결재 API", description = "결재 관련 API")
@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @Operation(summary = "주문 결제 요청", description = "주문 번호를 기반으로 결제를 진행합니다.")
    @PostMapping
    public ResponseEntity<PaymentResponse> processPayment(@RequestBody PaymentRequest request) {
        PaymentResponse response = paymentService.processPayment(request);
        return ResponseEntity.ok(response);
    }

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
