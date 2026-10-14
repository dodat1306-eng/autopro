<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <%@ include file="/WEB-INF/jspf/favicon.jspf" %>
        <title>Hồ sơ của tôi - AutoPro</title>
        <link rel="preconnect" href="https://fonts.googleapis.com">
        <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
        <link href="https://fonts.googleapis.com/css2?family=Manrope:wght@400;500;600;700;800&display=swap" rel="stylesheet">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
        <style>
            :root {
                --ui-bg: #f4f6fb;
                --ui-card: #ffffff;
                --ui-line: #e7ecf3;
                --ui-text: #14233b;
                --ui-muted: #65758b;
                --ui-gold: #f5b700;
                --ui-gold-soft: #fff4cf;
                --ui-shadow: 0 18px 44px rgba(15, 23, 42, 0.08);
            }

            body {
                margin: 0;
                min-height: 100vh;
                font-family: 'Manrope', sans-serif;
                color: var(--ui-text);
                background:
                    radial-gradient(circle at top right, rgba(255, 214, 102, 0.2), transparent 28%),
                    linear-gradient(180deg, #fbfdff 0%, var(--ui-bg) 100%);
            }

            .header-top {
                position: sticky;
                top: 0;
                z-index: 1030;
                background: #ffffff;
                border-bottom: 1px solid rgba(226, 232, 240, 0.9);
                box-shadow: 0 1px 4px rgba(15, 23, 42, 0.04);
            }

            .header-shell {
                max-width: 1240px;
                margin: 0 auto;
                min-height: 84px;
                padding: 0 18px;
                display: flex;
                align-items: center;
                justify-content: space-between;
                gap: 16px;
            }

            .page-wrap {
                max-width: 1240px;
                margin: 28px auto 48px;
                padding: 0 16px;
            }

            .hero-card,
            .content-card {
                background: var(--ui-card);
                border: 1px solid var(--ui-line);
                border-radius: 28px;
                box-shadow: var(--ui-shadow);
            }

            .hero-card {
                padding: 28px;
                background:
                    linear-gradient(135deg, rgba(255, 240, 190, 0.86), rgba(255, 255, 255, 0.98) 44%),
                    #ffffff;
            }

            .content-card {
                padding: 24px;
            }

            .eyebrow {
                display: inline-flex;
                align-items: center;
                gap: 8px;
                padding: 8px 14px;
                border-radius: 999px;
                background: #111827;
                color: #ffffff;
                font-size: 12px;
                font-weight: 800;
                text-transform: uppercase;
                letter-spacing: 0.04em;
            }

            .hero-title {
                font-size: clamp(2rem, 4vw, 3rem);
                font-weight: 800;
                line-height: 1.04;
                margin: 16px 0 10px;
            }

            .hero-text,
            .muted-text {
                color: var(--ui-muted);
                line-height: 1.7;
                font-size: 14px;
            }

            .top-actions {
                display: flex;
                gap: 10px;
                flex-wrap: wrap;
                align-items: center;
            }

            .profile-head {
                display: flex;
                justify-content: space-between;
                align-items: center;
                gap: 20px;
                flex-wrap: wrap;
            }

            .profile-summary {
                display: flex;
                align-items: center;
                gap: 18px;
                flex-wrap: wrap;
            }

            .profile-avatar {
                width: 112px;
                height: 112px;
                border-radius: 50%;
                object-fit: cover;
                border: 4px solid #fff1ba;
                box-shadow: 0 12px 24px rgba(17, 24, 39, 0.12);
                background: #ffffff;
            }

            .profile-name {
                font-size: 28px;
                font-weight: 800;
                margin-bottom: 6px;
            }

            .role-pill,
            .status-pill {
                display: inline-flex;
                align-items: center;
                gap: 8px;
                padding: 8px 14px;
                border-radius: 999px;
                font-size: 12px;
                font-weight: 800;
            }

            .role-pill {
                background: #101828;
                color: #ffffff;
            }

            .status-approved {
                background: #dff6e8;
                color: #166534;
            }

            .status-pending {
                background: #fff3cf;
                color: #8a6200;
            }

            .status-rejected {
                background: #ffe3e1;
                color: #b42318;
            }

            .status-default {
                background: #e8edf4;
                color: #475569;
            }

            .summary-grid {
                display: grid;
                grid-template-columns: repeat(4, minmax(0, 1fr));
                gap: 14px;
                margin-top: 24px;
            }

            .summary-card {
                border-radius: 22px;
                border: 1px solid rgba(226, 232, 240, 0.92);
                background: rgba(255, 255, 255, 0.9);
                padding: 18px;
            }

            .summary-card .icon {
                width: 44px;
                height: 44px;
                border-radius: 14px;
                display: inline-flex;
                align-items: center;
                justify-content: center;
                background: var(--ui-gold-soft);
                color: #8a5b00;
                margin-bottom: 12px;
            }

            .summary-card .value {
                font-size: 28px;
                font-weight: 800;
                line-height: 1;
            }

            .summary-card .label {
                margin-top: 10px;
                font-size: 13px;
                font-weight: 700;
            }

            .summary-card .meta {
                margin-top: 6px;
                color: var(--ui-muted);
                font-size: 12px;
                line-height: 1.55;
            }

            .section-head {
                display: flex;
                justify-content: space-between;
                align-items: flex-start;
                gap: 14px;
                margin-bottom: 18px;
            }

            .section-head h3 {
                margin: 0;
                font-weight: 800;
            }

            .panel-soft {
                border-radius: 22px;
                border: 1px solid var(--ui-line);
                background: #fbfdff;
                padding: 18px;
            }

            .form-control,
            .form-select {
                border-radius: 16px;
                border-color: #d9e1ea;
                padding: 12px 14px;
                min-height: 50px;
            }

            textarea.form-control {
                min-height: 120px;
            }

            .form-control:focus,
            .form-select:focus {
                box-shadow: 0 0 0 0.2rem rgba(245, 183, 0, 0.15);
                border-color: #f5b700;
            }

            .btn-primary-soft {
                border: none;
                background: #111827;
                color: #ffffff;
                border-radius: 16px;
                padding: 12px 20px;
                font-weight: 700;
            }

            .btn-primary-soft:hover {
                background: #000000;
                color: #ffffff;
            }

            .btn-gold {
                border: none;
                background: var(--ui-gold);
                color: #111827;
                border-radius: 16px;
                padding: 12px 20px;
                font-weight: 800;
            }

            .btn-gold:hover {
                background: #e7ac00;
                color: #111827;
            }

            .btn-outline-soft {
                border-radius: 16px;
                padding: 12px 18px;
                font-weight: 700;
            }

            .thumb-preview {
                width: 100%;
                max-width: 180px;
                aspect-ratio: 1 / 1;
                object-fit: cover;
                border-radius: 24px;
                border: 1px solid #d9e1ea;
                background: #f8fafc;
            }

            .identity-grid {
                display: grid;
                grid-template-columns: repeat(2, minmax(0, 1fr));
                gap: 14px;
            }

            .identity-card {
                border: 1px solid var(--ui-line);
                border-radius: 18px;
                padding: 12px;
                background: #ffffff;
            }

            .identity-card img {
                width: 100%;
                height: 150px;
                object-fit: contain;
                display: block;
                border-radius: 14px;
                background: #f8fafc;
                border: 1px solid #e2e8f0;
            }

            .identity-alert {
                border-radius: 16px;
                border: 1px solid #fde68a;
                background: #fffbeb;
                color: #92400e;
                padding: 12px 14px;
                font-size: 13px;
                line-height: 1.6;
            }

            .history-list {
                display: grid;
                gap: 12px;
            }

            .history-item {
                display: flex;
                justify-content: space-between;
                gap: 16px;
                align-items: center;
                border: 1px solid var(--ui-line);
                border-radius: 18px;
                padding: 14px 16px;
                background: #ffffff;
            }

            @media (max-width: 991px) {
                .summary-grid {
                    grid-template-columns: repeat(2, minmax(0, 1fr));
                }
            }

            @media (max-width: 767px) {
                .header-shell {
                    min-height: 76px;
                }

                .summary-grid {
                    grid-template-columns: 1fr;
                }

                .identity-grid {
                    grid-template-columns: 1fr;
                }

                .profile-head {
                    align-items: flex-start;
                }
            }
        </style>
    </head>
    <body>
        <c:set var="ctx" value="${pageContext.request.contextPath}" />
        <div class="header-top">
            <div class="header-shell">
                <c:choose>
                    <c:when test="${currentUser.admin}">
                        <a href="${ctx}/admin/dashboard" class="d-inline-flex align-items-center text-decoration-none">
                            <jsp:include page="/WEB-INF/includes/site-logo.jsp">
                                <jsp:param name="logoHeight" value="58px" />
                            </jsp:include>
                        </a>
                    </c:when>
                    <c:otherwise>
                        <a href="${ctx}/home" class="d-inline-flex align-items-center text-decoration-none">
                            <jsp:include page="/WEB-INF/includes/site-logo.jsp">
                                <jsp:param name="logoHeight" value="58px" />
                            </jsp:include>
                        </a>
                    </c:otherwise>
                </c:choose>
                <div class="top-actions">
                    <c:choose>
                        <c:when test="${currentUser.admin}">
                            <a href="${ctx}/admin/dashboard" class="btn btn-outline-dark btn-outline-soft">
                                <i class="fa-solid fa-arrow-left me-2"></i>Về dashboard admin
                            </a>
                        </c:when>
                        <c:otherwise>
                            <a href="${ctx}/home" class="btn btn-outline-dark btn-outline-soft">
                                <i class="fa-solid fa-house me-2"></i>Trang chủ
                            </a>
                        </c:otherwise>
                    </c:choose>
                    <%@ include file="/WEB-INF/includes/account-menu.jsp" %>
                </div>
            </div>
        </div>

        <div class="page-wrap">
            <div class="hero-card mb-4">
                <div class="profile-head">
                    <div>
                        <span class="eyebrow"><i class="fa-solid fa-id-badge"></i>Hồ sơ của tôi</span>
                        <div class="hero-title">Cập nhật thông tin tài khoản và seller profile</div>
                        <div class="hero-text">Bạn có thể chỉnh sửa email, số điện thoại, ảnh đại diện và các thông tin người bán đang dùng để hiển thị trên AutoPro.</div>
                    </div>
                    <div class="profile-summary">
                        <img src="${avatarUrl}" alt="Ảnh đại diện" class="profile-avatar">
                        <div>
                            <div class="profile-name">${currentUser.hoTen}</div>
                            <div class="d-flex flex-wrap gap-2 mb-2">
                                <span class="role-pill">
                                    <i class="fa-solid fa-user-shield"></i>${currentUser.vaiTroHienThi}
                                </span>
                                <c:choose>
                                    <c:when test="${isApprovedSeller}">
                                        <span class="status-pill status-approved"><i class="fa-solid fa-circle-check"></i>Seller đã duyệt</span>
                                    </c:when>
                                    <c:when test="${hasPendingSellerRequest}">
                                        <span class="status-pill status-pending"><i class="fa-solid fa-hourglass-half"></i>Đang chờ duyệt seller</span>
                                    </c:when>
                                    <c:when test="${not empty latestSellerRequest and latestSellerRequest.trangThai eq 'TU_CHOI'}">
                                        <span class="status-pill status-rejected"><i class="fa-solid fa-triangle-exclamation"></i>Seller bị từ chối</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="status-pill status-default"><i class="fa-solid fa-user"></i>Tài khoản thành viên</span>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                            <div class="muted-text">
                                <div><strong>Email:</strong> ${empty currentUser.email ? 'Chưa cập nhật' : currentUser.email}</div>
                                <div><strong>Số điện thoại:</strong> ${empty currentUser.soDienThoai ? 'Chưa cập nhật' : currentUser.soDienThoai}</div>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="summary-grid">
                    <div class="summary-card">
                        <span class="icon"><i class="fa-solid fa-user-large"></i></span>
                        <div class="value">${currentUser.maNguoiDung}</div>
                        <div class="label">Mã tài khoản</div>
                        <div class="meta">Dùng khi cần đối chiếu nhanh hồ sơ hoặc trao đổi với admin.</div>
                    </div>
                    <div class="summary-card">
                        <span class="icon"><i class="fa-solid fa-envelope"></i></span>
                        <div class="value">${empty currentUser.email ? '--' : currentUser.email}</div>
                        <div class="label">Email hiện tại</div>
                        <div class="meta">Đây là email hệ thống dùng cho OTP, thông báo và liên hệ seller.</div>
                    </div>
                    <div class="summary-card">
                        <span class="icon"><i class="fa-solid fa-phone"></i></span>
                        <div class="value">${empty currentUser.soDienThoai ? '--' : currentUser.soDienThoai}</div>
                        <div class="label">Số điện thoại</div>
                        <div class="meta">Nên giữ thông tin liên hệ rõ ràng để người mua kết nối nhanh hơn.</div>
                    </div>
                    <div class="summary-card">
                        <span class="icon"><i class="fa-solid fa-file-lines"></i></span>
                        <div class="value">${empty sellerRequestHistory ? 0 : fn:length(sellerRequestHistory)}</div>
                        <div class="label">Lần gửi hồ sơ seller</div>
                        <div class="meta">Giúp bạn theo dõi lịch sử xét duyệt seller của tài khoản này.</div>
                    </div>
                </div>
            </div>

            <c:if test="${param.updated eq '1'}">
                <div class="alert alert-success border-0 shadow-sm">Hồ sơ cá nhân đã được cập nhật thành công.</div>
            </c:if>
            <c:if test="${param.sellerUpdated eq '1'}">
                <div class="alert alert-success border-0 shadow-sm">Thông tin seller đã được cập nhật thành công.</div>
            </c:if>
            <c:if test="${param.identityUpdated eq '1'}">
                <div class="alert alert-success border-0 shadow-sm">Thông tin Đã gửi CCCD mới để admin xét duyệt thành công.</div>
            </c:if>
            <c:if test="${not empty error}">
                <div class="alert alert-danger border-0 shadow-sm">${error}</div>
            </c:if>

            <div class="row g-4">
                <div class="col-lg-7">
                    <div class="content-card h-100">
                        <div class="section-head">
                            <div>
                                <h3>Thông tin cá nhân</h3>
                                <div class="muted-text">Cập nhật phần hồ sơ đang hiển thị ở menu người dùng và những nơi người khác nhìn thấy tài khoản của bạn.</div>
                            </div>
                        </div>

                        <form action="${ctx}/profile" method="post" enctype="multipart/form-data" class="row g-3">
<input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                            <input type="hidden" name="action" value="updateBasic">
                            <div class="col-12">
                                <div class="panel-soft d-flex flex-wrap align-items-center gap-4">
                                    <img src="${avatarUrl}" alt="Avatar hiện tại" class="thumb-preview">
                                    <div class="flex-grow-1">
                                        <label class="form-label fw-bold">Ảnh đại diện</label>
                                        <input type="file" name="anhDaiDien" class="form-control" accept="image/*">
                                        <div class="form-text">Nên dùng ảnh vuông, rõ nét. Hệ thống sẽ hiển thị ảnh này ở menu tài khoản và hồ sơ của bạn.</div>
                                    </div>
                                </div>
                            </div>
                            <div class="col-md-6">
                                <label class="form-label fw-bold">Họ tên</label>
                                <input type="text" name="hoTen" class="form-control"
                                       value="${empty param.hoTen ? currentUser.hoTen : param.hoTen}" required>
                            </div>
                            <div class="col-md-6">
                                <label class="form-label fw-bold">Email</label>
                                <input type="email" name="email" readonly class="form-control"
                                       value="${empty param.email ? currentUser.email : param.email}"
                                       placeholder="vd: tenban@email.com">
                            </div>
                            <div class="col-md-6">
                                <label class="form-label fw-bold">Số điện thoại</label>
                                <input type="text" name="soDienThoai" readonly class="form-control"
                                       value="${empty param.soDienThoai ? currentUser.soDienThoai : param.soDienThoai}"
                                       placeholder="vd: 0900000123">
                            </div>
                            <div class="col-md-6">
                                <label class="form-label fw-bold">Vai trò hiện tại</label>
                                <input type="text" class="form-control" value="${currentUser.vaiTroHienThi}" readonly>
                            </div>
                            <div class="col-12 d-flex flex-wrap gap-2 pt-2">
                                <button type="submit" class="btn btn-primary-soft">
                                    <i class="fa-solid fa-floppy-disk me-2"></i>Lưu hồ sơ cá nhân
                                </button>
                                <c:if test="${not currentUser.admin}">
                                    <a href="${ctx}/seller-register/status" class="btn btn-outline-dark btn-outline-soft">
                                        <i class="fa-solid fa-file-shield me-2"></i>Xem trạng thái seller
                                    </a>
                                </c:if>
                            </div>
                        </form>
                    </div>
                </div>

                <div class="col-lg-5">
                    <div class="content-card h-100">
                        <div class="section-head">
                            <div>
                                <h3>Thông tin người bán</h3>
                                <div class="muted-text">Phần này dùng cho hồ sơ seller đã gửi hoặc seller đã được xác minh.</div>
                            </div>
                        </div>

                        <c:choose>
                            <c:when test="${currentUser.admin}">
                                <div class="panel-soft">
                                    <div class="fw-bold mb-2">Tài khoản quản trị</div>
                                    <div class="muted-text">Admin không dùng seller profile. Nếu cần kiểm tra seller, bạn có thể vào phần quản lý hồ sơ seller từ dashboard admin.</div>
                                </div>
                            </c:when>
                            <c:when test="${empty latestSellerRequest}">
                                <div class="panel-soft">
                                    <div class="fw-bold mb-2">Bạn chưa có hồ sơ seller</div>
                                    <div class="muted-text mb-3">Để đăng tin bán xe, bạn cần gửi hồ sơ xác minh seller trước. Sau khi admin duyệt, tài khoản sẽ mở quyền đăng tin.</div>
                                    <a href="${ctx}/seller-register" class="btn btn-gold">
                                        <i class="fa-solid fa-id-card me-2"></i>Đăng ký trở thành seller
                                    </a>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <div class="panel-soft mb-3">
                                    <div class="d-flex justify-content-between align-items-center flex-wrap gap-2">
                                        <div class="fw-bold">Hồ sơ seller #${latestSellerRequest.maDangKySeller}</div>
                                        <span class="status-pill ${latestSellerRequest.trangThaiCssClass}">
                                            <i class="fa-solid fa-shield-check"></i>${latestSellerRequest.trangThaiHienThi}
                                        </span>
                                    </div>
                                    <div class="muted-text mt-2">
                                        <div>Gửi lần gần nhất: <fmt:formatDate value="${latestSellerRequest.ngayTao}" pattern="dd/MM/yyyy HH:mm" /></div>
                                        <c:if test="${not empty latestSellerRequest.lyDoTuChoi}">
                                            <div class="mt-2"><strong>Phản hồi từ admin:</strong> ${latestSellerRequest.lyDoTuChoi}</div>
                                        </c:if>
                                    </div>
                                </div>

                                <form action="${ctx}/profile" method="post" class="row g-3">
<input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                                    <input type="hidden" name="action" value="updateSellerInfo">
                                    <div class="col-12">
                                        <label class="form-label fw-bold">Tỉnh/Thành</label>
                                        <select name="maTinh" class="form-select" required>
                                            <option value="">Chọn tỉnh/thành</option>
                                            <c:forEach items="${listTinhThanh}" var="tinh">
                                                <option value="${tinh.maTinh}"
                                                        <c:if test="${(not empty param.maTinh and param.maTinh == tinh.maTinh) or (empty param.maTinh and latestSellerRequest.maTinh == tinh.maTinh)}">selected</c:if>>
                                                    ${tinh.tenTinh} - ${tinh.mien}
                                                </option>
                                            </c:forEach>
                                        </select>
                                    </div>
                                    <div class="col-12">
                                        <label class="form-label fw-bold">Địa chỉ chi tiết</label>
                                        <input type="text" name="diaChiChiTiet" class="form-control"
                                               value="${empty param.diaChiChiTiet ? latestSellerRequest.diaChiChiTiet : param.diaChiChiTiet}"
                                               placeholder="Ví dụ: 123 Nguyễn Văn Linh, Hải Châu, Đà Nẵng">
                                    </div>
                                    <div class="col-12">
                                        <label class="form-label fw-bold">Ghi chú seller</label>
                                        <textarea name="ghiChuNguoiDangKy" class="form-control"
                                                  placeholder="Ví dụ: chuyên bán xe chính chủ, hỗ trợ xem xe buổi tối, có bãi xe tại nhà...">${empty param.ghiChuNguoiDangKy ? latestSellerRequest.ghiChuNguoiDangKy : param.ghiChuNguoiDangKy}</textarea>
                                    </div>
                                    <div class="col-12 d-flex flex-wrap gap-2 pt-2">
                                        <button type="submit" class="btn btn-primary-soft">
                                            <i class="fa-solid fa-pen-to-square me-2"></i>Lưu thông tin seller
                                        </button>
                                        <a href="${ctx}/seller-register/status" class="btn btn-outline-dark btn-outline-soft">
                                            <i class="fa-solid fa-file-lines me-2"></i>Xem hồ sơ seller
                                        </a>
                                    </div>
                                </form>

                                <c:set var="frontDocPath" value="${empty latestSellerRequest.anhCCCDMatTruoc ? 'uploads/seller-verify/chua-cap-nhat-mat-truoc.svg' : latestSellerRequest.anhCCCDMatTruoc}" />
                                <c:set var="backDocPath" value="${empty latestSellerRequest.anhCCCDMatSau ? 'uploads/seller-verify/chua-cap-nhat-mat-sau.svg' : latestSellerRequest.anhCCCDMatSau}" />
                                <c:set var="frontDocMissing" value="${empty latestSellerRequest.anhCCCDMatTruoc or fn:contains(latestSellerRequest.anhCCCDMatTruoc, 'chua-cap-nhat')}" />
                                <c:set var="backDocMissing" value="${empty latestSellerRequest.anhCCCDMatSau or fn:contains(latestSellerRequest.anhCCCDMatSau, 'chua-cap-nhat')}" />

                                <form id="seller-identity" action="${ctx}/profile" method="post" enctype="multipart/form-data" class="row g-3 mt-4">
<input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                                    <input type="hidden" name="action" value="updateSellerIdentity">
                                    <div class="col-12">
                                        <div class="d-flex justify-content-between align-items-start flex-wrap gap-2 mb-2">
                                            <div>
                                                <div class="fw-bold fs-5">Xác minh CCCD</div>
                                                <div class="muted-text">Bổ sung hoặc thay ảnh CCCD khi hồ sơ seller cũ chưa có giấy tờ rõ ràng.</div>
                                            </div>
                                            <c:if test="${frontDocMissing or backDocMissing}">
                                                <span class="status-pill status-pending">
                                                    <i class="fa-solid fa-triangle-exclamation"></i>Thiếu CCCD
                                                </span>
                                            </c:if>
                                        </div>
                                    </div>

                                    <c:if test="${frontDocMissing or backDocMissing}">
                                        <div class="col-12">
                                            <div class="identity-alert">
                                                Hồ sơ seller này được tạo từ tài khoản seller cũ nên chưa có đủ ảnh CCCD thật.
                                                Hãy nhập số CCCD thật và tải lên cả hai mặt để admin có thể đối chiếu khi cần.
                                            </div>
                                        </div>
                                    </c:if>

                                    <div class="col-12">
                                        <label class="form-label fw-bold">Số CCCD</label>
                                        <input type="text" name="soCCCD" class="form-control"
                                               value="${empty param.soCCCD ? latestSellerRequest.soCCCD : param.soCCCD}"
                                               pattern="[0-9]{9,12}" maxlength="12" required
                                               placeholder="Nhập 9 đến 12 chữ số CCCD">
                                        <div class="form-text">Nếu đang là mã tạm của hồ sơ cũ, hãy thay bằng số CCCD thật.</div>
                                    </div>

                                    <div class="col-12">
                                        <div class="identity-grid">
                                            <div class="identity-card">
                                                <div class="fw-bold mb-2">CCCD mặt trước</div>
                                                <c:choose>
                                                    <c:when test="${fn:startsWith(frontDocPath, 'http://') or fn:startsWith(frontDocPath, 'https://')}">
                                                        <img src="${ctx}/private-document?type=seller&amp;id=${latestSellerRequest.maDangKySeller}&amp;side=front" alt="CCCD mặt trước hiện tại">
                                                    </c:when>
                                                    <c:otherwise>
                                                        <img src="${ctx}/private-document?type=seller&amp;id=${latestSellerRequest.maDangKySeller}&amp;side=front" alt="CCCD mặt trước hiện tại">
                                                    </c:otherwise>
                                                </c:choose>
                                                <input type="file" name="anhCCCDMatTruoc" class="form-control mt-3" accept="image/*">
                                                <div class="form-text">
                                                    <c:choose>
                                                        <c:when test="${frontDocMissing}">Bắt buộc tải lên vì hồ sơ chưa có ảnh mặt trước.</c:when>
                                                        <c:otherwise>Chỉ chọn file nếu muốn thay ảnh mặt trước hiện tại.</c:otherwise>
                                                    </c:choose>
                                                </div>
                                            </div>
                                            <div class="identity-card">
                                                <div class="fw-bold mb-2">CCCD mặt sau</div>
                                                <c:choose>
                                                    <c:when test="${fn:startsWith(backDocPath, 'http://') or fn:startsWith(backDocPath, 'https://')}">
                                                        <img src="${ctx}/private-document?type=seller&amp;id=${latestSellerRequest.maDangKySeller}&amp;side=back" alt="CCCD mặt sau hiện tại">
                                                    </c:when>
                                                    <c:otherwise>
                                                        <img src="${ctx}/private-document?type=seller&amp;id=${latestSellerRequest.maDangKySeller}&amp;side=back" alt="CCCD mặt sau hiện tại">
                                                    </c:otherwise>
                                                </c:choose>
                                                <input type="file" name="anhCCCDMatSau" class="form-control mt-3" accept="image/*">
                                                <div class="form-text">
                                                    <c:choose>
                                                        <c:when test="${backDocMissing}">Bắt buộc tải lên vì hồ sơ chưa có ảnh mặt sau.</c:when>
                                                        <c:otherwise>Chỉ chọn file nếu muốn thay ảnh mặt sau hiện tại.</c:otherwise>
                                                    </c:choose>
                                                </div>
                                            </div>
                                        </div>
                                    </div>

                                    <div class="col-12 pt-2">
                                        <button type="submit" class="btn btn-gold w-100">
                                            <i class="fa-solid fa-id-card-clip me-2"></i>Cập nhật CCCD
                                        </button>
                                    </div>
                                </form>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </div>

            <c:if test="${not empty sellerRequestHistory and not currentUser.admin}">
                <div class="content-card mt-4">
                    <div class="section-head">
                        <div>
                            <h3>Lịch sử hồ sơ seller</h3>
                            <div class="muted-text">Giúp bạn theo dõi các lần gửi hồ sơ và phản hồi gần đây từ phía admin.</div>
                        </div>
                    </div>
                    <div class="history-list">
                        <c:forEach items="${sellerRequestHistory}" var="item">
                            <div class="history-item">
                                <div>
                                    <div class="fw-bold">Hồ sơ #${item.maDangKySeller}</div>
                                    <div class="muted-text">
                                        <fmt:formatDate value="${item.ngayTao}" pattern="dd/MM/yyyy HH:mm" />
                                        <c:if test="${not empty item.tenTinh}">
                                            • ${item.tenTinh}
                                        </c:if>
                                    </div>
                                </div>
                                <span class="status-pill ${item.trangThaiCssClass}">
                                    <i class="fa-solid fa-clock-rotate-left"></i>${item.trangThaiHienThi}
                                </span>
                            </div>
                        </c:forEach>
                    </div>
                </div>
            </c:if>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
        <jsp:include page="/WEB-INF/includes/site-footer.jsp" />

    <div class="container mb-4"><a class="btn btn-warning" href="${pageContext.request.contextPath}/account-security">Bảo mật: xác minh email / số điện thoại và đổi mật khẩu</a></div>
</body>
</html>
