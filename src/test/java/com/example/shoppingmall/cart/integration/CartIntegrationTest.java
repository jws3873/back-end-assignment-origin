package com.example.shoppingmall.cart.integration;

import com.example.shoppingmall.cart.dto.CartRequest;
import com.example.shoppingmall.cart.dto.CartResponse;
import com.example.shoppingmall.cart.entity.Cart;
import com.example.shoppingmall.cart.repository.CartRepository;
import com.example.shoppingmall.cart.service.CartService;
import com.example.shoppingmall.product.entity.Product;
import com.example.shoppingmall.product.repository.ProductRepository;
import com.example.shoppingmall.user.entity.User;
import com.example.shoppingmall.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class CartIntegrationTest {

    @Autowired private CartService cartService;
    @Autowired private UserRepository userRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private CartRepository cartRepository;

    @Test
    @DisplayName("통합 테스트 - 장바구니 추가, 조회, 수정, 삭제")
    void cartIntegrationTest() {
        // given
        User user = userRepository.save(User.builder()
                .username("테스트 사용자")
                .email("user@test.com")
                .build());

        Product product = productRepository.save(Product.builder()
                .name("테스트 상품")
                .price(1000)
                .stock(10)
                .build());

        // 1. 장바구니 추가
        CartRequest addRequest = new CartRequest();
        addRequest.setUserId(user.getId());
        addRequest.setProductId(product.getId());
        addRequest.setQuantity(2);

        cartService.addToCart(addRequest);

        // then - 추가 확인
        List<CartResponse> cartItems = cartService.getCartItems(user.getId());
        assertThat(cartItems).hasSize(1);
        assertThat(cartItems.get(0).getQuantity()).isEqualTo(2);

        // 2. 장바구니 수량 수정
        Cart existingCart = cartRepository.findByUser(user).get(0);

        CartRequest updateRequest = new CartRequest();
        updateRequest.setUserId(user.getId());
        updateRequest.setProductId(product.getId());
        updateRequest.setCartId(existingCart.getId());
        updateRequest.setQuantity(5);

        cartService.updateQuantity(updateRequest);

        Cart updatedCart = cartRepository.findById(existingCart.getId()).orElseThrow();
        assertThat(updatedCart.getQuantity()).isEqualTo(5);

        // 3. 장바구니 삭제
        cartService.removeFromCart(user.getId(), updatedCart.getId());
        List<Cart> remainingCarts = cartRepository.findByUser(user);
        assertThat(remainingCarts).isEmpty();
    }
}
