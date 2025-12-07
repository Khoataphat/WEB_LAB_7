package com.example.productmanagement.controller;

import com.example.productmanagement.entity.Product;
import com.example.productmanagement.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductRestController {

    private final ProductService productService;

    public ProductRestController(ProductService productService) {
        this.productService = productService;
    }

    // GET /api/products : Lấy tất cả sản phẩm
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        // Sử dụng Service findAll() mà không cần Pageable để lấy List<Product>
        // Ta sẽ tạo thêm một phương thức getAllProducts() không tham số trong ProductService
        List<Product> products = productService.getAllProducts(); 
        return ResponseEntity.ok(products); 
    }

    // GET /api/products/{id} : Lấy sản phẩm theo ID
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProduct(@PathVariable Long id) {
        return productService.getProductById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build()); // Trả về 404 nếu không tìm thấy
    }

    // POST /api/products : Tạo sản phẩm mới
    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        try {
            // Đảm bảo ID là null để tạo mới
            product.setId(null); 
            Product savedProduct = productService.saveProduct(product);
            // Trả về 201 Created và URI của tài nguyên mới
            return ResponseEntity
                    .created(URI.create("/api/products/" + savedProduct.getId()))
                    .body(savedProduct);
        } catch (IllegalArgumentException e) {
             // Xử lý lỗi nghiệp vụ (ví dụ: Product Code bị trùng)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    // PUT /api/products/{id} : Cập nhật sản phẩm
    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody Product product) {
        if (!productService.getProductById(id).isPresent()) {
            return ResponseEntity.notFound().build(); // Không tìm thấy sản phẩm để cập nhật
        }
        
        try {
            product.setId(id); // Thiết lập ID từ path variable
            Product updatedProduct = productService.saveProduct(product);
            return ResponseEntity.ok(updatedProduct);
        } catch (IllegalArgumentException e) {
            // Xử lý lỗi nghiệp vụ (ví dụ: Product Code bị trùng)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    // DELETE /api/products/{id} : Xóa sản phẩm
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        if (!productService.getProductById(id).isPresent()) {
            return ResponseEntity.notFound().build(); // Không tìm thấy sản phẩm, trả về 404
        }
        
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build(); // Trả về 204 No Content
    }
}