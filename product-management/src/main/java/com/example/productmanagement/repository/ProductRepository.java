package com.example.productmanagement.repository;

import com.example.productmanagement.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

        // Spring Data JPA generates implementation automatically!

        // Custom query methods (derived from method names)
        Page<Product> findByCategory(String category, Pageable pageable);

        List<Product> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);

        List<Product> findByCategoryOrderByPriceAsc(String category);

        boolean existsByProductCode(String productCode);

        // All basic CRUD methods inherited from JpaRepository:
        // - findAll()
        // - findById(Long id)
        // - save(Product product)
        // - deleteById(Long id)
        // - count()
        // - existsById(Long id)

        // Exercise 5.1
        @Query("SELECT p FROM Product p WHERE " +
                        "(:name IS NULL OR p.name LIKE %:name%) AND " +
                        "(:category IS NULL OR p.category = :category) AND " +
                        "(:minPrice IS NULL OR p.price >= :minPrice) AND " +
                        "(:maxPrice IS NULL OR p.price <= :maxPrice)")
        Page<Product> searchProducts(@Param("name") String name,
                        @Param("category") String category,
                        @Param("minPrice") BigDecimal minPrice,
                        @Param("maxPrice") BigDecimal maxPrice,
                        Pageable pageable);

        // Exercise 5.2
        @Query("SELECT DISTINCT p.category FROM Product p ORDER BY p.category")
        List<String> findAllCategories();

        // Exercise 5.3
        Page<Product> findByNameContaining(String keyword, Pageable pageable);

        // XÓA BỎ CÁC PHƯƠNG THỨC BỊ TRÙNG LẶP CHO EX7:
        // List<Product> findAll(Sort sort);
        // List<Product> findByCategory(String category, Sort sort);

        // ===================================
        // EXERCISE 8: STATISTICS DASHBOARD
        // ===================================

        // Task 8.1.1: Count products by category
        @Query("SELECT COUNT(p) FROM Product p WHERE p.category = :category")
        long countByCategory(@Param("category") String category);

        // Task 8.1.2: Calculate total inventory value (price * quantity)
        @Query("SELECT SUM(p.price * p.quantity) FROM Product p")
        BigDecimal calculateTotalValue();

        // Task 8.1.3: Calculate average product price
        @Query("SELECT AVG(p.price) FROM Product p")
        BigDecimal calculateAveragePrice();

        // Task 8.1.4: Find products with low stock (quantity < threshold)
        @Query("SELECT p FROM Product p WHERE p.quantity < :threshold")
        List<Product> findLowStockProducts(@Param("threshold") int threshold);

        // Task 8.1.5: Find recent products (Helper method for Service)
        List<Product> findTop5ByOrderByCreatedAtDesc();
}
