package com.example.productmanagement.controller;

import com.example.productmanagement.entity.Product;
import com.example.productmanagement.service.ProductService;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/export")
public class ExportController {

    private final ProductService productService;

    public ExportController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/excel")
    public void exportToExcel(HttpServletResponse response) throws IOException {
        
        // 1. Cấu hình Header cho Response
        response.setContentType("application/octet-stream");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        String currentDateTime = dateFormatter.format(new Date());
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=products_" + currentDateTime + ".xlsx";
        response.setHeader(headerKey, headerValue);

        // Lấy dữ liệu
        List<Product> products = productService.getAllProducts();

        // 2. Tạo Workbook và Sheet
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Products");

        // 3. Viết Header Row
        Row headerRow = sheet.createRow(0);
        String[] headers = {"ID", "Code", "Name", "Category", "Price", "Quantity", "Description"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
        }

        // 4. Viết Data Rows
        int rowNum = 1;
        for (Product product : products) {
            Row row = sheet.createRow(rowNum++);
            
            row.createCell(0).setCellValue(product.getId());
            row.createCell(1).setCellValue(product.getProductCode());
            row.createCell(2).setCellValue(product.getName());
            row.createCell(3).setCellValue(product.getCategory());
            
            // Xử lý giá tiền (BigDecimal)
            Cell priceCell = row.createCell(4);
            if (product.getPrice() != null) {
                priceCell.setCellValue(product.getPrice().doubleValue());
            } else {
                priceCell.setCellValue(0.0);
            }
            
            row.createCell(5).setCellValue(product.getQuantity());
            row.createCell(6).setCellValue(product.getDescription());
        }

        // 5. Gửi Workbook tới Response
        workbook.write(response.getOutputStream());
        workbook.close();
    }
}