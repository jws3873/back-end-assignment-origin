package com.example.shoppingmall.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.Parameter;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("ShoppingMall API")
                        .version("v1.0")
                        .description("쇼핑몰 서비스 API 명세서"));
    }

    /**
     * 전역 헤더(X-USER-ID) 등록
     */
    @Bean
    public OpenApiCustomizer globalHeaderCustomizer() {
        return openApi -> openApi.getPaths().values().forEach(pathItem ->
                pathItem.readOperations().forEach(operation ->
                        operation.addParametersItem(new Parameter()
                                .in("header")
                                .schema(new StringSchema())
                                .name("Authorization")
                                .description("임시 인증용 (예: Authorization: Bearer mock-user-1)")
                                .required(false)
                        )
                )
        );
    }

}
