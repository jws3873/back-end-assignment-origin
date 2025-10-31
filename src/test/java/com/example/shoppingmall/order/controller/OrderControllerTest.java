package com.example.shoppingmall.order.controller;

import com.example.shoppingmall.order.dto.OrderRequest;
import com.example.shoppingmall.order.dto.OrderResponse;
import com.example.shoppingmall.order.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
public class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderService orderService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("주문 생성 및 결제 요청 성공")
    void createOrder() throws Exception {
        // given
        OrderResponse mockResponse = OrderResponse.builder()
                .orderId(1L)
                .status("PAYMENT_COMPLETED")
                .totalAmount(12000)
                .transactionId("TX-9999")
                .message("결제가 완료되었습니다.")
                .build();

        when(orderService.createOrder(any(OrderRequest.class))).thenReturn(mockResponse);

        OrderRequest mockRequest = new OrderRequest();
        mockRequest.setUserId(1L);
        mockRequest.setCartIds(List.of(2L, 3L));

        // when & then
        mockMvc.perform(post("/api/v1/order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value(1))
                .andExpect(jsonPath("$.status").value("PAYMENT_COMPLETED"))
                .andExpect(jsonPath("$.totalAmount").value(12000))
                .andExpect(jsonPath("$.transactionId").value("TX-9999"));
    }

}
