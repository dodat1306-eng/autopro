<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Chi tiết hồ sơ seller - AutoPro Admin</title>
        <link rel="preconnect" href="https://fonts.googleapis.com">
        <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
        <link href="https://fonts.googleapis.com/css2?family=Manrope:wght@400;500;600;700;800&display=swap" rel="stylesheet">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
        <style>
            :root {
                --ui-bg: #f4f6fa;
                --ui-card: #ffffff;
                --ui-line: #e5ebf3;
                --ui-text: #132238;
                --ui-muted: #61738b;
                --ui-gold-soft: #fff4cf;
                --ui-blue-soft: #e8f0ff;
                --ui-green-soft: #dff6e8;
                --ui-red-soft: #ffe3e1;
                --ui-shadow: 0 20px 48px rgba(15, 23, 42, 0.08);
            }

            body {
                background:
                    radial-gradient(circle at top left, rgba(255, 214, 102, 0.2), transparent 24%),
                    linear-gradient(180deg, #f8fafc 0%, var(--ui-bg) 100%);
                color: var(--ui-text);
                font-family: 'Manrope', sans-serif;
                min-height: 100vh;
            }

            .page-wrap {
                max-width: 1320px;
                margin: 30px auto 48px;
            }

            .shell-card,
            .panel-card {
                background: var(--ui-card);
                border: 1px solid var(--ui-line);
                border-radius: 28px;
                box-shadow: var(--ui-shadow);
            }

            .shell-card {
                padding: 30px;
                background:
                    linear-gradient(135deg, rgba(255, 239, 184, 0.94), rgba(255, 255, 255, 0.98) 48%),
                    #ffffff;
            }

            .panel-card {
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
                letter-spacing: 0.04em;
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

            .top-actions {
                display: flex;
                gap: 10px;
                flex-wrap: wrap;
                align-items: center;
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
                background: rgba(255, 255, 255, 0.92);
                border: 1px solid rgba(226, 232, 240, 0.96);
                border-radius: 22px;
                padding: 18px;
                min-height: 116px;
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
                font-size: 16px;
                font-weight: 800;
                line-height: 1.35;
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
                height: 300px;
                object-fit: cover;
                border-radius: 18px;
                border: 1px solid #e5e7eb;
                background: #f8fafc;
            }

            .sticky-card {
                position: sticky;
                top: 20px;
            }

            .action-box {
                border-radius: 22px;
                border: 1px solid var(--ui-line);
                background: #fbfcfe;
                padding: 18px;
                margin-bottom: 16px;
            }

            .helper-list {
                margin: 0;
                padding-left: 18px;
            }

            .helper-list li {
                color: var(--ui-muted);
                font-size: 13px;
                line-height: 1.7;
                margin-bottom: 6px;
            }

            .form-control {
                border-radius: 16px;
                padding: 13px 15px;
                border-color: #dbe3ee;
                box-shadow: none !important;
            }

            .form-control:focus {
                border-color: #f5b700;
            }

            @media (max-width: 1199.98px) {
                .summary-grid {
                    grid-template-columns: repeat(2, minmax(0, 1fr));
                }
            }

            @media (max-width: 991.98px) {
                .sticky-card {
                    position: static;
                }
            }

            @media (max-width: 767.98px) {
                .summary-grid,
                .detail-grid,
                .doc-grid {
                    grid-template-columns: 1fr;
                }
            }
        </style>
    </head>
    <body>
        <div class="container page-wrap">
            <div class="shell-card mb-4">
                <div class="d-flex justify-content-between align-items-start flex-wrap gap-3">
                    <div>
                        <span class="eyebrow">
                            <i class="fa-solid fa-id-card-clip"></i>
                            Admin Seller Review
                        </span>
                        <h1 class="hero-title">Chi tiết hồ sơ seller #${sellerRequest.maDangKySeller}</h1>
                        <p class="hero-text">
                            Kiểm tra toàn bộ thông tin xác minh, so sánh với tài khoản hiện có và xử lý phê duyệt hoặc từ chối ngay trên hồ sơ này.
                        </p>
                    </div>
                    <div class="top-actions">
                        <a href="${pageContext.request.contextPath}/admin/dashboard" class="btn btn-outline-secondary">
                            <i class="fa-solid fa-arrow-left me-2"></i>Về Dashboard
                        </a>
                        <a href="${pageContext.request.contextPath}/admin/seller-requests" class="btn btn-outline-dark">Danh sách hồ sơ</a>
                    </div>
                </div>

                <div class="summary-grid">
                    <div class="summary-card">
                        <div class="icon"><i class="fa-solid fa-shield-halved"></i></div>
                        <div class="value"><span class="status-pill ${sellerRequest.trangThaiCssClass}"><i class="fa-solid fa-circle"></i>${sellerRequest.trangThaiHienThi}</span></div>
                        <div class="label">Trạng thái hồ sơ</div>
                        <div class="meta">Backend cũng kiểm tra trạng thái trước khi cho phép phê duyệt hoặc từ chối.</div>
                    </div>
                    <div class="summary-card">
                        <div class="icon"><i class="fa-solid fa-user"></i></div>
                        <div class="value">${sellerRequest.hoTen}</div>
                        <div class="label">Người đăng ký</div>
                        <div class="meta">User ID: ${sellerRequest.maNguoiDung}</div>
                    </div>
                    <div class="summary-card">
                        <div class="icon"><i class="fa-solid fa-location-dot"></i></div>
                        <div class="value">${empty sellerRequest.tenTinh ? '-' : sellerRequest.tenTinh}</div>
                        <div class="label">Khu vực</div>
                        <div class="meta">${empty sellerRequest.mien ? 'Chưa cập nhật miền' : sellerRequest.mien}</div>
                    </div>
                    <div class="summary-card">
                        <div class="icon"><i class="fa-solid fa-clock"></i></div>
                        <div class="value"><fmt:formatDate value="${sellerRequest.ngayTao}" pattern="dd/MM/yyyy HH:mm" /></div>
                        <div class="label">Ngày tạo hồ sơ</div>
                        <div class="meta">Cập nhật lần cuối: <fmt:formatDate value="${sellerRequest.ngayCapNhat}" pattern="dd/MM/yyyy HH:mm" /></div>
                    </div>
                </div>
            </div>

            <c:if test="${param.result == 'approved'}">
                <div class="alert alert-success rounded-4 border-0 shadow-sm">Hồ sơ seller đã được phê duyệt. Tài khoản người dùng đã được cấp quyền seller.</div>
            </c:if>
            <c:if test="${param.result == 'rejected'}">
                <div class="alert alert-warning rounded-4 border-0 shadow-sm">Hồ sơ seller đã bị từ chối và thông báo đã được gửi cho người dùng.</div>
            </c:if>
            <c:if test="${param.result == 'invalid-state'}">
                <div class="alert alert-danger rounded-4 border-0 shadow-sm">Hồ sơ không còn ở trạng thái chờ duyệt hoặc đã bị xử lý trước đó.</div>
            </c:if>

            <div class="row g-4">
                <div class="col-lg-8">
                    <div class="panel-card mb-4">
                        <div class="section-head">
                            <div>
                                <h3>Thông tin hồ sơ</h3>
                                <div class="muted-text">Xem nhanh dữ liệu xác minh mà người dùng đã gửi cho admin.</div>
                            </div>
                        </div>

                        <div class="detail-grid">
                            <div class="detail-item">
                                <div class="label">Họ tên</div>
                                <div class="value">${sellerRequest.hoTen}</div>
                                <div class="muted">Email hồ sơ: ${sellerRequest.email}</div>
                            </div>
                            <div class="detail-item">
                                <div class="label">Tài khoản hệ thống</div>
                                <div class="value">${empty sellerRequest.tenNguoiDung ? '-' : sellerRequest.tenNguoiDung}</div>
                                <div class="muted">Email tài khoản: ${empty sellerRequest.emailNguoiDung ? '-' : sellerRequest.emailNguoiDung}</div>
                            </div>
                            <div class="detail-item">
                                <div class="label">Số điện thoại</div>
                                <div class="value">${sellerRequest.soDienThoai}</div>
                                <div class="muted">CCCD: ${sellerRequest.soCCCD}</div>
                            </div>
                            <div class="detail-item">
                                <div class="label">Khu vực</div>
                                <div class="value">${empty sellerRequest.tenTinh ? '-' : sellerRequest.tenTinh}</div>
                                <div class="muted">${empty sellerRequest.mien ? 'Chưa cập nhật miền' : sellerRequest.mien}</div>
                            </div>
                        </div>

                        <div class="message-box">
                            <strong class="d-block mb-2">Địa chỉ chi tiết</strong>
                            <div class="muted-text">${empty sellerRequest.diaChiChiTiet ? 'Không có địa chỉ chi tiết.' : sellerRequest.diaChiChiTiet}</div>
                        </div>

                        <div class="message-box">
                            <strong class="d-block mb-2">Ghi chú người đăng ký</strong>
                            <div class="muted-text">${empty sellerRequest.ghiChuNguoiDangKy ? 'Không có ghi chú bổ sung.' : sellerRequest.ghiChuNguoiDangKy}</div>
                        </div>

                        <c:if test="${not empty sellerRequest.tenNguoiPheDuyet}">
                            <div class="message-box">
                                <strong class="d-block mb-2">Thông tin xử lý</strong>
                                <div class="muted-text">
                                    Admin xử lý: ${sellerRequest.tenNguoiPheDuyet}
                                    <c:if test="${not empty sellerRequest.ngayPheDuyet}">
                                        <br>Thời gian xử lý:
                                        <fmt:formatDate value="${sellerRequest.ngayPheDuyet}" pattern="dd/MM/yyyy HH:mm" />
                                    </c:if>
                                    <c:if test="${sellerRequest.tuChoi and not empty sellerRequest.lyDoTuChoi}">
                                        <br>Lý do từ chối: ${sellerRequest.lyDoTuChoi}
                                    </c:if>
                                </div>
                            </div>
                        </c:if>
                    </div>

                    <div class="panel-card">
                        <div class="section-head">
                            <div>
                                <h4>Ảnh xác minh CCCD</h4>
                                <div class="muted-text">Nhấn mở từng ảnh để kiểm tra rõ nét hơn nếu cần.</div>
                            </div>
                        </div>

                        <div class="doc-grid">
                            <div class="doc-card">
                                <div class="fw-bold mb-3">CCCD mặt trước</div>
                                <c:choose>
                                    <c:when test="${fn:startsWith(sellerRequest.anhCCCDMatTruoc, 'http://') or fn:startsWith(sellerRequest.anhCCCDMatTruoc, 'https://')}">
                                        <a href="${pageContext.request.contextPath}/private-document?type=seller&amp;id=${sellerRequest.maDangKySeller}&amp;side=front" target="_blank" class="text-decoration-none">
                                            <img src="${pageContext.request.contextPath}/private-document?type=seller&amp;id=${sellerRequest.maDangKySeller}&amp;side=front" alt="CCCD mặt trước" class="doc-thumb">
                                        </a>
                                    </c:when>
                                    <c:otherwise>
                                        <a href="${pageContext.request.contextPath}/private-document?type=seller&amp;id=${sellerRequest.maDangKySeller}&amp;side=front" target="_blank" class="text-decoration-none">
                                            <img src="${pageContext.request.contextPath}/private-document?type=seller&amp;id=${sellerRequest.maDangKySeller}&amp;side=front" alt="CCCD mặt trước" class="doc-thumb">
                                        </a>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                            <div class="doc-card">
                                <div class="fw-bold mb-3">CCCD mặt sau</div>
                                <c:choose>
                                    <c:when test="${fn:startsWith(sellerRequest.anhCCCDMatSau, 'http://') or fn:startsWith(sellerRequest.anhCCCDMatSau, 'https://')}">
                                        <a href="${pageContext.request.contextPath}/private-document?type=seller&amp;id=${sellerRequest.maDangKySeller}&amp;side=back" target="_blank" class="text-decoration-none">
                                            <img src="${pageContext.request.contextPath}/private-document?type=seller&amp;id=${sellerRequest.maDangKySeller}&amp;side=back" alt="CCCD mặt sau" class="doc-thumb">
                                        </a>
                                    </c:when>
                                    <c:otherwise>
                                        <a href="${pageContext.request.contextPath}/private-document?type=seller&amp;id=${sellerRequest.maDangKySeller}&amp;side=back" target="_blank" class="text-decoration-none">
                                            <img src="${pageContext.request.contextPath}/private-document?type=seller&amp;id=${sellerRequest.maDangKySeller}&amp;side=back" alt="CCCD mặt sau" class="doc-thumb">
                                        </a>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="col-lg-4">
                    <div class="panel-card sticky-card">
                        <div class="section-head">
                            <div>
                                <h4>Thao tác admin</h4>
                                <div class="muted-text">Chỉ còn các thao tác hợp lệ theo trạng thái hiện tại của hồ sơ.</div>
                            </div>
                        </div>

                        <div class="action-box">
                            <strong class="d-block mb-2">Quy tắc xử lý</strong>
                            <ul class="helper-list">
                                <li>Chỉ hồ sơ <strong>Chờ duyệt</strong> mới được phê duyệt hoặc từ chối.</li>
                                <li>Hồ sơ đã duyệt hoặc từ chối sẽ bị khóa thao tác này ở cả frontend và backend.</li>
                                <li>Xóa hồ sơ là xóa mềm; nếu hồ sơ đã duyệt thì quyền seller vẫn được giữ nguyên.</li>
                            </ul>
                        </div>

                        <c:choose>
                            <c:when test="${sellerRequest.choDuyet}">
                                <div class="alert alert-warning rounded-4">Hồ sơ đang chờ duyệt. Đây là thời điểm duy nhất admin có thể đưa ra quyết định phê duyệt hoặc từ chối.</div>

                                <form action="${pageContext.request.contextPath}/admin/seller-request-approve" method="post" class="mb-3">
<input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                                    <input type="hidden" name="id" value="${sellerRequest.maDangKySeller}">
                                    <button type="submit" class="btn btn-success w-100 fw-bold"
                                            onclick="return confirm('Phê duyệt hồ sơ seller này và cấp quyền đăng tin cho người dùng?')">
                                        <i class="fa-solid fa-circle-check me-2"></i>Phê duyệt hồ sơ
                                    </button>
                                </form>

                                <form action="${pageContext.request.contextPath}/admin/seller-request-reject" method="post" class="mb-3">
<input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                                    <input type="hidden" name="id" value="${sellerRequest.maDangKySeller}">
                                    <label class="form-label fw-bold">Lý do từ chối</label>
                                    <textarea class="form-control mb-3" name="lyDoTuChoi" rows="4"
                                              placeholder="Ví dụ: ảnh CCCD mờ, thông tin liên hệ chưa trùng khớp, cần bổ sung địa chỉ rõ hơn..."></textarea>
                                    <button type="submit" class="btn btn-warning w-100 fw-bold"
                                            onclick="return confirm('Từ chối hồ sơ seller này? Người dùng sẽ nhận được thông báo và có thể nộp lại hồ sơ mới.')">
                                        <i class="fa-solid fa-ban me-2"></i>Từ chối hồ sơ
                                    </button>
                                </form>
                            </c:when>
                            <c:when test="${sellerRequest.daDuyet}">
                                <div class="alert alert-success rounded-4">Hồ sơ đã được duyệt. Quyền seller đã cấp cho người dùng nên nút phê duyệt và từ chối không còn khả dụng.</div>
                            </c:when>
                            <c:when test="${sellerRequest.tuChoi}">
                                <div class="alert alert-danger rounded-4">Hồ sơ này đã bị từ chối trước đó. Admin không thể phê duyệt lại trực tiếp trên cùng bản ghi.</div>
                            </c:when>
                            <c:otherwise>
                                <div class="alert alert-secondary rounded-4">Hồ sơ này đã được lưu trữ hoặc xóa mềm. Không còn thao tác phê duyệt hoặc từ chối.</div>
                            </c:otherwise>
                        </c:choose>

                        <c:if test="${!sellerRequest.daXoa}">
                            <form action="${pageContext.request.contextPath}/admin/seller-request-delete" method="post">
<input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                                <input type="hidden" name="id" value="${sellerRequest.maDangKySeller}">
                                <button type="submit" class="btn btn-outline-danger w-100 fw-bold"
                                        onclick="return confirm('Xóa mềm hồ sơ seller này khỏi danh sách quản lý? Nếu hồ sơ đã duyệt thì quyền seller hiện tại vẫn được giữ nguyên.')">
                                    <i class="fa-solid fa-trash me-2"></i>Xóa hồ sơ
                                </button>
                            </form>
                        </c:if>
                    </div>
                </div>
            </div>
        </div>
    </body>
</html>
