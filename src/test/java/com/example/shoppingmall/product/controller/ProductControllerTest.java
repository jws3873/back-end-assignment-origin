package com.example.shoppingmall.product.controller;

import com.example.shoppingmall.product.dto.ProductResponse;
import com.example.shoppingmall.product.service.ProductService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService productService;

    @Test
    @DisplayName("상품 목록 조회 API")
    void getProducts_success() throws Exception {

        // given
        ProductResponse mockProduct = ProductResponse.builder()
                .id(10L)
                .name("testName")
                .category("testCategory")
                .price(1000)
                .stock(10)
                .build();

        Page<ProductResponse> mockPage = new PageImpl<>(List.of(mockProduct));
        when(productService.getProducts(any())).thenReturn(mockPage); // mockPage 리턴

        // when & then 요청 실행 + 응답 검증
        mockMvc.perform(get("/api/v1/products")
                        .param("name", "testName")
                        .param("category", "testCategory")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("testName"))
                .andExpect(jsonPath("$.content[0].category").value("testCategory"));
    }

}
