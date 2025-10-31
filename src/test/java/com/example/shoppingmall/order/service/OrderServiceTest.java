package com.example.shoppingmall.order.service;

import com.example.shoppingmall.cart.entity.Cart;
import com.example.shoppingmall.cart.repository.CartRepository;
import com.example.shoppingmall.global.exception.BusinessException;
import com.example.shoppingmall.order.dto.OrderRequest;
import com.example.shoppingmall.order.dto.OrderResponse;
import com.example.shoppingmall.order.entity.OrderStatus;
import com.example.shoppingmall.order.repository.OrderRepository;
import com.example.shoppingmall.product.entity.Product;
import com.example.shoppingmall.user.entity.User;
import com.example.shoppingmall.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock private CartRepository cartRepository;
    @Mock private OrderRepository orderRepository;
    @Mock private UserRepository userRepository;

    @InjectMocks
    private OrderService orderService;

    @Test
    @DisplayName("주문 생성 성공 - 결제 대기 상태로 생성")
    void createOrder_success() {
        // given
        User user = User.builder().id(1L).build();
        Product product = Product.builder()
                .id(1L)
                .name("테스트 상품")
                .price(1000)
                .stock(10)
                .build();

        Cart cart = Cart.builder()
                .id(1L)
                .user(user)
                .product(product)
                .quantity(2)
                .build();

        OrderRequest request = new OrderRequest();
        request.setUserId(1L);
        request.setCartIds(List.of(1L));

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(cartRepository.findAllById(request.getCartIds())).thenReturn(List.of(cart));

        // when
        OrderResponse response = orderService.createOrder(request);

        // then
        assertThat(response.getStatus()).isEqualTo(OrderStatus.PENDING_PAYMENT.name());
        assertThat(response.getMessage()).contains("결제");
        verify(orderRepository, times(1)).save(any());
        verify(cartRepository, times(1)).deleteAll(any());
    }

    @Test
    @DisplayName("재고 부족 시 주문 실패")
    void createOrder_insufficientStock() {
        // given
        User user = User.builder().id(1L).build();
        Product product = Product.builder()
                .id(1L)
                .name("테스트 상품")
                .price(1000)
                .stock(0)
                .build();

        Cart cart = Cart.builder()
                .id(1L)
                .user(user)
                .product(product)
                .quantity(2)
                .build();

        OrderRequest request = new OrderRequest();
        request.setUserId(1L);
        request.setCartIds(List.of(1L));

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(cartRepository.findAllById(any())).thenReturn(List.of(cart));

        // when & then
        assertThatThrownBy(() -> orderService.createOrder(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("재고가 부족");
        verify(orderRepository, never()).save(any());
    }

    @Test
    @DisplayName("장바구니가 비어 있으면 예외 발생")
    void createOrder_emptyCart() {
        // given
        User user = User.builder().id(1L).build();
        OrderRequest request = new OrderRequest();
        request.setUserId(1L);
        request.setCartIds(List.of());

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(cartRepository.findAllById(any())).thenReturn(List.of());

        // when & then
        assertThatThrownBy(() -> orderService.createOrder(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("장바구니가 비어 있습니다.");
    }
}
