package com.example.shoppingmall.order.repository;

import com.example.shoppingmall.order.entity.Order;
import com.example.shoppingmall.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUser(User user);
}
