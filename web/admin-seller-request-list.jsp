<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="model.DangKySeller" %>
<%
    List<DangKySeller> sellerRequests = (List<DangKySeller>) request.getAttribute("sellerRequests");
    String selectedStatus = request.getAttribute("selectedStatus") == null ? "" : String.valueOf(request.getAttribute("selectedStatus"));
    String keyword = request.getAttribute("keyword") == null ? "" : String.valueOf(request.getAttribute("keyword"));

    int totalCount = 0;
    int pendingCount = 0;
    int approvedCount = 0;
    int rejectedCount = 0;
    int deletedCount = 0;

    if (sellerRequests != null) {
        totalCount = sellerRequests.size();
        for (DangKySeller item : sellerRequests) {
            if (item == null || item.getTrangThai() == null) {
                continue;
            }
            switch (item.getTrangThai()) {
                case "CHO_DUYET":
                    pendingCount++;
                    break;
                case "DA_DUYET":
                    approvedCount++;
                    break;
                case "TU_CHOI":
                    rejectedCount++;
                    break;
                case "DA_XOA":
                    deletedCount++;
                    break;
                default:
                    break;
            }
        }
    }
%>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Quản lý hồ sơ seller - AutoPro Admin</title>
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
                --ui-gold: #f5b700;
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

            .summary-grid {
                display: grid;
                grid-template-columns: repeat(5, minmax(0, 1fr));
                gap: 14px;
                margin-top: 24px;
            }

            .summary-card {
                background: rgba(255, 255, 255, 0.92);
                border: 1px solid rgba(226, 232, 240, 0.96);
                border-radius: 22px;
                padding: 18px;
                min-height: 118px;
            }

            .summary-card .icon {
                width: 44px;
                height: 44px;
                border-radius: 14px;
                display: inline-flex;
                align-items: center;
                justify-content: center;
                background: #fff4cf;
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

            .filter-grid {
                display: grid;
                grid-template-columns: 2fr 1fr auto;
                gap: 14px;
                align-items: end;
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
            }

            .quick-filters {
                display: flex;
                flex-wrap: wrap;
                gap: 10px;
                margin-top: 14px;
            }

            .quick-pill {
                display: inline-flex;
                align-items: center;
                gap: 8px;
                padding: 10px 14px;
                border-radius: 999px;
                border: 1px solid #d7deea;
                background: #fbfcff;
                color: #243447;
                text-decoration: none;
                font-size: 13px;
                font-weight: 700;
            }

            .quick-pill.active {
                border-color: #0f172a;
                background: #0f172a;
                color: #ffffff;
            }

            .status-pill {
                display: inline-flex;
                align-items: center;
                gap: 8px;
                padding: 8px 13px;
                border-radius: 999px;
                font-size: 12px;
                font-weight: 800;
            }

            .status-pending {
                background: #fff2cf;
                color: #8a6200;
            }

            .status-approved {
                background: #dff6e8;
                color: #166534;
            }

            .status-rejected {
                background: #ffe3e1;
                color: #b42318;
            }

            .status-deleted,
            .status-default {
                background: #e8edf4;
                color: #475569;
            }

            .table-shell {
                overflow: hidden;
                border-radius: 24px;
                border: 1px solid var(--ui-line);
            }

            .table {
                margin: 0;
            }

            .table thead th {
                border: none;
                background: #0f172a;
                color: #ffffff;
                font-size: 12px;
                text-transform: uppercase;
                letter-spacing: 0.04em;
                padding: 16px 14px;
            }

            .table tbody td {
                padding: 18px 14px;
                vertical-align: middle;
                border-color: #edf1f6;
            }

            .table tbody tr.highlight-row {
                background: #fffdf6;
            }

            .user-badge {
                display: inline-flex;
                align-items: center;
                gap: 10px;
            }

            .user-avatar {
                width: 42px;
                height: 42px;
                border-radius: 14px;
                display: inline-flex;
                align-items: center;
                justify-content: center;
                background: #0f172a;
                color: #ffffff;
                font-weight: 800;
            }

            .empty-state {
                text-align: center;
                padding: 50px 24px;
                color: var(--ui-muted);
            }

            @media (max-width: 1199.98px) {
                .summary-grid {
                    grid-template-columns: repeat(3, minmax(0, 1fr));
                }
            }

            @media (max-width: 991.98px) {
                .filter-grid {
                    grid-template-columns: 1fr;
                }
            }

            @media (max-width: 767.98px) {
                .summary-grid {
                    grid-template-columns: repeat(2, minmax(0, 1fr));
                }
            }

            @media (max-width: 575.98px) {
                .summary-grid {
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
                        <h1 class="hero-title">Quản lý hồ sơ đăng ký người bán</h1>
                        <p class="hero-text">
                            Theo dõi hồ sơ seller chờ duyệt, lọc nhanh theo trạng thái và mở từng hồ sơ để phê duyệt, từ chối hoặc lưu trữ.
                        </p>
                    </div>
                    <div class="top-actions">
                        <a href="${pageContext.request.contextPath}/admin/dashboard" class="btn btn-outline-secondary">
                            <i class="fa-solid fa-arrow-left me-2"></i>Về Dashboard
                        </a>
                        <a href="${pageContext.request.contextPath}/admin/users" class="btn btn-outline-dark">Quản lý người dùng</a>
                    </div>
                </div>

                <div class="summary-grid">
                    <div class="summary-card">
                        <div class="icon"><i class="fa-solid fa-folder-open"></i></div>
                        <div class="value"><%= totalCount %></div>
                        <div class="label">Tổng hồ sơ trong danh sách</div>
                        <div class="meta">Số lượng hiển thị theo bộ lọc hiện tại.</div>
                    </div>
                    <div class="summary-card">
                        <div class="icon"><i class="fa-solid fa-hourglass-half"></i></div>
                        <div class="value"><%= pendingCount %></div>
                        <div class="label">Chờ duyệt</div>
                        <div class="meta">Các hồ sơ cần admin xử lý trước.</div>
                    </div>
                    <div class="summary-card">
                        <div class="icon"><i class="fa-solid fa-circle-check"></i></div>
                        <div class="value"><%= approvedCount %></div>
                        <div class="label">Đã duyệt</div>
                        <div class="meta">Tài khoản đã được mở quyền seller.</div>
                    </div>
                    <div class="summary-card">
                        <div class="icon"><i class="fa-solid fa-ban"></i></div>
                        <div class="value"><%= rejectedCount %></div>
                        <div class="label">Từ chối</div>
                        <div class="meta">Hồ sơ chưa đạt yêu cầu xác minh.</div>
                    </div>
                    <div class="summary-card">
                        <div class="icon"><i class="fa-solid fa-box-archive"></i></div>
                        <div class="value"><%= deletedCount %></div>
                        <div class="label">Đã xóa mềm</div>
                        <div class="meta">Hồ sơ đã được lưu trữ khỏi luồng xử lý.</div>
                    </div>
                </div>
            </div>

            <% if ("deleted".equals(request.getParameter("result"))) { %>
            <div class="alert alert-success rounded-4 border-0 shadow-sm">Hồ sơ seller đã được lưu trữ hoặc xóa mềm khỏi danh sách xử lý.</div>
            <% } else if ("invalid".equals(request.getParameter("result")) || "invalid-state".equals(request.getParameter("result"))) { %>
            <div class="alert alert-danger rounded-4 border-0 shadow-sm">Yêu cầu không hợp lệ hoặc hồ sơ đã được xử lý trước đó.</div>
            <% } else if ("not-found".equals(request.getParameter("result"))) { %>
            <div class="alert alert-warning rounded-4 border-0 shadow-sm">Không tìm thấy hồ sơ seller cần mở.</div>
            <% } %>

            <div class="panel-card mb-4">
                <div class="d-flex justify-content-between align-items-start flex-wrap gap-3 mb-3">
                    <div>
                        <h3 class="mb-1 fw-bold">Bộ lọc và tìm kiếm</h3>
                        <div class="muted-text">Tìm nhanh theo tên, email, số điện thoại hoặc CCCD rồi mở thẳng hồ sơ cần xử lý.</div>
                    </div>
                    <div class="muted-text">Trạng thái đang chọn: <strong><%= selectedStatus.isEmpty() ? "Tất cả hồ sơ đang quản lý" : selectedStatus %></strong></div>
                </div>

                <form action="${pageContext.request.contextPath}/admin/seller-requests" method="get" class="filter-grid">
                    <div>
                        <label class="form-label">Từ khóa</label>
                        <input type="text" class="form-control" name="keyword" value="<%= keyword %>" placeholder="Tên, email, CCCD, số điện thoại">
                    </div>
                    <div>
                        <label class="form-label">Trạng thái</label>
                        <select class="form-select" name="status">
                            <option value="" <%= selectedStatus.isEmpty() ? "selected" : "" %>>Tất cả hồ sơ đang quản lý</option>
                            <option value="CHO_DUYET" <%= "CHO_DUYET".equals(selectedStatus) ? "selected" : "" %>>Chờ duyệt</option>
                            <option value="DA_DUYET" <%= "DA_DUYET".equals(selectedStatus) ? "selected" : "" %>>Đã duyệt</option>
                            <option value="TU_CHOI" <%= "TU_CHOI".equals(selectedStatus) ? "selected" : "" %>>Từ chối</option>
                            <option value="DA_XOA" <%= "DA_XOA".equals(selectedStatus) ? "selected" : "" %>>Đã xóa</option>
                        </select>
                    </div>
                    <div class="d-flex gap-2 flex-wrap">
                        <button type="submit" class="btn btn-primary px-4">
                            <i class="fa-solid fa-magnifying-glass me-2"></i>Tìm kiếm
                        </button>
                        <a href="${pageContext.request.contextPath}/admin/seller-requests" class="btn btn-outline-secondary px-4">Reset</a>
                    </div>
                </form>

                <div class="quick-filters">
                    <a href="${pageContext.request.contextPath}/admin/seller-requests" class="quick-pill <%= selectedStatus.isEmpty() ? "active" : "" %>">
                        <i class="fa-solid fa-layer-group"></i>Tất cả
                    </a>
                    <a href="${pageContext.request.contextPath}/admin/seller-requests?status=CHO_DUYET" class="quick-pill <%= "CHO_DUYET".equals(selectedStatus) ? "active" : "" %>">
                        <i class="fa-solid fa-hourglass-half"></i>Chờ duyệt
                    </a>
                    <a href="${pageContext.request.contextPath}/admin/seller-requests?status=DA_DUYET" class="quick-pill <%= "DA_DUYET".equals(selectedStatus) ? "active" : "" %>">
                        <i class="fa-solid fa-circle-check"></i>Đã duyệt
                    </a>
                    <a href="${pageContext.request.contextPath}/admin/seller-requests?status=TU_CHOI" class="quick-pill <%= "TU_CHOI".equals(selectedStatus) ? "active" : "" %>">
                        <i class="fa-solid fa-ban"></i>Từ chối
                    </a>
                    <a href="${pageContext.request.contextPath}/admin/seller-requests?status=DA_XOA" class="quick-pill <%= "DA_XOA".equals(selectedStatus) ? "active" : "" %>">
                        <i class="fa-solid fa-box-archive"></i>Đã xóa
                    </a>
                </div>
            </div>

            <div class="panel-card">
                <div class="d-flex justify-content-between align-items-center flex-wrap gap-3 mb-3">
                    <div>
                        <h3 class="mb-1 fw-bold">Danh sách hồ sơ seller</h3>
                        <div class="muted-text">Các hồ sơ chờ duyệt được đưa lên đầu để admin xử lý nhanh hơn.</div>
                    </div>
                    <span class="status-pill status-default">
                        <i class="fa-solid fa-table-list"></i><%= totalCount %> hồ sơ
                    </span>
                </div>

                <div class="table-shell">
                    <div class="table-responsive">
                        <table class="table table-hover align-middle">
                            <thead>
                                <tr>
                                    <th>Mã hồ sơ</th>
                                    <th>Người đăng ký</th>
                                    <th>Liên hệ</th>
                                    <th>CCCD</th>
                                    <th>Khu vực</th>
                                    <th>Trạng thái</th>
                                    <th>Ngày tạo</th>
                                    <th>Hành động</th>
                                </tr>
                            </thead>
                            <tbody>
                                <% if (sellerRequests != null && !sellerRequests.isEmpty()) {
                                    for (DangKySeller requestItem : sellerRequests) {
                                        String fullName = requestItem.getHoTen() == null ? "?" : requestItem.getHoTen().trim();
                                        String initial = fullName.isEmpty() ? "?" : fullName.substring(0, 1).toUpperCase();
                                %>
                                <tr class="<%= requestItem.isChoDuyet() ? "highlight-row" : "" %>">
                                    <td class="fw-bold">#<%= requestItem.getMaDangKySeller() %></td>
                                    <td>
                                        <div class="user-badge">
                                            <span class="user-avatar"><%= initial %></span>
                                            <div>
                                                <div class="fw-bold"><%= requestItem.getHoTen() %></div>
                                                <div class="small text-muted">User ID: <%= requestItem.getMaNguoiDung() %></div>
                                            </div>
                                        </div>
                                    </td>
                                    <td>
                                        <div><%= requestItem.getEmail() %></div>
                                        <div class="small text-muted"><%= requestItem.getSoDienThoai() %></div>
                                    </td>
                                    <td><%= requestItem.getSoCCCD() %></td>
                                    <td>
                                        <div><%= requestItem.getTenTinh() == null ? "-" : requestItem.getTenTinh() %></div>
                                        <div class="small text-muted"><%= requestItem.getMien() == null ? "Chưa rõ miền" : requestItem.getMien() %></div>
                                    </td>
                                    <td>
                                        <span class="status-pill <%= requestItem.getTrangThaiCssClass() %>">
                                            <i class="fa-solid fa-circle"></i><%= requestItem.getTrangThaiHienThi() %>
                                        </span>
                                    </td>
                                    <td><%= requestItem.getNgayTao() %></td>
                                    <td>
                                        <a href="${pageContext.request.contextPath}/admin/seller-request-detail?id=<%= requestItem.getMaDangKySeller() %>" class="btn btn-sm btn-outline-primary">
                                            <i class="fa-solid fa-eye me-1"></i>Xem chi tiết
                                        </a>
                                    </td>
                                </tr>
                                <% } } else { %>
                                <tr>
                                    <td colspan="8">
                                        <div class="empty-state">
                                            <i class="fa-regular fa-folder-open fa-3x mb-3 d-block"></i>
                                            <div class="fw-bold mb-2">Không có hồ sơ seller phù hợp</div>
                                            <div>Hãy thử đổi trạng thái lọc hoặc bỏ bớt từ khóa để xem thêm kết quả.</div>
                                        </div>
                                    </td>
                                </tr>
                                <% } %>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>
    </body>
</html>
