<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="model.GiaoDichXe"%>
<%@page import="util.HtmlUtil"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%!
    private String h(Object value) {
        return HtmlUtil.escape(value);
    }

    private String formatDate(java.sql.Timestamp timestamp) {
        if (timestamp == null) {
            return "-";
        }
        return new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm").format(timestamp);
    }

    private String formatMoney(Double value) {
        if (value == null) {
            return "Chưa cập nhật";
        }
        return String.format("%,.0f đ", value).replace(",", ".");
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

    private boolean canSellerConfirm(GiaoDichXe giaoDich) {
        return giaoDich != null
                && !giaoDich.isXacNhanNguoiBan()
                && !"HOAN_TAT".equals(giaoDich.getTrangThai())
                && !"HUY".equals(giaoDich.getTrangThai())
                && !"TRANH_CHAP".equals(giaoDich.getTrangThai())
                && !"MOI_TAO".equals(giaoDich.getTrangThai());
    }

    private boolean canCancel(GiaoDichXe giaoDich) {
        return giaoDich != null
                && !"HOAN_TAT".equals(giaoDich.getTrangThai())
                && !"HUY".equals(giaoDich.getTrangThai())
                && !"TRANH_CHAP".equals(giaoDich.getTrangThai())
                && !giaoDich.isXacNhanNguoiMua() && !giaoDich.isXacNhanNguoiBan();
    }
%>
<%
    GiaoDichXe giaoDich = (GiaoDichXe) request.getAttribute("giaoDich");
%>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Chi tiết giao dịch - Người bán - AutoPro</title>
        <base href="${pageContext.request.contextPath}/">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
        <style>
            body {
                background: #f5f6f8;
                font-family: Arial, sans-serif;
            }

            .page-wrap {
                max-width: 1180px;
                margin: 32px auto;
            }

            .panel {
                background: #fff;
                border-radius: 18px;
                padding: 24px;
                box-shadow: 0 8px 28px rgba(0, 0, 0, 0.08);
                margin-bottom: 22px;
            }

            .info-grid {
                display: grid;
                grid-template-columns: repeat(2, minmax(0, 1fr));
                gap: 14px 18px;
            }

            .info-item {
                background: #f8fafc;
                border-radius: 14px;
                padding: 14px 16px;
            }

            .info-label {
                display: block;
                color: #64748b;
                font-size: 13px;
                margin-bottom: 6px;
            }

            .info-value {
                font-weight: 700;
                color: #0f172a;
            }

            .note-box {
                background: #f8fafc;
                border-radius: 14px;
                padding: 14px 16px;
                min-height: 92px;
                white-space: pre-wrap;
            }

            .tx-progress {
                display: grid;
                grid-template-columns: 1fr 42px 1fr 42px 1fr 42px 1fr;
                align-items: center;
                gap: 10px;
                margin: 18px 0 22px;
            }

            .tx-step {
                background: #f8fafc;
                border: 1px solid #e2e8f0;
                border-radius: 14px;
                padding: 12px;
                text-align: center;
                font-size: 13px;
                color: #64748b;
                font-weight: 700;
            }

            .tx-step.done {
                background: #dcfce7;
                border-color: #86efac;
                color: #166534;
            }

            .tx-line {
                height: 4px;
                border-radius: 999px;
                background: #e2e8f0;
            }

            .tx-line.done {
                background: #22c55e;
            }

            @media (max-width: 768px) {
                .info-grid {
                    grid-template-columns: 1fr;
                }

                .tx-progress {
                    grid-template-columns: 1fr;
                }

                .tx-line {
                    display: none;
                }
            }
        </style>
    </head>
    <body>
        <div class="container page-wrap">
            <div class="panel">
                <div class="d-flex justify-content-between align-items-center flex-wrap gap-3">
                    <div>
                        <div class="d-flex align-items-center gap-2 mb-2">
                            <a href="${pageContext.request.contextPath}/home" class="text-decoration-none d-inline-flex align-items-center">
                                <jsp:include page="/WEB-INF/includes/site-logo.jsp">
                                    <jsp:param name="logoHeight" value="46px" />
                                </jsp:include>
                            </a>
                            <span class="badge bg-warning text-dark">Giao dịch người bán</span>
                        </div>
                        <h2 class="mb-1">Chi tiết giao dịch #<%= giaoDich != null ? giaoDich.getMaGiaoDich() : "" %></h2>
                        <p class="text-muted mb-0">Ghi nhận bàn giao xe ngoài thực tế, theo dõi xác nhận thanh toán và nhận xe của người mua.</p>
                    </div>
                    <div class="d-flex align-items-center gap-2">
                        <a href="${pageContext.request.contextPath}/lich-hen-cua-toi" class="btn btn-outline-secondary">
                            <i class="fa-solid fa-arrow-left me-2"></i>Về lịch hẹn
                        </a>
                        <% if (giaoDich != null && giaoDich.getMaHoiThoai() != null) { %>
                        <a href="${pageContext.request.contextPath}/chat?room=<%= giaoDich.getMaHoiThoai() %>" class="btn btn-outline-primary">
                            <i class="fa-solid fa-comments me-2"></i>Mở chat
                        </a>
                        <% } %>
                        <jsp:include page="/WEB-INF/includes/account-menu.jsp" />
                    </div>
                </div>
            </div>

            <c:if test="${not empty transactionSuccess}">
                <div class="alert alert-success"><c:out value="${transactionSuccess}" /></div>
            </c:if>
            <c:if test="${not empty transactionError}">
                <div class="alert alert-danger"><c:out value="${transactionError}" /></div>
            </c:if>

            <% if (giaoDich != null) { %>
            <jsp:include page="/WEB-INF/includes/transaction-workflow.jsp" />
            <div class="row g-4">
                <div class="col-lg-8">
                    <div class="panel">
                        <div class="d-flex justify-content-between align-items-start flex-wrap gap-3 mb-3">
                            <div>
                                <h4 class="mb-1"><%= h(giaoDich.getTenXe() != null ? giaoDich.getTenXe() : ("Xe #" + giaoDich.getMaXe())) %></h4>
                                <div class="text-muted">Yêu cầu kết nối #<%= giaoDich.getMaYeuCau() %></div>
                            </div>
                            <span class="badge text-bg-<%= getStatusClass(giaoDich.getTrangThai()) %> px-3 py-2">
                                <%= getStatusLabel(giaoDich.getTrangThai()) %>
                            </span>
                        </div>

                        <% if ("HOAN_TAT".equals(giaoDich.getTrangThai())) { %>
                        <div class="alert alert-success">
                            Giao dịch này đã được cả hai bên xác nhận hoàn tất.
                        </div>
                        <% } else if ("HUY".equals(giaoDich.getTrangThai())) { %>
                        <div class="alert alert-danger">
                            Giao dịch đã bị hủy và không thể xác nhận thêm.
                        </div>
                        <% } else if ("TRANH_CHAP".equals(giaoDich.getTrangThai())) { %>
                        <div class="alert alert-warning">
                            Giao dịch đang ở trạng thái tranh chấp để admin xử lý thủ công.
                        </div>
                        <% } else { %>
                        <div class="alert alert-light border">
                            Bạn là người bán. Sau khi đã bàn giao xe ngoài thực tế theo thông tin người mua cung cấp, hãy bấm <strong>Đã giao xe</strong> để hệ thống ghi nhận xác nhận của bạn.
                        </div>
                        <% } %>

                        <div class="tx-progress" aria-label="Tiến trình giao dịch">
                            <div class="tx-step done"><i class="fa-solid fa-file-circle-check me-1"></i>Tạo giao dịch</div>
                            <div class="tx-line <%= giaoDich.isXacNhanNguoiMua() ? "done" : "" %>"></div>
                            <div class="tx-step <%= giaoDich.isXacNhanNguoiMua() ? "done" : "" %>"><i class="fa-solid fa-user-check me-1"></i>Người mua xác nhận</div>
                            <div class="tx-line <%= giaoDich.isXacNhanNguoiMua() && giaoDich.isXacNhanNguoiBan() ? "done" : "" %>"></div>
                            <div class="tx-step <%= giaoDich.isXacNhanNguoiBan() ? "done" : "" %>"><i class="fa-solid fa-truck-fast me-1"></i>Người bán xác nhận</div>
                            <div class="tx-line <%= "HOAN_TAT".equals(giaoDich.getTrangThai()) ? "done" : "" %>"></div>
                            <div class="tx-step <%= "HOAN_TAT".equals(giaoDich.getTrangThai()) ? "done" : "" %>"><i class="fa-solid fa-circle-check me-1"></i>Hoàn tất</div>
                        </div>

                        <div class="info-grid">
                            <div class="info-item">
                                <span class="info-label">Người mua</span>
                                <div class="info-value"><%= h(giaoDich.getTenNguoiMua() != null ? giaoDich.getTenNguoiMua() : "Chưa cập nhật") %></div>
                                <div class="text-muted small mt-2"><%= h(giaoDich.getEmailNguoiMua() != null ? giaoDich.getEmailNguoiMua() : "") %></div>
                                <div class="text-muted small"><%= h(giaoDich.getSoDienThoaiNguoiMua() != null ? giaoDich.getSoDienThoaiNguoiMua() : "") %></div>
                            </div>
                            <div class="info-item">
                                <span class="info-label">Giá chốt / tham khảo</span>
                                <div class="info-value"><%= formatMoney(giaoDich.getGiaChot() != null ? giaoDich.getGiaChot() : giaoDich.getGiaThamKhao()) %></div>
                            </div>
                            <div class="info-item">
                                <span class="info-label">Ngày tạo giao dịch</span>
                                <div class="info-value"><%= formatDate(giaoDich.getNgayTao()) %></div>
                            </div>
                            <div class="info-item">
                                <span class="info-label">Ngày chốt</span>
                                <div class="info-value"><%= formatDate(giaoDich.getNgayChot()) %></div>
                            </div>
                            <div class="info-item">
                                <span class="info-label">Ngày giao xe</span>
                                <div class="info-value"><%= formatDate(giaoDich.getNgayGiaoXe()) %></div>
                            </div>
                            <div class="info-item">
                                <span class="info-label">Địa điểm giao xe</span>
                                <div class="info-value"><%= h(giaoDich.getDiaDiemGiaoXe() != null ? giaoDich.getDiaDiemGiaoXe() : "Chưa cập nhật") %></div>
                            </div>
                            <div class="info-item">
                                <span class="info-label">Số điện thoại nhận xe</span>
                                <div class="info-value"><%= h(giaoDich.getSoDienThoaiGiaoXe() != null ? giaoDich.getSoDienThoaiGiaoXe() : "Chưa cập nhật") %></div>
                            </div>
                        </div>
                    </div>

                    <div class="panel">
                        <h4 class="mb-3">Xác nhận của hai bên</h4>
                        <div class="row g-3">
                            <div class="col-md-6">
                                <div class="info-item h-100">
                                    <span class="info-label">Người bán</span>
                                    <div class="info-value <%= giaoDich.isXacNhanNguoiBan() ? "text-success" : "text-secondary" %>">
                                        <i class="fa-solid <%= giaoDich.isXacNhanNguoiBan() ? "fa-circle-check" : "fa-clock" %> me-2"></i>
                                        <%= giaoDich.isXacNhanNguoiBan() ? "Đã xác nhận giao xe" : "Chưa xác nhận" %>
                                    </div>
                                    <div class="small text-muted mt-2">Thời gian: <%= formatDate(giaoDich.getThoiGianXacNhanBan()) %></div>
                                </div>
                            </div>
                            <div class="col-md-6">
                                <div class="info-item h-100">
                                    <span class="info-label">Người mua</span>
                                    <div class="info-value <%= giaoDich.isXacNhanNguoiMua() ? "text-success" : "text-secondary" %>">
                                        <i class="fa-solid <%= giaoDich.isXacNhanNguoiMua() ? "fa-circle-check" : "fa-clock" %> me-2"></i>
                                        <%= giaoDich.isXacNhanNguoiMua() ? "Đã xác nhận thanh toán và nhận xe" : "Đang chờ xác nhận" %>
                                    </div>
                                    <div class="small text-muted mt-2">Thời gian: <%= formatDate(giaoDich.getThoiGianXacNhanMua()) %></div>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div class="panel">
                        <h4 class="mb-3">Ghi chú các bên</h4>
                        <div class="row g-3">
                            <div class="col-md-6">
                                <div class="small text-muted mb-2">Ghi chú từ người bán</div>
                                <div class="note-box"><%= h(giaoDich.getGhiChuNguoiBan() != null ? giaoDich.getGhiChuNguoiBan() : "Chưa có ghi chú.") %></div>
                            </div>
                            <div class="col-md-6">
                                <div class="small text-muted mb-2">Ghi chú từ người mua</div>
                                <div class="note-box"><%= h(giaoDich.getGhiChuNguoiMua() != null ? giaoDich.getGhiChuNguoiMua() : "Chưa có ghi chú.") %></div>
                            </div>
                        </div>
                        <% if (giaoDich.getGhiChuAdmin() != null && !giaoDich.getGhiChuAdmin().isBlank()) { %>
                        <div class="alert alert-warning mt-3 mb-0">
                            <strong>Ghi chú admin:</strong> <%= h(giaoDich.getGhiChuAdmin()) %>
                        </div>
                        <% } %>
                    </div>
                </div>

                <div class="col-lg-4">
                    <div class="panel">
                        <h4 class="mb-3">Thao tác của người bán</h4>
                        <% if (canSellerConfirm(giaoDich)) { %>
                        <form action="${pageContext.request.contextPath}/giao-dich/xac-nhan" method="post" class="mb-3">
<input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                            <input type="hidden" name="maGiaoDich" value="<%= giaoDich.getMaGiaoDich() %>">
                            <input type="hidden" name="action" value="seller-confirm">
                            <label class="form-label fw-semibold">Ghi chú khi xác nhận đã giao xe</label>
                            <textarea name="ghiChu" rows="4" maxlength="1000" class="form-control mb-3" placeholder="Ví dụ: Tôi đã bàn giao xe, giấy tờ và chìa khóa đầy đủ cho người mua."></textarea>
                            <button type="submit" class="btn btn-success w-100 fw-semibold">
                                <i class="fa-solid fa-truck-fast me-2"></i>Đã giao xe
                            </button>
                        </form>
                        <% } else { %>
                        <div class="alert alert-light border">
                            <strong>Trạng thái hiện tại:</strong> <%= giaoDich.isXacNhanNguoiBan() ? "Bạn đã xác nhận giao xe." : "Giao dịch hiện không còn cho phép xác nhận từ phía người bán." %>
                        </div>
                        <% } %>

                        <% if (canCancel(giaoDich)) { %>
                        <form action="${pageContext.request.contextPath}/giao-dich/huy" method="post" onsubmit="return confirm('Bạn chắc chắn muốn hủy giao dịch này?');">
<input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                            <input type="hidden" name="maGiaoDich" value="<%= giaoDich.getMaGiaoDich() %>">
                            <label class="form-label fw-semibold">Lý do hủy giao dịch</label>
                            <textarea name="lyDoHuy" rows="3" maxlength="1000" class="form-control mb-3" placeholder="Ví dụ: Người mua không tiếp tục giao dịch, hồ sơ giấy tờ không phù hợp, hai bên không thống nhất được điều kiện bàn giao..."></textarea>
                            <button type="submit" class="btn btn-outline-danger w-100 fw-semibold">
                                <i class="fa-solid fa-ban me-2"></i>Hủy giao dịch
                            </button>
                        </form>
                        <% } %>
                    </div>
                </div>
            </div>
            <% } %>
        </div>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
        <jsp:include page="/WEB-INF/includes/site-footer.jsp" />

    </body>
</html>
