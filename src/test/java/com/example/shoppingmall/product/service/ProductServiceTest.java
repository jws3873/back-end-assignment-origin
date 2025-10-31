package com.example.shoppingmall.product.service;

import com.example.shoppingmall.product.dto.ProductRequest;
import com.example.shoppingmall.product.dto.ProductResponse;
import com.example.shoppingmall.product.entity.Product;
import com.example.shoppingmall.product.repository.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @Test
    @DisplayName("상품 목록 조회 테스트")
    void getProducts_success() {

        // given
        Product mockProduct = Product.builder()
                .id(1L)
                .name("테스트 상품")
                .category("테스트 카테고리")
                .price(10000)
                .stock(50)
                .build();

        Page<Product> mockPage = new PageImpl<>(List.of(mockProduct));
        when(productRepository.findByCategoryContainingAndNameContainingAndPriceBetween(
                any(String.class),
                any(String.class),
                any(Integer.class),
                any(Integer.class),
                any(PageRequest.class)
        )).thenReturn(mockPage);

        ProductRequest request = new ProductRequest();

        // when
        Page<ProductResponse> result = productService.getProducts(request);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo("테스트 상품");
    }

}
