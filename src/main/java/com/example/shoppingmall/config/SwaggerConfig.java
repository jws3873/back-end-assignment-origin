package com.example.shoppingmall.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("ShoppingMall API")
                        .description("상품, 장바구니, 주문/결제 기능을 포함한 쇼핑몰 API 문서")
                        .version("v1.0.0"));
    }

}
