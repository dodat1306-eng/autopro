<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="model.YeuCauKetNoi"%>
<%
    YeuCauKetNoi item = (YeuCauKetNoi) request.getAttribute("yeuCauKetNoi");
%>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <title>Cập nhật yêu cầu kết nối - Admin AutoPro</title>
        <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
        <style>
            body {
                font-family: 'Inter', sans-serif;
                background: #f5f5f5;
                padding: 40px 0;
            }

            .box-white {
                background: #fff;
                border-radius: 18px;
                padding: 30px;
                box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05);
                max-width: 760px;
                margin: auto;
            }
        </style>
    </head>
    <body>
        <div class="container">
            <div class="d-flex justify-content-center gap-2 mb-3">
                <a href="${pageContext.request.contextPath}/admin/dashboard" class="btn btn-outline-secondary">
                    <i class="fa-solid fa-arrow-left me-2"></i>Về Dashboard
                </a>
                <a href="${pageContext.request.contextPath}/admin/lich-hen" class="btn btn-outline-dark">Danh sách lịch hẹn</a>
            </div>

            <% if (item != null) { %>
            <div class="box-white">
                <h3 class="fw-bold mb-4 text-center">Cập nhật yêu cầu kết nối #<%= item.getMaYeuCau() %></h3>

                <form action="${pageContext.request.contextPath}/admin/lich-hen/cap-nhat" method="POST">
<input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                    <input type="hidden" name="maYeuCau" value="<%= item.getMaYeuCau() %>" />

                    <div class="row">
                        <div class="col-md-6 mb-3">
                            <label class="text-muted small">Người mua</label>
                            <input type="text" class="form-control bg-light" value="<%= item.getTenNguoiMua() != null ? item.getTenNguoiMua() : ("ID " + item.getMaNguoiMua()) %>" readonly>
                        </div>
                        <div class="col-md-6 mb-3">
                            <label class="text-muted small">Người bán</label>
                            <input type="text" class="form-control bg-light" value="<%= item.getTenNguoiBan() != null ? item.getTenNguoiBan() : "Chưa xác định" %>" readonly>
                        </div>
                    </div>

                    <div class="mb-3">
                        <label class="text-muted small">Xe quan tâm</label>
                        <input type="text" class="form-control bg-light" value="<%= item.getTenXe() != null ? item.getTenXe() : "Chưa rõ xe" %>" readonly>
                    </div>

                    <div class="mb-3">
                        <label class="text-muted small">Ngày tạo yêu cầu</label>
                        <input type="text" class="form-control bg-light" value="<%= item.getNgayTao() %>" readonly>
                    </div>

                    <div class="mb-3">
                        <label class="text-muted small">Giá tham khảo</label>
                        <input type="text" class="form-control bg-light text-danger fw-bold" value="<%= String.format("%,.0f", item.getGiaThamKhao()).replace(",", ".") %> đ" readonly>
                    </div>

                    <div class="mb-3">
                        <label class="text-muted small">Ghi chú từ người mua</label>
                        <textarea class="form-control bg-light" rows="3" readonly><%= item.getGhiChuNguoiMua() != null ? item.getGhiChuNguoiMua() : "" %></textarea>
                    </div>

                    <div class="mb-4">
                        <label class="fw-bold mb-2">Trạng thái yêu cầu / lịch hẹn</label>
                        <select name="trangThai" class="form-select form-select-lg">
                            <option value="CHO_LIEN_HE" <%= "CHO_LIEN_HE".equals(item.getTrangThai()) ? "selected" : "" %>>Chờ liên hệ</option>
                            <option value="DA_LIEN_HE" <%= "DA_LIEN_HE".equals(item.getTrangThai()) ? "selected" : "" %>>Đã liên hệ</option>
                            <option value="DA_XEM_XE" <%= "DA_XEM_XE".equals(item.getTrangThai()) ? "selected" : "" %>>Đã xác nhận lịch hẹn</option>
                            <option value="DA_CHOT" <%= "DA_CHOT".equals(item.getTrangThai()) ? "selected" : "" %>>Đã chốt ngoài hệ thống</option>
                            <option value="DA_HUY" <%= "DA_HUY".equals(item.getTrangThai()) ? "selected" : "" %>>Đã hủy</option>
                        </select>
                    </div>

                    <div class="d-grid gap-2">
                        <button type="submit" class="btn btn-warning btn-lg fw-semibold"><i class="fa-solid fa-floppy-disk me-2"></i>Lưu thay đổi</button>
                        <a href="${pageContext.request.contextPath}/admin/lich-hen" class="btn btn-light mt-2">Hủy bỏ</a>
                    </div>
                </form>
            </div>
            <% } else { %>
            <div class="alert alert-danger text-center">Không tìm thấy lịch hẹn.</div>
            <% } %>
        </div>
    </body>
</html>
