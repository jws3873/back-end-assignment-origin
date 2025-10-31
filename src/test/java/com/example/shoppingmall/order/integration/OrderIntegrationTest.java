package com.example.shoppingmall.order.integration;

import com.example.shoppingmall.cart.entity.Cart;
import com.example.shoppingmall.cart.repository.CartRepository;
import com.example.shoppingmall.order.dto.OrderRequest;
import com.example.shoppingmall.order.dto.OrderResponse;
import com.example.shoppingmall.order.entity.Order;
import com.example.shoppingmall.order.entity.OrderStatus;
import com.example.shoppingmall.order.repository.OrderRepository;
import com.example.shoppingmall.order.service.OrderService;
import com.example.shoppingmall.product.entity.Product;
import com.example.shoppingmall.product.repository.ProductRepository;
import com.example.shoppingmall.user.entity.User;
import com.example.shoppingmall.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class OrderIntegrationTest {

    @Autowired private UserRepository userRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private CartRepository cartRepository;
    @Autowired private OrderRepository orderRepository;
    @Autowired private OrderService orderService;

    @Test
    @DisplayName("통합 테스트 - 주문 생성 시 장바구니가 비워지고 상태는 결제 대기")
    void createOrder_integration() {
        // given
        User user = userRepository.save(User.builder()
                .username("테스트 사용자")
                .email("test@test.com")
                .build());

        Product product = productRepository.save(Product.builder()
                .name("테스트 상품")
                .price(1000)
                .stock(10)
                .build());

        Cart cart = cartRepository.save(Cart.builder()
                .user(user)
                .product(product)
                .quantity(2)
                .build());

        OrderRequest request = new OrderRequest();
        request.setUserId(user.getId());
        request.setCartIds(List.of(cart.getId()));

        // when
        OrderResponse response = orderService.createOrder(request);

        // then
        // 1. 주문이 정상적으로 생성되었는지
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OrderStatus.PENDING_PAYMENT.name());
        assertThat(response.getTotalAmount()).isEqualTo(2000);

        // 2. 주문이 DB에 실제로 저장되었는지
        Order savedOrder = orderRepository.findById(response.getOrderId())
                .orElseThrow(() -> new AssertionError("주문이 DB에 존재하지 않습니다."));
        assertThat(savedOrder.getOrderItems()).hasSize(1);
        assertThat(savedOrder.getTotalAmount()).isEqualTo(2000);
        assertThat(savedOrder.getStatus()).isEqualTo(OrderStatus.PENDING_PAYMENT);

        // 3. 장바구니 비워졌는지
        List<Cart> remainingCarts = cartRepository.findByUser(user);
        assertThat(remainingCarts).isEmpty();
    }
}
