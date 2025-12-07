package com.example.productmanagement.service;

import com.example.productmanagement.entity.Product;
import com.example.productmanagement.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Product> findAll(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    // Phương thức cũ đã bị xóa: public List<Product> getAllProducts()

    @Override
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    @Override
    public Product saveProduct(Product product) {
        // Validation logic can go here
        return productRepository.save(product);
    }

    @Override
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    /**
     * Search Products (Name) with Pagination (ĐÃ SỬA LỖI CHÍNH TẢ REPOSITORY)
     */
    @Override
    @Transactional(readOnly = true)
    public Page<Product> searchProducts(String keyword, Pageable pageable) {
        if (keyword == null || keyword.trim().isEmpty()) {
            // Nếu keyword trống, trả về tất cả sản phẩm có phân trang
            return productRepository.findAll(pageable);
        }

        // SỬA LỖI CHÍNH TẢ: findByNameContainings -> findByNameContaining
        return productRepository.findByNameContaining(keyword, pageable);
    }

    // Phương thức cũ đã bị xóa: public List<Product> searchProducts(String keyword)

    /**
     * THAY THẾ getProductsByCategory() cũ (Category Filter có phân trang)
     */
    @Override
    @Transactional(readOnly = true)
    public Page<Product> getProductsByCategory(String category, Pageable pageable) {
        // Giả định Repository có phương thức Page<Product> findByCategory(String
        // category, Pageable pageable)
        return productRepository.findByCategory(category, pageable);
    }

    // Exercise 5
    // PHƯƠNG THỨC SAU ĐÃ BỊ XÓA HOẶC KHÔNG CẦN THIẾT NỮA:
    // @Override
    // public List<Product> searchProducts(String name, String category, BigDecimal
    // minPrice, BigDecimal maxPrice) {
    // // Xử lý các tham số rỗng (empty strings) thành null trước khi truyền vào
    // // Repository
    // String finalName = (name != null && !name.trim().isEmpty()) ? name : null;
    // String finalCategory = (category != null && !category.trim().isEmpty()) ?
    // category : null;

    // // Gọi phương thức Repository đã được tùy chỉnh
    // return productRepository.searchProducts(finalName, finalCategory, minPrice,
    // maxPrice);
    // }

    @Override
    public List<String> findAllCategories() {
        return productRepository.findAllCategories();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Product> advancedSearchProducts(String name, String category, BigDecimal minPrice, BigDecimal maxPrice,
            Pageable pageable) {
        String finalName = (name != null && !name.trim().isEmpty()) ? name : null;
        String finalCategory = (category != null && !category.trim().isEmpty()) ? category : null;

        // Call the Repository method with the new Pageable argument
        return productRepository.searchProducts(finalName, finalCategory, minPrice, maxPrice, pageable);
    }

    // EX7
    // XÓA 3 phương thức List<Product> cũ (getAllProducts, getAllProducts(Sort),
    // getProductsByCategoryAndSort)
    // vì chúng đã được thay thế bằng các phương thức Page<Product> ở trên.

    // ===================================
    // EXERCISE 8: STATISTICS DASHBOARD
    // ===================================

    @Override
    @Transactional(readOnly = true)
    public long countProducts() {
        return productRepository.count(); // Kế thừa từ JpaRepository
    }

    @Override
    @Transactional(readOnly = true)
    public long countByCategory(String category) {
        return productRepository.countByCategory(category);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal calculateTotalInventoryValue() {
        return productRepository.calculateTotalValue();
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal calculateAverageProductPrice() {
        return productRepository.calculateAveragePrice();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> getLowStockAlerts(int threshold) {
        return productRepository.findLowStockProducts(threshold);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> getRecentProducts() {
        // Sử dụng phương thức findTop5ByOrderByCreatedAtDesc() đã thêm
        return productRepository.findTop5ByOrderByCreatedAtDesc();
    }

    // Bonus
    @Override
    @Transactional(readOnly = true)
    public List<Product> getAllProducts() {
        // Kế thừa từ JpaRepository, lấy tất cả mà không phân trang
        return productRepository.findAll();
    }
}