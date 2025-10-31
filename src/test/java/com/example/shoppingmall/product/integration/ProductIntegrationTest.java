package com.example.shoppingmall.product.integration;

import com.example.shoppingmall.product.entity.Product;
import com.example.shoppingmall.product.repository.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ProductIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    @Test
    @DisplayName("상품 목록 조회 통합 테스트")
    void getProducts_integration() throws Exception {
        // given
        productRepository.save(Product.builder()
                .name("테스트 상품")
                .category("테스트 카테고리")
                .price(10000)
                .stock(50)
                .build());

        // when & then
        mockMvc.perform(get("/api/v1/products")
                        .param("category", "테스트 카테고리")
                        .param("name", "테스트 상품")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("테스트 상품"))
                .andExpect(jsonPath("$.content[0].category").value("테스트 카테고리"));
    }

}
