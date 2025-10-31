package com.example.shoppingmall.cart.controller;

import com.example.shoppingmall.cart.dto.CartRequest;
import com.example.shoppingmall.cart.dto.CartResponse;
import com.example.shoppingmall.cart.service.CartService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CartController.class)
public class CartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CartService cartService;

    @Test
    @DisplayName("장바구니 조회")
    void getCartItems() throws Exception {
        // given
        CartResponse response = CartResponse.builder()
                .cartId(1L)
                .productName("테스트 상품")
                .price(10000)
                .quantity(2)
                .build();

        when(cartService.getCartItems(1L)).thenReturn(List.of(response));

        // when & then
        mockMvc.perform(get("/api/v1/cart/{userId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].productName").value("테스트 상품"))
                .andExpect(jsonPath("$[0].quantity").value(2));
    }

    @Test
    @DisplayName("장바구니 상품 추가")
    void addToCart() throws Exception {
        // given
        doNothing().when(cartService).addToCart(any(CartRequest.class));

        String requestBody = """
                {
                  "userId": 1,
                  "productId": 10,
                  "quantity": 3
                }
                """;

        // when & then
        mockMvc.perform(post("/api/v1/cart")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("장바구니 수량 수정")
    void updateQuantity() throws Exception {
        // given
        doNothing().when(cartService).updateQuantity(any(CartRequest.class));

        String requestBody = """
                {
                  "userId": 1,
                  "productId": 10,
                  "quantity": 5
                }
                """;

        // when & then
        mockMvc.perform(put("/api/v1/cart")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("장바구니 상품 삭제")
    void removeFromCart() throws Exception {
        // given
        doNothing().when(cartService).removeFromCart(1L, 1L);

        // when & then
        mockMvc.perform(delete("/api/v1/cart/{userId}/{cartId}", 1L, 1L))
                .andExpect(status().isOk());
    }

}
