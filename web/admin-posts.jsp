<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Quản lý bài đăng & xe - AutoPro Admin</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
        <style>
            :root {
                --bg: #f3f5f9;
                --card: #ffffff;
                --line: #e6ebf2;
                --text: #132238;
                --muted: #63758c;
                --gold: #f5b700;
                --gold-soft: #fff5cf;
                --green-soft: #dff5e8;
                --red-soft: #ffe3e1;
                --orange-soft: #ffeacf;
                --slate-soft: #edf2f7;
                --shadow: 0 18px 44px rgba(15, 23, 42, 0.08);
            }

            body {
                margin: 0;
                padding: 28px;
                font-family: Arial, sans-serif;
                background:
                    radial-gradient(circle at top left, rgba(255, 214, 102, 0.22), transparent 24%),
                    linear-gradient(180deg, #f8fafc 0%, var(--bg) 100%);
                color: var(--text);
            }

            .page-shell {
                max-width: 1280px;
                margin: auto;
            }

            .surface-card {
                background: var(--card);
                border: 1px solid var(--line);
                border-radius: 26px;
                box-shadow: var(--shadow);
            }

            .hero-card {
                padding: 30px;
                background:
                    linear-gradient(135deg, rgba(255, 239, 184, 0.96), rgba(255, 255, 255, 0.98) 48%),
                    #ffffff;
            }

            .section-card {
                margin-top: 22px;
                padding: 24px;
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
                text-transform: uppercase;
            }

            .hero-title {
                margin: 16px 0 10px;
                font-size: clamp(2rem, 3.6vw, 3rem);
                line-height: 1.06;
                font-weight: 800;
            }

            .hero-text,
            .section-subtitle,
            .tiny-muted {
                color: var(--muted);
            }

            .top-actions {
                display: flex;
                gap: 10px;
                flex-wrap: wrap;
                justify-content: flex-end;
            }

            .summary-grid {
                display: grid;
                grid-template-columns: repeat(3, minmax(0, 1fr));
                gap: 14px;
                margin-top: 24px;
            }

            .summary-card {
                border-radius: 22px;
                border: 1px solid rgba(226, 232, 240, 0.92);
                background: rgba(255, 255, 255, 0.92);
                padding: 18px;
            }

            .summary-card .icon {
                width: 46px;
                height: 46px;
                border-radius: 14px;
                display: inline-flex;
                align-items: center;
                justify-content: center;
                margin-bottom: 12px;
                background: var(--gold-soft);
                color: #8a5b00;
            }

            .summary-card .value {
                font-size: 30px;
                font-weight: 800;
                line-height: 1;
            }

            .summary-card .label {
                margin-top: 10px;
                font-size: 14px;
                font-weight: 700;
            }

            .summary-card .meta {
                margin-top: 6px;
                font-size: 12px;
                color: var(--muted);
                line-height: 1.6;
            }

            .search-form {
                display: grid;
                grid-template-columns: minmax(0, 1fr) auto auto;
                gap: 12px;
                align-items: end;
            }

            .form-label {
                font-weight: 700;
                margin-bottom: 8px;
            }

            .form-control {
                border-radius: 16px;
                border-color: #dbe3ee;
                padding: 13px 15px;
            }

            .form-control:focus {
                border-color: var(--gold);
                box-shadow: none;
            }

            .btn-soft {
                border-radius: 16px;
                padding: 12px 16px;
                font-weight: 700;
            }

            .section-head {
                display: flex;
                justify-content: space-between;
                align-items: start;
                gap: 14px;
                margin-bottom: 18px;
            }

            .section-head h3 {
                margin: 0;
                font-weight: 800;
            }

            .section-badge {
                display: inline-flex;
                align-items: center;
                gap: 8px;
                padding: 10px 14px;
                border-radius: 999px;
                font-size: 12px;
                font-weight: 800;
            }

            .section-badge.pending {
                background: #fff2cf;
                color: #8a6200;
            }

            .section-badge.other {
                background: var(--slate-soft);
                color: #334155;
            }

            .section-note {
                margin-top: 10px;
                padding: 14px 16px;
                border-radius: 18px;
                background: #fffaf0;
                border: 1px solid #ffe6a8;
                color: #946200;
                font-size: 13px;
                line-height: 1.65;
            }

            .post-table {
                margin: 0;
            }

            .post-table th:nth-child(2) {
                min-width: 220px;
            }

            .post-table th:nth-child(3) {
                min-width: 360px;
            }

            .post-table th:nth-child(4) {
                min-width: 210px;
            }

            .post-table th:nth-child(5) {
                min-width: 140px;
            }

            .post-table th:nth-child(7) {
                min-width: 150px;
            }

            .post-table th {
                background: #1f2937;
                color: #ffffff;
                font-size: 13px;
                border: none;
                vertical-align: middle;
            }

            .post-table td {
                vertical-align: middle;
                border-color: #edf1f6;
            }

            .title-cell strong {
                display: block;
                font-size: 16px;
                line-height: 1.5;
                margin-bottom: 6px;
            }

            .status-badge {
                padding: 7px 12px;
                border-radius: 999px;
                font-size: 12px;
                font-weight: 700;
                display: inline-block;
            }

            .status-cho_duyet {
                background: #fff3cd;
                color: #946200;
            }

            .status-da_duyet {
                background: #d1e7dd;
                color: #146c43;
            }

            .status-tu_choi {
                background: var(--red-soft);
                color: #b42318;
            }

            .status-canh_bao {
                background: var(--orange-soft);
                color: #b45309;
            }

            .status-da_ban {
                background: #dbeafe;
                color: #1d4ed8;
            }

            .status-dang_giao_dich {
                background: #fff3cd;
                color: #946200;
            }

            .status-default {
                background: var(--slate-soft);
                color: #334155;
            }

            .empty-state {
                text-align: center;
                padding: 40px 20px;
                color: var(--muted);
            }

            .empty-state i {
                font-size: 40px;
                margin-bottom: 12px;
                color: #94a3b8;
            }

            .action-stack {
                display: flex;
                flex-wrap: wrap;
                gap: 8px;
                justify-content: flex-end;
            }

            .action-stack .btn,
            .action-stack button {
                white-space: nowrap;
            }

            .id-stack {
                display: grid;
                gap: 4px;
                font-size: 13px;
            }

            .id-stack strong {
                color: var(--text);
            }

            .vehicle-name {
                font-weight: 800;
                color: var(--text);
                line-height: 1.45;
            }

            .vehicle-card {
                display: grid;
                grid-template-columns: 76px minmax(0, 1fr);
                gap: 12px;
                align-items: start;
            }

            .vehicle-thumb {
                width: 76px;
                aspect-ratio: 4 / 3;
                border-radius: 14px;
                object-fit: cover;
                background: #eef2f7;
                border: 1px solid #e2e8f0;
            }

            .vehicle-price {
                color: #b77900;
                font-weight: 800;
                margin-top: 3px;
            }

            .vehicle-meta {
                display: flex;
                flex-wrap: wrap;
                gap: 6px;
                margin-top: 9px;
            }

            .vehicle-chip {
                display: inline-flex;
                align-items: center;
                min-height: 26px;
                padding: 5px 9px;
                border-radius: 999px;
                background: #f8fafc;
                border: 1px solid #e2e8f0;
                color: #475569;
                font-size: 12px;
                font-weight: 700;
                white-space: nowrap;
            }

            .seller-name {
                font-weight: 800;
                color: var(--text);
            }

            .seller-contact,
            .note-muted {
                color: var(--muted);
                font-size: 12px;
                line-height: 1.55;
                overflow-wrap: anywhere;
            }

            .note-muted {
                max-width: 220px;
            }

            @media (max-width: 992px) {
                body {
                    padding: 18px;
                }

                .search-form,
                .summary-grid {
                    grid-template-columns: 1fr;
                }

                .top-actions,
                .section-head {
                    justify-content: flex-start;
                }
            }
        </style>
    </head>
    <body>
        <div class="page-shell">
            <section class="surface-card hero-card">
                <div class="d-flex justify-content-between align-items-start flex-wrap gap-3">
                    <div>
                        <span class="eyebrow">
                            <i class="fa-solid fa-car-side"></i>
                            Bài đăng & xe
                        </span>
                        <h1 class="hero-title">Quản lý bài đăng và xe</h1>
                        <p class="hero-text mb-0">
                            Một màn duy nhất để admin kiểm duyệt bài đăng, xem thông tin xe, người bán, trạng thái giao dịch và xử lý các tin cần duyệt.
                        </p>
                    </div>
                    <div class="top-actions">
                        <a href="${ctx}/admin/dashboard" class="btn btn-outline-secondary btn-soft">
                            <i class="fa-solid fa-arrow-left me-2"></i>Về Dashboard
                        </a>
                        <a href="${ctx}/admin/reports" class="btn btn-outline-dark btn-soft">
                            <i class="fa-solid fa-flag me-2"></i>Xem báo cáo
                        </a>
                    </div>
                </div>

                <div class="summary-grid">
                    <div class="summary-card">
                        <div class="icon"><i class="fa-solid fa-hourglass-half"></i></div>
                        <div class="value">${pendingPostCount}</div>
                        <div class="label">Bài/xe đang chờ duyệt</div>
                        <div class="meta">Ưu tiên xử lý các xe vừa được người bán đăng hoặc gửi duyệt lại.</div>
                    </div>
                    <div class="summary-card">
                        <div class="icon"><i class="fa-solid fa-layer-group"></i></div>
                        <div class="value">${otherPostCount}</div>
                        <div class="label">Bài/xe đã xử lý hoặc trạng thái khác</div>
                        <div class="meta">Bao gồm xe đã duyệt, đang giao dịch, đã bán, cảnh báo hoặc từ chối.</div>
                    </div>
                    <div class="summary-card">
                        <div class="icon"><i class="fa-solid fa-magnifying-glass"></i></div>
                        <div class="value">${matchedPostCount}</div>
                        <div class="label">Kết quả theo bộ lọc hiện tại</div>
                        <div class="meta">
                            <c:choose>
                                <c:when test="${not empty sellerKeyword}">
                                    Đang lọc theo từ khóa: <strong><c:out value="${sellerKeyword}" /></strong>
                                </c:when>
                                <c:otherwise>
                                    Chưa nhập từ khóa. Hệ thống đang hiển thị toàn bộ bài đăng.
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>
                </div>
            </section>

            <section class="surface-card section-card">
                <div class="section-head">
                    <div>
                            <h3>Tìm bài đăng hoặc xe</h3>
                            <div class="section-subtitle">Tìm nhanh theo tên xe, hãng xe, tên người đăng, email hoặc số điện thoại.</div>
                    </div>
                </div>

                <form method="get" action="${ctx}/admin/posts" class="search-form">
                    <div>
                        <label class="form-label" for="sellerKeyword">Tên xe / hãng / người đăng / email / số điện thoại</label>
                        <input id="sellerKeyword" type="text" name="sellerKeyword" class="form-control"
                               value="${fn:escapeXml(sellerKeyword)}" placeholder="Ví dụ: Toyota Camry, Toyota hoặc autopro@gmail.com">
                    </div>
                    <button type="submit" class="btn btn-warning btn-soft">
                        <i class="fa-solid fa-magnifying-glass me-2"></i>Tìm kiếm
                    </button>
                    <a href="${ctx}/admin/posts" class="btn btn-outline-secondary btn-soft">
                        <i class="fa-solid fa-rotate-left me-2"></i>Xóa lọc
                    </a>
                </form>
            </section>

            <c:if test="${param.result == 'approved'}">
                <div class="alert alert-success mt-3">Đã duyệt bài đăng.</div>
            </c:if>
            <c:if test="${param.result == 'rejected'}">
                <div class="alert alert-danger mt-3">Đã từ chối bài đăng và lưu ghi chú cho người bán.</div>
            </c:if>
            <c:if test="${param.result == 'warned'}">
                <div class="alert alert-warning mt-3">Đã cảnh báo bài đăng và gửi ghi chú cho người bán.</div>
            </c:if>
            <c:if test="${param.result == 'deleted'}">
                <div class="alert alert-success mt-3">Đã xóa bài đăng.</div>
            </c:if>
            <c:if test="${param.result == 'error'}">
                <div class="alert alert-danger mt-3">Không thể xử lý bài đăng. Vui lòng thử lại.</div>
            </c:if>
            <c:if test="${param.result == 'invalid'}">
                <div class="alert alert-danger mt-3">Dữ liệu thao tác không hợp lệ.</div>
            </c:if>
            <c:if test="${param.result == 'invalid_state'}">
                <div class="alert alert-warning mt-3">Bài đăng này đã được xử lý ở trạng thái khác nên thao tác vừa rồi không còn hợp lệ.</div>
            </c:if>

            <section class="surface-card section-card">
                <div class="section-head">
                    <div>
                        <h3>Khu bài/xe cần duyệt ngay</h3>
                        <div class="section-subtitle">Nơi tập trung các xe mới đăng hoặc bài đã sửa gửi lại để admin duyệt nhanh.</div>
                    </div>
                    <span class="section-badge pending">
                        <i class="fa-solid fa-hourglass-half"></i>${pendingPostCount} bài chờ duyệt
                    </span>
                </div>

                <div class="section-note">
                    Bất kỳ xe/bài đăng nào được người bán sửa lại và gửi duyệt lần nữa sẽ quay về khu này, nên admin không cần mở màn quản lý xe riêng.
                </div>

                <c:choose>
                    <c:when test="${not empty pendingPosts}">
                        <div class="table-responsive mt-3">
                            <table class="table table-hover align-middle post-table">
                                <thead>
                                    <tr>
                                        <th>Mã</th>
                                        <th>Bài đăng</th>
                                        <th>Thông tin xe</th>
                                        <th>Người đăng</th>
                                        <th>Trạng thái</th>
                                        <th>Ghi chú admin</th>
                                        <th class="text-end">Thao tác</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="bd" items="${pendingPosts}">
                                        <tr>
                                            <td>
                                                <div class="id-stack">
                                                    <strong>Bài #${bd.maBaiDang}</strong>
                                                    <span class="text-muted">Xe #${bd.maXe}</span>
                                                </div>
                                            </td>
                                            <td class="title-cell">
                                                <strong><c:out value="${bd.tieuDe}" /></strong>
                                                <div class="small text-muted">
                                                    <fmt:formatDate value="${bd.ngayDang}" pattern="dd/MM/yyyy HH:mm" />
                                                </div>
                                                <div class="small text-muted mt-1">
                                                    <i class="fa-solid fa-location-dot me-1"></i><c:out value="${empty bd.tenTinh ? 'Chưa cập nhật tỉnh/thành' : bd.tenTinh}" />
                                                </div>
                                            </td>
                                            <td>
                                                <div class="vehicle-card">
                                                    <c:set var="thumbnail" value="${bd.anhDaiDien}" />
                                                    <c:choose>
                                                        <c:when test="${empty thumbnail}">
                                                            <img class="vehicle-thumb" src="${ctx}/uploads/cars/car-placeholder.svg" alt="AutoPro" data-context-path="${ctx}" loading="lazy" onerror="useAutoProCarFallback(this)">
                                                        </c:when>
                                                        <c:when test="${fn:startsWith(thumbnail, 'http://') or fn:startsWith(thumbnail, 'https://')}">
                                                            <img class="vehicle-thumb" src="${fn:escapeXml(thumbnail)}" alt="${fn:escapeXml(empty bd.tenXe ? 'AutoPro' : bd.tenXe)}" data-car-name="${fn:escapeXml(empty bd.tenXe ? '' : bd.tenXe)}" data-context-path="${ctx}" loading="lazy" onerror="useAutoProCarFallback(this)">
                                                        </c:when>
                                                        <c:when test="${fn:startsWith(thumbnail, '/')}">
                                                            <img class="vehicle-thumb" src="${ctx}${fn:escapeXml(thumbnail)}" alt="${fn:escapeXml(empty bd.tenXe ? 'AutoPro' : bd.tenXe)}" data-car-name="${fn:escapeXml(empty bd.tenXe ? '' : bd.tenXe)}" data-context-path="${ctx}" loading="lazy" onerror="useAutoProCarFallback(this)">
                                                        </c:when>
                                                        <c:otherwise>
                                                            <img class="vehicle-thumb" src="${ctx}/${fn:escapeXml(thumbnail)}" alt="${fn:escapeXml(empty bd.tenXe ? 'AutoPro' : bd.tenXe)}" data-car-name="${fn:escapeXml(empty bd.tenXe ? '' : bd.tenXe)}" data-context-path="${ctx}" loading="lazy" onerror="useAutoProCarFallback(this)">
                                                        </c:otherwise>
                                                    </c:choose>
                                                    <div>
                                                        <div class="vehicle-name"><c:out value="${empty bd.tenXe ? 'Chưa rõ xe' : bd.tenXe}" /></div>
                                                        <div class="vehicle-price">
                                                            <fmt:formatNumber value="${bd.giaBan}" maxFractionDigits="0" groupingUsed="true" /> đ
                                                        </div>
                                                        <div class="vehicle-meta">
                                                            <span class="vehicle-chip"><c:out value="${empty bd.tenHang ? 'Chưa rõ hãng' : bd.tenHang}" /></span>
                                                            <span class="vehicle-chip"><c:out value="${empty bd.namSanXuat ? 'Chưa rõ năm' : bd.namSanXuat}" /></span>
                                                            <span class="vehicle-chip"><c:out value="${empty bd.tinhTrangXe ? 'Chưa rõ tình trạng' : bd.tinhTrangXe}" /></span>
                                                            <span class="vehicle-chip"><c:out value="${empty bd.nhienLieu ? 'Chưa rõ nhiên liệu' : bd.nhienLieu}" /></span>
                                                            <span class="vehicle-chip"><c:out value="${empty bd.hopSo ? 'Chưa rõ hộp số' : bd.hopSo}" /></span>
                                                            <c:if test="${not empty bd.soKmDaDi}">
                                                                <span class="vehicle-chip"><fmt:formatNumber value="${bd.soKmDaDi}" maxFractionDigits="0" groupingUsed="true" /> km</span>
                                                            </c:if>
                                                        </div>
                                                    </div>
                                                </div>
                                            </td>
                                            <td>
                                                <div class="seller-name"><c:out value="${empty bd.tenNguoiDang ? 'Không rõ người đăng' : bd.tenNguoiDang}" /></div>
                                                <div class="seller-contact"><i class="fa-solid fa-envelope me-1"></i><c:out value="${empty bd.emailNguoiDang ? 'Chưa cập nhật email' : bd.emailNguoiDang}" /></div>
                                                <div class="seller-contact"><i class="fa-solid fa-phone me-1"></i><c:out value="${empty bd.soDienThoaiNguoiDang ? 'Chưa cập nhật SĐT' : bd.soDienThoaiNguoiDang}" /></div>
                                            </td>
                                            <td>
                                                <span class="status-badge status-cho_duyet">Chờ duyệt</span>
                                            </td>
                                            <td><div class="note-muted"><c:out value="${empty bd.ghiChuAdmin ? 'Chưa có ghi chú' : bd.ghiChuAdmin}" /></div></td>
                                            <td class="text-end">
                                                <div class="action-stack">
                                                    <a href="${ctx}/admin/post-detail?id=${bd.maBaiDang}" class="btn btn-sm btn-outline-dark">Chi tiết</a>
                                                    <form method="post" action="${ctx}/admin/posts" class="m-0">
<input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                                                        <input type="hidden" name="id" value="${bd.maBaiDang}">
                                                        <input type="hidden" name="sellerKeyword" value="${fn:escapeXml(sellerKeyword)}">
                                                        <button type="submit" name="action" value="approve" class="btn btn-sm btn-success">Duyệt ngay</button>
                                                    </form>
                                                </div>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="empty-state">
                            <i class="fa-solid fa-inbox"></i>
                            <div>Hiện không có bài/xe nào đang chờ duyệt trong bộ lọc này.</div>
                        </div>
                    </c:otherwise>
                </c:choose>
            </section>

            <section class="surface-card section-card">
                <div class="section-head">
                    <div>
                        <h3>Danh sách bài đăng & xe còn lại</h3>
                        <div class="section-subtitle">Các xe đã duyệt, đang giao dịch, đã bán, bị cảnh báo, bị từ chối hoặc các trạng thái khác để admin tra cứu khi cần.</div>
                    </div>
                    <span class="section-badge other">
                        <i class="fa-solid fa-table-list"></i>${otherPostCount} bài
                    </span>
                </div>

                <c:choose>
                    <c:when test="${not empty otherPosts}">
                        <div class="table-responsive">
                            <table class="table table-hover align-middle post-table">
                                <thead>
                                    <tr>
                                        <th>Mã</th>
                                        <th>Bài đăng</th>
                                        <th>Thông tin xe</th>
                                        <th>Người đăng</th>
                                        <th>Trạng thái</th>
                                        <th>Ghi chú admin</th>
                                        <th class="text-end">Thao tác</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="bd" items="${otherPosts}">
                                        <tr>
                                            <td>
                                                <div class="id-stack">
                                                    <strong>Bài #${bd.maBaiDang}</strong>
                                                    <span class="text-muted">Xe #${bd.maXe}</span>
                                                </div>
                                            </td>
                                            <td class="title-cell">
                                                <strong><c:out value="${bd.tieuDe}" /></strong>
                                                <div class="small text-muted">
                                                    <fmt:formatDate value="${bd.ngayDang}" pattern="dd/MM/yyyy HH:mm" />
                                                </div>
                                                <div class="small text-muted mt-1">
                                                    <i class="fa-solid fa-location-dot me-1"></i><c:out value="${empty bd.tenTinh ? 'Chưa cập nhật tỉnh/thành' : bd.tenTinh}" />
                                                </div>
                                            </td>
                                            <td>
                                                <div class="vehicle-card">
                                                    <c:set var="thumbnail" value="${bd.anhDaiDien}" />
                                                    <c:choose>
                                                        <c:when test="${empty thumbnail}">
                                                            <img class="vehicle-thumb" src="${ctx}/uploads/cars/car-placeholder.svg" alt="AutoPro" data-context-path="${ctx}" loading="lazy" onerror="useAutoProCarFallback(this)">
                                                        </c:when>
                                                        <c:when test="${fn:startsWith(thumbnail, 'http://') or fn:startsWith(thumbnail, 'https://')}">
                                                            <img class="vehicle-thumb" src="${fn:escapeXml(thumbnail)}" alt="${fn:escapeXml(empty bd.tenXe ? 'AutoPro' : bd.tenXe)}" data-car-name="${fn:escapeXml(empty bd.tenXe ? '' : bd.tenXe)}" data-context-path="${ctx}" loading="lazy" onerror="useAutoProCarFallback(this)">
                                                        </c:when>
                                                        <c:when test="${fn:startsWith(thumbnail, '/')}">
                                                            <img class="vehicle-thumb" src="${ctx}${fn:escapeXml(thumbnail)}" alt="${fn:escapeXml(empty bd.tenXe ? 'AutoPro' : bd.tenXe)}" data-car-name="${fn:escapeXml(empty bd.tenXe ? '' : bd.tenXe)}" data-context-path="${ctx}" loading="lazy" onerror="useAutoProCarFallback(this)">
                                                        </c:when>
                                                        <c:otherwise>
                                                            <img class="vehicle-thumb" src="${ctx}/${fn:escapeXml(thumbnail)}" alt="${fn:escapeXml(empty bd.tenXe ? 'AutoPro' : bd.tenXe)}" data-car-name="${fn:escapeXml(empty bd.tenXe ? '' : bd.tenXe)}" data-context-path="${ctx}" loading="lazy" onerror="useAutoProCarFallback(this)">
                                                        </c:otherwise>
                                                    </c:choose>
                                                    <div>
                                                        <div class="vehicle-name"><c:out value="${empty bd.tenXe ? 'Chưa rõ xe' : bd.tenXe}" /></div>
                                                        <div class="vehicle-price">
                                                            <fmt:formatNumber value="${bd.giaBan}" maxFractionDigits="0" groupingUsed="true" /> đ
                                                        </div>
                                                        <div class="vehicle-meta">
                                                            <span class="vehicle-chip"><c:out value="${empty bd.tenHang ? 'Chưa rõ hãng' : bd.tenHang}" /></span>
                                                            <span class="vehicle-chip"><c:out value="${empty bd.namSanXuat ? 'Chưa rõ năm' : bd.namSanXuat}" /></span>
                                                            <span class="vehicle-chip"><c:out value="${empty bd.tinhTrangXe ? 'Chưa rõ tình trạng' : bd.tinhTrangXe}" /></span>
                                                            <span class="vehicle-chip"><c:out value="${empty bd.nhienLieu ? 'Chưa rõ nhiên liệu' : bd.nhienLieu}" /></span>
                                                            <span class="vehicle-chip"><c:out value="${empty bd.hopSo ? 'Chưa rõ hộp số' : bd.hopSo}" /></span>
                                                            <c:if test="${not empty bd.soKmDaDi}">
                                                                <span class="vehicle-chip"><fmt:formatNumber value="${bd.soKmDaDi}" maxFractionDigits="0" groupingUsed="true" /> km</span>
                                                            </c:if>
                                                        </div>
                                                    </div>
                                                </div>
                                            </td>
                                            <td>
                                                <div class="seller-name"><c:out value="${empty bd.tenNguoiDang ? 'Không rõ người đăng' : bd.tenNguoiDang}" /></div>
                                                <div class="seller-contact"><i class="fa-solid fa-envelope me-1"></i><c:out value="${empty bd.emailNguoiDang ? 'Chưa cập nhật email' : bd.emailNguoiDang}" /></div>
                                                <div class="seller-contact"><i class="fa-solid fa-phone me-1"></i><c:out value="${empty bd.soDienThoaiNguoiDang ? 'Chưa cập nhật SĐT' : bd.soDienThoaiNguoiDang}" /></div>
                                            </td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${bd.trangThai eq 'da_duyet'}">
                                                        <span class="status-badge status-da_duyet">Đã duyệt</span>
                                                    </c:when>
                                                    <c:when test="${bd.trangThai eq 'tu_choi'}">
                                                        <span class="status-badge status-tu_choi">Từ chối</span>
                                                    </c:when>
                                                    <c:when test="${bd.trangThai eq 'canh_bao'}">
                                                        <span class="status-badge status-canh_bao">Cảnh báo</span>
                                                    </c:when>
                                                    <c:when test="${bd.trangThai eq 'da_ban'}">
                                                        <span class="status-badge status-da_ban">Đã bán</span>
                                                    </c:when>
                                                    <c:when test="${bd.trangThai eq 'dang_giao_dich'}">
                                                        <span class="status-badge status-dang_giao_dich">Đang giao dịch</span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="status-badge status-default"><c:out value="${empty bd.trangThai ? 'Chưa cập nhật' : bd.trangThai}" /></span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td><div class="note-muted"><c:out value="${empty bd.ghiChuAdmin ? 'Chưa có ghi chú' : bd.ghiChuAdmin}" /></div></td>
                                            <td class="text-end">
                                                <div class="action-stack">
                                                    <a href="${ctx}/admin/post-detail?id=${bd.maBaiDang}" class="btn btn-sm btn-outline-dark">Chi tiết</a>
                                                    <c:if test="${bd.trangThai eq 'da_ban' and not empty bd.maGiaoDich}">
                                                        <a href="${ctx}/admin/giao-dich?id=${bd.maGiaoDich}" class="btn btn-sm btn-primary">Chi tiết giao dịch</a>
                                                    </c:if>
                                                </div>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="empty-state">
                            <i class="fa-solid fa-folder-open"></i>
                            <div>Không có bài/xe nào ở nhóm đã xử lý hoặc trạng thái khác theo bộ lọc hiện tại.</div>
                        </div>
                    </c:otherwise>
                </c:choose>
            </section>
        </div>
        <script src="${ctx}/assets/js/autopro-image-fallback.js"></script>
    </body>
</html>
