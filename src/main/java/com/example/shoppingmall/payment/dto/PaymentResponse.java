package com.example.shoppingmall.payment.dto;

import com.example.shoppingmall.payment.entity.Payment;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 결제 이력 응답 DTO
 */
@Getter
@Builder
public class PaymentResponse {

    private Long id;               // 결제 이력 ID
    private String transactionId;  // 트랜잭션 ID
    private String status;         // 결제 상태 (SUCCESS / FAILED)
    private String message;        // 응답 메시지
    private LocalDateTime createdAt; // 결제 요청 시각

    // Entity → DTO 변환
    public static PaymentResponse fromEntity(Payment payment) {
        return PaymentResponse.builder()
                .id(payment.getId())
                .transactionId(payment.getTransactionId())
                .status(payment.getStatus())
                .message(payment.getMessage())
                .createdAt(payment.getCreatedAt())
                .build();
    }

}
