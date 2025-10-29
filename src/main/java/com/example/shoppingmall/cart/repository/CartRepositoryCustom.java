package com.example.shoppingmall.cart.repository;

import com.example.shoppingmall.cart.entity.Cart;
import com.example.shoppingmall.user.entity.User;

import java.util.List;

public interface CartRepositoryCustom {

    /**
     * 특정 사용자의 장바구니 상품 목록 조회
     *
     * @param user 조회할 사용자
     * @return 사용자의 장바구니 상품 목록
     */
    List<Cart> findByUser(User user);

    /**
     * 특정 사용자와 상품을 기준으로 장바구니 상품 삭제
     *
     * @param user       사용자
     * @param productId  삭제할 상품 번호
     */
    void deleteByUserAndProductId(User user, Long productId);

}
