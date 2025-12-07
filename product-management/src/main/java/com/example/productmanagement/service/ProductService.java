package com.example.productmanagement.service;

import com.example.productmanagement.entity.Product;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService {

    Optional<Product> getProductById(Long id);

    Product saveProduct(Product product);

    void deleteProduct(Long id);

    /**
     * Lấy tất cả sản phẩm có phân trang (dùng cho trang chủ /products khi không có
     * filter).
     */
    Page<Product> findAll(Pageable pageable);

    /**
     * Lọc sản phẩm theo Category có phân trang (dùng cho filter dropdown).
     */
    Page<Product> getProductsByCategory(String category, Pageable pageable);

    // Exercise 5
    /**
     * Tìm kiếm sản phẩm theo từ khóa (Name) có phân trang (dùng cho
     * /products/search).
     */
    Page<Product> searchProducts(String keyword, Pageable pageable);

    /**
     * Tìm kiếm nâng cao theo nhiều tiêu chí (Hiện tại không có phân trang, nếu
     * muốn, cần thêm Pageable).
     */
    // List<Product> searchProducts(String name, String category, BigDecimal
    // minPrice, BigDecimal maxPrice);

    /**
     * Lấy danh sách tất cả các danh mục độc nhất (dùng để populate dropdown
     * filter).
     */
    List<String> findAllCategories();

    /**
     * Tìm kiếm nâng cao có hỗ trợ phân trang
     */
    Page<Product> advancedSearchProducts(String name, String category, BigDecimal minPrice, BigDecimal maxPrice,
            Pageable pageable);


    //EX7
    // XÓA CÁC PHƯƠNG THỨC TRÙNG LẶP CHO EX7 (Không cần thiết khi dùng Pageable):
    // List<Product> getAllProducts(Sort sort);
    // List<Product> getAllProducts();
    // List<Product> getProductsByCategoryAndSort(String category, Sort sort);

     // ===================================
    // EXERCISE 8: STATISTICS DASHBOARD
    // ===================================

    // Thống kê chung
    long countProducts(); // Dùng findAll().getTotalElements() hoặc count()
    
    // Thống kê theo Category (cần danh sách Category trước)
    long countByCategory(String category);
    
    // Giá trị kho
    BigDecimal calculateTotalInventoryValue();
    BigDecimal calculateAverageProductPrice();
    
    // Cảnh báo
    List<Product> getLowStockAlerts(int threshold);
    
    // Sản phẩm gần đây
    List<Product> getRecentProducts();  

    //Bonus
    /** Lấy tất cả sản phẩm (dùng cho API REST) */
    List<Product> getAllProducts();
}
