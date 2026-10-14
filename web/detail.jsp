<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>
<%@page import="model.BaiDang"%>
<%@page import="model.NguoiDung"%>
<%@page import="model.Xe"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%!
    private String resolveImageUrl(String rawImage, String contextPath) {
        if (rawImage == null || rawImage.trim().isEmpty()) {
            return contextPath + "/uploads/cars/car-placeholder.svg";
        }
        String image = rawImage.trim();
        if (image.startsWith("http://") || image.startsWith("https://")) {
            return image;
        }
        if (image.startsWith("/")) {
            return contextPath + image;
        }
        return contextPath + "/" + image;
    }

    private String resolveAvatarUrl(String rawAvatar, String fullName, String contextPath) {
        if (rawAvatar != null && !rawAvatar.trim().isEmpty()) {
            String avatar = rawAvatar.trim();
            if (avatar.startsWith("http://") || avatar.startsWith("https://")) {
                return avatar;
            }
            if (avatar.startsWith("/")) {
                return contextPath + avatar;
            }
            return contextPath + "/" + avatar;
        }

        String safeName = (fullName == null || fullName.trim().isEmpty())
                ? "Nguoi+ban"
                : fullName.trim().replace(" ", "+");
        return "https://ui-avatars.com/api/?name=" + safeName + "&background=FFC400&color=000";
    }
%>
<%
    Xe xe = (Xe) request.getAttribute("xe");
    BaiDang baiDang = (BaiDang) request.getAttribute("baiDang");
    NguoiDung seller = (NguoiDung) request.getAttribute("seller");
    NguoiDung currentUser = (NguoiDung) session.getAttribute("user");
    boolean isOwnPost = Boolean.TRUE.equals(request.getAttribute("isOwnPost"));
    boolean isSavedPost = Boolean.TRUE.equals(request.getAttribute("isSavedPost"));
    boolean isSoldPost = xe != null && xe.isDaBan();
    boolean isTradingPost = xe != null && xe.isDangGiaoDich();
    boolean isLockedPost = isSoldPost || isTradingPost;
    boolean viewerIsPostOwner = isOwnPost
            || (currentUser != null && baiDang != null && baiDang.getMaNguoiDung() == currentUser.getMaNguoiDung())
            || (currentUser != null && xe != null && xe.getMaNguoiBan() != null && xe.getMaNguoiBan() == currentUser.getMaNguoiDung());
    boolean canBuyerContact = currentUser != null
            && currentUser.isThanhVien()
            && !"blocked".equalsIgnoreCase(currentUser.getTrangThai())
            && !viewerIsPostOwner
            && !isLockedPost;
    boolean canSavePost = currentUser != null
            && currentUser.isThanhVien()
            && !"blocked".equalsIgnoreCase(currentUser.getTrangThai())
            && !viewerIsPostOwner
            && baiDang != null
            && !isLockedPost;
    List<String> imageList = new ArrayList<>();

    if (xe != null) {
        for (String img : xe.getDanhSachHinhAnh()) {
            if (img != null && !img.trim().isEmpty()) {
                imageList.add(resolveImageUrl(img.trim(), request.getContextPath()));
            }
        }
    }
%>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <%@ include file="/WEB-INF/jspf/favicon.jspf" %>
        <title><%= xe != null ? xe.getTenXe() + " - AutoPro" : "Chi tiết xe - AutoPro" %></title>
        <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css">
        <style>
            :root {
                --bg-body: #f5f5f5;
                --color-primary: #ffc400;
                --color-text: #222222;
                --color-gray: #6b7280;
                --border-color: #e5e7eb;
                --price-color: #e53935;
                --radius-box: 18px;
            }

            body {
                font-family: 'Inter', sans-serif;
                background-color: var(--bg-body);
                color: var(--color-text);
                padding-top: 90px;
            }

            .header-top {
                position: fixed;
                top: 0;
                left: 0;
                right: 0;
                min-height: 86px;
                background: #fff;
                z-index: 1030;
                display: flex;
                align-items: center;
                overflow: visible;
                box-shadow: 0 1px 4px rgba(0, 0, 0, 0.05);
                padding: 0 24px;
            }

            .h-logo {
                display: inline-flex;
                align-items: center;
                overflow: visible;
                font-size: 24px;
                font-weight: 800;
                color: #e68a00;
                text-decoration: none;
            }

            .main-container {
                max-width: 1200px;
                margin: 0 auto;
                padding-bottom: 40px;
            }

            .box-white {
                background: #fff;
                border-radius: var(--radius-box);
                padding: 24px;
                margin-bottom: 20px;
                box-shadow: 0 2px 8px rgba(0, 0, 0, 0.03);
            }

            .car-image {
                width: 100%;
                height: 460px;
                object-fit: cover;
                border-radius: 14px;
            }

            .carousel-item {
                position: relative;
            }

            .car-title {
                font-size: 30px;
                font-weight: 700;
                margin-bottom: 12px;
            }

            .car-price {
                font-size: 34px;
                font-weight: 700;
                color: var(--price-color);
                margin-bottom: 18px;
            }

            .notice-box {
                border-radius: 14px;
                padding: 14px 16px;
                margin-bottom: 24px;
                background: #fff8db;
                color: #6b5300;
            }

            .sold-alert {
                border: 1px solid #f7c948;
                background: #fff7d6;
                color: #6b4e00;
                border-radius: 14px;
                padding: 14px 16px;
            }

            .sold-badge {
                display: inline-flex;
                align-items: center;
                gap: 6px;
                padding: 8px 12px;
                border-radius: 999px;
                background: #198754;
                color: #ffffff;
                font-size: 14px;
                font-weight: 800;
                vertical-align: middle;
            }

            .sold-overlay {
                position: absolute;
                top: 18px;
                left: 18px;
                z-index: 2;
                box-shadow: 0 10px 24px rgba(0, 0, 0, 0.18);
            }

            .specs-grid {
                display: grid;
                grid-template-columns: repeat(2, minmax(0, 1fr));
                gap: 14px;
                background: #f9fafb;
                padding: 18px;
                border-radius: 14px;
                margin-bottom: 24px;
            }

            .spec-item {
                display: flex;
                align-items: center;
                gap: 10px;
            }

            .spec-item i {
                width: 20px;
                color: var(--color-gray);
                text-align: center;
            }

            .spec-label {
                color: var(--color-gray);
                min-width: 100px;
            }

            .spec-value {
                font-weight: 600;
            }

            .desc-box {
                line-height: 1.7;
                color: #475467;
                white-space: pre-wrap;
            }

            .seller-box {
                text-align: center;
            }

            .seller-avatar {
                width: 88px;
                height: 88px;
                border-radius: 50%;
                object-fit: cover;
                margin-bottom: 14px;
                border: 3px solid #ffe48f;
            }

            .seller-name {
                font-size: 20px;
                font-weight: 700;
                margin-bottom: 6px;
            }

            .seller-status {
                font-size: 13px;
                font-weight: 600;
                margin-bottom: 14px;
            }

            .btn-contact {
                display: flex;
                align-items: center;
                justify-content: center;
                gap: 10px;
                width: 100%;
                padding: 12px;
                border-radius: 999px;
                font-weight: 700;
                font-size: 15px;
                margin-bottom: 12px;
                border: none;
                text-decoration: none;
                transition: 0.2s;
            }

            .btn-mail {
                background: #4caf50;
                color: white;
            }

            .btn-mail:hover {
                background: #3d9341;
                color: white;
            }

            .btn-chat {
                background: var(--color-primary);
                color: #1f2937;
            }

            .btn-chat:hover {
                background: #e6b000;
                color: #1f2937;
            }

            .btn-buy {
                background: #111827;
                color: white;
            }

            .btn-buy:hover {
                background: #000;
                color: white;
            }

            .btn-report {
                background: #fff1f2;
                color: #c62828;
                border: 1px solid #fecdd3;
            }

            .btn-secondary-light {
                border: 1px solid var(--border-color);
                background: #fff;
                color: var(--color-text);
            }

            .btn-secondary-light:hover {
                background: #f9fafb;
            }

            @media (max-width: 768px) {
                .car-image {
                    height: 280px;
                }

                .specs-grid {
                    grid-template-columns: 1fr;
                }
            }
        </style>
    </head>
    <body>
        <header class="header-top">
            <div class="container-fluid px-0">
                <div class="d-flex align-items-center justify-content-between gap-3">
                    <a href="${pageContext.request.contextPath}/home" class="h-logo d-inline-flex align-items-center">
                        <jsp:include page="/WEB-INF/includes/site-logo.jsp">
                            <jsp:param name="logoHeight" value="50px" />
                        </jsp:include>
                    </a>
                    <div class="d-flex align-items-center gap-2">
                        <a href="home" class="btn btn-outline-secondary btn-sm">Trang chủ</a>
                        <% if (currentUser == null || !currentUser.isAdmin()) { %>
                        <a href="chat-list" class="btn btn-outline-secondary btn-sm">Tin nhắn</a>
                        <% } %>
                        <jsp:include page="/WEB-INF/includes/account-menu.jsp" />
                    </div>
                </div>
            </div>
        </header>

        <div class="main-container">
            <c:if test="${not empty detailError}">
                <div class="alert alert-danger">${detailError}</div>
            </c:if>
            <c:if test="${not empty detailSuccess}">
                <div class="alert alert-success">${detailSuccess}</div>
            </c:if>
            <c:if test="${not empty xe and xe.daBan}">
                <div class="sold-alert mb-3">
                    <strong>Xe này đã bán.</strong> AutoPro đã khóa các thao tác chat mới, lưu bài, báo cáo và gửi yêu cầu kết nối từ bài đăng này.
                </div>
            </c:if>

            <% if (xe != null) { %>
            <div class="mb-3 text-muted" style="font-size: 14px;">
                <a href="home" class="text-decoration-none text-muted">Trang chủ</a>
                <span class="mx-2">/</span>
                <span class="text-dark fw-medium"><%= xe.getTenXe() %></span>
            </div>

            <div class="row">
                <div class="col-lg-8">
                    <div class="box-white">
                        <% if (!imageList.isEmpty()) { %>
                        <div id="carCarousel" class="carousel slide mb-4" data-bs-ride="carousel">
                            <div class="carousel-inner">
                                <% for (int i = 0; i < imageList.size(); i++) { %>
                                <div class="carousel-item <%= i == 0 ? "active" : "" %>">
                                    <% if (isLockedPost && i == 0) { %>
                                    <span class="sold-badge sold-overlay"><i class="fa-solid <%= isSoldPost ? "fa-circle-check" : "fa-handshake" %>"></i> <%= isSoldPost ? "Đã bán" : "Đang giao dịch" %></span>
                                    <% } %>
                                    <img src="<%= imageList.get(i) %>" class="car-image d-block w-100" alt="<%= xe.getTenXe() %>" data-car-name="<%= xe.getTenXe() %>" data-context-path="<%= request.getContextPath() %>" onerror="useAutoProCarFallback(this)">
                                </div>
                                <% } %>
                            </div>

                            <% if (imageList.size() > 1) { %>
                            <button class="carousel-control-prev" type="button" data-bs-target="#carCarousel" data-bs-slide="prev">
                                <span class="carousel-control-prev-icon" aria-hidden="true"></span>
                                <span class="visually-hidden">Trước</span>
                            </button>
                            <button class="carousel-control-next" type="button" data-bs-target="#carCarousel" data-bs-slide="next">
                                <span class="carousel-control-next-icon" aria-hidden="true"></span>
                                <span class="visually-hidden">Sau</span>
                            </button>
                            <% } %>
                        </div>
                        <% } else { %>
                        <div class="mb-4">
                            <img src="<%= request.getContextPath() %>/uploads/cars/car-placeholder.svg" class="car-image d-block w-100" alt="Chưa có hình ảnh" data-car-name="<%= xe.getTenXe() %>" data-context-path="<%= request.getContextPath() %>" onerror="useAutoProCarFallback(this)">
                        </div>
                        <% } %>

                        <h1 class="car-title">
                            <%= xe.getTenXe() %>
                            <% if (isLockedPost) { %>
                            <span class="sold-badge ms-2"><i class="fa-solid <%= isSoldPost ? "fa-circle-check" : "fa-handshake" %>"></i> <%= isSoldPost ? "Đã bán" : "Đang giao dịch" %></span>
                            <% } %>
                        </h1>
                        <div class="car-price"><fmt:formatNumber value="${xe.gia}" type="number" maxFractionDigits="0"/> đ</div>

                        <div class="notice-box">
                            AutoPro không hỗ trợ thanh toán trực tiếp trên website. Người mua và người bán sẽ trao đổi qua chat, hẹn xem xe và ký hợp đồng trực tiếp.
                        </div>

                        <h5 class="fw-bold mb-3">Thông số kỹ thuật</h5>
                        <div class="specs-grid">
                            <div class="spec-item"><i class="fa-regular fa-calendar"></i><span class="spec-label">Năm SX</span><span class="spec-value"><%= xe.getNamSanXuat() %></span></div>
                            <div class="spec-item"><i class="fa-solid fa-gauge-high"></i><span class="spec-label">Số km</span><span class="spec-value"><fmt:formatNumber value="${xe.soKmDaDi}" type="number" maxFractionDigits="0"/> km</span></div>
                            <div class="spec-item"><i class="fa-solid fa-gas-pump"></i><span class="spec-label">Nhiên liệu</span><span class="spec-value"><%= xe.getNhienLieu() != null && !xe.getNhienLieu().isBlank() ? xe.getNhienLieu() : "Chưa cập nhật" %></span></div>
                            <div class="spec-item"><i class="fa-solid fa-gear"></i><span class="spec-label">Hộp số</span><span class="spec-value"><%= xe.getHopSo() != null && !xe.getHopSo().isBlank() ? xe.getHopSo() : "Chưa cập nhật" %></span></div>
                            <div class="spec-item"><i class="fa-solid fa-car-side"></i><span class="spec-label">Kiểu dáng</span><span class="spec-value"><%= xe.getKieuDang() != null && !xe.getKieuDang().isBlank() ? xe.getKieuDang() : "Chưa cập nhật" %></span></div>
                            <div class="spec-item"><i class="fa-solid fa-globe"></i><span class="spec-label">Xuất xứ</span><span class="spec-value"><%= xe.getXuatXu() != null && !xe.getXuatXu().isBlank() ? xe.getXuatXu() : "Chưa cập nhật" %></span></div>
                            <div class="spec-item"><i class="fa-solid fa-users"></i><span class="spec-label">Số chỗ</span><span class="spec-value"><%= xe.getSoCho() > 0 ? xe.getSoCho() + " chỗ" : "Chưa cập nhật" %></span></div>
                            <div class="spec-item"><i class="fa-solid fa-palette"></i><span class="spec-label">Màu xe</span><span class="spec-value"><%= xe.getMauXe() != null && !xe.getMauXe().isBlank() ? xe.getMauXe() : "Chưa cập nhật" %></span></div>
                            <div class="spec-item"><i class="fa-solid fa-location-dot"></i><span class="spec-label">Tỉnh/TP</span><span class="spec-value"><%= xe.getTenTinh() != null && !xe.getTenTinh().isBlank() ? xe.getTenTinh() : "Chưa cập nhật" %></span></div>
                            <div class="spec-item"><i class="fa-solid fa-map"></i><span class="spec-label">Miền</span><span class="spec-value"><%= xe.getMien() != null && !xe.getMien().isBlank() ? xe.getMien() : "Chưa cập nhật" %></span></div>
                        </div>

                        <h5 class="fw-bold mb-3">Mô tả chi tiết</h5>
                        <div class="desc-box"><%= (xe.getMoTa() != null && !xe.getMoTa().isEmpty()) ? xe.getMoTa() : "Người bán chưa bổ sung mô tả cho xe này." %></div>
                    </div>
                </div>

                <div class="col-lg-4">
                    <div class="box-white seller-box">
                        <img src="https://ui-avatars.com/api/?name=<%= seller != null ? seller.getHoTen().replace(" ", "+") : "Nguoi+ban" %>&background=FFC400&color=000" class="seller-avatar" alt="Avatar người bán">
                        <div class="seller-name"><%= seller != null ? seller.getHoTen() : "Người bán" %></div>
                        <div class="seller-status <%= seller != null && "blocked".equalsIgnoreCase(seller.getTrangThai()) ? "text-danger" : "text-success" %>">
                            <i class="fa-solid <%= seller != null && "blocked".equalsIgnoreCase(seller.getTrangThai()) ? "fa-triangle-exclamation" : "fa-circle-check" %>"></i>
                            <%= seller != null && "blocked".equalsIgnoreCase(seller.getTrangThai()) ? "Tài khoản tạm khóa" : "Đang hoạt động" %>
                        </div>

                        <% if (seller != null) { %>
                        <div class="small text-muted mb-3 text-start">
                            <% if (seller.getEmail() != null && !seller.getEmail().isBlank()) { %>
                            <div><strong>Email:</strong> <%= seller.getEmail() %></div>
                            <% } %>
                            <% if (seller.getSoDienThoai() != null && !seller.getSoDienThoai().isBlank()) { %>
                            <div><strong>Số điện thoại:</strong> <%= seller.getSoDienThoai() %></div>
                            <% } %>
                        </div>
                        <% } %>

                        <div class="alert alert-light text-start small mb-3">
                            Gợi ý: hãy chat trước để thống nhất giá, lịch xem xe, tình trạng giấy tờ và nơi ký hợp đồng.
                        </div>

                        <% if (isLockedPost) { %>
                        <div class="alert alert-warning text-start mb-3">
                            <strong><%= isSoldPost ? "Bài đăng đã hoàn tất giao dịch." : "Xe này đang trong quá trình giao dịch." %></strong><br>
                            <%= isSoldPost ? "Xe này không còn nhận chat mới, lưu bài, báo cáo hoặc yêu cầu kết nối." : "Hệ thống tạm khóa yêu cầu mua mới cho đến khi giao dịch hoàn tất, hủy hoặc admin xử lý." %>
                        </div>
                        <button type="button" class="btn-contact btn-secondary-light" disabled>
                            <i class="fa-solid fa-lock"></i> <%= isSoldPost ? "Đã bán - đã khóa chức năng" : "Đang giao dịch - tạm khóa yêu cầu mua" %>
                        </button>
                        <% } else if (currentUser == null) { %>
                        <a href="login" class="btn-contact btn-chat">
                            <i class="fa-solid fa-right-to-bracket"></i> Đăng nhập để chat và gửi yêu cầu hẹn xem xe
                        </a>
                        <% } else if (viewerIsPostOwner) { %>
                        <div class="alert alert-warning text-start mb-3">
                            Đây là bài đăng của bạn. Bạn có thể chỉnh sửa trong mục "Tin của tôi".
                        </div>
                        <a href="my-posts" class="btn-contact btn-secondary-light">
                            <i class="fa-solid fa-pen-to-square"></i> Quản lý tin đăng
                        </a>
                        <a href="seller/dashboard" class="btn-contact btn-chat">
                            <i class="fa-solid fa-chart-simple"></i> Dashboard người bán
                        </a>
                        <% } else if (!currentUser.isThanhVien()) { %>
                        <div class="alert alert-secondary text-start mb-3">
                            Tài khoản quản trị chỉ được xem thông tin bài đăng, không gửi email, chat hay tạo lịch hẹn.
                        </div>
                        <% } else if ("blocked".equalsIgnoreCase(currentUser.getTrangThai())) { %>
                        <div class="alert alert-danger text-start mb-3">
                            Tài khoản của bạn đang bị hạn chế nên chưa thể chat, gửi email hay tạo lịch hẹn.
                        </div>
                        <% } else { %>
                            <% if (seller != null && seller.getEmail() != null && !seller.getEmail().isBlank()) { %>
                            <a href="mailto:<%= seller.getEmail() %>" class="btn-contact btn-mail">
                                <i class="fa-solid fa-envelope"></i> Gửi email cho người bán
                            </a>
                            <% } %>

                            <% if (canSavePost) { %>
                            <form action="saved-posts/action" method="post" class="m-0">
<input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                                <input type="hidden" name="maBaiDang" value="<%= baiDang.getMaBaiDang() %>">
                                <input type="hidden" name="source" value="detail">
                                <input type="hidden" name="action" value="<%= isSavedPost ? "unsave" : "save" %>">
                                <button type="submit" class="btn-contact btn-secondary-light">
                                    <i class="fa-<%= isSavedPost ? "solid" : "regular" %> fa-heart"></i>
                                    <%= isSavedPost ? "Bỏ lưu bài đăng" : "Lưu bài đăng" %>
                                </button>
                            </form>
                            <% } %>

                            <% if (xe.getMaNguoiBan() != null) { %>
                            <a href="chat?seller=<%= xe.getMaNguoiBan() %>&car=<%= xe.getMaXe() %>" class="btn-contact btn-chat">
                                <i class="fa-solid fa-comment-dots"></i> Chat với người bán
                            </a>
                            <% } %>

                            <button type="button" class="btn-contact btn-buy" data-bs-toggle="modal" data-bs-target="#purchaseRequestModal">
                                <i class="fa-solid fa-calendar-check"></i> G&#7917;i y&ecirc;u c&#7847;u mua xe
                            </button>

                            <% if (baiDang != null) { %>
                            <button type="button" class="btn-contact btn-report" data-bs-toggle="modal" data-bs-target="#reportPostModal">
                                <i class="fa-solid fa-flag"></i> Báo cáo bài đăng
                            </button>
                            <% } %>
                        <% } %>

                        <% if (!isSoldPost) { %>
                        <a href="chat-list" class="btn-contact btn-secondary-light">
                            <i class="fa-solid fa-comments"></i> Xem cuộc trò chuyện
                        </a>
                        <% } %>
                        <a href="home" class="btn-contact btn-secondary-light">
                            <i class="fa-solid fa-house"></i> Quay lại danh sách xe
                        </a>
                    </div>
                </div>
            </div>

            <% if (baiDang != null && canBuyerContact) { %>
            <div class="modal fade" id="purchaseRequestModal" tabindex="-1" aria-hidden="true">
                <div class="modal-dialog modal-dialog-centered">
                    <div class="modal-content">
                        <form>
                            <div class="modal-header">
                                <h5 class="modal-title">X&aacute;c nh&#7853;n c&aacute;ch mua xe</h5>
                                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Đóng"></button>
                            </div>
                            <div class="modal-body">
                                <div class="alert alert-warning">
                                    B&#7841;n c&oacute; x&aacute;c nh&#7853;n h&#7865;n xem xe kh&ocirc;ng? N&#7871;u ch&#7885;n <strong>Kh&ocirc;ng</strong>, h&#7879; th&#7889;ng s&#7869; t&#7841;o giao d&#7883;ch v&agrave; chuy&#7875;n xe sang tr&#7841;ng th&aacute;i &#273;ang giao d&#7883;ch.
                                </div>

                                <div class="mb-3">
                                    <label class="form-label fw-semibold">Xe quan tâm</label>
                                    <input type="text" class="form-control" value="<%= xe.getTenXe() %>" readonly>
                                </div>

                                <div class="row g-3">
                                    <div class="col-md-6">
                                        <label class="form-label fw-semibold">Người mua</label>
                                        <input type="text" class="form-control" value="<%= currentUser != null ? currentUser.getHoTen() : "" %>" readonly>
                                    </div>
                                    <div class="col-md-6">
                                        <label class="form-label fw-semibold">Người bán</label>
                                        <input type="text" class="form-control" value="<%= seller != null ? seller.getHoTen() : "Người bán" %>" readonly>
                                    </div>
                                </div>

                                <div class="alert alert-light border mb-0">
                                    Nếu không hẹn xem xe, bạn cần nhập địa chỉ và số điện thoại nhận xe ở bước tiếp theo để người bán liên hệ giao xe.
                                </div>
                            </div>
                            <div class="modal-footer">
                                <button type="button" class="btn btn-outline-warning" data-bs-toggle="modal" data-bs-target="#purchaseDirectModal">
                                    Không - tạo giao dịch giao xe
                                </button>
                                <button type="button" class="btn btn-dark" data-bs-toggle="modal" data-bs-target="#purchaseScheduleModal">
                                    X&aacute;c nh&#7853;n h&#7865;n xem xe
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>

            <div class="modal fade" id="purchaseDirectModal" tabindex="-1" aria-hidden="true">
                <div class="modal-dialog modal-dialog-centered">
                    <div class="modal-content">
                        <form action="yeu-cau-ket-noi" method="post">
<input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                            <div class="modal-header">
                                <h5 class="modal-title">Thông tin giao xe</h5>
                                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Đóng"></button>
                            </div>
                            <div class="modal-body">
                                <input type="hidden" name="maXe" value="<%= xe.getMaXe() %>">
                                <input type="hidden" name="purchaseMode" value="create-transaction">

                                <div class="alert alert-warning">
                                    Hệ thống sẽ tạo giao dịch ngay và khóa bài đăng ở trạng thái <strong>đang giao dịch</strong>. AutoPro không xử lý thanh toán online; hai bên tự thanh toán, ký giấy tờ và bàn giao ngoài thực tế.
                                </div>

                                <div class="mb-3">
                                    <label class="form-label fw-semibold">Xe quan tâm</label>
                                    <input type="text" class="form-control" value="<%= xe.getTenXe() %>" readonly>
                                </div>

                                <div class="row g-3">
                                    <div class="col-md-6">
                                        <label class="form-label fw-semibold">Người mua</label>
                                        <input type="text" class="form-control" value="<%= currentUser != null ? currentUser.getHoTen() : "" %>" readonly>
                                    </div>
                                    <div class="col-md-6">
                                        <label class="form-label fw-semibold">Người bán</label>
                                        <input type="text" class="form-control" value="<%= seller != null ? seller.getHoTen() : "Người bán" %>" readonly>
                                    </div>
                                </div>

                                <div class="mt-3">
                                    <label class="form-label fw-semibold">Số điện thoại nhận xe</label>
                                    <input type="tel" name="soDienThoaiGiaoXe" maxlength="20" class="form-control"
                                           value="<%= currentUser != null && currentUser.getSoDienThoai() != null ? currentUser.getSoDienThoai() : "" %>"
                                           placeholder="VD: 0901234567" required>
                                    <div class="form-text">Người bán sẽ dùng số này để liên hệ trước khi giao xe.</div>
                                </div>

                                <div class="mt-3">
                                    <label class="form-label fw-semibold">Địa chỉ giao xe</label>
                                    <textarea name="diaDiemGiaoXe" rows="3" maxlength="255" class="form-control"
                                              placeholder="Nhập địa chỉ cụ thể để người bán giao xe..." required></textarea>
                                </div>

                                <div class="mt-3">
                                    <label class="form-label fw-semibold">Nhu cầu trao đổi / ghi chú</label>
                                    <textarea name="ghiChuNguoiMua" rows="4" maxlength="1000" class="form-control" placeholder="Ví dụ: Tôi muốn người bán gọi trước khi giao, cần trao đổi thêm giấy tờ, hợp đồng hoặc giá bán..."></textarea>
                                </div>
                            </div>
                            <div class="modal-footer">
                                <button type="button" class="btn btn-light" data-bs-toggle="modal" data-bs-target="#purchaseRequestModal">Quay lại</button>
                                <button type="submit" class="btn btn-warning fw-semibold">
                                    Tạo giao dịch ngay
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>

            <div class="modal fade" id="purchaseScheduleModal" tabindex="-1" aria-hidden="true">
                <div class="modal-dialog modal-dialog-centered">
                    <div class="modal-content">
                        <form action="yeu-cau-ket-noi" method="post">
<input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                            <div class="modal-header">
                                <h5 class="modal-title">&#272;&#7863;t l&#7883;ch xem xe</h5>
                                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="&#272;&oacute;ng"></button>
                            </div>
                            <div class="modal-body">
                                <input type="hidden" name="maXe" value="<%= xe.getMaXe() %>">
                                <input type="hidden" name="purchaseMode" value="schedule">

                                <div class="mb-3">
                                    <label class="form-label fw-semibold">Xe quan t&acirc;m</label>
                                    <input type="text" class="form-control" value="<%= xe.getTenXe() %>" readonly>
                                </div>

                                <div class="row g-3">
                                    <div class="col-md-6">
                                        <label class="form-label fw-semibold">Th&#7901;i gian h&#7865;n</label>
                                        <input type="datetime-local" name="thoiGianHen" class="form-control" required>
                                    </div>
                                    <div class="col-md-6">
                                        <label class="form-label fw-semibold">&#272;&#7883;a &#273;i&#7875;m h&#7865;n</label>
                                        <input type="text" name="diaDiemHen" maxlength="255" class="form-control" placeholder="VD: showroom, nh&agrave; ri&ecirc;ng, gara ki&#7875;m tra..." required>
                                    </div>
                                </div>

                                <div class="mt-3">
                                    <label class="form-label fw-semibold">Ghi ch&uacute; y&ecirc;u c&#7847;u mua</label>
                                    <textarea name="ghiChuNguoiMua" rows="3" maxlength="1000" class="form-control" placeholder="N&#7897;i dung mu&#7889;n trao &#273;&#7893;i v&#7899;i ng&#432;&#7901;i b&aacute;n..."></textarea>
                                </div>

                                <div class="mt-3">
                                    <label class="form-label fw-semibold">Ghi ch&uacute; l&#7883;ch h&#7865;n</label>
                                    <textarea name="ghiChuHen" rows="3" maxlength="1000" class="form-control" placeholder="VD: T&ocirc;i mu&#7889;n xem xe v&agrave;o cu&#7889;i tu&#7847;n, c&#7847;n ki&#7875;m tra gi&#7845;y t&#7901; v&agrave; b&#7843;o d&#432;&#7905;ng..."></textarea>
                                </div>
                            </div>
                            <div class="modal-footer">
                                <button type="button" class="btn btn-light" data-bs-toggle="modal" data-bs-target="#purchaseRequestModal">Quay l&#7841;i</button>
                                <button type="submit" class="btn btn-dark">G&#7917;i y&ecirc;u c&#7847;u v&agrave; &#273;&#7863;t l&#7883;ch</button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>

            <div class="modal fade" id="reportPostModal" tabindex="-1" aria-hidden="true">
                <div class="modal-dialog modal-dialog-centered">
                    <div class="modal-content">
                        <form action="report-post" method="post">
<input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                            <div class="modal-header">
                                <h5 class="modal-title">Báo cáo bài đăng vi phạm</h5>
                                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Đóng"></button>
                            </div>
                            <div class="modal-body">
                                <input type="hidden" name="maBaiDang" value="<%= baiDang.getMaBaiDang() %>">
                                <input type="hidden" name="maXe" value="<%= xe.getMaXe() %>">

                                <div class="mb-3">
                                    <label class="form-label fw-semibold">Lý do báo cáo</label>
                                    <select name="lyDo" class="form-select" required>
                                        <option value="">Chọn lý do</option>
                                        <option value="Thông tin sai lệch">Thông tin sai lệch</option>
                                        <option value="Giá bán không minh bạch">Giá bán không minh bạch</option>
                                        <option value="Hình ảnh không đúng xe">Hình ảnh không đúng xe</option>
                                        <option value="Nội dung lừa đảo / đáng ngờ">Nội dung lừa đảo / đáng ngờ</option>
                                        <option value="Ngôn từ phản cảm">Ngôn từ phản cảm</option>
                                        <option value="Khác">Khác</option>
                                    </select>
                                </div>

                                <div class="mb-0">
                                    <label class="form-label fw-semibold">Chi tiết thêm</label>
                                    <textarea name="chiTiet" rows="4" class="form-control" placeholder="Mô tả cụ thể nội dung vi phạm để admin xử lý nhanh hơn..."></textarea>
                                </div>
                            </div>
                            <div class="modal-footer">
                                <button type="button" class="btn btn-outline-secondary" data-bs-dismiss="modal">Hủy</button>
                                <button type="submit" class="btn btn-danger">Gửi báo cáo</button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
            <% } %>

            <% } else { %>
            <div class="box-white text-center py-5">
                <i class="fa-solid fa-car-burst text-muted mb-3" style="font-size: 64px;"></i>
                <h3>Không tìm thấy xe</h3>
                <p class="text-muted">Xe bạn đang tìm kiếm không tồn tại, đã bị xóa hoặc chưa được duyệt hiển thị công khai.</p>
                <a href="home" class="btn btn-warning mt-3 fw-semibold">Quay lại trang chủ</a>
            </div>
            <% } %>
        </div>

        <script>
            document.addEventListener('DOMContentLoaded', function () {
                const sellerAvatar = document.querySelector('.seller-avatar');
                if (sellerAvatar) {
                    sellerAvatar.src = '<%= resolveAvatarUrl(seller != null ? seller.getAnhDaiDien() : null, seller != null ? seller.getHoTen() : null, request.getContextPath()) %>';
                }
            });
        </script>
        <script src="<%= request.getContextPath() %>/assets/js/autopro-image-fallback.js"></script>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
        <jsp:include page="/WEB-INF/includes/site-footer.jsp" />

    </body>
</html>
