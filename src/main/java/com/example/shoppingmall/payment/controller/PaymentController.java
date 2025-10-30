package com.example.shoppingmall.payment.controller;

import com.example.shoppingmall.payment.dto.PaymentResponse;
import com.example.shoppingmall.payment.service.PaymentService;
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

    /**
     * 특정 주문의 결제 이력 조회 API
     *
     * @param orderId 주문 번호
     * @return 결제 이력 리스트
     */
    @GetMapping("/{orderId}")
    public List<PaymentResponse> getPayments(@PathVariable Long orderId) {
        return paymentService.getPaymentsByOrder(orderId);
    }

}
