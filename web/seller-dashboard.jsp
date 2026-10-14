<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="model.SellerDashboardItem" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%
    List<SellerDashboardItem> dashboardItems = (List<SellerDashboardItem>) request.getAttribute("sellerDashboardItems");
    int maxInterest = 1;
    if (dashboardItems != null) {
        for (SellerDashboardItem item : dashboardItems) {
            if (item.getDiemQuanTam() > maxInterest) {
                maxInterest = item.getDiemQuanTam();
            }
        }
    }
    request.setAttribute("maxInterest", maxInterest);
%>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <base href="${pageContext.request.contextPath}/">
        <title>Dashboard người bán - AutoPro</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
        <style>
            :root {
                --bg: #f4f6f8;
                --ink: #111827;
                --muted: #64748b;
                --line: #e5e7eb;
                --gold: #ffc400;
                --green: #12805c;
                --red: #dc3545;
                --orange: #f59e0b;
                --blue: #0d6efd;
            }

            body {
                background: var(--bg);
                color: var(--ink);
                font-family: Arial, sans-serif;
            }

            .page-shell {
                max-width: 1220px;
                margin: 28px auto;
                padding: 0 16px 42px;
            }

            .hero {
                background: radial-gradient(circle at top left, #fff2a8 0, #fff 32%, #ffffff 100%);
                border-radius: 24px;
                padding: 28px;
                box-shadow: 0 12px 36px rgba(15, 23, 42, 0.08);
                border: 1px solid rgba(255, 196, 0, 0.28);
            }

            .hero-kicker {
                display: inline-flex;
                align-items: center;
                gap: 8px;
                padding: 6px 12px;
                border-radius: 999px;
                background: #111827;
                color: white;
                font-size: 13px;
                font-weight: 700;
            }

            .metric-card {
                border: 1px solid var(--line);
                border-radius: 20px;
                padding: 18px;
                background: white;
                height: 100%;
                box-shadow: 0 8px 26px rgba(15, 23, 42, 0.05);
            }

            .metric-icon {
                width: 42px;
                height: 42px;
                border-radius: 14px;
                display: inline-flex;
                align-items: center;
                justify-content: center;
                background: #fff7d6;
                color: #a16207;
                margin-bottom: 14px;
            }

            .metric-value {
                font-size: 30px;
                font-weight: 800;
                letter-spacing: -0.03em;
            }

            .metric-label {
                color: var(--muted);
                font-size: 14px;
            }

            .panel {
                background: white;
                border: 1px solid var(--line);
                border-radius: 22px;
                box-shadow: 0 10px 28px rgba(15, 23, 42, 0.06);
            }

            .post-row {
                display: grid;
                grid-template-columns: 96px minmax(0, 1fr);
                gap: 16px;
                padding: 18px;
                border-bottom: 1px solid #eef2f7;
            }

            .post-row:last-child {
                border-bottom: 0;
            }

            .post-thumb {
                width: 96px;
                height: 72px;
                border-radius: 16px;
                object-fit: cover;
                background: #eef2f7;
            }

            .status-pill {
                display: inline-flex;
                align-items: center;
                padding: 6px 10px;
                border-radius: 999px;
                font-size: 12px;
                font-weight: 800;
            }

            .status-active {
                background: #dcfce7;
                color: #166534;
            }

            .status-pending {
                background: #fef3c7;
                color: #92400e;
            }

            .status-warning {
                background: #ffedd5;
                color: #9a3412;
            }

            .status-rejected {
                background: #fee2e2;
                color: #991b1b;
            }

            .status-sold {
                background: #dbeafe;
                color: #1d4ed8;
            }

            .status-trading {
                background: #dcfce7;
                color: #166534;
            }

            .status-muted {
                background: #e2e8f0;
                color: #334155;
            }

            .mini-stat {
                display: inline-flex;
                align-items: center;
                gap: 6px;
                padding: 8px 10px;
                border-radius: 12px;
                background: #f8fafc;
                color: #334155;
                font-size: 13px;
                font-weight: 700;
            }

            .interest-bar {
                height: 10px;
                background: #edf2f7;
                border-radius: 999px;
                overflow: hidden;
            }

            .interest-bar span {
                display: block;
                height: 100%;
                min-width: 6px;
                background: linear-gradient(90deg, var(--gold), #ff8a00);
                border-radius: inherit;
            }

            .status-line {
                display: flex;
                align-items: center;
                justify-content: space-between;
                padding: 12px 0;
                border-bottom: 1px solid #eef2f7;
            }

            .status-line:last-child {
                border-bottom: 0;
            }

            .empty-box {
                border: 1px dashed #cbd5e1;
                border-radius: 22px;
                padding: 44px 24px;
                background: #f8fafc;
            }

            @media (max-width: 768px) {
                .hero {
                    padding: 22px;
                }

                .post-row {
                    grid-template-columns: 1fr;
                }

                .post-thumb {
                    width: 100%;
                    height: 180px;
                }
            }
        </style>
    </head>
    <body>
        <div class="page-shell">
            <div class="hero mb-4">
                <div class="d-flex justify-content-between align-items-start flex-wrap gap-3">
                    <div>
                        <div class="hero-kicker mb-3">
                            <i class="fa-solid fa-chart-simple"></i>
                            Dashboard người bán
                        </div>
                        <h1 class="fw-bold mb-2">Theo dõi hiệu quả tin bán xe</h1>
                        <p class="text-muted mb-0">
                            Xem nhanh lượt xem, người nhắn tin, yêu cầu kết nối, lịch hẹn và trạng thái duyệt của từng bài đăng.
                        </p>
                    </div>
                    <div class="d-flex align-items-center gap-2 flex-wrap">
                        <a href="home" class="btn btn-outline-secondary">
                            <i class="fa-solid fa-house me-2"></i>Trang chủ
                        </a>
                        <a href="chat-list" class="btn btn-outline-primary">
                            <i class="fa-solid fa-comments me-2"></i>Tin nhắn
                        </a>
                        <a href="my-posts#sold-posts" class="btn btn-outline-success">
                            <i class="fa-solid fa-circle-check me-2"></i>Đã bán
                        </a>
                        <a href="my-posts" class="btn btn-outline-dark">
                            <i class="fa-solid fa-layer-group me-2"></i>Tin của tôi
                        </a>
                        <a href="lich-hen-cua-toi" class="btn btn-outline-primary">
                            <i class="fa-solid fa-calendar-check me-2"></i>Lịch hẹn
                        </a>
                        <a href="post-car" class="btn btn-warning fw-bold">
                            <i class="fa-solid fa-plus me-2"></i>Đăng tin mới
                        </a>
                        <jsp:include page="/WEB-INF/includes/account-menu.jsp" />
                    </div>
                </div>
            </div>

            <div class="row g-3 mb-4">
                <div class="col-md-6 col-xl-2">
                    <div class="metric-card">
                        <div class="metric-icon"><i class="fa-solid fa-car-side"></i></div>
                        <div class="metric-value">${sellerDashboardSummary.totalPosts}</div>
                        <div class="metric-label">Tổng tin đăng</div>
                    </div>
                </div>
                <div class="col-md-6 col-xl-2">
                    <div class="metric-card">
                        <div class="metric-icon"><i class="fa-solid fa-eye"></i></div>
                        <div class="metric-value">${sellerDashboardSummary.totalViews}</div>
                        <div class="metric-label">Lượt xem</div>
                    </div>
                </div>
                <div class="col-md-6 col-xl-2">
                    <div class="metric-card">
                        <div class="metric-icon"><i class="fa-solid fa-comments"></i></div>
                        <div class="metric-value">${sellerDashboardSummary.totalMessengers}</div>
                        <div class="metric-label">Người nhắn tin</div>
                    </div>
                </div>
                <div class="col-md-6 col-xl-2">
                    <div class="metric-card">
                        <div class="metric-icon"><i class="fa-solid fa-handshake"></i></div>
                        <div class="metric-value">${sellerDashboardSummary.totalRequests}</div>
                        <div class="metric-label">Yêu cầu kết nối</div>
                    </div>
                </div>
                <div class="col-md-6 col-xl-2">
                    <div class="metric-card">
                        <div class="metric-icon"><i class="fa-solid fa-calendar-check"></i></div>
                        <div class="metric-value">${sellerDashboardSummary.totalAppointments}</div>
                        <div class="metric-label">Lịch hẹn xem xe</div>
                    </div>
                </div>
                <div class="col-md-6 col-xl-2">
                    <div class="metric-card">
                        <div class="metric-icon"><i class="fa-solid fa-circle-check"></i></div>
                        <div class="metric-value">${sellerDashboardSummary.completedTransactions}</div>
                        <div class="metric-label">Giao dịch hoàn tất</div>
                    </div>
                </div>
            </div>

            <div class="row g-4">
                <div class="col-lg-8">
                    <div class="panel overflow-hidden">
                        <div class="p-4 border-bottom">
                            <div class="d-flex justify-content-between align-items-center flex-wrap gap-2">
                                <div>
                                    <h4 class="fw-bold mb-1">Hiệu quả từng tin</h4>
                                    <div class="text-muted small">Điểm quan tâm được tính từ lượt xem, chat, yêu cầu kết nối và lịch hẹn.</div>
                                </div>
                                <a href="my-posts" class="btn btn-sm btn-outline-secondary">Quản lý tin</a>
                            </div>
                        </div>

                        <c:choose>
                            <c:when test="${not empty sellerDashboardItems}">
                                <c:forEach items="${sellerDashboardItems}" var="post">
                                    <div class="post-row">
                                        <c:choose>
                                            <c:when test="${not empty post.anhDaiDien}">
                                                <img class="post-thumb" src="${post.anhDaiDien}" alt="${post.tenXe}" data-car-name="${post.tenXe}" data-context-path="${pageContext.request.contextPath}" loading="lazy" onerror="useAutoProCarFallback(this)">
                                            </c:when>
                                            <c:otherwise>
                                                <img class="post-thumb" src="${pageContext.request.contextPath}/uploads/cars/car-placeholder.svg" alt="AutoPro" data-context-path="${pageContext.request.contextPath}" loading="lazy" onerror="useAutoProCarFallback(this)">
                                            </c:otherwise>
                                        </c:choose>

                                        <div class="min-w-0">
                                            <div class="d-flex justify-content-between align-items-start flex-wrap gap-2 mb-2">
                                                <div>
                                                    <div class="d-flex align-items-center gap-2 flex-wrap mb-1">
                                                        <span class="status-pill ${post.trangThaiCssClass}">${post.trangThaiHienThi}</span>
                                                        <span class="small text-muted">#${post.maBaiDang}</span>
                                                        <span class="small text-muted">
                                                            <fmt:formatDate value="${post.ngayDang}" pattern="dd/MM/yyyy HH:mm" />
                                                        </span>
                                                    </div>
                                                    <h5 class="fw-bold mb-1">${empty post.tieuDe ? post.tenXe : post.tieuDe}</h5>
                                                    <div class="text-muted">
                                                        ${empty post.tenXe ? 'Chưa cập nhật tên xe' : post.tenXe}
                                                        <span class="mx-2">•</span>
                                                        <fmt:formatNumber value="${post.giaBan}" type="number" maxFractionDigits="0"/> đ
                                                    </div>
                                                </div>
                                                <div class="d-flex gap-2">
                                                    <a class="btn btn-sm btn-outline-primary" href="detail?id=${post.maXe}">
                                                        <i class="fa-solid fa-eye me-1"></i>Xem
                                                    </a>
                                                    <a class="btn btn-sm btn-outline-dark" href="edit-post?id=${post.maBaiDang}">
                                                        <i class="fa-solid fa-pen-to-square me-1"></i>Sửa
                                                    </a>
                                                </div>
                                            </div>

                                            <div class="d-flex gap-2 flex-wrap my-3">
                                                <span class="mini-stat"><i class="fa-solid fa-eye"></i>${post.luotXem} lượt xem</span>
                                                <span class="mini-stat"><i class="fa-solid fa-comments"></i>${post.soNguoiNhanTin} người chat</span>
                                                <span class="mini-stat"><i class="fa-solid fa-handshake"></i>${post.soYeuCau} yêu cầu</span>
                                                <span class="mini-stat"><i class="fa-solid fa-calendar-check"></i>${post.soLichHen} lịch hẹn</span>
                                            </div>

                                            <div class="interest-bar mb-2">
                                                <span style="width: ${post.diemQuanTam * 100 / maxInterest}%;"></span>
                                            </div>
                                            <div class="d-flex justify-content-between small text-muted">
                                                <span>Điểm quan tâm: ${post.diemQuanTam}</span>
                                                <c:if test="${not empty post.ngayYeuCauMoiNhat}">
                                                    <span>Yêu cầu mới nhất: <fmt:formatDate value="${post.ngayYeuCauMoiNhat}" pattern="dd/MM/yyyy HH:mm" /></span>
                                                </c:if>
                                            </div>

                                            <c:if test="${not empty post.ghiChuAdmin}">
                                                <div class="alert alert-warning mt-3 mb-0">
                                                    <strong>Ghi chú admin:</strong> ${post.ghiChuAdmin}
                                                </div>
                                            </c:if>
                                        </div>
                                    </div>
                                </c:forEach>
                            </c:when>
                            <c:otherwise>
                                <div class="p-4">
                                    <div class="empty-box text-center">
                                        <i class="fa-solid fa-car-rear text-secondary mb-3" style="font-size: 54px;"></i>
                                        <h4 class="fw-bold">Bạn chưa có tin bán xe nào</h4>
                                        <p class="text-muted mb-4">Khi bạn đăng tin, dashboard sẽ hiển thị hiệu quả từng bài để bạn biết khách đang quan tâm tới xe nào.</p>
                                        <a href="post-car" class="btn btn-warning fw-bold">Đăng tin bán xe đầu tiên</a>
                                    </div>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>

                <div class="col-lg-4">
                    <div class="panel p-4 mb-4">
                        <h5 class="fw-bold mb-3">Trạng thái duyệt</h5>
                        <div class="status-line">
                            <span><i class="fa-solid fa-circle-check text-success me-2"></i>Đang hiển thị</span>
                            <strong>${sellerDashboardSummary.activePosts}</strong>
                        </div>
                        <div class="status-line">
                            <span><i class="fa-solid fa-handshake text-success me-2"></i>Đang giao dịch</span>
                            <strong>${sellerDashboardSummary.tradingPosts}</strong>
                        </div>
                        <div class="status-line">
                            <span><i class="fa-solid fa-clock text-warning me-2"></i>Chờ duyệt</span>
                            <strong>${sellerDashboardSummary.pendingPosts}</strong>
                        </div>
                        <div class="status-line">
                            <span><i class="fa-solid fa-triangle-exclamation text-warning me-2"></i>Cảnh báo</span>
                            <strong>${sellerDashboardSummary.warningPosts}</strong>
                        </div>
                        <div class="status-line">
                            <span><i class="fa-solid fa-ban text-danger me-2"></i>Từ chối</span>
                            <strong>${sellerDashboardSummary.rejectedPosts}</strong>
                        </div>
                        <div class="status-line">
                            <span><i class="fa-solid fa-award text-primary me-2"></i>Đã bán</span>
                            <strong>${sellerDashboardSummary.soldPosts}</strong>
                        </div>
                    </div>

                    <div class="panel p-4">
                        <h5 class="fw-bold mb-3">Việc cần theo dõi</h5>
                        <div class="alert alert-light border mb-3">
                            <strong>${sellerDashboardSummary.totalRequests}</strong> yêu cầu kết nối đang là nguồn chính để chốt lịch xem xe. Kiểm tra mục lịch hẹn nếu có yêu cầu mới.
                        </div>
                        <div class="alert alert-light border mb-3">
                            <strong>${sellerDashboardSummary.totalMessengers}</strong> người đã mở hội thoại với bạn. Phản hồi nhanh giúp tăng khả năng chốt giao dịch ngoài đời.
                        </div>
                        <div class="alert alert-warning mb-0">
                            AutoPro chỉ ghi nhận trạng thái kết nối và giao dịch. Việc xem xe, ký hợp đồng và thanh toán vẫn thực hiện trực tiếp giữa hai bên.
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <script src="${pageContext.request.contextPath}/assets/js/autopro-image-fallback.js"></script>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
        <jsp:include page="/WEB-INF/includes/site-footer.jsp" />

    </body>
</html>
