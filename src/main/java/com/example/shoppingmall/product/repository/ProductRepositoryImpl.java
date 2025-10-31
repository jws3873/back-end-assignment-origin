package com.example.shoppingmall.product.repository;

import com.example.shoppingmall.product.entity.Product;
import com.example.shoppingmall.product.entity.QProduct;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Product> searchProducts(String category, String name, Integer minPrice, Integer maxPrice, Pageable pageable) {
        QProduct product = QProduct.product;

        BooleanBuilder builder = new BooleanBuilder();

        if (category != null && !category.isEmpty()) {
            builder.and(product.category.containsIgnoreCase(category));
        }

        if (name != null && !name.isEmpty()) {
            builder.and(product.name.containsIgnoreCase(name));
        }

        if (minPrice != null) {
            builder.and(product.price.goe(minPrice));
        }

        if (maxPrice != null) {
            builder.and(product.price.loe(maxPrice));
        }

        // 결과 조회
        List<Product> results = queryFactory
                .selectFrom(product)
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(product.id.desc())
                .fetch();

        // 전체 개수 조회
        long total = queryFactory
                .select(product.count())
                .from(product)
                .where(builder)
                .fetchOne();

        return new PageImpl<>(results, pageable, total);
    }
}
