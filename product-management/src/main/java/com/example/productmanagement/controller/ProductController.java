package com.example.productmanagement.controller;

import com.example.productmanagement.entity.Product;
import com.example.productmanagement.service.FileStorageService;
import com.example.productmanagement.service.ProductService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.math.BigDecimal;

import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;

import com.example.productmanagement.service.FileStorageService;

@Controller
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;
    private final FileStorageService fileStorageService;

    public ProductController(ProductService productService, FileStorageService fileStorageService) {
        this.productService = productService;
        this.fileStorageService = fileStorageService;
    }

    // Phương thức chung để thêm Categories vào Model
    private void addCommonAttributes(Model model) {
        List<String> categories = productService.findAllCategories();
        model.addAttribute("categories", categories);
    }

    // List all products
    // List all products (bao gồm cả phân trang và lọc Category)
    @GetMapping({ "", "/" }) // Giả định mapping là "/products"
    public String listProducts(
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sortBy, // THAM SỐ SORTING MỚI
            @RequestParam(defaultValue = "asc") String sortDir, // THAM SỐ SORTING MỚI
            Model model) {

        // 1. XỬ LÝ SORTING
        Sort sort = Sort.unsorted();
        if (sortBy != null && !sortBy.isEmpty()) {
            sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        }

        // 2. TẠO PAGEABLE KẾT HỢP PAGINATION VÀ SORTING
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Product> productPage;

        // 3. XỬ LÝ FILTERING VÀ GỌI SERVICE
        if (category != null && !category.isEmpty()) {
            // Phải đảm bảo Service/Repository có phương thức findByCategory(..., Pageable)
            productPage = productService.getProductsByCategory(category, pageable);
            model.addAttribute("selectedCategory", category);
        } else {
            // Nếu không có filter, hiển thị tất cả sản phẩm có phân trang và sắp xếp
            productPage = productService.findAll(pageable);
        }

        // 4. TRUYỀN THUỘC TÍNH VÀO MODEL
        model.addAttribute("products", productPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productPage.getTotalPages());
        model.addAttribute("sortBy", sortBy); // Cần thiết để duy trì trạng thái sắp xếp
        model.addAttribute("sortDir", sortDir); // Cần thiết để đảo chiều sắp xếp

        addCommonAttributes(model);
        return "product-list";
    }

    // Show form for new product
    @GetMapping("/new")
    public String showNewForm(Model model) {
        Product product = new Product();
        model.addAttribute("product", product);
        addCommonAttributes(model);
        return "product-form";
    }

    // Show form for editing product
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        return productService.getProductById(id)
                .map(product -> {
                    model.addAttribute("product", product);
                    addCommonAttributes(model);
                    return "product-form";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("error", "Product not found");
                    return "redirect:/products";
                });
    }

    // Save product (create or update)
    @PostMapping("/save")
    public String saveProduct(
            @Valid @ModelAttribute("product") Product product, // Kích hoạt Validation
            BindingResult result, // Chứa kết quả Validation
            @RequestParam("imageFile") MultipartFile imageFile,
            Model model, // Để truyền dữ liệu lại nếu có lỗi
            RedirectAttributes redirectAttributes) {

        // 1. KIỂM TRA LỖI XÁC THỰC
        if (result.hasErrors()) {
            addCommonAttributes(model);
            // Nếu có lỗi, KHÔNG LƯU, và trả lại view form
            // Cần đảm bảo Model có đủ dữ liệu (ví dụ: product object, categories nếu cần)
            // LƯU Ý: Nếu đây là trang NEW, bạn cần gọi addCommonAttributes(model)
            // Nếu là trang EDIT, product object đã đủ.
            return "product-form";
        }

        // 2. XỬ LÝ LỖI NGHIỆP VỤ (Ví dụ: Trùng Product Code)
        try {
            // Xử lý Upload File nếu có file mới
            if (!imageFile.isEmpty()) {
                String fileName = fileStorageService.storeFile(imageFile);
                product.setImagePath(fileName); // Lưu tên file vào Entity
            } else if (product.getId() == null || (product.getImagePath() == null || product.getImagePath().isEmpty())) {
                // Nếu tạo mới mà không có file, gán giá trị mặc định nếu cần
                // Hoặc giữ nguyên nếu là update và không có file mới
                product.setImagePath(null); 
            }
            // Lưu sản phẩm nếu không có lỗi validation
            productService.saveProduct(product);

            redirectAttributes.addFlashAttribute("message",
                    product.getId() == null ? "Product added successfully!" : "Product updated successfully!");
        } catch (Exception e) {
            // Xử lý lỗi từ Service/Repository (ví dụ: Unique Constraint violation)
            redirectAttributes.addFlashAttribute("error", "Error saving product: " + e.getMessage());
            return "redirect:/products";
        }

        return "redirect:/products";
    }

    // Delete product
    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            productService.deleteProduct(id);
            redirectAttributes.addFlashAttribute("message", "Product deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error deleting product: " + e.getMessage());
        }
        return "redirect:/products";
    }

    // Search products
    @GetMapping("/search")
    public String searchProducts(
            @RequestParam("keyword") String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            Model model) {

        // 1. XỬ LÝ SORTING (Sao chép từ listProducts)
        Sort sort = Sort.unsorted();
        if (sortBy != null && !sortBy.isEmpty()) {
            sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        }

        // 1. Tạo đối tượng Pageable
        // Sử dụng PageRequest.of(page, size)
        Pageable pageable = PageRequest.of(page, size, sort);

        // 2. Gọi Service và nhận Page<Product>
        Page<Product> productPage = productService.searchProducts(keyword, pageable);

        // 3. Đặt các thuộc tính cần thiết vào Model
        model.addAttribute("products", productPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productPage.getTotalPages());
        model.addAttribute("keyword", keyword); // Giữ lại keyword trong Model
        // 4. Cần thêm danh sách categories nếu bạn vẫn muốn hiển thị bộ lọc
        List<String> categories = productService.findAllCategories();
        model.addAttribute("categories", categories);

        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);

        return "product-list";
    }

    // Exercise 5: Advanced search
    @GetMapping("/advanced-search")
    public String advancedSearch(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(defaultValue = "0") int page, // Tham số phân trang
            @RequestParam(defaultValue = "10") int size, // Tham số phân trang
            @RequestParam(required = false) String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            Model model) {

        // 1. XỬ LÝ SORTING (Sao chép từ listProducts)
        Sort sort = Sort.unsorted();
        if (sortBy != null && !sortBy.isEmpty()) {
            sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        }

        Pageable pageable = PageRequest.of(page, size, sort);

        // 1. Gọi Service và nhận Page<Product>
        Page<Product> productPage = productService.advancedSearchProducts(name, category, minPrice, maxPrice, pageable);

        // 2. Đặt dữ liệu phân trang vào Model
        model.addAttribute("products", productPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productPage.getTotalPages());

        // 3. Đặt lại các tham số tìm kiếm vào Model để giữ lại giá trị trên form
        model.addAttribute("searchName", name);
        model.addAttribute("searchCategory", category);
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);

        addCommonAttributes(model);
        model.addAttribute("selectedCategory", category);
        model.addAttribute("sortBy", sortBy); 
        model.addAttribute("sortDir", sortDir);

        // 4. Trả về view danh sách sản phẩm
        return "product-list";
    }

}
