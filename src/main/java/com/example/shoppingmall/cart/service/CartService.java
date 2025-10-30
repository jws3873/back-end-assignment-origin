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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CartService {

    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    /**
     * 장바구니 조회
     * 특정 사용자의 장바구니 상품 목록 반환
     *
     * @param userId 조회할 사용자 번호
     * @return 장바구니 상품 목록
     */
    @Transactional(readOnly = true)
    public List<CartResponse> getCartItems(Long userId) {

        // 사용자 번호(userId)로 DB에서 User조회
        // Optional.orElseThrow()를 사용하여 없으면 예외 처리
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("사용자를 찾을 수 없습니다.", 404));

        // 특정 사용자의 장바구니 리스트 반환
        // map() 장바구니 엔터티Cart를 응답 DTO(CartResponse)로 변환 후 리스트로 반환
        return cartRepository.findByUser(user).stream()
                .map(cart -> CartResponse.builder()
                        .cartId(cart.getId())
                        .productId(cart.getProduct().getId())
                        .productName(cart.getProduct().getName())
                        .price(cart.getProduct().getPrice())
                        .quantity(cart.getQuantity())
                        .soldOut(cart.getProduct().isSoldOut())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * 장바구니 상품 추가
     * 이미 장바구니에 있는 상품이면 수량 누적, 없으면 새로 추가
     *
     * @param request 사용자 번호, 상품 번호, 추가 수량
     */
    public void addToCart(CartRequest request) {

        // 사용자 조회
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new BusinessException("사용자를 찾을 수 없습니다.", 404));

        // 상품 조회
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new BusinessException("사용자를 찾을 수 없습니다.", 404));

        // 품절 확인
        if (product.isSoldOut()) {
            throw new BusinessException("품절 상품은 장바구니에 추가할 수 없습니다.", 409);
        }

        // filter 장바구니에서 같은 상품(productId)이 있는지 확인
        // findFirst 조건에 맞는 요소 만환
        // orElse 조건에 맞는 장바구니 항목이 없으면 새 Cart객체 생성
        // 기존 장바구니 상품이면 가져오고, 없으면 새 객체 생성
        Cart cart = cartRepository.findByUser(user).stream()
                .filter(c -> c.getProduct().getId().equals(product.getId()))
                .findFirst()
                .orElse(Cart.builder()
                        .user(user)
                        .product(product)
                        .quantity(0)
                        .build());

        // 기존 수량에 요청 수량 누적
        cart.setQuantity(cart.getQuantity() + request.getQuantity());

        // DB에 저장
        cartRepository.save(cart);

    }

    /**
     * 장바구니 상품 수량 수정
     *
     * @param request 사용자 번호, 상품 번호, 수정할 수량
     */
    public void updateQuantity(CartRequest request) {

        // 사용자 검증
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new BusinessException("사용자를 찾을 수 없습니다.", 404));

        // 상품 검증
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new BusinessException("상품을 찾을 수 없습니다.", 404));

        // 장바구니 항목 검증 (cartId로 조회)
         Cart cart = cartRepository.findById(request.getCartId())
                 .orElseThrow(() -> new BusinessException("장바구니 항목을 찾을 수 없습니다.", 404));

        // 수량 수정 후 저장
        if (request.getQuantity() <= 0) {
            throw new BusinessException("수량은 1 이상이어야 합니다.", 409);
        }

        // 수량을 요청한 값으로 변경 후 저장
        cart.setQuantity(request.getQuantity());
        cartRepository.save(cart);
    }

    /**
     * 장바구니 상품 삭제
     *
     * @param userId 사용자 번호
     * @param cartId 장바구니 번호
     */
    public void removeFromCart(Long userId, Long cartId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("사용자를 찾을 수 없습니다.", 404));
        // 사용자와 장바구니 ID로 직접 DB 삭제
        cartRepository.deleteByUserAndCartId(user, cartId);
    }

}
