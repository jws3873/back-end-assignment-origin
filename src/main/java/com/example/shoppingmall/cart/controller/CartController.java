package com.example.shoppingmall.cart.controller;

import com.example.shoppingmall.cart.dto.CartRequest;
import com.example.shoppingmall.cart.dto.CartResponse;
import com.example.shoppingmall.cart.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    /**
     * 장바구니 조회 API
     * 특정 사용자의 장바구니에 담긴 상품 목록을 조회한다.
     *
     * @param userId 조회할 사용자 번호
     * @return 해당 사용자의 장바구니 상품 목록 (CartResponse List)
     */
    @GetMapping("/{userId}")
    public List<CartResponse> getCartItems(@PathVariable Long userId) {
        return cartService.getCartItems(userId);
    }

    /**
     * 장바구니 상품 추가 API
     * 요청 DTO에 포함된 상품과 수량을 해당 사용자의 장바구니에 추가한다.
     * 이미 장바구니에 존재하는 상품이면 수량을 누적한다.
     *
     * @param request 사용자 번호, 상품 번호, 추가 수량
     */
    @PostMapping
    public void addToCart(@RequestBody CartRequest request) {
        cartService.addToCart(request);
    }

    /**
     * 장바구니 상품 수량 수정 API
     * 장바구니에 존재하는 상품의 수량을 변경한다.
     *
     * @param request 사용자 번호, 상품 번호, 변경할 수량
     */
    @PutMapping
    public void updateQuantity(@RequestBody CartRequest request) {
        cartService.updateQuantity(request);
    }

    /**
     * 장바구니 상품 삭제 API
     * 장바구니에서 특정 상품을 제거한다.
     *
     * @param userId 사용자 번호
     * @param cartId 장바구니 번호
     */
    @DeleteMapping("{userId}/{cartId}")
    public void removeFromCart(@PathVariable Long userId, @PathVariable Long cartId) {
        cartService.removeFromCart(userId, cartId);
    }

}
