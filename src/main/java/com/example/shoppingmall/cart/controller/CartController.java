package com.example.shoppingmall.cart.controller;

import com.example.shoppingmall.cart.dto.CartRequest;
import com.example.shoppingmall.cart.dto.CartResponse;
import com.example.shoppingmall.cart.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "장바구니 API", description = "장바구니 조회, 추가, 수정, 삭제 관련 API")
@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @Operation(
            summary = "장바구니 조회",
            description = "사용자 번호를 기반으로 장바구니 상품 목록을 조회"
    )
    @GetMapping("/{userId}")
    public List<CartResponse> getCartItems(@PathVariable Long userId) {
        return cartService.getCartItems(userId);
    }

    @Operation(
            summary = "장바구니 상품 추가",
            description = "상품과 수량을 해당 사용자의 장바구니에 추가한다, 이미 장바구니에 존재하는 상품이면 수량을 누적한다."
    )
    @PostMapping
    public void addToCart(@RequestBody CartRequest request) {
        cartService.addToCart(request);
    }

    @Operation(
            summary = "장바구니 상품 수량 수정",
            description = "장바구니에 존재하는 상품의 수량을 변경한다."
    )
    @PutMapping
    public void updateQuantity(@RequestBody CartRequest request) {
        cartService.updateQuantity(request);
    }

    @Operation(
            summary = "장바구니 상품 삭제",
            description = "장바구니에서 특정 상품을 제거한다."
    )
    @DeleteMapping("{userId}/{cartId}")
    public void removeFromCart(@PathVariable Long userId, @PathVariable Long cartId) {
        cartService.removeFromCart(userId, cartId);
    }

}
