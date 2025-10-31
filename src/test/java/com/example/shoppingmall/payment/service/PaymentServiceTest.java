package com.example.shoppingmall.payment.service;

import com.example.shoppingmall.order.entity.Order;
import com.example.shoppingmall.payment.dto.PaymentResponse;
import com.example.shoppingmall.payment.entity.Payment;
import com.example.shoppingmall.payment.repository.PaymentRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private PaymentService paymentService;

    @Test
    @DisplayName("결제 요청 Payment 객체 반환")
    void processPayment() {
        // given
        Order mockOrder = Order.builder()
                .id(1L)
                .totalAmount(5000)
                .build();

        Map<String, Object> mockResponseBody = Map.of(
                "transactionId", "T12345",
                "status", "SUCCESS",
                "message", "Payment processed successfully"
        );

        ResponseEntity<Map> mockResponse =
                new ResponseEntity<>(mockResponseBody, HttpStatus.OK);

        // 외부 API 호출 모의
        when(restTemplate.postForEntity(anyString(), any(), eq(Map.class)))
                .thenReturn(mockResponse);

        // when
        Payment result = paymentService.processPayment(mockOrder);

        // then
        assertThat(result.getStatus()).isEqualTo("SUCCESS");
        assertThat(result.getTransactionId()).isEqualTo("T12345");
        verify(paymentRepository, times(1)).save(any(Payment.class));
    }

    @Test
    @DisplayName("주문번호로 결제 이력 조회")
    void getPaymentsByOrder() {
        // given
        Payment payment = Payment.builder()
                .id(1L)
                .transactionId("T11111")
                .status("SUCCESS")
                .message("결제 완료")
                .createdAt(LocalDateTime.now())
                .build();

        when(paymentRepository.findByOrderId(1L)).thenReturn(List.of(payment));

        // when
        List<PaymentResponse> result = paymentService.getPaymentsByOrder(1L);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTransactionId()).isEqualTo("T11111");
        verify(paymentRepository, times(1)).findByOrderId(1L);
    }

    @Test
    @DisplayName("사용자 번호로 전체 결제 이력 조회 성공")
    void getPaymentsByUser() {
        // given
        Payment payment = Payment.builder()
                .id(2L)
                .transactionId("T22222")
                .status("FAILED")
                .message("결제 실패")
                .createdAt(LocalDateTime.now())
                .build();

        when(paymentRepository.findByOrder_User_Id(5L)).thenReturn(List.of(payment));

        // when
        List<PaymentResponse> result = paymentService.getPaymentsByUser(5L);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatus()).isEqualTo("FAILED");
        verify(paymentRepository, times(1)).findByOrder_User_Id(5L);
    }

}
