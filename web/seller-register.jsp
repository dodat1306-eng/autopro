<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Đăng ký người bán - AutoPro</title>
        <link rel="preconnect" href="https://fonts.googleapis.com">
        <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
        <link href="https://fonts.googleapis.com/css2?family=Manrope:wght@400;500;600;700;800&display=swap" rel="stylesheet">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
        <style>
            :root {
                --ui-bg: #f3f5f9;
                --ui-card: #ffffff;
                --ui-line: #e6ebf2;
                --ui-text: #132238;
                --ui-muted: #63758c;
                --ui-gold: #f5b700;
                --ui-gold-soft: #fff4cf;
                --ui-blue: #1d4ed8;
                --ui-blue-soft: #e8f0ff;
                --ui-green-soft: #dff6e8;
                --ui-red-soft: #ffe3e1;
                --ui-shadow: 0 18px 44px rgba(15, 23, 42, 0.08);
            }

            body {
                background:
                    radial-gradient(circle at top left, rgba(255, 214, 102, 0.26), transparent 28%),
                    linear-gradient(180deg, #f8fafc 0%, var(--ui-bg) 100%);
                color: var(--ui-text);
                font-family: 'Manrope', sans-serif;
                min-height: 100vh;
            }

            .page-wrap {
                max-width: 1220px;
                margin: 28px auto 48px;
            }

            .hero-card,
            .glass-card {
                background: var(--ui-card);
                border: 1px solid var(--ui-line);
                border-radius: 28px;
                box-shadow: var(--ui-shadow);
            }

            .hero-card {
                padding: 32px;
                background:
                    linear-gradient(135deg, rgba(255, 239, 184, 0.92), rgba(255, 255, 255, 0.98) 42%),
                    #ffffff;
                overflow: hidden;
                position: relative;
            }

            .hero-card::after {
                content: "";
                position: absolute;
                right: -30px;
                top: -30px;
                width: 220px;
                height: 220px;
                background: radial-gradient(circle, rgba(245, 183, 0, 0.18), transparent 68%);
                pointer-events: none;
            }

            .glass-card {
                padding: 26px;
            }

            .eyebrow {
                display: inline-flex;
                align-items: center;
                gap: 8px;
                padding: 8px 14px;
                border-radius: 999px;
                background: #0f172a;
                color: #ffffff;
                font-size: 12px;
                font-weight: 800;
                letter-spacing: 0.04em;
                text-transform: uppercase;
            }

            .hero-title {
                font-size: clamp(2rem, 4vw, 3.15rem);
                line-height: 1.04;
                font-weight: 800;
                margin: 18px 0 14px;
                max-width: 680px;
            }

            .hero-text {
                max-width: 660px;
                color: var(--ui-muted);
                font-size: 15px;
                line-height: 1.7;
                margin: 0;
            }

            .hero-stats {
                display: grid;
                grid-template-columns: repeat(3, minmax(0, 1fr));
                gap: 14px;
                margin-top: 26px;
            }

            .hero-stat {
                border-radius: 22px;
                background: rgba(255, 255, 255, 0.92);
                border: 1px solid rgba(226, 232, 240, 0.92);
                padding: 18px;
                min-height: 112px;
            }

            .hero-stat .icon {
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

            .hero-stat strong {
                display: block;
                font-size: 16px;
                margin-bottom: 6px;
            }

            .hero-stat span {
                color: var(--ui-muted);
                font-size: 13px;
                line-height: 1.55;
            }

            .top-actions {
                display: flex;
                align-items: center;
                gap: 10px;
                flex-wrap: wrap;
            }

            .section-head {
                display: flex;
                justify-content: space-between;
                align-items: start;
                gap: 14px;
                margin-bottom: 20px;
            }

            .section-head h3,
            .section-head h4 {
                margin: 0;
                font-weight: 800;
            }

            .section-subtitle {
                margin-top: 6px;
                color: var(--ui-muted);
                font-size: 14px;
            }

            .status-pill {
                display: inline-flex;
                align-items: center;
                gap: 8px;
                padding: 9px 14px;
                border-radius: 999px;
                font-size: 12px;
                font-weight: 800;
            }

            .status-pending {
                background: #fff2cf;
                color: #8a6200;
            }

            .status-approved {
                background: var(--ui-green-soft);
                color: #166534;
            }

            .status-rejected {
                background: var(--ui-red-soft);
                color: #b42318;
            }

            .status-deleted,
            .status-default {
                background: #e8edf4;
                color: #475569;
            }

            .step-list {
                display: grid;
                gap: 14px;
            }

            .step-item {
                display: flex;
                gap: 14px;
                align-items: start;
                padding: 16px;
                border-radius: 20px;
                border: 1px solid var(--ui-line);
                background: linear-gradient(180deg, #ffffff, #fbfdff);
            }

            .step-number {
                width: 38px;
                height: 38px;
                flex: 0 0 38px;
                border-radius: 12px;
                background: #0f172a;
                color: #ffffff;
                display: inline-flex;
                align-items: center;
                justify-content: center;
                font-weight: 800;
                font-size: 14px;
            }

            .step-item strong {
                display: block;
                font-size: 15px;
                margin-bottom: 5px;
            }

            .step-item span,
            .hint-note,
            .card-note {
                color: var(--ui-muted);
                font-size: 13px;
                line-height: 1.65;
            }

            .mini-summary {
                display: grid;
                gap: 12px;
                margin-top: 18px;
            }

            .mini-card {
                padding: 16px 18px;
                border-radius: 20px;
                border: 1px solid var(--ui-line);
                background: #fbfcfe;
            }

            .mini-card .label {
                font-size: 12px;
                font-weight: 700;
                text-transform: uppercase;
                letter-spacing: 0.04em;
                color: #7a8a9c;
            }

            .mini-card .value {
                margin-top: 8px;
                font-size: 16px;
                font-weight: 800;
            }

            .mini-card .muted {
                margin-top: 4px;
                color: var(--ui-muted);
                font-size: 13px;
            }

            .requirement-box {
                border: 1px solid #ffe3a4;
                background: linear-gradient(180deg, #fff8e7, #fffdf7);
                border-radius: 22px;
                padding: 18px;
                margin-top: 18px;
            }

            .requirement-box ul {
                margin: 12px 0 0;
                padding-left: 18px;
            }

            .requirement-box li {
                color: #755400;
                font-size: 13px;
                line-height: 1.7;
                margin-bottom: 6px;
            }

            .form-section {
                padding: 22px;
                border-radius: 24px;
                border: 1px solid var(--ui-line);
                background: linear-gradient(180deg, #ffffff, #fbfcff);
                margin-bottom: 18px;
            }

            .section-tag {
                display: inline-flex;
                align-items: center;
                gap: 8px;
                padding: 8px 12px;
                border-radius: 999px;
                background: var(--ui-blue-soft);
                color: var(--ui-blue);
                font-weight: 800;
                font-size: 12px;
                margin-bottom: 14px;
            }

            .form-label {
                font-weight: 700;
                color: #243447;
            }

            .form-control,
            .form-select {
                border-radius: 16px;
                padding: 13px 15px;
                border-color: #dbe3ee;
                box-shadow: none !important;
                font-size: 14px;
            }

            .form-control:focus,
            .form-select:focus {
                border-color: #f5b700;
            }

            .upload-grid {
                display: grid;
                grid-template-columns: repeat(2, minmax(0, 1fr));
                gap: 16px;
            }

            .upload-card {
                border: 1px dashed #cfd8e4;
                border-radius: 22px;
                padding: 18px;
                background: #fafcff;
            }

            .upload-preview {
                margin-top: 12px;
                height: 200px;
                border-radius: 18px;
                border: 1px solid #e5eaf1;
                background:
                    linear-gradient(135deg, rgba(244, 247, 251, 0.95), rgba(255, 255, 255, 0.96));
                display: flex;
                align-items: center;
                justify-content: center;
                overflow: hidden;
                color: #7b8794;
                font-size: 13px;
                text-align: center;
                padding: 16px;
            }

            .upload-preview img {
                width: 100%;
                height: 100%;
                object-fit: cover;
            }

            .locked-banner {
                display: inline-flex;
                align-items: center;
                gap: 8px;
                padding: 8px 12px;
                border-radius: 999px;
                background: #111827;
                color: #ffffff;
                font-size: 12px;
                font-weight: 800;
            }

            .empty-shell {
                border-radius: 22px;
                border: 1px dashed #cfd8e4;
                padding: 26px;
                background: #fafcff;
            }

            @media (max-width: 991.98px) {
                .hero-stats {
                    grid-template-columns: 1fr;
                }
            }

            @media (max-width: 767.98px) {
                .page-wrap {
                    margin-top: 18px;
                }

                .hero-card,
                .glass-card {
                    border-radius: 22px;
                }

                .hero-card {
                    padding: 22px;
                }

                .glass-card {
                    padding: 20px;
                }

                .upload-grid {
                    grid-template-columns: 1fr;
                }
            }
        </style>
    </head>
    <body>
        <c:set var="ctx" value="${pageContext.request.contextPath}" />
        <c:set var="prefillRequest" value="${not empty formDangKySeller ? formDangKySeller : latestSellerRequest}" />
        <c:set var="selectedMaTinh" value="${not empty prefillRequest ? prefillRequest.maTinh : 0}" />

        <div class="container page-wrap">
            <div class="hero-card mb-4">
                <div class="d-flex justify-content-between align-items-start flex-wrap gap-3">
                    <div>
                        <span class="eyebrow">
                            <i class="fa-solid fa-shield-halved"></i>
                            Seller Verification
                        </span>
                        <h1 class="hero-title">Đăng ký người bán để mở quyền đăng tin trên AutoPro</h1>
                        <p class="hero-text">
                            AutoPro chỉ cho phép tài khoản seller đã được duyệt đăng bài bán xe. Hồ sơ xác minh này giúp tăng độ tin cậy, minh bạch thông tin và bảo vệ người mua khi liên hệ, hẹn xem xe và giao dịch trực tiếp.
                        </p>
                    </div>
                    <div class="top-actions">
                        <a href="${ctx}/home" class="btn btn-outline-secondary">
                            <i class="fa-solid fa-house me-2"></i>Trang chủ
                        </a>
                        <a href="${ctx}/seller-register/status" class="btn btn-outline-primary">
                            <i class="fa-solid fa-file-lines me-2"></i>Trạng thái hồ sơ
                        </a>
                        <jsp:include page="/WEB-INF/includes/account-menu.jsp" />
                    </div>
                </div>

                <div class="hero-stats">
                    <div class="hero-stat">
                        <div class="icon"><i class="fa-solid fa-id-card"></i></div>
                        <strong>Xác minh bằng CCCD</strong>
                        <span>Admin kiểm tra họ tên, số CCCD, ảnh mặt trước và mặt sau trước khi mở quyền seller.</span>
                    </div>
                    <div class="hero-stat">
                        <div class="icon"><i class="fa-solid fa-user-check"></i></div>
                        <strong>Mỗi tài khoản chỉ có 1 hồ sơ chờ duyệt</strong>
                        <span>Hệ thống khóa nộp trùng để tránh nhiều hồ sơ chồng chéo và dễ theo dõi hơn cho admin.</span>
                    </div>
                    <div class="hero-stat">
                        <div class="icon"><i class="fa-solid fa-car-side"></i></div>
                        <strong>Duyệt xong là đăng tin ngay</strong>
                        <span>Sau khi được duyệt, tài khoản của bạn được nâng quyền seller và có thể đăng bài bán xe ngay.</span>
                    </div>
                </div>
            </div>

            <c:if test="${param.needSeller == '1'}">
                <div class="alert alert-warning rounded-4 border-0 shadow-sm">
                    Bạn chưa được phê duyệt là người bán. Vui lòng hoàn tất hồ sơ seller và chờ admin xét duyệt trước khi đăng tin bán xe.
                </div>
            </c:if>
            <c:if test="${not empty error}">
                <div class="alert alert-danger rounded-4 border-0 shadow-sm">${error}</div>
            </c:if>

            <div class="row g-4">
                <div class="col-lg-4">
                    <div class="glass-card h-100">
                        <div class="section-head">
                            <div>
                                <h3>Tiến trình xác minh</h3>
                                <div class="section-subtitle">Xem nhanh hồ sơ hiện tại và những gì sẽ diễn ra sau khi gửi.</div>
                            </div>
                            <c:if test="${!canSubmitSellerRequest}">
                                <span class="locked-banner">
                                    <i class="fa-solid fa-lock"></i>Tạm khóa
                                </span>
                            </c:if>
                        </div>

                        <div class="step-list">
                            <div class="step-item">
                                <div class="step-number">1</div>
                                <div>
                                    <strong>Điền hồ sơ cá nhân</strong>
                                    <span>Cung cấp họ tên, email, số điện thoại, CCCD và tỉnh/thành để đối chiếu với tài khoản.</span>
                                </div>
                            </div>
                            <div class="step-item">
                                <div class="step-number">2</div>
                                <div>
                                    <strong>Tải ảnh xác minh</strong>
                                    <span>Ảnh CCCD cần rõ nét, đủ góc và không bị che mờ thông tin quan trọng.</span>
                                </div>
                            </div>
                            <div class="step-item">
                                <div class="step-number">3</div>
                                <div>
                                    <strong>Admin kiểm tra và phản hồi</strong>
                                    <span>Nếu hồ sơ đạt yêu cầu, tài khoản sẽ được cấp quyền seller; nếu chưa đạt, bạn có thể nộp lại.</span>
                                </div>
                            </div>
                        </div>

                        <div class="mini-summary">
                            <div class="mini-card">
                                <div class="label">Trạng thái hiện tại</div>
                                <c:choose>
                                    <c:when test="${isApprovedSeller}">
                                        <div class="value text-success">Đã là seller</div>
                                        <div class="muted">Quyền đăng tin đã được kích hoạt trên tài khoản.</div>
                                    </c:when>
                                    <c:when test="${not empty latestSellerRequest}">
                                        <div class="value">
                                            <span class="status-pill ${latestSellerRequest.trangThaiCssClass}">
                                                <i class="fa-solid fa-circle"></i>${latestSellerRequest.trangThaiHienThi}
                                            </span>
                                        </div>
                                        <div class="muted">
                                            Hồ sơ gần nhất nộp lúc
                                            <fmt:formatDate value="${latestSellerRequest.ngayTao}" pattern="dd/MM/yyyy HH:mm" />.
                                        </div>
                                    </c:when>
                                    <c:otherwise>
                                        <div class="value">Chưa có hồ sơ</div>
                                        <div class="muted">Bạn có thể gửi hồ sơ seller đầu tiên ngay trong form bên phải.</div>
                                    </c:otherwise>
                                </c:choose>
                            </div>

                            <c:if test="${not empty latestSellerRequest}">
                                <div class="mini-card">
                                    <div class="label">Thông tin gần nhất</div>
                                    <div class="value">${latestSellerRequest.hoTen}</div>
                                    <div class="muted">${latestSellerRequest.email} · ${latestSellerRequest.soDienThoai}</div>
                                </div>
                            </c:if>

                            <c:if test="${not empty latestSellerRequest and latestSellerRequest.tuChoi}">
                                <div class="mini-card" style="background: #fff6f5; border-color: #ffd4cf;">
                                    <div class="label text-danger">Lý do từ chối</div>
                                    <div class="muted" style="color: #8f2d23;">
                                        ${empty latestSellerRequest.lyDoTuChoi ? 'Admin chưa ghi chi tiết. Bạn có thể chuẩn bị hồ sơ rõ hơn và nộp lại.' : latestSellerRequest.lyDoTuChoi}
                                    </div>
                                </div>
                            </c:if>
                        </div>

                        <div class="requirement-box">
                            <strong class="d-flex align-items-center gap-2">
                                <i class="fa-solid fa-circle-info"></i>
                                Hồ sơ hợp lệ nên có
                            </strong>
                            <ul>
                                <li>Thông tin liên hệ trùng khớp với tài khoản đang dùng.</li>
                                <li>CCCD rõ số, không bị mất góc, không chụp quá xa hoặc quá tối.</li>
                                <li>Tỉnh/thành và địa chỉ chi tiết đủ rõ để tăng độ tin cậy khi giao dịch.</li>
                            </ul>
                        </div>
                    </div>
                </div>

                <div class="col-lg-8">
                    <div class="glass-card">
                        <div class="section-head">
                            <div>
                                <h3>
                                    <c:choose>
                                        <c:when test="${not empty latestSellerRequest and latestSellerRequest.tuChoi}">
                                            Nộp lại hồ sơ seller
                                        </c:when>
                                        <c:when test="${not empty latestSellerRequest and latestSellerRequest.daXoa}">
                                            Tạo hồ sơ seller mới
                                        </c:when>
                                        <c:otherwise>
                                            Hồ sơ xác minh người bán
                                        </c:otherwise>
                                    </c:choose>
                                </h3>
                                <div class="section-subtitle">Điền đầy đủ thông tin, sau đó tải lên 2 ảnh xác minh CCCD để hệ thống gửi admin kiểm tra.</div>
                            </div>
                            <c:if test="${!canSubmitSellerRequest}">
                                <span class="status-pill status-default">
                                    <i class="fa-solid fa-pause"></i>Đang khóa nộp mới
                                </span>
                            </c:if>
                        </div>

                        <c:choose>
                            <c:when test="${canSubmitSellerRequest}">
                                <form action="${ctx}/seller-register" method="post" enctype="multipart/form-data">
<input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                                    <div class="form-section">
                                        <div class="section-tag">
                                            <i class="fa-solid fa-user"></i>
                                            Thông tin cá nhân
                                        </div>
                                        <div class="row">
                                            <div class="col-md-6 mb-3">
                                                <label class="form-label">Họ tên</label>
                                                <input type="text" class="form-control" name="hoTen"
                                                       value="${not empty prefillRequest.hoTen ? prefillRequest.hoTen : currentUser.hoTen}" required>
                                            </div>
                                            <div class="col-md-6 mb-3">
                                                <label class="form-label">Số điện thoại</label>
                                                <input type="text" class="form-control" name="soDienThoai"
                                                       value="${not empty prefillRequest.soDienThoai ? prefillRequest.soDienThoai : currentUser.soDienThoai}" required>
                                            </div>
                                            <div class="col-md-6 mb-3">
                                                <label class="form-label">Email</label>
                                                <input type="email" class="form-control" name="email"
                                                       value="${not empty prefillRequest.email ? prefillRequest.email : currentUser.email}" required>
                                            </div>
                                            <div class="col-md-6 mb-3">
                                                <label class="form-label">Số CCCD</label>
                                                <input type="text" class="form-control" name="soCCCD"
                                                       value="${not empty prefillRequest.soCCCD ? prefillRequest.soCCCD : ''}" required>
                                            </div>
                                        </div>
                                    </div>

                                    <div class="form-section">
                                        <div class="section-tag">
                                            <i class="fa-solid fa-location-dot"></i>
                                            Khu vực và địa chỉ
                                        </div>
                                        <div class="row">
                                            <div class="col-md-6 mb-3">
                                                <label class="form-label">Tỉnh/Thành</label>
                                                <select class="form-select" name="maTinh" required>
                                                    <option value="">Chọn tỉnh/thành</option>
                                                    <c:forEach items="${listTinhThanh}" var="tinh">
                                                        <option value="${tinh.maTinh}" ${selectedMaTinh == tinh.maTinh ? 'selected' : ''}>${tinh.tenTinh}</option>
                                                    </c:forEach>
                                                </select>
                                            </div>
                                            <div class="col-md-6 mb-3">
                                                <label class="form-label">Địa chỉ chi tiết</label>
                                                <input type="text" class="form-control" name="diaChiChiTiet"
                                                       value="${not empty prefillRequest.diaChiChiTiet ? prefillRequest.diaChiChiTiet : ''}"
                                                       placeholder="Ví dụ: số nhà, đường, phường/xã...">
                                            </div>
                                        </div>

                                        <div>
                                            <label class="form-label">Ghi chú bổ sung</label>
                                            <textarea class="form-control" name="ghiChuNguoiDangKy" rows="4"
                                                      placeholder="Ví dụ: Tôi có showroom riêng, có thể cung cấp thêm giấy tờ gốc khi admin cần đối chiếu.">${not empty prefillRequest.ghiChuNguoiDangKy ? prefillRequest.ghiChuNguoiDangKy : ''}</textarea>
                                            <div class="form-text">Phần này không bắt buộc nhưng giúp admin hiểu rõ hơn hồ sơ của bạn.</div>
                                        </div>
                                    </div>

                                    <div class="form-section mb-0">
                                        <div class="section-tag">
                                            <i class="fa-solid fa-camera"></i>
                                            Ảnh xác minh CCCD
                                        </div>

                                        <div class="upload-grid">
                                            <div class="upload-card">
                                                <label class="form-label">CCCD mặt trước</label>
                                                <input type="file" class="form-control" name="anhCCCDMatTruoc" accept="image/*" required data-preview="previewFront">
                                                <div class="form-text">Ảnh cần rõ số, không mờ và không bị che khuất.</div>
                                                <div class="upload-preview" id="previewFront">
                                                    <div>
                                                        <i class="fa-regular fa-image fa-2x mb-3 d-block"></i>
                                                        Chưa có ảnh mặt trước
                                                    </div>
                                                </div>
                                            </div>

                                            <div class="upload-card">
                                                <label class="form-label">CCCD mặt sau</label>
                                                <input type="file" class="form-control" name="anhCCCDMatSau" accept="image/*" required data-preview="previewBack">
                                                <div class="form-text">Ảnh nên đủ 4 góc để admin dễ kiểm tra hơn.</div>
                                                <div class="upload-preview" id="previewBack">
                                                    <div>
                                                        <i class="fa-regular fa-image fa-2x mb-3 d-block"></i>
                                                        Chưa có ảnh mặt sau
                                                    </div>
                                                </div>
                                            </div>
                                        </div>

                                        <div class="d-flex justify-content-between align-items-center flex-wrap gap-3 mt-4">
                                            <div class="card-note">
                                                Sau khi gửi, hồ sơ sẽ xuất hiện trong danh sách chờ duyệt của admin và bạn sẽ nhận thông báo khi có kết quả.
                                            </div>
                                            <div class="d-flex gap-2 flex-wrap">
                                                <a href="${ctx}/home" class="btn btn-light px-4">Hủy</a>
                                                <button type="submit" class="btn btn-warning fw-bold px-4">
                                                    <i class="fa-solid fa-paper-plane me-2"></i>Gửi hồ sơ seller
                                                </button>
                                            </div>
                                        </div>
                                    </div>
                                </form>
                            </c:when>
                            <c:otherwise>
                                <div class="empty-shell">
                                    <c:choose>
                                        <c:when test="${isApprovedSeller}">
                                            <h4 class="fw-bold mb-2">Tài khoản của bạn đã là seller</h4>
                                            <p class="card-note mb-3">Form đăng ký mới đã được ẩn vì quyền seller hiện đã kích hoạt trên tài khoản này.</p>
                                            <div class="d-flex gap-2 flex-wrap">
                                                <a href="${ctx}/post-car" class="btn btn-warning fw-bold">
                                                    <i class="fa-solid fa-plus me-2"></i>Đăng tin bán xe
                                                </a>
                                                <a href="${ctx}/seller/dashboard" class="btn btn-outline-dark">
                                                    <i class="fa-solid fa-chart-simple me-2"></i>Dashboard người bán
                                                </a>
                                            </div>
                                        </c:when>
                                        <c:otherwise>
                                            <h4 class="fw-bold mb-2">Bạn đang có hồ sơ chờ admin xem xét</h4>
                                            <p class="card-note mb-3">Để tránh trùng lặp, hệ thống không cho tạo thêm hồ sơ mới cho đến khi hồ sơ hiện tại được xử lý.</p>
                                            <a href="${ctx}/seller-register/status" class="btn btn-outline-primary">
                                                <i class="fa-solid fa-eye me-2"></i>Xem trạng thái hồ sơ
                                            </a>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </div>
        </div>

        <script>
            function bindPreview(input) {
                input.addEventListener('change', function () {
                    const previewId = input.getAttribute('data-preview');
                    const preview = document.getElementById(previewId);
                    if (!preview || !input.files || !input.files[0]) {
                        return;
                    }

                    const reader = new FileReader();
                    reader.onload = function (event) {
                        preview.innerHTML = '<img src="' + event.target.result + '" alt="Preview">';
                    };
                    reader.readAsDataURL(input.files[0]);
                });
            }

            document.querySelectorAll('input[type="file"][data-preview]').forEach(bindPreview);
        </script>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
        <jsp:include page="/WEB-INF/includes/site-footer.jsp" />

    </body>
</html>
