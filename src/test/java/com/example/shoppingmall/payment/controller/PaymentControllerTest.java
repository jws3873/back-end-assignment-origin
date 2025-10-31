package com.example.shoppingmall.payment.controller;

import com.example.shoppingmall.payment.dto.PaymentRequest;
import com.example.shoppingmall.payment.dto.PaymentResponse;
import com.example.shoppingmall.payment.service.PaymentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PaymentController.class)
@AutoConfigureMockMvc(addFilters = false)
public class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PaymentService paymentService;

    @Autowired
    private ObjectMapper objectMapper;

    // 1. 결제 생성 (POST)
    @Test
    @DisplayName("결제 요청 성공 - 주문번호와 사용자번호 기반")
    void processPayment_success() throws Exception {
        // given
        PaymentResponse mockResponse = PaymentResponse.builder()
                .id(1L)
                .transactionId("TX-1111")
                .status("SUCCESS")
                .message("결제가 완료되었습니다.")
                .createdAt(LocalDateTime.now())
                .build();

        when(paymentService.processPayment(any(PaymentRequest.class)))
                .thenReturn(mockResponse);

        PaymentRequest mockRequest = new PaymentRequest();
        mockRequest.setUserId(1L);
        mockRequest.setOrderId(10L);

        // when & then
        mockMvc.perform(post("/api/v1/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transactionId").value("TX-1111"))
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("결제가 완료되었습니다."));
    }

    // 2. 주문번호로 결제 이력 조회
    @Test
    @DisplayName("주문번호로 결제 이력 조회")
    void getPaymentsByOrder() throws Exception {
        Long orderId = 1L;
        PaymentResponse mockResponse = PaymentResponse.builder()
                .id(1L)
                .transactionId("T123456")
                .status("SUCCESS")
                .message("결제 완료")
                .createdAt(LocalDateTime.now())
                .build();

        when(paymentService.getPaymentsByOrder(orderId))
                .thenReturn(List.of(mockResponse));

        mockMvc.perform(get("/api/v1/payments/{orderId}", orderId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].transactionId").value("T123456"))
                .andExpect(jsonPath("$[0].status").value("SUCCESS"));
    }

    // 3. 사용자 전체 결제 이력 조회
    @Test
    @DisplayName("사용자 번호로 결제 이력 조회")
    void getPaymentsByUser() throws Exception {
        Long userId = 5L;
        PaymentResponse mockResponse = PaymentResponse.builder()
                .id(2L)
                .transactionId("T987654")
                .status("FAILED")
                .message("결제 실패")
                .createdAt(LocalDateTime.now())
                .build();

        when(paymentService.getPaymentsByUser(userId))
                .thenReturn(List.of(mockResponse));

        mockMvc.perform(get("/api/v1/payments/users/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].transactionId").value("T987654"))
                .andExpect(jsonPath("$[0].status").value("FAILED"));
    }
}
