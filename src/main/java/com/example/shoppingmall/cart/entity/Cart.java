package com.example.shoppingmall.cart.entity;

import com.example.shoppingmall.product.entity.Product;
import com.example.shoppingmall.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "cart")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // User와 N:1 관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    // Product와 N:1 관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    private int quantity; // 수량
}