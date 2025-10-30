package com.example.shoppingmall.payment.entity;

import com.example.shoppingmall.order.entity.Order;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "payment")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", unique = true)
    private Order order; // 주문 (1:1)

    private String transactionId; // 외부 결재 트랜잭션 번호

    private String status; // 결제 상태 (SUCCESS / FAILED)

    private String message; // 결제 메시지 (성공/실패 상세 사유)

    private LocalDateTime createdAt; // 결제 요청 시각

}
