package com.example.shoppingmall.order.entity;

import com.example.shoppingmall.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user; // 주문자 정보

    private int totalAmount; // 총 결제 금액

    @Enumerated(EnumType.STRING)
    private OrderStatus status; // 주문 상태 (Enum)

    private String transactionId; // 외부 결제 트랜잭션 ID

    /**
     * 주문 상품 목록 (OrderItem과 1:N 관계)
     * 주문이 삭제되면 관련 주문상품도 함께 삭제됨
     */
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default // @Builder가 객체를 만들 때 orderItems를 null로 둔 상태로 생성하지 않도록
    private List<OrderItem> orderItems = new ArrayList<>();

    // 연관관계 편의 메서드
    public void addOrderItem(OrderItem item) {
        orderItems.add(item);
        item.setOrder(this);
    }
}
