package com.example.shoppingmall.product.dto;

import com.example.shoppingmall.global.dto.PageRequestDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductSearchRequest extends PageRequestDto {
    private String category = "";     // 기본값: 전체 검색
    private String name = "";         // 기본값: 전체 검색
    private int minPrice = 0;         // 최소 가격, 기본값 0
    private int maxPrice = 9999999;   // 최대 가격, 기본값 제한 없음
}
