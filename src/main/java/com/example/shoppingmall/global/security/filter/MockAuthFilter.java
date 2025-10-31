package com.example.shoppingmall.global.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class MockAuthFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // Authorization 헤더에서 토큰 추출
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {

            // "Bearer mock-user-1" → "mock-user-1"
            String token = authHeader.substring(7).trim();

            // 임시 토큰 규칙: "mock-user-{userId}" 형태
            if (token.startsWith("mock-user-")) {
                try {
                    Long userId = Long.parseLong(token.replace("mock-user-", ""));

                    // SecurityContext에 Authentication 저장
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    userId, // principal
                                    null,   // credentials
                                    null    // authorities
                            );
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                } catch (NumberFormatException e) {
                    logger.warn("Invalid mock token format: " + token);
                }
            }
        }

        filterChain.doFilter(request, response);
    }
}
