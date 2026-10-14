<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
<%@page import="model.YeuCauKetNoi"%>
<%!
    private String getStatusLabel(String status) {
        if (status == null) {
            return "Chưa cập nhật";
        }

        switch (status) {
            case "CHO_LIEN_HE":
                return "Chờ liên hệ";
            case "DA_LIEN_HE":
                return "Đã liên hệ";
            case "DA_XEM_XE":
                return "Đã xác nhận lịch hẹn";
            case "DA_CHOT":
                return "Đã chốt ngoài hệ thống";
            case "DA_HUY":
                return "Đã hủy";
            default:
                return status;
        }
    }
%>
<%
    List<YeuCauKetNoi> danhSachYeuCau = (List<YeuCauKetNoi>) request.getAttribute("danhSachYeuCau");
%>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <%@ include file="/WEB-INF/jspf/favicon.jspf" %>
        <title>Quản lý kết nối và lịch hẹn - Admin AutoPro</title>
        <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
        <style>
            * { margin: 0; padding: 0; box-sizing: border-box; font-family: 'Inter', sans-serif; }
            body { display: flex; background: #f1f2f6; color: #1f2937; }
            .sidebar { width: 260px; height: 100vh; position: sticky; top: 0; background: #111827; color: white; padding-top: 22px; overflow-y: auto; }
            .logo { display: flex; justify-content: center; align-items: center; margin-bottom: 30px; padding-inline: 12px; min-height: 64px; overflow: visible; text-decoration: none; }
            .logo img { display: block; height: 46px; width: auto; max-width: 100%; }
            .menu a { display: block; color: #d1d5db; text-decoration: none; padding: 16px 25px; transition: 0.2s; font-size: 15px; }
            .menu a:hover, .menu a.active { background: #1f2937; color: white; }
            .logout { color: #fb7185 !important; }
            .main { flex: 1; padding: 24px 30px; overflow-x: hidden; min-width: 0; }
            
            .topbar { background: white; padding: 22px; border-radius: 14px; margin-bottom: 24px; display: flex; justify-content: space-between; align-items: center; box-shadow: 0 4px 18px rgba(0, 0, 0, 0.06); }
            .topbar h2 { margin-bottom: 6px; font-weight: 700; font-size: 24px; }
            .page-card { background: white; border-radius: 16px; padding: 24px; box-shadow: 0 4px 18px rgba(0, 0, 0, 0.06); margin-bottom: 20px; }

            .table-hover tbody tr:hover { background-color: #f8fafc; }
            .table th { border-bottom-width: 1px; color: #64748b; font-weight: 600; text-transform: uppercase; font-size: 12px; padding: 14px 10px; background: white; }
            .table td { padding: 16px 10px; vertical-align: middle; border-bottom: 1px solid #f1f5f9; }

            .badge-status { padding: 6px 12px; border-radius: 6px; font-size: 12px; font-weight: 600; }
            .status-CHO_LIEN_HE { background: #fff7cc; color: #a16207; }
            .status-DA_LIEN_HE { background: #dbeafe; color: #1d4ed8; }
            .status-DA_XEM_XE { background: #ffedd5; color: #c2410c; }
            .status-DA_CHOT { background: #dcfce7; color: #15803d; }
            .status-DA_HUY { background: #fee2e2; color: #b91c1c; }

            .btn-action { border-radius: 8px; font-weight: 500; font-size: 13px; }
            
            @media (max-width: 860px) {
                body { display: block; }
                .sidebar { width: 100%; min-height: auto; position: static; height: auto; }
            }
        </style>
    </head>
    <body>
        <div class="sidebar">
            <a class="logo" href="${pageContext.request.contextPath}/home">
                <jsp:include page="/WEB-INF/includes/site-logo.jsp">
                    <jsp:param name="logoHeight" value="46px" />
                    <jsp:param name="onDark" value="1" />
                </jsp:include>
            </a>
            <div class="menu">
                <a href="${pageContext.request.contextPath}/admin/dashboard">Tổng quan</a>
                <a href="${pageContext.request.contextPath}/admin/users">Quản lý người dùng</a>
                <a href="${pageContext.request.contextPath}/admin/posts">Quản lý bài đăng & xe</a>
                <a href="${pageContext.request.contextPath}/admin/categories">Quản lý danh mục</a>
                <a href="${pageContext.request.contextPath}/admin/reports">Báo cáo bài đăng</a>
                <a href="${pageContext.request.contextPath}/admin/lich-hen" class="active">Quản lý lịch hẹn</a>
                <a href="${pageContext.request.contextPath}/admin/giao-dich">Quản lý giao dịch xe</a>
                <a href="${pageContext.request.contextPath}/home">Xem trang chủ</a>
                <a class="logout" href="${pageContext.request.contextPath}/logout">Đăng xuất</a>
            </div>
        </div>

        <div class="main">
            <div class="topbar flex-wrap gap-3">
                <div>
                    <h2><i class="fa-solid fa-calendar-check text-warning me-2"></i>Quản lý kết nối và lịch hẹn</h2>
                    <div class="text-muted">Theo dõi yêu cầu kết nối, ghi chú của người mua và tiến trình hẹn xem xe.</div>
                </div>
                <div class="d-none d-md-block">
                    <strong>Admin:</strong> ${sessionScope.user.hoTen}
                </div>
            </div>

            <div class="page-card">

                <div class="table-responsive">
                    <table class="table table-hover align-middle">
                        <thead>
                            <tr>
                                <th>Mã lịch hẹn</th>
                                <th>Xe</th>
                                <th>Người mua</th>
                                <th>Người bán</th>
                                <th>Ngày tạo</th>
                                <th>Giá tham khảo</th>
                                <th>Trạng thái</th>
                                <th class="text-center">Thao tác</th>
                            </tr>
                        </thead>
                        <tbody>
                            <% if (danhSachYeuCau != null && !danhSachYeuCau.isEmpty()) {
                                for (YeuCauKetNoi item : danhSachYeuCau) { %>
                            <tr>
                                <td><strong>#<%= item.getMaYeuCau() %></strong></td>
                                <td>
                                    <strong><%= item.getTenXe() != null ? item.getTenXe() : "Chưa rõ xe" %></strong>
                                    <% if (item.getMaXe() != null) { %>
                                    <div><a href="${pageContext.request.contextPath}/detail?id=<%= item.getMaXe() %>" class="small text-decoration-none">Xem tin xe</a></div>
                                    <% } %>
                                    <% if (item.getGhiChuNguoiMua() != null && !item.getGhiChuNguoiMua().isBlank()) { %>
                                    <div class="small text-muted mt-2"><strong>Nhu cầu kết nối:</strong> <%= item.getGhiChuNguoiMua() %></div>
                                    <% } %>
                                </td>
                                <td>
                                    <div><%= item.getTenNguoiMua() != null ? item.getTenNguoiMua() : ("ID " + item.getMaNguoiMua()) %></div>
                                    <div class="small text-muted"><%= item.getEmailNguoiMua() != null ? item.getEmailNguoiMua() : "" %></div>
                                </td>
                                <td><%= item.getTenNguoiBan() != null ? item.getTenNguoiBan() : "Chưa xác định" %></td>
                                <td class="text-muted"><%= item.getNgayTao() %></td>
                                <td class="fw-bold text-danger"><%= String.format("%,.0f", item.getGiaThamKhao()).replace(",", ".") %> đ</td>
                                <td>
                                    <span class="badge-status status-<%= item.getTrangThai() %>">
                                        <%= getStatusLabel(item.getTrangThai()) %>
                                    </span>
                                </td>
                                <td class="text-center">
                                    <a href="${pageContext.request.contextPath}/admin/lich-hen/cap-nhat?id=<%= item.getMaYeuCau() %>" class="btn btn-sm btn-outline-dark btn-action">
                                        <i class="fa-solid fa-pen-to-square"></i> Cập nhật
                                    </a>
                                </td>
                            </tr>
                            <% } } else { %>
                            <tr>
                                <td colspan="8" class="text-center py-5">
                                    <i class="fa-solid fa-box-open text-muted mb-3" style="font-size: 48px;"></i>
                                    <p class="text-muted fs-5">Hiện tại chưa có lịch hẹn nào trong hệ thống.</p>
                                </td>
                            </tr>
                            <% } %>
                        </tbody>
                    </table>
                </div>
        </div>
        </div>
    </body>
</html>
