package com.example.shoppingmall.global.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PageRequestDto {
    private int page = 0; // 기본값 첫 페이지
    private int size = 10; // 기본값 페이지당 데이터 수
}
