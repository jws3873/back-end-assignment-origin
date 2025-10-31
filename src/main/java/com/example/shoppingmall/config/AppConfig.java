package com.example.shoppingmall.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {

    // 테스트 용의성 : RestTemplate을 @Mock으로 대체 가능
    // Spring 의존성 관리 : 스프링 컨테이너에서 관리하므로 재사용 및 유지보수 용이
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}
