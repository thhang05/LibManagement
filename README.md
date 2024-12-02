# Library Management Application

## Thành viên:
- Nguyễn Thúy Hằng  
- Hồ Thúy Hằng  
- Trần Hữu Huy Hoàng  

---

## Tổng quan về ứng dụng
Ứng dụng quản lý thư viện được xây dựng bằng Java, sử dụng JavaFX để tạo giao diện người dùng trực quan và SQL để quản lý cơ sở dữ liệu. Ứng dụng hướng đến việc hỗ trợ thủ thư trong việc quản lý tài liệu và người dùng thư viện, đồng thời mang lại trải nghiệm mượn và trả tài liệu dễ dàng cho người dùng.

---

## Tính năng chính:

### 1. Dành cho thủ thư:
#### Quản lý tài liệu:
- Thêm tài liệu mới (sách, tạp chí, tài liệu tham khảo...).  
- Sửa đổi thông tin tài liệu (tên sách, tác giả, chủ đề, số lượng, ISBN).  
- Xóa tài liệu không còn sử dụng.  
- Tìm kiếm tài liệu dựa trên nhiều tiêu chí như tiêu đề, ISBN, tác giả, hoặc chủ đề.  

#### Quản lý người dùng:
- Quản lý danh sách thành viên thư viện, thêm hoặc xóa thành viên.  
- Theo dõi lịch sử mượn/trả tài liệu của từng thành viên.  
- Hỗ trợ ghi nhận thông tin mượn trả tài liệu (thời gian mượn, hạn trả).  

### 2. Dành cho người dùng thư viện:
#### Quản lý tài khoản cá nhân:
- Đăng ký tài khoản mới với thông tin cần thiết (tên, email, số điện thoại...).  
- Đăng nhập và bảo mật tài khoản bằng mật khẩu.  
- Đổi mật khẩu khi cần thiết.  
- Cập nhật thông tin cá nhân (email, địa chỉ, số điện thoại).  

#### Tra cứu và mượn sách:
- Tìm kiếm tài liệu theo các tiêu chí như tiêu đề, ISBN, tác giả hoặc chủ đề.  
- Xem chi tiết thông tin tài liệu, trạng thái (còn sẵn hay đã mượn).  
- Thực hiện mượn sách nếu tài liệu sẵn có và trả sách sau khi sử dụng.  

#### Lịch sử mượn/trả sách:
- Xem lại lịch sử các lần mượn/trả tài liệu.  
- Theo dõi trạng thái mượn hiện tại, số ngày còn lại trước hạn trả.  

---

## Tính năng nâng cao:

### 1. Tích hợp API (Google Books API):
- **Tìm kiếm tài liệu online**:  
  Thủ thư có thể tra cứu thông tin sách online (tựa sách, ISBN, tác giả, mô tả) thông qua Google Books API.  
  Tự động thêm tài liệu từ kết quả tìm kiếm vào cơ sở dữ liệu thư viện.  

### 2. Sử dụng đa luồng trong các tác vụ online:
- Tăng hiệu năng khi tìm kiếm tài liệu trên Google Books API.  
- Tải ảnh bìa sách từ link API hiển thị trong giao diện người dùng mà không gây gián đoạn các tác vụ khác.  

### 3. Gợi ý tài liệu cho người dùng:
- Gợi ý các tài liệu được mượn nhiều nhất trong thư viện.  
- Đưa ra gợi ý cá nhân hóa dựa trên lịch sử mượn sách của từng người dùng.  

---

## Công nghệ và thư viện sử dụng:

- **JavaFX**:  
  - Phát triển giao diện người dùng với thiết kế trực quan, hỗ trợ các thành phần như: bảng hiển thị dữ liệu (TableView), ô tìm kiếm (SearchBar), và hộp thoại thông báo (DialogBox).  
  - Sử dụng CSS để tùy chỉnh giao diện, mang lại trải nghiệm mượt mà, chuyên nghiệp.  

- **SQL**:  
  - Lưu trữ và quản lý dữ liệu thư viện (tài liệu, thành viên, lịch sử mượn trả).  
  - Hỗ trợ các truy vấn như thêm, xóa, sửa, tìm kiếm nhanh chóng.  

- **Zxing**:  
  - Tạo mã QR để hỗ trợ quản lý sách hoặc mã thành viên.  
  - Phân tích mã QR khi người dùng quét để lấy thông tin tài liệu.  

- **Google Books API**:  
  - Tra cứu thông tin tài liệu online.  
  - Lấy ảnh bìa sách, thông tin tác giả, mô tả sách phục vụ cho mục đích quản lý và hiển thị.  

---

## Lợi ích:

- **Đối với thủ thư**: Quản lý thư viện dễ dàng, giảm thiểu công việc thủ công, tăng tính chính xác.  
- **Đối với người dùng thư viện**: Tiện lợi trong việc tra cứu và mượn sách, được gợi ý các tài liệu phù hợp.  
- **Đối với hệ thống**: Tăng hiệu quả hoạt động nhờ tích hợp API và đa luồng.  
