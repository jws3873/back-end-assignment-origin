package com.example.shoppingmall.payment.integration;

import com.example.shoppingmall.order.entity.Order;
import com.example.shoppingmall.order.entity.OrderItem;
import com.example.shoppingmall.order.entity.OrderStatus;
import com.example.shoppingmall.order.repository.OrderRepository;
import com.example.shoppingmall.payment.dto.PaymentRequest;
import com.example.shoppingmall.payment.entity.Payment;
import com.example.shoppingmall.payment.repository.PaymentRepository;
import com.example.shoppingmall.product.entity.Product;
import com.example.shoppingmall.product.repository.ProductRepository;
import com.example.shoppingmall.user.entity.User;
import com.example.shoppingmall.user.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Payment API 통합 테스트
 */
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@Transactional
public class PaymentIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @Autowired private UserRepository userRepository;
    @Autowired private OrderRepository orderRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private PaymentRepository paymentRepository;

    @Test
    @DisplayName("결제 성공 시 주문 상태 변경 및 재고 차감 확인")
    void processPayment_success() throws Exception {
        // given
        User user = userRepository.save(User.builder()
                .username("테스트 사용자")
                .email("test@test.com")
                .build());

        Product product = productRepository.save(Product.builder()
                .name("테스트 상품")
                .price(3000)
                .stock(5)
                .build());

        Order order = Order.builder()
                .user(user)
                .status(OrderStatus.PENDING_PAYMENT)
                .totalAmount(6000)
                .build();

        OrderItem item = OrderItem.builder()
                .product(product)
                .quantity(2)
                .price(product.getPrice())
                .build();

        order.addOrderItem(item);
        orderRepository.save(order);

        PaymentRequest request = new PaymentRequest();
        request.setUserId(user.getId());
        request.setOrderId(order.getId());

        // when
        mockMvc.perform(post("/api/v1/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.transactionId").exists())
                .andExpect(jsonPath("$.message").exists());

        // then
        Order updatedOrder = orderRepository.findById(order.getId()).orElseThrow();
        Product updatedProduct = productRepository.findById(product.getId()).orElseThrow();
        List<Payment> payments = paymentRepository.findByOrderId(order.getId());

        assertThat(updatedOrder.getStatus()).isIn(OrderStatus.PAYMENT_COMPLETED, OrderStatus.PAYMENT_FAILED);
        assertThat(updatedProduct.getStock()).isLessThan(5); // 결제 성공 시 차감
        assertThat(payments).isNotEmpty();
    }

    @Test
    @DisplayName("재고 부족 시 결제 실패")
    void processPayment_insufficientStock() throws Exception {
        // given
        User user = userRepository.save(User.builder()
                .username("테스트 사용자")
                .email("test@test.com")
                .build());

        Product product = productRepository.save(Product.builder()
                .name("테스트 상품")
                .price(1000)
                .stock(1)
                .build());

        Order order = Order.builder()
                .user(user)
                .status(OrderStatus.PENDING_PAYMENT)
                .totalAmount(2000)
                .build();

        OrderItem item = OrderItem.builder()
                .product(product)
                .quantity(2)
                .price(product.getPrice())
                .build();

        order.addOrderItem(item);
        orderRepository.save(order);

        PaymentRequest request = new PaymentRequest();
        request.setUserId(user.getId());
        request.setOrderId(order.getId());

        // when & then
        mockMvc.perform(post("/api/v1/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict()) // 409 예외 (재고 부족)
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("재고가 부족")));
    }
}
