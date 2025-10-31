package com.example.shoppingmall.payment.controller;

import com.example.shoppingmall.payment.dto.PaymentResponse;
import com.example.shoppingmall.payment.service.PaymentService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PaymentController.class)
public class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PaymentService paymentService;

    @Test
    @DisplayName("주문번호로 결제 이력 조회")
    void getPaymentsByOrder() throws Exception {
        // given
        Long orderId = 1L; // ✅ 명시적으로 지정
        PaymentResponse mockResponse = PaymentResponse.builder()
                .id(1L)
                .transactionId("T123456")
                .status("SUCCESS")
                .message("결제 완료")
                .createdAt(LocalDateTime.now())
                .build();

        when(paymentService.getPaymentsByOrder(orderId))
                .thenReturn(List.of(mockResponse));

        // when & then
        mockMvc.perform(get("/api/v1/payments/{orderId}", orderId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].transactionId").value("T123456"))
                .andExpect(jsonPath("$[0].status").value("SUCCESS"));
    }

    @Test
    @DisplayName("사용자 번호로 결제 이력 조회")
    void getPaymentsByUser() throws Exception {
        // given
        Long userId = 5L; // ✅ 임시 사용자 ID 지정
        PaymentResponse mockResponse = PaymentResponse.builder()
                .id(2L)
                .transactionId("T987654")
                .status("FAILED")
                .message("결제 실패")
                .createdAt(LocalDateTime.now())
                .build();

        when(paymentService.getPaymentsByUser(userId))
                .thenReturn(List.of(mockResponse));

        // when & then
        mockMvc.perform(get("/api/v1/payments/users/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].transactionId").value("T987654"))
                .andExpect(jsonPath("$[0].status").value("FAILED"));
    }
}
