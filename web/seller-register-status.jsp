<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Trạng thái hồ sơ seller - AutoPro</title>
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
                --ui-blue-soft: #e8f0ff;
                --ui-green-soft: #dff6e8;
                --ui-red-soft: #ffe3e1;
                --ui-shadow: 0 18px 44px rgba(15, 23, 42, 0.08);
            }

            body {
                background:
                    radial-gradient(circle at top left, rgba(255, 214, 102, 0.22), transparent 26%),
                    linear-gradient(180deg, #f8fafc 0%, var(--ui-bg) 100%);
                color: var(--ui-text);
                font-family: 'Manrope', sans-serif;
                min-height: 100vh;
            }

            .page-wrap {
                max-width: 1260px;
                margin: 28px auto 48px;
            }

            .shell-card,
            .content-card {
                background: var(--ui-card);
                border: 1px solid var(--ui-line);
                border-radius: 28px;
                box-shadow: var(--ui-shadow);
            }

            .shell-card {
                padding: 30px;
                background:
                    linear-gradient(135deg, rgba(255, 239, 184, 0.92), rgba(255, 255, 255, 0.98) 44%),
                    #ffffff;
            }

            .content-card {
                padding: 24px;
            }

            .top-actions {
                display: flex;
                align-items: center;
                gap: 10px;
                flex-wrap: wrap;
            }

            .hero-title {
                font-size: clamp(2rem, 3.8vw, 3rem);
                font-weight: 800;
                line-height: 1.05;
                margin: 16px 0 12px;
            }

            .hero-text,
            .muted-text {
                color: var(--ui-muted);
                line-height: 1.7;
                font-size: 14px;
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
                letter-spacing: 0.04em;
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
                min-height: 120px;
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
                align-items: start;
                gap: 14px;
                margin-bottom: 18px;
            }

            .section-head h3,
            .section-head h4 {
                margin: 0;
                font-weight: 800;
            }

            .detail-grid {
                display: grid;
                grid-template-columns: repeat(2, minmax(0, 1fr));
                gap: 14px;
            }

            .detail-item {
                border-radius: 20px;
                border: 1px solid var(--ui-line);
                background: #fbfcfe;
                padding: 18px;
            }

            .detail-item .label {
                color: #7a8a9c;
                font-size: 12px;
                font-weight: 700;
                text-transform: uppercase;
                letter-spacing: 0.04em;
                margin-bottom: 8px;
            }

            .detail-item .value {
                font-weight: 800;
                font-size: 16px;
            }

            .detail-item .muted {
                color: var(--ui-muted);
                margin-top: 6px;
                font-size: 13px;
                line-height: 1.6;
            }

            .message-box {
                border-radius: 22px;
                border: 1px solid var(--ui-line);
                background: linear-gradient(180deg, #ffffff, #fbfdff);
                padding: 18px 20px;
                margin-top: 14px;
            }

            .doc-grid {
                display: grid;
                grid-template-columns: repeat(2, minmax(0, 1fr));
                gap: 16px;
            }

            .doc-card {
                border-radius: 24px;
                border: 1px solid var(--ui-line);
                background: #fbfdff;
                padding: 18px;
            }

            .doc-thumb {
                width: 100%;
                height: 260px;
                object-fit: cover;
                border-radius: 18px;
                border: 1px solid #e5e7eb;
                background: #f8fafc;
            }

            .timeline {
                display: grid;
                gap: 12px;
            }

            .timeline-item {
                display: flex;
                gap: 14px;
                align-items: start;
                padding: 15px;
                border-radius: 18px;
                border: 1px solid var(--ui-line);
                background: #fbfcfe;
            }

            .timeline-icon {
                width: 40px;
                height: 40px;
                border-radius: 12px;
                display: inline-flex;
                align-items: center;
                justify-content: center;
                background: var(--ui-blue-soft);
                color: #2563eb;
                flex: 0 0 40px;
            }

            .history-table {
                border-collapse: separate;
                border-spacing: 0 10px;
                margin: 0;
            }

            .history-table thead th {
                border: none;
                color: #64748b;
                font-size: 12px;
                text-transform: uppercase;
                letter-spacing: 0.04em;
                font-weight: 800;
                padding-bottom: 8px;
            }

            .history-table tbody tr {
                background: #fbfcfe;
            }

            .history-table tbody td {
                border-top: 1px solid var(--ui-line);
                border-bottom: 1px solid var(--ui-line);
                padding: 16px 14px;
                vertical-align: middle;
            }

            .history-table tbody td:first-child {
                border-left: 1px solid var(--ui-line);
                border-top-left-radius: 18px;
                border-bottom-left-radius: 18px;
            }

            .history-table tbody td:last-child {
                border-right: 1px solid var(--ui-line);
                border-top-right-radius: 18px;
                border-bottom-right-radius: 18px;
            }

            .sticky-card {
                position: sticky;
                top: 20px;
            }

            @media (max-width: 991.98px) {
                .summary-grid {
                    grid-template-columns: repeat(2, minmax(0, 1fr));
                }

                .sticky-card {
                    position: static;
                }
            }

            @media (max-width: 767.98px) {
                .shell-card,
                .content-card {
                    border-radius: 22px;
                }

                .summary-grid,
                .detail-grid,
                .doc-grid {
                    grid-template-columns: 1fr;
                }
            }
        </style>
    </head>
    <body>
        <c:set var="ctx" value="${pageContext.request.contextPath}" />
        <div class="container page-wrap">
            <div class="shell-card mb-4">
                <div class="d-flex justify-content-between align-items-start flex-wrap gap-3">
                    <div>
                        <span class="eyebrow">
                            <i class="fa-solid fa-file-shield"></i>
                            Hồ sơ seller
                        </span>
                        <h1 class="hero-title">Theo dõi tình trạng xác minh người bán của bạn</h1>
                        <p class="hero-text">
                            Xem hồ sơ mới nhất, trạng thái xử lý, lịch sử nộp hồ sơ và những bước tiếp theo để mở quyền đăng tin trên AutoPro.
                        </p>
                    </div>
                    <div class="top-actions">
                        <a href="${ctx}/home" class="btn btn-outline-secondary">
                            <i class="fa-solid fa-house me-2"></i>Trang chủ
                        </a>
                        <a href="${ctx}/seller-register" class="btn btn-outline-primary">
                            <i class="fa-solid fa-pen-to-square me-2"></i>Trang đăng ký
                        </a>
                        <c:if test="${isApprovedSeller}">
                            <a href="${ctx}/post-car" class="btn btn-warning fw-bold">
                                <i class="fa-solid fa-plus me-2"></i>Đăng tin bán xe
                            </a>
                        </c:if>
                        <jsp:include page="/WEB-INF/includes/account-menu.jsp" />
                    </div>
                </div>

                <div class="summary-grid">
                    <div class="summary-card">
                        <div class="icon"><i class="fa-solid fa-folder-open"></i></div>
                        <div class="value">${not empty sellerRequestHistory ? fn:length(sellerRequestHistory) : 0}</div>
                        <div class="label">Tổng hồ sơ đã gửi</div>
                        <div class="meta">Lưu lại toàn bộ lịch sử xử lý hồ sơ seller của bạn.</div>
                    </div>
                    <div class="summary-card">
                        <div class="icon"><i class="fa-solid fa-shield-halved"></i></div>
                        <div class="value" style="font-size: 16px;">
                            <c:choose>
                                <c:when test="${isApprovedSeller}">Đã có quyền seller</c:when>
                                <c:when test="${not empty latestSellerRequest}">${latestSellerRequest.trangThaiHienThi}</c:when>
                                <c:otherwise>Chưa có hồ sơ</c:otherwise>
                            </c:choose>
                        </div>
                        <div class="label">Trạng thái hiện tại</div>
                        <div class="meta">Hệ thống sẽ cập nhật ngay khi admin duyệt hoặc từ chối hồ sơ.</div>
                    </div>
                    <div class="summary-card">
                        <div class="icon"><i class="fa-solid fa-clock"></i></div>
                        <div class="value" style="font-size: 16px;">
                            <c:choose>
                                <c:when test="${not empty latestSellerRequest}">
                                    <fmt:formatDate value="${latestSellerRequest.ngayCapNhat}" pattern="dd/MM HH:mm" />
                                </c:when>
                                <c:otherwise>-</c:otherwise>
                            </c:choose>
                        </div>
                        <div class="label">Lần cập nhật gần nhất</div>
                        <div class="meta">Thời điểm phản ánh bản ghi seller gần nhất của bạn được thay đổi.</div>
                    </div>
                    <div class="summary-card">
                        <div class="icon"><i class="fa-solid fa-badge-check"></i></div>
                        <div class="value">${isApprovedSeller ? 'Mở' : 'Chưa mở'}</div>
                        <div class="label">Quyền đăng tin</div>
                        <div class="meta">Chỉ tài khoản seller đã duyệt mới được vào trang đăng bài bán xe.</div>
                    </div>
                </div>
            </div>

            <c:if test="${param.submitted == '1'}">
                <div class="alert alert-success rounded-4 border-0 shadow-sm">Hồ sơ seller đã được gửi thành công. Admin sẽ xem xét trong thời gian sớm nhất.</div>
            </c:if>
            <c:if test="${param.pending == '1'}">
                <div class="alert alert-warning rounded-4 border-0 shadow-sm">Bạn đang có hồ sơ seller chờ duyệt nên chưa thể nộp thêm hồ sơ mới.</div>
            </c:if>
            <c:if test="${param.alreadySeller == '1'}">
                <div class="alert alert-success rounded-4 border-0 shadow-sm">Tài khoản của bạn hiện đã có quyền seller.</div>
            </c:if>

            <div class="row g-4">
                <div class="col-lg-8">
                    <div class="content-card mb-4">
                        <div class="section-head">
                            <div>
                                <h3>Hồ sơ mới nhất</h3>
                                <div class="muted-text">Chi tiết hồ sơ seller gần nhất để bạn theo dõi trạng thái và kiểm tra lại thông tin đã gửi.</div>
                            </div>
                            <c:if test="${not empty latestSellerRequest}">
                                <span class="status-pill ${latestSellerRequest.trangThaiCssClass}">
                                    <i class="fa-solid fa-shield-halved"></i>${latestSellerRequest.trangThaiHienThi}
                                </span>
                            </c:if>
                        </div>

                        <c:choose>
                            <c:when test="${not empty latestSellerRequest}">
                                <div class="detail-grid">
                                    <div class="detail-item">
                                        <div class="label">Họ tên</div>
                                        <div class="value">${latestSellerRequest.hoTen}</div>
                                        <div class="muted">Email: ${latestSellerRequest.email}</div>
                                    </div>
                                    <div class="detail-item">
                                        <div class="label">Liên hệ</div>
                                        <div class="value">${latestSellerRequest.soDienThoai}</div>
                                        <div class="muted">CCCD: ${latestSellerRequest.soCCCD}</div>
                                    </div>
                                    <div class="detail-item">
                                        <div class="label">Khu vực</div>
                                        <div class="value">${latestSellerRequest.tenTinh}</div>
                                        <div class="muted">${empty latestSellerRequest.mien ? 'Chưa cập nhật miền' : latestSellerRequest.mien}</div>
                                    </div>
                                    <div class="detail-item">
                                        <div class="label">Thời gian hồ sơ</div>
                                        <div class="value"><fmt:formatDate value="${latestSellerRequest.ngayTao}" pattern="dd/MM/yyyy HH:mm" /></div>
                                        <div class="muted">Cập nhật lần cuối: <fmt:formatDate value="${latestSellerRequest.ngayCapNhat}" pattern="dd/MM/yyyy HH:mm" /></div>
                                    </div>
                                </div>

                                <div class="message-box">
                                    <strong class="d-block mb-2">Địa chỉ chi tiết</strong>
                                    <div class="muted-text">${empty latestSellerRequest.diaChiChiTiet ? 'Không có địa chỉ chi tiết.' : latestSellerRequest.diaChiChiTiet}</div>
                                </div>

                                <div class="message-box">
                                    <strong class="d-block mb-2">Ghi chú người đăng ký</strong>
                                    <div class="muted-text">${empty latestSellerRequest.ghiChuNguoiDangKy ? 'Không có ghi chú bổ sung.' : latestSellerRequest.ghiChuNguoiDangKy}</div>
                                </div>

                                <c:if test="${latestSellerRequest.tuChoi}">
                                    <div class="alert alert-danger rounded-4 mt-3 mb-0">
                                        <strong>Lý do từ chối:</strong>
                                        ${empty latestSellerRequest.lyDoTuChoi ? 'Admin chưa ghi rõ lý do chi tiết.' : latestSellerRequest.lyDoTuChoi}
                                    </div>
                                </c:if>

                                <c:if test="${not empty latestSellerRequest.tenNguoiPheDuyet}">
                                    <div class="message-box">
                                        <strong class="d-block mb-2">Thông tin xử lý</strong>
                                        <div class="muted-text">
                                            Admin xử lý: ${latestSellerRequest.tenNguoiPheDuyet}
                                            <c:if test="${not empty latestSellerRequest.ngayPheDuyet}">
                                                <br>Thời gian xử lý:
                                                <fmt:formatDate value="${latestSellerRequest.ngayPheDuyet}" pattern="dd/MM/yyyy HH:mm" />
                                            </c:if>
                                        </div>
                                    </div>
                                </c:if>
                            </c:when>
                            <c:otherwise>
                                <div class="alert alert-light border rounded-4 mb-0">
                                    <c:choose>
                                        <c:when test="${isApprovedSeller}">
                                            Tài khoản của bạn đã là seller, nhưng hiện chưa có bản ghi hồ sơ seller trong module mới. Bạn vẫn có thể đăng tin bán xe bình thường.
                                        </c:when>
                                        <c:otherwise>
                                            Bạn chưa có hồ sơ seller nào. Hãy mở trang đăng ký và gửi hồ sơ xác minh đầu tiên.
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>

                    <div class="content-card mb-4">
                        <div class="section-head">
                            <div>
                                <h4>Ảnh xác minh CCCD</h4>
                                <div class="muted-text">Admin dựa vào hai ảnh này để kiểm tra nhanh mức độ đầy đủ và rõ nét của hồ sơ.</div>
                            </div>
                        </div>

                        <c:choose>
                            <c:when test="${not empty latestSellerRequest}">
                                <c:set var="frontDocMissing" value="${empty latestSellerRequest.anhCCCDMatTruoc or fn:contains(latestSellerRequest.anhCCCDMatTruoc, 'chua-cap-nhat')}" />
                                <c:set var="backDocMissing" value="${empty latestSellerRequest.anhCCCDMatSau or fn:contains(latestSellerRequest.anhCCCDMatSau, 'chua-cap-nhat')}" />
                                <div class="doc-grid">
                                    <div class="doc-card">
                                        <div class="fw-bold mb-3">CCCD mặt trước</div>
                                        <c:choose>
                                            <c:when test="${fn:startsWith(latestSellerRequest.anhCCCDMatTruoc, 'http://') or fn:startsWith(latestSellerRequest.anhCCCDMatTruoc, 'https://')}">
                                                <img src="${pageContext.request.contextPath}/private-document?type=seller&amp;id=${latestSellerRequest.maDangKySeller}&amp;side=front" alt="CCCD mặt trước" class="doc-thumb">
                                            </c:when>
                                            <c:otherwise>
                                                <img src="${pageContext.request.contextPath}/private-document?type=seller&amp;id=${latestSellerRequest.maDangKySeller}&amp;side=front" alt="CCCD mặt trước" class="doc-thumb">
                                            </c:otherwise>
                                        </c:choose>
                                    </div>
                                    <div class="doc-card">
                                        <div class="fw-bold mb-3">CCCD mặt sau</div>
                                        <c:choose>
                                            <c:when test="${fn:startsWith(latestSellerRequest.anhCCCDMatSau, 'http://') or fn:startsWith(latestSellerRequest.anhCCCDMatSau, 'https://')}">
                                                <img src="${pageContext.request.contextPath}/private-document?type=seller&amp;id=${latestSellerRequest.maDangKySeller}&amp;side=back" alt="CCCD mặt sau" class="doc-thumb">
                                            </c:when>
                                            <c:otherwise>
                                                <img src="${pageContext.request.contextPath}/private-document?type=seller&amp;id=${latestSellerRequest.maDangKySeller}&amp;side=back" alt="CCCD mặt sau" class="doc-thumb">
                                            </c:otherwise>
                                        </c:choose>
                                    </div>
                                </div>
                                <c:if test="${frontDocMissing or backDocMissing}">
                                    <div class="alert alert-warning border-0 rounded-4 mt-3 mb-0">
                                        <div class="fw-bold mb-1">
                                            <i class="fa-solid fa-triangle-exclamation me-2"></i>Hồ sơ này chưa có đủ CCCD thật
                                        </div>
                                        <div class="mb-3">Bạn có thể bổ sung số CCCD và tải lại ảnh hai mặt trong trang hồ sơ cá nhân.</div>
                                        <a href="${ctx}/profile#seller-identity" class="btn btn-warning fw-bold">
                                            <i class="fa-solid fa-id-card-clip me-2"></i>Cập nhật CCCD
                                        </a>
                                    </div>
                                </c:if>
                            </c:when>
                            <c:otherwise>
                                <div class="muted-text">Chưa có ảnh xác minh nào để hiển thị.</div>
                            </c:otherwise>
                        </c:choose>
                    </div>

                    <div class="content-card">
                        <div class="section-head">
                            <div>
                                <h4>Lịch sử hồ sơ</h4>
                                <div class="muted-text">Giữ lại toàn bộ các lần gửi hồ sơ seller để bạn tiện theo dõi và đối chiếu.</div>
                            </div>
                            <c:if test="${not empty sellerRequestHistory}">
                                <span class="status-pill status-default">
                                    <i class="fa-solid fa-folder-open"></i>${fn:length(sellerRequestHistory)} hồ sơ
                                </span>
                            </c:if>
                        </div>

                        <c:choose>
                            <c:when test="${not empty sellerRequestHistory}">
                                <div class="table-responsive">
                                    <table class="table history-table align-middle">
                                        <thead>
                                            <tr>
                                                <th>Mã hồ sơ</th>
                                                <th>Ngày tạo</th>
                                                <th>Trạng thái</th>
                                                <th>Tỉnh/Thành</th>
                                                <th>Người xử lý</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <c:forEach items="${sellerRequestHistory}" var="history">
                                                <tr>
                                                    <td class="fw-bold">#${history.maDangKySeller}</td>
                                                    <td><fmt:formatDate value="${history.ngayTao}" pattern="dd/MM/yyyy HH:mm" /></td>
                                                    <td><span class="status-pill ${history.trangThaiCssClass}">${history.trangThaiHienThi}</span></td>
                                                    <td>${history.tenTinh}</td>
                                                    <td>${empty history.tenNguoiPheDuyet ? '-' : history.tenNguoiPheDuyet}</td>
                                                </tr>
                                            </c:forEach>
                                        </tbody>
                                    </table>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <div class="muted-text">Chưa có lịch sử hồ sơ seller.</div>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>

                <div class="col-lg-4">
                    <div class="content-card sticky-card">
                        <div class="section-head">
                            <div>
                                <h4>Hành động tiếp theo</h4>
                                <div class="muted-text">Tùy theo trạng thái hiện tại, đây là bước phù hợp tiếp theo cho tài khoản của bạn.</div>
                            </div>
                        </div>

                        <div class="timeline mb-4">
                            <div class="timeline-item">
                                <div class="timeline-icon"><i class="fa-solid fa-file-arrow-up"></i></div>
                                <div>
                                    <strong class="d-block mb-1">Đã gửi hồ sơ</strong>
                                    <span class="muted-text">Hồ sơ được lưu vào hệ thống và đẩy vào hàng chờ admin kiểm tra.</span>
                                </div>
                            </div>
                            <div class="timeline-item">
                                <div class="timeline-icon"><i class="fa-solid fa-magnifying-glass"></i></div>
                                <div>
                                    <strong class="d-block mb-1">Admin xem xét</strong>
                                    <span class="muted-text">Nếu thiếu thông tin, bạn sẽ thấy lý do từ chối và có thể nộp lại.</span>
                                </div>
                            </div>
                            <div class="timeline-item">
                                <div class="timeline-icon"><i class="fa-solid fa-car-side"></i></div>
                                <div>
                                    <strong class="d-block mb-1">Mở quyền đăng tin</strong>
                                    <span class="muted-text">Khi hồ sơ được duyệt, tài khoản chuyển sang seller và có thể đăng bài bán xe.</span>
                                </div>
                            </div>
                        </div>

                        <c:choose>
                            <c:when test="${isApprovedSeller}">
                                <div class="alert alert-success rounded-4">Tài khoản của bạn đã có quyền seller và có thể bắt đầu đăng tin ngay.</div>
                                <div class="d-grid gap-2">
                                    <a href="${ctx}/post-car" class="btn btn-warning fw-bold">
                                        <i class="fa-solid fa-plus me-2"></i>Đăng tin bán xe
                                    </a>
                                    <a href="${ctx}/seller/dashboard" class="btn btn-outline-dark">
                                        <i class="fa-solid fa-chart-simple me-2"></i>Dashboard người bán
                                    </a>
                                </div>
                            </c:when>
                            <c:when test="${not empty latestSellerRequest and latestSellerRequest.choDuyet}">
                                <div class="alert alert-warning rounded-4">Hồ sơ đang chờ admin duyệt. Bạn chỉ cần theo dõi thông báo và chờ phản hồi.</div>
                                <div class="d-grid gap-2">
                                    <a href="${ctx}/notifications" class="btn btn-outline-primary">
                                        <i class="fa-solid fa-bell me-2"></i>Mở thông báo
                                    </a>
                                    <a href="${ctx}/home" class="btn btn-light">Quay lại trang chủ</a>
                                </div>
                            </c:when>
                            <c:when test="${not empty latestSellerRequest and (latestSellerRequest.tuChoi or latestSellerRequest.daXoa)}">
                                <div class="alert alert-danger rounded-4">Bạn có thể chuẩn bị lại thông tin rõ hơn và nộp một hồ sơ seller mới.</div>
                                <div class="d-grid gap-2">
                                    <a href="${ctx}/seller-register" class="btn btn-warning fw-bold">
                                        <i class="fa-solid fa-rotate-right me-2"></i>Nộp lại hồ sơ
                                    </a>
                                    <a href="${ctx}/notifications" class="btn btn-outline-secondary">Xem thông báo</a>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <div class="alert alert-light border rounded-4">Bạn chưa tạo hồ sơ seller nào.</div>
                                <a href="${ctx}/seller-register" class="btn btn-warning fw-bold w-100">
                                    <i class="fa-solid fa-file-circle-plus me-2"></i>Tạo hồ sơ seller
                                </a>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </div>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
        <jsp:include page="/WEB-INF/includes/site-footer.jsp" />

    </body>
</html>
