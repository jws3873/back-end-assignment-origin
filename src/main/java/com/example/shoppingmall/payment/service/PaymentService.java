package com.example.shoppingmall.payment.service;

import com.example.shoppingmall.order.entity.Order;
import com.example.shoppingmall.payment.dto.PaymentResponse;
import com.example.shoppingmall.payment.entity.Payment;
import com.example.shoppingmall.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 결제 이력 조회 서비스
 */
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;

    private static final String PAYMENT_API_URL = "https://mock-payment-api.free.beeceptor.com/api/v1/payment";

    /**
     * 외부 결제 요청 및 결과 저장
     *
     * @param order
     * @return 결제 결과
     */
    @Transactional
    public Payment processPayment(Order order) {
        Map<String, Object> requestBody = Map.of(
                "orderId", order.getId().toString(),
                "amount", order.getTotalAmount()
        );

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Map> response = restTemplate.postForEntity(PAYMENT_API_URL, requestBody, Map.class);
        Map<String, Object> body = response.getBody();
        // 결제 결과 저장
        Payment payment = Payment.builder()
                .order(order)
                .transactionId((String) body.get("transactionId"))
                .status((String) body.get("status"))
                .message((String) body.get("message"))
                .createdAt(LocalDateTime.now())
                .build();

        paymentRepository.save(payment);
        return payment;
    }

    /**
     * 특정 주문의 결제 이력 조회
     *
     * @param orderId 주문 번호
     * @return 결제 이력 리스트
     */
    @Transactional(readOnly = true)
    public List<PaymentResponse> getPaymentsByOrder(Long orderId) {
        List<Payment> payments = paymentRepository.findByOrderId(orderId);

        return payments.stream()
                .map(PaymentResponse::fromEntity)
                .collect(Collectors.toList());
    }
}
