package com.example.shoppingmall.order.repository;

import com.example.shoppingmall.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> { }
