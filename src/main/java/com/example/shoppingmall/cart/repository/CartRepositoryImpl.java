package com.example.shoppingmall.cart.repository;

import com.example.shoppingmall.cart.entity.Cart;
import com.example.shoppingmall.user.entity.User;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;
import com.example.shoppingmall.cart.entity.QCart;

import java.util.List;

@Repository
public class CartRepositoryImpl implements CartRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public CartRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<Cart> findByUser(User user) {
        return queryFactory
                .selectFrom(QCart.cart)
                .where(QCart.cart.user.eq(user))
                .fetch();
    }

    @Override
    public void deleteByUserAndProductId(User user, Long productId) {
        queryFactory
                .delete(QCart.cart)
                .where(QCart.cart.user.eq(user)
                        .and(QCart.cart.product.id.eq(productId)))
                .execute();
    }
}
