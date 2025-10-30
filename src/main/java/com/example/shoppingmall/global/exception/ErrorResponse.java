package com.example.shoppingmall.global.exception;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 예외 발생 시 클라이언트에게 전달되는 표준 응답 포맷
 */
@Getter
@Builder
public class ErrorResponse {
    private final LocalDateTime timestamp; // 예외 발생 시각
    private final int status;              // HTTP 상태 코드
    private final String error;            // 예외 타입명
    private final String message;          // 예외 메시지
    private final String path;             // 요청 경로 (Optional)
}
