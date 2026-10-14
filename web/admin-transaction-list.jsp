<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
<%@page import="model.GiaoDichXe"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%
    List<GiaoDichXe> danhSachGiaoDich = (List<GiaoDichXe>) request.getAttribute("danhSachGiaoDich");
    String selectedStatus = (String) request.getAttribute("selectedStatus");
%>
<%!
    private String formatDate(java.sql.Timestamp timestamp) {
        if (timestamp == null) {
            return "-";
        }
        return new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm").format(timestamp);
    }

    private String getStatusLabel(String status) {
        if (status == null) {
            return "Chưa cập nhật";
        }
        switch (status) {
            case "MOI_TAO":
                return "Chờ người bán tiếp nhận";
            case "CHO_XAC_NHAN_HAI_BEN":
                return "Chờ cả hai bên xác nhận";
            case "CHO_XAC_NHAN_CON_LAI":
                return "Chờ bên còn lại xác nhận";
            case "HOAN_TAT":
                return "Hoàn tất";
            case "HUY":
                return "Đã hủy";
            case "TRANH_CHAP":
                return "Tranh chấp";
            default:
                return status;
        }
    }

    private String getStatusClass(String status) {
        if ("HOAN_TAT".equals(status)) {
            return "success";
        }
        if ("HUY".equals(status)) {
            return "danger";
        }
        if ("TRANH_CHAP".equals(status)) {
            return "warning";
        }
        return "secondary";
    }
%>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Quản lý giao dịch xe - Admin AutoPro</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
        <style>
            body {
                background: #f5f6f8;
                font-family: Arial, sans-serif;
            }

            .page-wrap {
                max-width: 1240px;
                margin: 32px auto;
            }

            .panel {
                background: #fff;
                border-radius: 18px;
                padding: 24px;
                box-shadow: 0 8px 28px rgba(0, 0, 0, 0.08);
                margin-bottom: 22px;
            }
        </style>
    </head>
    <body>
        <div class="container page-wrap">
            <div class="panel">
                <div class="d-flex justify-content-between align-items-center flex-wrap gap-3">
                    <div>
                        <div class="d-flex align-items-center gap-2 mb-2">
                            <a href="${pageContext.request.contextPath}/admin/dashboard" class="text-decoration-none d-inline-flex align-items-center">
                                <jsp:include page="/WEB-INF/includes/site-logo.jsp">
                                    <jsp:param name="logoHeight" value="42px" />
                                </jsp:include>
                            </a>
                            <span class="badge bg-warning text-dark">Giao dịch xe</span>
                        </div>
                        <h2 class="mb-1">Danh sách giao dịch mua bán xe</h2>
                        <p class="text-muted mb-0">Theo dõi tình trạng xác nhận của người mua, người bán và can thiệp xử lý thủ công khi có mâu thuẫn.</p>
                    </div>
                    <div class="d-flex align-items-center gap-2">
                        <a href="${pageContext.request.contextPath}/admin/dashboard" class="btn btn-outline-secondary">
                            <i class="fa-solid fa-arrow-left me-2"></i>Về dashboard
                        </a>
                    </div>
                </div>
            </div>

            <c:if test="${not empty adminTransactionSuccess}">
                <div class="alert alert-success">${adminTransactionSuccess}</div>
            </c:if>
            <c:if test="${not empty adminTransactionError}">
                <div class="alert alert-danger">${adminTransactionError}</div>
            </c:if>

            <div class="panel">
                <form action="${pageContext.request.contextPath}/admin/giao-dich" method="get" class="row g-3 align-items-end mb-3">
                    <div class="col-md-4">
                        <label class="form-label fw-semibold">Lọc theo trạng thái</label>
                        <select name="status" class="form-select">
                            <option value="" <%= selectedStatus == null || selectedStatus.isBlank() ? "selected" : "" %>>Tất cả trạng thái</option>
                            <option value="MOI_TAO" <%= "MOI_TAO".equals(selectedStatus) ? "selected" : "" %>>Chờ người bán tiếp nhận</option>
                            <option value="CHO_XAC_NHAN_HAI_BEN" <%= "CHO_XAC_NHAN_HAI_BEN".equals(selectedStatus) ? "selected" : "" %>>Chờ cả hai bên xác nhận</option>
                            <option value="CHO_XAC_NHAN_CON_LAI" <%= "CHO_XAC_NHAN_CON_LAI".equals(selectedStatus) ? "selected" : "" %>>Chờ bên còn lại xác nhận</option>
                            <option value="HOAN_TAT" <%= "HOAN_TAT".equals(selectedStatus) ? "selected" : "" %>>Hoàn tất</option>
                            <option value="HUY" <%= "HUY".equals(selectedStatus) ? "selected" : "" %>>Đã hủy</option>
                            <option value="TRANH_CHAP" <%= "TRANH_CHAP".equals(selectedStatus) ? "selected" : "" %>>Tranh chấp</option>
                        </select>
                    </div>
                    <div class="col-md-4">
                        <button type="submit" class="btn btn-warning fw-semibold">
                            <i class="fa-solid fa-filter me-2"></i>Lọc danh sách
                        </button>
                        <a href="${pageContext.request.contextPath}/admin/giao-dich" class="btn btn-outline-secondary ms-2">Xóa lọc</a>
                    </div>
                </form>

                <div class="table-responsive">
                    <table class="table table-hover align-middle">
                        <thead>
                            <tr>
                                <th>Mã giao dịch</th>
                                <th>Xe</th>
                                <th>Người mua</th>
                                <th>Người bán</th>
                                <th>Trạng thái</th>
                                <th>Xác nhận</th>
                                <th>Ngày tạo</th>
                                <th class="text-end">Chi tiết</th>
                            </tr>
                        </thead>
                        <tbody>
                            <% if (danhSachGiaoDich != null && !danhSachGiaoDich.isEmpty()) { %>
                                <% for (GiaoDichXe item : danhSachGiaoDich) { %>
                                <tr>
                                    <td><strong>#<%= item.getMaGiaoDich() %></strong></td>
                                    <td>
                                        <div class="fw-semibold"><%= item.getTenXe() != null ? item.getTenXe() : ("Xe #" + item.getMaXe()) %></div>
                                        <div class="small text-muted">Yêu cầu #<%= item.getMaYeuCau() %></div>
                                    </td>
                                    <td>
                                        <div><%= item.getTenNguoiMua() != null ? item.getTenNguoiMua() : ("ID " + item.getMaNguoiMua()) %></div>
                                        <div class="small text-muted"><%= item.getEmailNguoiMua() != null ? item.getEmailNguoiMua() : "" %></div>
                                    </td>
                                    <td>
                                        <div><%= item.getTenNguoiBan() != null ? item.getTenNguoiBan() : ("ID " + item.getMaNguoiBan()) %></div>
                                        <div class="small text-muted"><%= item.getEmailNguoiBan() != null ? item.getEmailNguoiBan() : "" %></div>
                                    </td>
                                    <td><span class="badge text-bg-<%= getStatusClass(item.getTrangThai()) %>"><%= getStatusLabel(item.getTrangThai()) %></span></td>
                                    <td>
                                        <div class="small">
                                            <div><i class="fa-solid <%= item.isXacNhanNguoiBan() ? "fa-circle-check text-success" : "fa-clock text-muted" %> me-2"></i>Người bán</div>
                                            <div><i class="fa-solid <%= item.isXacNhanNguoiMua() ? "fa-circle-check text-success" : "fa-clock text-muted" %> me-2"></i>Người mua</div>
                                        </div>
                                    </td>
                                    <td><%= formatDate(item.getNgayTao()) %></td>
                                    <td class="text-end">
                                        <a href="${pageContext.request.contextPath}/admin/giao-dich?id=<%= item.getMaGiaoDich() %>" class="btn btn-sm btn-outline-dark">
                                            <i class="fa-solid fa-eye me-1"></i>Xem
                                        </a>
                                    </td>
                                </tr>
                                <% } %>
                            <% } else { %>
                            <tr>
                                <td colspan="8" class="text-center py-5 text-muted">
                                    <i class="fa-solid fa-file-circle-xmark fa-2x mb-3 d-block"></i>
                                    Chưa có giao dịch nào phù hợp với bộ lọc hiện tại.
                                </td>
                            </tr>
                            <% } %>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>
