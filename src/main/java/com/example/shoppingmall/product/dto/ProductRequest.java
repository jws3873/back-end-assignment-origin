package com.example.shoppingmall.product.dto;

import com.example.shoppingmall.global.dto.PageRequestDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductRequest extends PageRequestDto {
    private String category;  // 카테고리
    private String name;      // 상품명
    private Integer minPrice; // min 가격
    private Integer maxPrice; // max 가격
}
