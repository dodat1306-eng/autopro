<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="dao.ThongBaoDAO" %>
<%@ page import="java.util.List" %>
<%@ page import="model.NguoiDung" %>
<%@ page import="model.ThongBao" %>
<%@ page import="util.HtmlUtil" %>
<%
    NguoiDung accountMenuUser = (NguoiDung) session.getAttribute("user");
    boolean accountMenuIsAdmin = accountMenuUser != null && accountMenuUser.isAdmin();
    boolean accountMenuIsMember = accountMenuUser != null && accountMenuUser.isThanhVien();
    boolean accountMenuIsSeller = accountMenuUser != null && accountMenuUser.isSeller();
    boolean accountMenuIsBuyer = accountMenuUser != null && accountMenuUser.isBuyer();
    String accountMenuName = accountMenuUser != null && accountMenuUser.getHoTen() != null
            ? accountMenuUser.getHoTen().trim()
            : "Tài khoản";
    if (accountMenuName.isEmpty()) {
        accountMenuName = "Tài khoản";
    }
    String accountMenuInitial = accountMenuName.substring(0, 1).toUpperCase();
    String accountMenuContact = "";
    String accountMenuAvatarUrl = "";
    int unreadNotificationCount = 0;
    List<ThongBao> recentNotifications = java.util.Collections.emptyList();
    if (accountMenuUser != null) {
        if (accountMenuUser.getEmail() != null && !accountMenuUser.getEmail().isBlank()) {
            accountMenuContact = accountMenuUser.getEmail().trim();
        } else if (accountMenuUser.getSoDienThoai() != null && !accountMenuUser.getSoDienThoai().isBlank()) {
            accountMenuContact = accountMenuUser.getSoDienThoai().trim();
        }

        if (accountMenuUser.getAnhDaiDien() != null && !accountMenuUser.getAnhDaiDien().isBlank()) {
            String rawAvatar = accountMenuUser.getAnhDaiDien().trim();
            if (rawAvatar.startsWith("http://") || rawAvatar.startsWith("https://")) {
                accountMenuAvatarUrl = rawAvatar;
            } else if (rawAvatar.startsWith("/")) {
                accountMenuAvatarUrl = request.getContextPath() + rawAvatar;
            } else {
                accountMenuAvatarUrl = request.getContextPath() + "/" + rawAvatar;
            }
        }

        ThongBaoDAO thongBaoDAO = new ThongBaoDAO();
        unreadNotificationCount = thongBaoDAO.countUnread(accountMenuUser.getMaNguoiDung());
        recentNotifications = thongBaoDAO.getRecentNotifications(accountMenuUser.getMaNguoiDung(), 5);
    }
%>

<% if (accountMenuUser != null) { %>
<div class="dropdown">
    <button
        class="btn border rounded-circle position-relative d-flex align-items-center justify-content-center shadow-sm"
        type="button"
        data-bs-toggle="dropdown"
        aria-expanded="false"
        title="Thông báo"
        style="width: 44px; height: 44px; background: #ffffff; color: #111827;">
        <i class="fa-solid fa-bell"></i>
        <% if (unreadNotificationCount > 0) { %>
        <span class="position-absolute top-0 start-100 translate-middle badge rounded-pill bg-danger">
            <%= unreadNotificationCount > 99 ? "99+" : unreadNotificationCount %>
        </span>
        <% } %>
    </button>
    <div class="dropdown-menu dropdown-menu-end shadow border-0 p-0 overflow-hidden" style="width: 330px;">
        <div class="px-3 py-3 border-bottom bg-light d-flex justify-content-between align-items-center">
            <div>
                <div class="fw-bold text-dark">Thông báo</div>
                <div class="small text-muted"><%= unreadNotificationCount %> thông báo chưa đọc</div>
            </div>
            <a class="btn btn-sm btn-outline-secondary" href="<%= request.getContextPath() %>/notifications">Xem tất cả</a>
        </div>
        <% if (recentNotifications != null && !recentNotifications.isEmpty()) { %>
            <% for (ThongBao thongBao : recentNotifications) { %>
            <a class="dropdown-item py-3 border-bottom <%= thongBao.isDaDoc() ? "" : "bg-light" %>"
               href="<%= request.getContextPath() %>/notifications?open=<%= thongBao.getMaThongBao() %>">
                <div class="d-flex justify-content-between align-items-start gap-2">
                    <div class="flex-grow-1">
                        <div class="fw-semibold text-dark"><%= HtmlUtil.escape(thongBao.getTieuDe()) %></div>
                        <div class="small text-muted" style="white-space: normal;"><%= HtmlUtil.escape(thongBao.getNoiDung()) %></div>
                    </div>
                    <% if (!thongBao.isDaDoc()) { %>
                    <span class="badge bg-danger rounded-pill mt-1">Mới</span>
                    <% } %>
                </div>
                <div class="small text-muted mt-1"><%= thongBao.getNgayTao() %></div>
            </a>
            <% } %>
        <% } else { %>
        <div class="px-3 py-4 text-center text-muted">
            <i class="fa-regular fa-bell-slash mb-2 d-block"></i>
            Chưa có thông báo nào.
        </div>
        <% } %>
    </div>
</div>
<% } %>

<div class="dropdown">
    <button
        class="btn border rounded-circle d-flex align-items-center justify-content-center shadow-sm"
        type="button"
        data-bs-toggle="dropdown"
        aria-expanded="false"
        title="Tài khoản"
        style="width: 44px; height: 44px; padding: 0; overflow: hidden; background: <%= accountMenuUser != null && !accountMenuAvatarUrl.isEmpty() ? "#ffffff" : (accountMenuUser != null ? "#111827" : "#ffffff") %>; color: <%= accountMenuUser != null ? "#ffffff" : "#111827" %>;">
        <% if (accountMenuUser != null) { %>
            <% if (!accountMenuAvatarUrl.isEmpty()) { %>
        <img src="<%= HtmlUtil.escape(accountMenuAvatarUrl) %>" alt="Avatar" style="width: 100%; height: 100%; object-fit: cover; display: block;">
            <% } else { %>
        <span class="fw-bold"><%= HtmlUtil.escape(accountMenuInitial) %></span>
            <% } %>
        <% } else { %>
        <i class="fa-solid fa-user"></i>
        <% } %>
    </button>
    <div class="dropdown-menu dropdown-menu-end shadow border-0 p-0 overflow-hidden" style="min-width: 290px;">
        <% if (accountMenuUser != null) { %>
        <div class="px-3 py-3 border-bottom" style="background: #111827; color: white;">
            <div class="d-flex align-items-center gap-3">
                <% if (!accountMenuAvatarUrl.isEmpty()) { %>
                <img src="<%= HtmlUtil.escape(accountMenuAvatarUrl) %>" alt="Avatar" style="width: 44px; height: 44px; border-radius: 50%; object-fit: cover; border: 2px solid rgba(255,255,255,0.7);">
                <% } else { %>
                <div class="d-flex align-items-center justify-content-center fw-bold rounded-circle" style="width: 44px; height: 44px; background: #ffffff; color: #111827;"><%= HtmlUtil.escape(accountMenuInitial) %></div>
                <% } %>
                <div>
                    <div class="fw-bold"><%= HtmlUtil.escape(accountMenuName) %></div>
                    <div class="small text-white-50"><%= accountMenuContact.isEmpty() ? "Tài khoản AutoPro" : HtmlUtil.escape(accountMenuContact) %></div>
                </div>
            </div>
        </div>
        <% if (accountMenuIsAdmin) { %>
        <a class="dropdown-item py-2" href="<%= request.getContextPath() %>/notifications">
            <i class="fa-solid fa-bell me-2 text-warning"></i>Thông báo
            <% if (unreadNotificationCount > 0) { %>
            <span class="badge bg-danger rounded-pill float-end"><%= unreadNotificationCount > 99 ? "99+" : unreadNotificationCount %></span>
            <% } %>
        </a>
        <a class="dropdown-item py-2" href="<%= request.getContextPath() %>/profile">
            <i class="fa-solid fa-id-card me-2 text-warning"></i>Hồ sơ của tôi
        </a>
        <a class="dropdown-item py-2" href="<%= request.getContextPath() %>/admin/dashboard">
            <i class="fa-solid fa-chart-line me-2 text-warning"></i>Dashboard admin
        </a>
        <a class="dropdown-item py-2" href="<%= request.getContextPath() %>/admin/posts">
            <i class="fa-solid fa-car-on me-2 text-warning"></i>Quản lý bài đăng & xe
        </a>
        <a class="dropdown-item py-2" href="<%= request.getContextPath() %>/admin/users">
            <i class="fa-solid fa-users-gear me-2 text-warning"></i>Quản lý người dùng
        </a>
        <a class="dropdown-item py-2" href="<%= request.getContextPath() %>/admin/seller-requests">
            <i class="fa-solid fa-id-card-clip me-2 text-warning"></i>Hồ sơ seller
        </a>
        <a class="dropdown-item py-2" href="<%= request.getContextPath() %>/admin/categories">
            <i class="fa-solid fa-list-check me-2 text-warning"></i>Danh mục hệ thống
        </a>
        <a class="dropdown-item py-2" href="<%= request.getContextPath() %>/admin/lich-hen">
            <i class="fa-solid fa-calendar-check me-2 text-warning"></i>Quản lý lịch hẹn
        </a>
        <a class="dropdown-item py-2" href="<%= request.getContextPath() %>/admin/reports">
            <i class="fa-solid fa-shield-halved me-2 text-warning"></i>Báo cáo bài đăng
        </a>
        <a class="dropdown-item py-2" href="<%= request.getContextPath() %>/admin/giao-dich">
            <i class="fa-solid fa-handshake me-2 text-warning"></i>Quản lý giao dịch xe
        </a>
        <a class="dropdown-item py-2" href="<%= request.getContextPath() %>/home">
            <i class="fa-solid fa-house me-2 text-warning"></i>Xem trang chủ
        </a>
        <% } else { %>
        <a class="dropdown-item py-2" href="<%= request.getContextPath() %>/notifications">
            <i class="fa-solid fa-bell me-2 text-warning"></i>Thông báo
            <% if (unreadNotificationCount > 0) { %>
            <span class="badge bg-danger rounded-pill float-end"><%= unreadNotificationCount > 99 ? "99+" : unreadNotificationCount %></span>
            <% } %>
        </a>
        <a class="dropdown-item py-2" href="<%= request.getContextPath() %>/profile">
            <i class="fa-solid fa-id-card me-2 text-warning"></i>Hồ sơ của tôi
        </a>
        <a class="dropdown-item py-2" href="<%= request.getContextPath() %>/account-security">
            <i class="fa-solid fa-shield-halved me-2 text-warning"></i>Bảo mật tài khoản
        </a>
        <% if (accountMenuIsMember) { %>
        <a class="dropdown-item py-2" href="<%= request.getContextPath() %>/saved-posts">
            <i class="fa-regular fa-heart me-2 text-warning"></i>Bài đã lưu
        </a>
        <a class="dropdown-item py-2" href="<%= request.getContextPath() %>/lich-hen-cua-toi">
            <i class="fa-solid fa-calendar-check me-2 text-warning"></i>Lịch hẹn & giao dịch
        </a>
        <a class="dropdown-item py-2" href="<%= request.getContextPath() %>/chat-list">
            <i class="fa-solid fa-comments me-2 text-warning"></i>Tin nhắn
        </a>
        <% if (accountMenuIsSeller) { %>
        <a class="dropdown-item py-2" href="<%= request.getContextPath() %>/my-posts#sold-posts">
            <i class="fa-solid fa-circle-check me-2 text-warning"></i>Đã bán
        </a>
        <% } %>
        <% } %>
        <% if (accountMenuIsSeller) { %>
        <a class="dropdown-item py-2" href="<%= request.getContextPath() %>/seller/dashboard">
            <i class="fa-solid fa-chart-simple me-2 text-warning"></i>Dashboard người bán
        </a>
        <a class="dropdown-item py-2" href="<%= request.getContextPath() %>/my-posts">
            <i class="fa-solid fa-layer-group me-2 text-warning"></i>Tin của tôi
        </a>
        <a class="dropdown-item py-2" href="<%= request.getContextPath() %>/post-car">
            <i class="fa-solid fa-plus me-2 text-warning"></i>Đăng tin bán xe
        </a>
        <% } else if (accountMenuIsBuyer) { %>
        <a class="dropdown-item py-2" href="<%= request.getContextPath() %>/seller-register/status">
            <i class="fa-solid fa-id-card me-2 text-warning"></i>Đăng ký Seller
        </a>
        <% } %>
        <a class="dropdown-item py-2" href="<%= request.getContextPath() %>/home">
            <i class="fa-solid fa-house me-2 text-warning"></i>Trang chủ
        </a>
        <% } %>
        <div class="dropdown-divider my-0"></div>
        <a class="dropdown-item py-2 text-danger" href="<%= request.getContextPath() %>/logout">
            <i class="fa-solid fa-right-from-bracket me-2"></i>Đăng xuất
        </a>
        <% } else { %>
        <div class="px-3 py-3 border-bottom" style="background: #f8fafc;">
            <div class="fw-bold text-dark">Tài khoản AutoPro</div>
            <div class="small text-muted">Đăng nhập để quản lý tin đăng, lịch hẹn, chat và nhận thông báo.</div>
        </div>
        <a class="dropdown-item py-2" href="<%= request.getContextPath() %>/login">
            <i class="fa-solid fa-right-to-bracket me-2 text-warning"></i>Đăng nhập
        </a>
        <a class="dropdown-item py-2" href="<%= request.getContextPath() %>/register">
            <i class="fa-solid fa-user-plus me-2 text-warning"></i>Đăng ký
        </a>
        <a class="dropdown-item py-2" href="<%= request.getContextPath() %>/forgot-password">
            <i class="fa-solid fa-key me-2 text-warning"></i>Quên mật khẩu
        </a>
        <% } %>
    </div>
</div>
