<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
<%@page import="model.NguoiDung"%>
<%@page import="util.HtmlUtil"%>
<%
    List<NguoiDung> users = (List<NguoiDung>) request.getAttribute("users");
    NguoiDung currentAdmin = (NguoiDung) session.getAttribute("user");
    Integer currentPage = (Integer) request.getAttribute("currentPage");
    Integer totalPages = (Integer) request.getAttribute("totalPages");
    Integer totalItems = (Integer) request.getAttribute("totalItems");
    String paginationBaseUrl = (String) request.getAttribute("paginationBaseUrl");
    if (currentPage == null) currentPage = 1;
    if (totalPages == null) totalPages = 1;
    if (totalItems == null) totalItems = users == null ? 0 : users.size();
%>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <%@ include file="/WEB-INF/jspf/favicon.jspf" %>
        <title>Quản lý người dùng - Admin AutoPro</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
        <style>
            * { margin: 0; padding: 0; box-sizing: border-box; font-family: Arial, sans-serif; }
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
            .page-card { background: white; border-radius: 16px; padding: 24px; box-shadow: 0 4px 18px rgba(0, 0, 0, 0.06); }

            .table-hover tbody tr:hover { background-color: #f8fafc; }
            .table th { border-bottom-width: 1px; color: #64748b; font-weight: 600; text-transform: uppercase; font-size: 12px; padding: 14px 10px; background: white; }
            .table td { padding: 16px 10px; vertical-align: middle; border-bottom: 1px solid #f1f5f9; }
            .badge { padding: 6px 12px; border-radius: 6px; font-weight: 500; font-size: 12px; }
            
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
                <a href="${pageContext.request.contextPath}/admin/users" class="active">Quản lý người dùng</a>
                <a href="${pageContext.request.contextPath}/admin/posts">Quản lý bài đăng & xe</a>
                <a href="${pageContext.request.contextPath}/admin/categories">Quản lý danh mục</a>
                <a href="${pageContext.request.contextPath}/admin/reports">Báo cáo bài đăng</a>
                <a href="${pageContext.request.contextPath}/admin/lich-hen">Quản lý lịch hẹn</a>
                <a href="${pageContext.request.contextPath}/admin/giao-dich">Quản lý giao dịch xe</a>
                <a href="${pageContext.request.contextPath}/home">Xem trang chủ</a>
                <a class="logout" href="${pageContext.request.contextPath}/logout">Đăng xuất</a>
            </div>
        </div>

        <div class="main">
            <div class="topbar flex-wrap gap-3">
                <div>
                    <h2><i class="fa-solid fa-users text-primary me-2"></i>Quản lý người dùng</h2>
                    <div class="text-muted">Khóa, mở, phân quyền hoặc xóa tài khoản. Quyền seller cấp qua module xét duyệt.</div>
                </div>
                <div class="d-flex align-items-center gap-3">
                    <a href="${pageContext.request.contextPath}/admin/seller-requests" class="btn btn-dark" style="border-radius: 8px; font-weight: 500;">
                        <i class="fa-solid fa-file-signature me-1"></i>Hồ sơ seller
                    </a>
                </div>
            </div>

            <div class="page-card">

            <% if ("deleted".equals(request.getParameter("result"))) { %>
            <div class="alert alert-success">Đã xóa tài khoản và toàn bộ dữ liệu liên quan của người dùng.</div>
            <% } else if ("delete-error".equals(request.getParameter("result"))) { %>
            <div class="alert alert-danger">Không thể xóa tài khoản này. Vui lòng thử lại.</div>
            <% } else if ("self-delete-denied".equals(request.getParameter("result"))) { %>
            <div class="alert alert-warning">Không thể tự xóa chính tài khoản admin đang đăng nhập.</div>
            <% } else if ("admin-protected".equals(request.getParameter("result"))) { %>
            <div class="alert alert-warning">Không thể khóa, hạ quyền hoặc xóa tài khoản admin.</div>
            <% } else if ("updated".equals(request.getParameter("result"))) { %>
            <div class="alert alert-info">Thông tin trạng thái hoặc phân quyền đã được cập nhật.</div>
            <% } else if ("invalid".equals(request.getParameter("result")) || "error".equals(request.getParameter("result"))) { %>
            <div class="alert alert-danger">Yêu cầu không hợp lệ hoặc không thực hiện được.</div>
            <% } %>

            <div class="table-responsive">
                <table class="table table-hover align-middle">
                    <thead class="table-dark">
                        <tr>
                            <th>ID</th>
                            <th>Họ tên</th>
                            <th>Email</th>
                            <th>Số điện thoại</th>
                            <th>Vai trò</th>
                            <th>Trạng thái</th>
                            <th>Hành động</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% if (users != null && !users.isEmpty()) {
                            for (NguoiDung u : users) {
                                boolean isSelf = currentAdmin != null && currentAdmin.getMaNguoiDung() == u.getMaNguoiDung();
                                boolean isAdminRow = u.isAdmin();
                                boolean protectedAdminRow = isSelf || isAdminRow;
                        %>
                        <tr>
                            <td><%= u.getMaNguoiDung() %></td>
                            <td><%= HtmlUtil.escape(u.getHoTen()) %></td>
                            <td><%= u.getEmail() == null ? "-" : HtmlUtil.escape(u.getEmail()) %></td>
                            <td><%= u.getSoDienThoai() == null ? "-" : HtmlUtil.escape(u.getSoDienThoai()) %></td>
                            <td><%= HtmlUtil.escape(u.getVaiTroHienThi()) %></td>
                            <td>
                                <% if ("blocked".equalsIgnoreCase(u.getTrangThai())) { %>
                                <span class="badge bg-danger">Blocked</span>
                                <% } else { %>
                                <span class="badge bg-success">Active</span>
                                <% } %>
                            </td>
                            <td>
                                <div class="d-flex flex-wrap gap-2">
                                    <% if (protectedAdminRow) { %>
                                    <span class="btn btn-sm btn-outline-warning disabled">Khóa</span>
                                    <form method="post" action="${pageContext.request.contextPath}/admin/user-action" class="m-0">
<input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                                        <input type="hidden" name="id" value="<%= u.getMaNguoiDung() %>">
                                        <input type="hidden" name="action" value="active">
                                        <button type="submit" class="btn btn-sm btn-outline-success">Mở</button>
                                    </form>
                                    <span class="btn btn-sm btn-outline-primary disabled">Admin</span>
                                    <span class="btn btn-sm btn-outline-secondary disabled">Buyer</span>
                                    <a href="${pageContext.request.contextPath}/admin/seller-requests" class="btn btn-sm btn-outline-dark">Seller request</a>
                                    <span class="btn btn-sm btn-outline-danger disabled"><%= isSelf ? "Đang đăng nhập" : "Admin" %></span>
                                    <% } else { %>
                                    <form method="post" action="${pageContext.request.contextPath}/admin/user-action" class="m-0">
<input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                                        <input type="hidden" name="id" value="<%= u.getMaNguoiDung() %>">
                                        <input type="hidden" name="action" value="block">
                                        <button type="submit" class="btn btn-sm btn-outline-warning">Khóa</button>
                                    </form>
                                    <form method="post" action="${pageContext.request.contextPath}/admin/user-action" class="m-0">
<input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                                        <input type="hidden" name="id" value="<%= u.getMaNguoiDung() %>">
                                        <input type="hidden" name="action" value="active">
                                        <button type="submit" class="btn btn-sm btn-outline-success">Mở</button>
                                    </form>
                                    <form method="post" action="${pageContext.request.contextPath}/admin/user-action" class="m-0">
<input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                                        <input type="hidden" name="id" value="<%= u.getMaNguoiDung() %>">
                                        <input type="hidden" name="action" value="admin">
                                        <button type="submit" class="btn btn-sm btn-outline-primary">Admin</button>
                                    </form>
                                    <form method="post" action="${pageContext.request.contextPath}/admin/user-action" class="m-0">
<input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                                        <input type="hidden" name="id" value="<%= u.getMaNguoiDung() %>">
                                        <input type="hidden" name="action" value="buyer">
                                        <button type="submit" class="btn btn-sm btn-outline-secondary">Buyer</button>
                                    </form>
                                    <a href="${pageContext.request.contextPath}/admin/seller-requests" class="btn btn-sm btn-outline-dark">Seller request</a>
                                    <form method="post" action="${pageContext.request.contextPath}/admin/user-action" class="m-0" onsubmit="return confirm('Bạn có chắc muốn xóa user này? Toàn bộ bài đăng, lịch hẹn, chat, báo cáo và dữ liệu liên quan cũng sẽ bị xóa.');">
<input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                                        <input type="hidden" name="id" value="<%= u.getMaNguoiDung() %>">
                                        <input type="hidden" name="action" value="delete">
                                        <button type="submit" class="btn btn-sm btn-outline-danger">Xóa</button>
                                    </form>
                                    <% } %>
                                </div>
                            </td>
                        </tr>
                        <% } } else { %>
                        <tr>
                            <td colspan="7" class="text-center text-muted py-4">Không có dữ liệu người dùng.</td>
                        </tr>
                        <% } %>
                    </tbody>
                </table>
            </div>
            <% if (totalPages > 1) { %>
            <div class="d-flex justify-content-between align-items-center flex-wrap gap-2 mt-3">
                <div class="text-muted small">Tổng <%= totalItems %> tài khoản. Trang <%= currentPage %>/<%= totalPages %>.</div>
                <nav aria-label="Phân trang người dùng">
                    <ul class="pagination mb-0">
                        <li class="page-item <%= currentPage <= 1 ? "disabled" : "" %>">
                            <a class="page-link" href="<%= HtmlUtil.escape(paginationBaseUrl) %><%= currentPage - 1 %>">Trước</a>
                        </li>
                        <% for (int p = 1; p <= totalPages; p++) { %>
                        <li class="page-item <%= p == currentPage ? "active" : "" %>">
                            <a class="page-link" href="<%= HtmlUtil.escape(paginationBaseUrl) %><%= p %>"><%= p %></a>
                        </li>
                        <% } %>
                        <li class="page-item <%= currentPage >= totalPages ? "disabled" : "" %>">
                            <a class="page-link" href="<%= HtmlUtil.escape(paginationBaseUrl) %><%= currentPage + 1 %>">Sau</a>
                        </li>
                    </ul>
                </nav>
            </div>
            <% } %>
        </div>
        </div>
    </body>
</html>
