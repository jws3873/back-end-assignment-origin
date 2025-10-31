package com.example.shoppingmall.cart.service;

import com.example.shoppingmall.cart.dto.CartRequest;
import com.example.shoppingmall.cart.dto.CartResponse;
import com.example.shoppingmall.cart.entity.Cart;
import com.example.shoppingmall.cart.repository.CartRepository;
import com.example.shoppingmall.global.exception.BusinessException;
import com.example.shoppingmall.product.entity.Product;
import com.example.shoppingmall.product.repository.ProductRepository;
import com.example.shoppingmall.user.entity.User;
import com.example.shoppingmall.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private CartService cartService;

    @Test
    @DisplayName("장바구니 조회")
    void getCartItems() {
        // given
        User user = User.builder()
                .id(1L)
                .build();

        Product product = Product.builder()
                .id(10L)
                .name("테스트 상품")
                .price(10000)
                .stock(5)
                .build();

        Cart cart = Cart.builder()
                .id(100L)
                .user(user)
                .product(product)
                .quantity(2)
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(cartRepository.findByUser(user)).thenReturn(List.of(cart));

        // when
        List<CartResponse> result = cartService.getCartItems(1L);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getProductName()).isEqualTo("테스트 상품");
        assertThat(result.get(0).getQuantity()).isEqualTo(2);
    }

    @Test
    @DisplayName("장바구니 추가 - 새 상품")
    void addToCart_newItem() {
        // given
        User user = User.builder()
                .id(1L)
                .build();

        Product product = Product.builder()
                .id(10L)
                .name("테스트 상품")
                .price(10000)
                .stock(10)
                .build();

        CartRequest request = new CartRequest();
        request.setUserId(1L);
        request.setProductId(10L);
        request.setQuantity(2);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(productRepository.findById(10L)).thenReturn(Optional.of(product));
        when(cartRepository.findByUser(user)).thenReturn(List.of());

        // when
        cartService.addToCart(request);

        // then
        verify(cartRepository, times(1)).save(any(Cart.class));
    }

    @Test
    @DisplayName("장바구니 수량 수정")
    void updateQuantity() {
        // given
        User user = User.builder()
                .id(1L)
                .build();

        Product product = Product.builder()
                .id(10L)
                .name("테스트 상품")
                .price(10000)
                .stock(10)
                .build();

        Cart cart = Cart.builder()
                .id(100L)
                .user(user)
                .product(product)
                .quantity(2)
                .build();

        CartRequest request = new CartRequest();
        request.setUserId(1L);
        request.setProductId(10L);
        request.setCartId(100L);
        request.setQuantity(5);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(productRepository.findById(10L)).thenReturn(Optional.of(product));
        when(cartRepository.findById(100L)).thenReturn(Optional.of(cart));

        // when
        cartService.updateQuantity(request);

        // then
        assertThat(cart.getQuantity()).isEqualTo(5);
        verify(cartRepository).save(cart);
    }

    @Test
    @DisplayName("장바구니 삭제")
    void removeFromCart() {
        // given
        User user = User.builder()
                .id(1L)
                .build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // when
        cartService.removeFromCart(1L, 100L);

        // then
        verify(cartRepository).deleteByUserAndCartId(user, 100L);
    }

    @Test
    @DisplayName("존재하지 않는 사용자 예외 발생")
    void getCartItems_userNotFound() {
        // given
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> cartService.getCartItems(99L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("사용자를 찾을 수 없습니다.");
    }

}
