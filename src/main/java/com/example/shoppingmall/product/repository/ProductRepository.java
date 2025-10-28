package com.example.shoppingmall.product.repository;

import com.example.shoppingmall.product.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
    /**
     * Spring Data JPA의 메서드 이름 기반 쿼리 자동 생성 기능 활용
     * CategoryContaining  : category 컬럼에 부분 일치 검색 (LIKE %?%)
     * NameContaining      : name 컬럼에 부분 일치 검색 (LIKE %?%)
     * PriceBetween        : 가격 범위 검색 (BETWEEN minPrice AND maxPrice)
     * Pageable            : 페이징 및 정렬 처리 (LIMIT / OFFSET)
     */
    Page<Product> findByCategoryContainingAndNameContainingAndPriceBetween(
            String category, String name, int minPrice, int maxPrice, Pageable pageable
    );
}
