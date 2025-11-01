package com.example.shoppingmall.global.security;

import com.example.shoppingmall.global.security.filter.MockAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final MockAuthFilter mockAuthFilter;

    public SecurityConfig(MockAuthFilter mockAuthFilter) {
        this.mockAuthFilter = mockAuthFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll() // Swagger 관련 경로 전체 허용
                        //.anyRequest().permitAll() // 인증 없이 전체 허용 (테스트용)
                        .anyRequest().authenticated() // 인증된 요청만 허용
                )
                .addFilterBefore(mockAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
