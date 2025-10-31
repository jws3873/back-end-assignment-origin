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
import com.example.shoppingmall.payment.entity.Payment;
import com.example.shoppingmall.payment.service.PaymentService;
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
    private final PaymentService paymentService;

    /**
     * 주문 생성 및 결제 처리
     *
     * 1. 장바구니 상품 조회
     * 2. 총 금액 계산 및 재고 확인
     * 3. 주문 생성
     * 4. 외부 결제 요청
     * 5. 결제 결과 저장
     * 6. 성공 시 재고 차감 및 장바구니 비움
     */
    public OrderResponse createOrder(OrderRequest request) {

        // 사용자 확인
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new BusinessException("사용자를 찾을 수 없습니다.", 404));

        // 장바구니 항목 조회
        List<Cart> cartItems = cartRepository.findAllById(request.getCartIds());
        if( cartItems.isEmpty() ) {
            throw new BusinessException("장바구니가 비어 있습니다.", 400);
        }

        // 총 금액 계산 및 재고 확인
        int totalAmount = 0;
        for (Cart item : cartItems) {
            Product product = item.getProduct();
            // 재고 부족 또는 품절 상태 확인
            if( product.isSoldOut() || product.getStock() < item.getQuantity()) {
                throw new BusinessException("상품 재고가 부족합니다: " + product.getName(), 409);
            }
            totalAmount += product.getPrice() * item.getQuantity();
        }

        // 주문 엔터티 생성 (CREATED 상태)
        Order order = Order.builder()
                .user(user)
                .totalAmount(totalAmount)
                .status(OrderStatus.CREATED)
                .build();

        // 장바구니 → 주문상품(OrderItem)으로 변환
        for (Cart item : cartItems) {
            Product product = item.getProduct();

            OrderItem orderItem = OrderItem.builder()
                    .product(product)
                    .quantity(item.getQuantity())
                    .price(product.getPrice())
                    .build();

            order.addOrderItem(orderItem);
        }

        orderRepository.save(order);

        // 결제 요청 전 상태 변경 (PAYMENT_PENDING)
        order.setStatus(OrderStatus.PAYMENT_PENDING);

        // 결제 처리
        Payment payment = paymentService.processPayment(order);

        // 결제 결과 반영
        if("SUCCESS".equals(payment.getStatus())) {
            for (Cart item : cartItems) {
                Product product = item.getProduct();
                product.setStock(product.getStock() - item.getQuantity());
            }
            order.setStatus(OrderStatus.PAYMENT_COMPLETED);
            orderRepository.save(order);
            cartRepository.deleteAll(cartItems); // 장바구니 비우기
        } else {
            order.setStatus(OrderStatus.PAYMENT_FAILED);
        }

        return OrderResponse.builder()
                .orderId(order.getId())
                .status(order.getStatus().name())
                .totalAmount(order.getTotalAmount())
                .transactionId(payment.getTransactionId())
                .message(payment.getMessage())
                .build();
    }
}
