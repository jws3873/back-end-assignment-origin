package com.example.shoppingmall.order.service;

import com.example.shoppingmall.cart.entity.Cart;
import com.example.shoppingmall.cart.repository.CartRepository;
import com.example.shoppingmall.global.exception.BusinessException;
import com.example.shoppingmall.order.dto.OrderRequest;
import com.example.shoppingmall.order.dto.OrderResponse;
import com.example.shoppingmall.order.entity.Order;
import com.example.shoppingmall.order.entity.OrderItem;
import com.example.shoppingmall.order.entity.OrderStatus;
import com.example.shoppingmall.order.repository.OrderRepository;
import com.example.shoppingmall.product.entity.Product;
import com.example.shoppingmall.user.entity.User;
import com.example.shoppingmall.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    /**
     * 주문 생성 (결제는 별도 단계)
     * 1. 사용자 확인
     * 2. 장바구니 상품 조회 및 검증
     * 3. 재고 확인 및 총 금액 계산
     * 4. 주문 생성 (PENDING_PAYMENT)
     * 5. 장바구니 비우기
     */
    public OrderResponse createOrder(OrderRequest request) {

        // 1. 사용자 조회
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new BusinessException("사용자를 찾을 수 없습니다.", 404));

        // 2. 장바구니 상품 조회
        List<Cart> cartItems = cartRepository.findAllById(request.getCartIds());
        if (cartItems.isEmpty()) {
            throw new BusinessException("장바구니가 비어 있습니다.", 400);
        }

        // 3. 재고 확인 및 총 금액 계산
        int totalAmount = 0;
        for (Cart item : cartItems) {
            Product product = item.getProduct();
            if (product.isSoldOut() || product.getStock() < item.getQuantity()) {
                throw new BusinessException("상품 재고가 부족합니다: " + product.getName(), 409);
            }
            totalAmount += product.getPrice() * item.getQuantity();
        }

        // 4. 주문 생성 (결제 대기 상태)
        Order order = Order.builder()
                .user(user)
                .totalAmount(totalAmount)
                .status(OrderStatus.PENDING_PAYMENT)
                .build();

        // 장바구니 → 주문상품으로 변환
        for (Cart item : cartItems) {
            OrderItem orderItem = OrderItem.builder()
                    .product(item.getProduct())
                    .quantity(item.getQuantity())
                    .price(item.getProduct().getPrice())
                    .build();
            order.addOrderItem(orderItem);
        }

        orderRepository.save(order);

        // 5. 장바구니 비우기
        cartRepository.deleteAll(cartItems);

        // 6. 응답 반환
        return OrderResponse.builder()
                .orderId(order.getId())
                .status(order.getStatus().name())
                .totalAmount(order.getTotalAmount())
                .message("주문이 생성되었습니다. 결제를 진행하세요.")
                .build();
    }

    /**
     * 주문 취소
     * 결제 완료된 주문만 취소 가능
     */
    public void cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException("주문을 찾을 수 없습니다.", 404));

        if (order.getStatus() != OrderStatus.PAYMENT_COMPLETED) {
            throw new BusinessException("결제 완료된 주문만 취소할 수 있습니다.", 400);
        }

        // 상태 변경
        order.setStatus(OrderStatus.CANCELED);
        orderRepository.save(order);
    }
}