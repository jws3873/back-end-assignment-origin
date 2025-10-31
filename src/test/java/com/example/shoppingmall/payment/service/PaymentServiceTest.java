package com.example.shoppingmall.payment.service;

import com.example.shoppingmall.global.exception.BusinessException;
import com.example.shoppingmall.order.entity.Order;
import com.example.shoppingmall.order.entity.OrderItem;
import com.example.shoppingmall.order.entity.OrderStatus;
import com.example.shoppingmall.order.repository.OrderRepository;
import com.example.shoppingmall.payment.dto.PaymentRequest;
import com.example.shoppingmall.payment.dto.PaymentResponse;
import com.example.shoppingmall.payment.entity.Payment;
import com.example.shoppingmall.payment.repository.PaymentRepository;
import com.example.shoppingmall.product.entity.Product;
import com.example.shoppingmall.user.entity.User;
import com.example.shoppingmall.user.repository.UserRepository;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock private PaymentRepository paymentRepository;
    @Mock private OrderRepository orderRepository;
    @Mock private UserRepository userRepository;
    @Mock private RestTemplate restTemplate;

    @InjectMocks
    private PaymentService paymentService;

    @Test
    @DisplayName("결제 요청 성공 - 외부 결제 API 응답이 SUCCESS일 때")
    void processPayment_success() {
        // given
        User user = User.builder().id(1L).build();

        Product product = Product.builder()
                .id(1L)
                .name("테스트 상품")
                .price(2000)
                .stock(10)
                .build();

        OrderItem orderItem = OrderItem.builder()
                .product(product)
                .quantity(2)
                .price(2000)
                .build();

        Order order = Order.builder()
                .id(100L)
                .user(user)
                .totalAmount(4000)
                .status(OrderStatus.PENDING_PAYMENT)
                .build();
        order.addOrderItem(orderItem);

        PaymentRequest request = new PaymentRequest();
        request.setUserId(1L);
        request.setOrderId(100L);

        Map<String, Object> mockResponseBody = Map.of(
                "transactionId", "TX-5555",
                "status", "SUCCESS",
                "message", "Payment processed successfully"
        );

        ResponseEntity<Map> mockResponse =
                new ResponseEntity<>(mockResponseBody, HttpStatus.OK);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(orderRepository.findById(100L)).thenReturn(Optional.of(order));
        when(restTemplate.postForEntity(anyString(), any(), eq(Map.class)))
                .thenReturn(mockResponse);

        // when
        PaymentResponse result = paymentService.processPayment(request);

        // then
        assertThat(result.getStatus()).isEqualTo("SUCCESS");
        assertThat(result.getTransactionId()).isEqualTo("TX-5555");
        verify(paymentRepository, times(1)).save(any(Payment.class));
        verify(orderRepository, times(1)).save(order);
        assertThat(product.getStock()).isEqualTo(8); // 10 - 2
        assertThat(order.getStatus()).isEqualTo(OrderStatus.PAYMENT_COMPLETED);
    }

    @Test
    @DisplayName("결제 실패 - 재고 부족으로 예외 발생")
    void processPayment_insufficientStock() {
        // given
        User user = User.builder().id(1L).build();

        Product product = Product.builder()
                .id(1L)
                .name("테스트 상품")
                .price(2000)
                .stock(1)
                .build();

        OrderItem orderItem = OrderItem.builder()
                .product(product)
                .quantity(2)
                .price(2000)
                .build();

        Order order = Order.builder()
                .id(200L)
                .user(user)
                .totalAmount(4000)
                .status(OrderStatus.PENDING_PAYMENT)
                .build();
        order.addOrderItem(orderItem);

        PaymentRequest request = new PaymentRequest();
        request.setUserId(1L);
        request.setOrderId(200L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(orderRepository.findById(200L)).thenReturn(Optional.of(order));

        // when & then
        assertThatThrownBy(() -> paymentService.processPayment(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("재고가 부족");
        verify(paymentRepository, never()).save(any());
    }

    @Test
    @DisplayName("주문번호로 결제 이력 조회 성공")
    void getPaymentsByOrder() {
        Payment payment = Payment.builder()
                .id(1L)
                .transactionId("T11111")
                .status("SUCCESS")
                .message("결제 완료")
                .createdAt(LocalDateTime.now())
                .build();

        when(paymentRepository.findByOrderId(1L)).thenReturn(List.of(payment));

        List<PaymentResponse> result = paymentService.getPaymentsByOrder(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTransactionId()).isEqualTo("T11111");
        verify(paymentRepository, times(1)).findByOrderId(1L);
    }

    @Test
    @DisplayName("사용자 번호로 전체 결제 이력 조회 성공")
    void getPaymentsByUser() {
        Payment payment = Payment.builder()
                .id(2L)
                .transactionId("T22222")
                .status("FAILED")
                .message("결제 실패")
                .createdAt(LocalDateTime.now())
                .build();

        when(paymentRepository.findByOrder_User_Id(5L)).thenReturn(List.of(payment));

        List<PaymentResponse> result = paymentService.getPaymentsByUser(5L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatus()).isEqualTo("FAILED");
        verify(paymentRepository, times(1)).findByOrder_User_Id(5L);
    }
}
