# 📦 Product Management System (Spring Boot & Thymeleaf)

Dự án Quản lý Sản phẩm được xây dựng bằng Spring Boot, sử dụng Thymeleaf cho giao diện người dùng và Spring Data JPA để tương tác với cơ sở dữ liệu MySQL.

---

## 👨‍🎓 Thông tin Sinh viên

* **Tên:** Phạm Tuấn Đăng Khoa
* **Mã số Sinh viên:** ITITIU22087
* **Lớp:** Lab Web Thứ 2

---

## ⚙️ Công nghệ Sử dụng

| Công nghệ | Phiên bản | Mô tả |
| :--- | :--- | :--- |
| **Backend Framework** | Spring Boot 3.3.x | Lõi của ứng dụng, xử lý logic nghiệp vụ. |
| **Database Access** | Spring Data JPA | Quản lý truy cập và thao tác dữ liệu. |
| **Database** | MySQL 8.0 | Hệ quản trị cơ sở dữ liệu quan hệ. |
| **Frontend** | Thymeleaf | Template Engine dùng cho giao diện người dùng. |
| **Build Tool** | Maven | Quản lý dependencies và xây dựng dự án. |

---

## ✅ Các Tính năng đã Hoàn thành

| Tính năng | Trạng thái |
| :--- | :--- |
| **CRUD** (Create, Read, Update, Delete) | ✅ Hoàn thành |
| **Search** | ✅ Hoàn thành |
| **Advanced Search** | ✅ Hoàn thành |
| **Validation** | ✅ Hoàn thành |
| **Sorting** | ✅ Hoàn thành |
| **Pagination** | ✅ Hoàn thành |
| **REST API** (Bonus) | ✅ Hoàn thành |
| **Image Upload** (Bonus) | ✅ Hoàn thành |
| **Excel** (Bonus) | ✅ Hoàn thành |

---

## 🚀 Hướng dẫn Cài đặt & Chạy

Để chạy ứng dụng trên máy local, vui lòng làm theo các bước sau:

### 1. Cấu hình Cơ sở Dữ liệu

1.  **Tạo Database:** Tạo một cơ sở dữ liệu MySQL mới với tên:
    ```sql
    CREATE DATABASE product_management;
    ```
2.  **Cập nhật `application.properties`:** Mở file `src/main/resources/application.properties` và cập nhật thông tin đăng nhập MySQL của bạn:

    ```properties
    spring.datasource.url=jdbc:mysql://localhost:3306/product_management?useSSL=false&serverTimezone=UTC
    spring.datasource.username=[YOUR_MYSQL_USERNAME] 
    spring.datasource.password=[YOUR_MYSQL_PASSWORD] 
    
    # Cấu hình JPA (Tự động tạo bảng)
    spring.jpa.hibernate.ddl-auto=update
    ```

### 2. Chạy Ứng dụng

1.  **Import Dự án:** Import thư mục dự án vào môi trường phát triển (IDE) bạn chọn (như VS Code, IntelliJ IDEA).
2.  **Chạy qua Maven:** Mở Terminal trong thư mục gốc của dự án và chạy lệnh:
    ```bash
    mvn spring-boot:run
    ```
3.  **Truy cập:** Mở trình duyệt và truy cập vào địa chỉ:
    $$\mathbf{\text{http://localhost:8080/products}}$$