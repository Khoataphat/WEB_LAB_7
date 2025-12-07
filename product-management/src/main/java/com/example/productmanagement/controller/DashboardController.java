package com.example.productmanagement.controller;

import com.example.productmanagement.entity.Product;
import com.example.productmanagement.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/dashboard") // <-- BASE MAPPING
public class DashboardController {

    private final ProductService productService;

    @Autowired
    public DashboardController(ProductService productService) {
        this.productService = productService;
    }

    // Mapping method: Resolves to /dashboard
    @GetMapping 
    public String showDashboard(Model model) {
        // 1. Thống kê cơ bản
        long totalCount = productService.countProducts();
        BigDecimal totalValue = productService.calculateTotalInventoryValue();
        BigDecimal avgPrice = productService.calculateAverageProductPrice();
        
        // 2. Thống kê theo Category
        List<String> categories = productService.findAllCategories();
        Map<String, Long> categoryCounts = new HashMap<>();
        for (String category : categories) {
            categoryCounts.put(category, productService.countByCategory(category));
        }
        
        // 3. Cảnh báo Low Stock (Ngưỡng 10 như yêu cầu)
        int threshold = 10;
        List<Product> lowStockProducts = productService.getLowStockAlerts(threshold);
        
        // 4. Sản phẩm gần đây
        List<Product> recentProducts = productService.getRecentProducts();

        // 5. Thêm dữ liệu vào Model
        model.addAttribute("totalCount", totalCount);
        model.addAttribute("totalValue", totalValue);
        model.addAttribute("avgPrice", avgPrice);
        model.addAttribute("categoryCounts", categoryCounts);
        model.addAttribute("lowStockProducts", lowStockProducts);
        model.addAttribute("lowStockThreshold", threshold);
        model.addAttribute("recentProducts", recentProducts);
        
        return "dashboard"; // Trả về template dashboard.html
    }
}