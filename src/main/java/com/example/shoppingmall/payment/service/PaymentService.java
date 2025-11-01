package com.example.shoppingmall.payment.service;

import com.example.shoppingmall.global.exception.BusinessException;
import com.example.shoppingmall.order.entity.Order;
import com.example.shoppingmall.order.entity.OrderStatus;
import com.example.shoppingmall.order.repository.OrderRepository;
import com.example.shoppingmall.payment.dto.PaymentRequest;
import com.example.shoppingmall.payment.dto.PaymentResponse;
import com.example.shoppingmall.payment.entity.Payment;
import com.example.shoppingmall.payment.repository.PaymentRepository;
import com.example.shoppingmall.user.entity.User;
import com.example.shoppingmall.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final RestTemplate restTemplate;

    private static final String PAYMENT_API_URL = "https://mock-payment-api.free.beeceptor.com/api/v1/payment";

    /**
     * 외부 결제 API 호출 + 결제 결과 저장
     */
    public PaymentResponse processPayment(PaymentRequest request) {

        // 1. 사용자 및 주문 검증
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new BusinessException("사용자를 찾을 수 없습니다.", 404));

        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new BusinessException("주문을 찾을 수 없습니다.", 404));

        if (!order.getUser().getId().equals(user.getId())) {
            throw new BusinessException("본인 주문만 결제할 수 있습니다.", 403);
        }

        // 결제 가능한 상태: 결제 대기(PENDING_PAYMENT), 결제 실패(PAYMENT_FAILED)
        if (!(order.getStatus() == OrderStatus.PENDING_PAYMENT ||
                order.getStatus() == OrderStatus.PAYMENT_FAILED)) {
            throw new BusinessException("결제할 수 없는 주문 상태입니다.", 400);
        }

        // 2.주문 아이템별 재고 검증
        order.getOrderItems().forEach(item -> {
            var product = item.getProduct();
            if (product.isSoldOut() || product.getStock() < item.getQuantity()) {
                throw new BusinessException("상품 재고가 부족합니다: " + product.getName(), 409);
            }
        });

        // 3. 외부 결제 API 요청
        Map<String, Object> requestBody = Map.of(
                "orderId", order.getId().toString(),
                "amount", order.getTotalAmount()
        );

        log.info("[결제 요청] 외부 API 호출: {}", PAYMENT_API_URL);
        ResponseEntity<Map> response =
                restTemplate.postForEntity(PAYMENT_API_URL, requestBody, Map.class);

        Map<String, Object> body = response.getBody();
        if (body == null) {
            throw new BusinessException("결제 응답이 올바르지 않습니다.", 500);
        }

        // 4. 결제 결과 저장
        Payment payment = Payment.builder()
                .order(order)
                .transactionId((String) body.get("transactionId"))
                .status((String) body.get("status"))
                .message((String) body.get("message"))
                .createdAt(LocalDateTime.now())
                .build();

        paymentRepository.save(payment);

        // 4. 주문 상태 변경 (성공/실패 반영)
        String status = (String) body.get("status");
        if ("SUCCESS".equalsIgnoreCase(status)) {
            order.getOrderItems().forEach(item -> {
                var product = item.getProduct();
                int remain = product.getStock() - item.getQuantity();
                if (remain < 0) {
                    throw new BusinessException("결제 중 재고가 부족합니다: " + product.getName(), 409);
                }
                product.setStock(remain);
            });
            order.setStatus(OrderStatus.PAYMENT_COMPLETED);
        } else {
            order.setStatus(OrderStatus.PAYMENT_FAILED);
        }
        orderRepository.save(order);

        log.info("[결제 결과] orderId={}, status={}, transactionId={}",
                order.getId(), status, body.get("transactionId"));

        // 5. DTO 반환
        return PaymentResponse.fromEntity(payment);
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

    /**
     * 특정 사용자의 전체 결제 이력 조회
     *
     * @param userId 사용자 번호
     * @return 결제 이력 리스트
     */
    @Transactional(readOnly = true)
    public List<PaymentResponse> getPaymentsByUser(Long userId) {
        return paymentRepository.findByOrder_User_Id(userId).stream()
                .map(PaymentResponse::fromEntity)
                .collect(Collectors.toList());
    }
}
