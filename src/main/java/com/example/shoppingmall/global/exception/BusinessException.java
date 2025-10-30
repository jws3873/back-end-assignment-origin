package com.example.shoppingmall.global.exception;

import lombok.Getter;

/**
 * 비즈니스 로직에서 발생하는 예외를 표현하는 커스텀 예외 클래스
 */
@Getter
public class BusinessException extends RuntimeException{

    private final int status; // HTTP 상태 코드

    public BusinessException(String message, int status) {
        super(message);
        this.status = status;
    }

}
