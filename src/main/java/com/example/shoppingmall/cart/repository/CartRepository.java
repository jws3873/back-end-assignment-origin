package com.example.shoppingmall.cart.repository;

import com.example.shoppingmall.cart.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, Long>, CartRepositoryCustom { }
