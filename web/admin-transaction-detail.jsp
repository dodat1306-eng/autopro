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
                return "Chờ hai bên xác nhận";
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

    private String getRequestStatusLabel(String status) {
        if (status == null) {
            return "Chưa cập nhật";
        }
        switch (status) {
            case "CHO_LIEN_HE":
                return "Chờ liên hệ";
            case "DA_LIEN_HE":
                return "Đã liên hệ";
            case "DA_XEM_XE":
                return "Đã hẹn xem xe";
            case "DA_CHOT":
                return "Đã chốt ngoài hệ thống";
            case "DA_HUY":
                return "Đã hủy";
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
<%
    GiaoDichXe giaoDich = (GiaoDichXe) request.getAttribute("giaoDich");
%>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Chi tiết giao dịch xe - Admin AutoPro</title>
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
            <div class="d-flex justify-content-end gap-2 mb-3">
                <a href="${pageContext.request.contextPath}/admin/dashboard" class="btn btn-outline-secondary">
                    <i class="fa-solid fa-arrow-left me-2"></i>Về Dashboard
                </a>
                <a href="${pageContext.request.contextPath}/admin/giao-dich" class="btn btn-outline-dark">Danh sách giao dịch</a>
            </div>

            <c:if test="${not empty adminTransactionSuccess}">
                <div class="alert alert-success"><c:out value="${adminTransactionSuccess}" /></div>
            </c:if>
            <c:if test="${not empty adminTransactionError}">
                <div class="alert alert-danger"><c:out value="${adminTransactionError}" /></div>
            </c:if>

            <% if (giaoDich != null) { %>
            <jsp:include page="/WEB-INF/includes/transaction-workflow.jsp" />
            <div class="row g-4">
                <div class="col-lg-8">
                    <div class="panel">
                        <div class="d-flex justify-content-between align-items-start flex-wrap gap-3 mb-3">
                            <div>
                                <div class="d-flex align-items-center gap-2 mb-2">
                                    <span class="badge bg-warning text-dark">Hồ sơ giao dịch xe</span>
                                    <span class="badge text-bg-<%= getStatusClass(giaoDich.getTrangThai()) %>">
                                        <%= getStatusLabel(giaoDich.getTrangThai()) %>
                                    </span>
                                </div>
                                <h2 class="mb-1">Giao dịch #<%= giaoDich.getMaGiaoDich() %></h2>
                                <div class="text-muted">Theo dõi trạng thái xác nhận bàn giao xe giữa người mua và người bán.</div>
                            </div>
                            <div class="text-end">
                                <div class="small text-muted">Ngày tạo</div>
                                <div class="fw-semibold"><%= formatDate(giaoDich.getNgayTao()) %></div>
                            </div>
                        </div>

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
                                <span class="info-label">Xe giao dịch</span>
                                <div class="info-value"><%= h(giaoDich.getTenXe() != null ? giaoDich.getTenXe() : ("Xe #" + giaoDich.getMaXe())) %></div>
                                <div class="small mt-2">
                                    <% if (giaoDich.getMaBaiDang() != null) { %>
                                    <a href="${pageContext.request.contextPath}/admin/post-detail?id=<%= giaoDich.getMaBaiDang() %>" class="text-decoration-none">Xem bài đăng liên quan</a>
                                    <% } else { %>
                                    <a href="${pageContext.request.contextPath}/detail?id=<%= giaoDich.getMaXe() %>" class="text-decoration-none">Xem chi tiết xe</a>
                                    <% } %>
                                </div>
                            </div>
                            <div class="info-item">
                                <span class="info-label">Giá chốt / tham khảo</span>
                                <div class="info-value"><%= formatMoney(giaoDich.getGiaChot() != null ? giaoDich.getGiaChot() : giaoDich.getGiaThamKhao()) %></div>
                            </div>
                            <div class="info-item">
                                <span class="info-label">Yêu cầu kết nối</span>
                                <div class="info-value">#<%= giaoDich.getMaYeuCau() %></div>
                                <div class="small text-muted mt-2">Trạng thái yêu cầu: <%= getRequestStatusLabel(giaoDich.getTrangThaiYeuCau()) %></div>
                            </div>
                            <div class="info-item">
                                <span class="info-label">Bài đăng liên quan</span>
                                <div class="info-value"><%= h(giaoDich.getTieuDeBaiDang() != null ? giaoDich.getTieuDeBaiDang() : "Chưa liên kết") %></div>
                                <div class="small text-muted mt-2">Trạng thái bài đăng: <%= h(giaoDich.getTrangThaiBaiDang() != null ? giaoDich.getTrangThaiBaiDang() : "Chưa cập nhật") %></div>
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
                            <div class="info-item">
                                <span class="info-label">Chat liên quan</span>
                                <div class="info-value"><%= giaoDich.getMaHoiThoai() != null ? ("Phòng #" + giaoDich.getMaHoiThoai()) : "Chưa có hội thoại" %></div>
                                <% if (giaoDich.getMaHoiThoai() != null) { %>
                                <div class="small mt-2">
                                    <a href="${pageContext.request.contextPath}/chat?room=<%= giaoDich.getMaHoiThoai() %>" class="text-decoration-none">Mở cuộc trò chuyện</a>
                                </div>
                                <% } %>
                            </div>
                        </div>
                    </div>

                    <div class="panel">
                        <h4 class="mb-3">Thông tin hai bên</h4>
                        <div class="row g-3">
                            <div class="col-md-6">
                                <div class="info-item h-100">
                                    <span class="info-label">Người mua</span>
                                    <div class="info-value"><%= h(giaoDich.getTenNguoiMua() != null ? giaoDich.getTenNguoiMua() : ("ID " + giaoDich.getMaNguoiMua())) %></div>
                                    <div class="small text-muted mt-2"><%= h(giaoDich.getEmailNguoiMua() != null ? giaoDich.getEmailNguoiMua() : "") %></div>
                                    <div class="small text-muted"><%= h(giaoDich.getSoDienThoaiNguoiMua() != null ? giaoDich.getSoDienThoaiNguoiMua() : "") %></div>
                                    <div class="small mt-3">
                                        <i class="fa-solid <%= giaoDich.isXacNhanNguoiMua() ? "fa-circle-check text-success" : "fa-clock text-muted" %> me-2"></i>
                                        <%= giaoDich.isXacNhanNguoiMua() ? "Đã thanh toán và nhận xe" : "Chưa xác nhận thanh toán/nhận xe" %>
                                    </div>
                                    <div class="small text-muted mt-2">Thời gian: <%= formatDate(giaoDich.getThoiGianXacNhanMua()) %></div>
                                </div>
                            </div>
                            <div class="col-md-6">
                                <div class="info-item h-100">
                                    <span class="info-label">Người bán</span>
                                    <div class="info-value"><%= h(giaoDich.getTenNguoiBan() != null ? giaoDich.getTenNguoiBan() : ("ID " + giaoDich.getMaNguoiBan())) %></div>
                                    <div class="small text-muted mt-2"><%= h(giaoDich.getEmailNguoiBan() != null ? giaoDich.getEmailNguoiBan() : "") %></div>
                                    <div class="small text-muted"><%= h(giaoDich.getSoDienThoaiNguoiBan() != null ? giaoDich.getSoDienThoaiNguoiBan() : "") %></div>
                                    <div class="small mt-3">
                                        <i class="fa-solid <%= giaoDich.isXacNhanNguoiBan() ? "fa-circle-check text-success" : "fa-clock text-muted" %> me-2"></i>
                                        <%= giaoDich.isXacNhanNguoiBan() ? "Đã giao xe" : "Chưa xác nhận giao xe" %>
                                    </div>
                                    <div class="small text-muted mt-2">Thời gian: <%= formatDate(giaoDich.getThoiGianXacNhanBan()) %></div>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div class="panel">
                        <h4 class="mb-3">Ghi chú các bên</h4>
                        <div class="row g-3">
                            <div class="col-md-6">
                                <div class="small text-muted mb-2">Ghi chú người mua</div>
                                <div class="note-box"><%= h(giaoDich.getGhiChuNguoiMua() != null ? giaoDich.getGhiChuNguoiMua() : "Chưa có ghi chú.") %></div>
                            </div>
                            <div class="col-md-6">
                                <div class="small text-muted mb-2">Ghi chú người bán</div>
                                <div class="note-box"><%= h(giaoDich.getGhiChuNguoiBan() != null ? giaoDich.getGhiChuNguoiBan() : "Chưa có ghi chú.") %></div>
                            </div>
                        </div>
                        <div class="small text-muted mt-3 mb-2">Ghi chú admin</div>
                        <div class="note-box"><%= h(giaoDich.getGhiChuAdmin() != null ? giaoDich.getGhiChuAdmin() : "Chưa có ghi chú xử lý từ admin.") %></div>
                    </div>
                </div>

                <div class="col-lg-4">
                    <div class="panel">
                        <h4 class="mb-3">Xử lý khiếu nại</h4>
                        <% if ("TRANH_CHAP".equals(giaoDich.getTrangThai()) || "CHO_XAC_NHAN_HAI_BEN".equals(giaoDich.getTrangThai()) || "CHO_XAC_NHAN_CON_LAI".equals(giaoDich.getTrangThai())) { %>
                        <p class="small text-muted">Ghi rõ lý do xử lý. Admin không xác nhận giao xe hoặc nhận xe thay hai bên.</p>
                        <form action="${pageContext.request.contextPath}/admin/giao-dich" method="post">
<input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                            <input type="hidden" name="maGiaoDich" value="<%= giaoDich.getMaGiaoDich() %>">
                            <div class="mb-3">
                                <label class="form-label fw-semibold">Trạng thái giao dịch</label>
                                <select name="trangThai" class="form-select">
                                    <% if ("TRANH_CHAP".equals(giaoDich.getTrangThai())) { %>
                                    <option value="CHO_XAC_NHAN_HAI_BEN">Đã giải quyết, mở lại để hai bên xác nhận</option>
                                    <option value="HUY">Kết thúc xử lý và hủy giao dịch</option>
                                    <% if (giaoDich.isXacNhanNguoiMua() && giaoDich.isXacNhanNguoiBan()) { %>
                                    <option value="HOAN_TAT">Hoàn tất theo xác nhận đã có của cả hai bên</option>
                                    <% } %>
                                    <% } else { %>
                                    <option value="TRANH_CHAP">Chuyển sang tranh chấp để xem xét</option>
                                    <% } %>
                                </select>
                            </div>
                            <div class="mb-3">
                                <label class="form-label fw-semibold">Ghi chú xử lý</label>
                                <textarea name="ghiChuAdmin" required minlength="10" maxlength="2000" rows="5" class="form-control" placeholder="Ví dụ: Hai bên xác nhận không thống nhất, admin chuyển tranh chấp để xử lý..."><%= h(giaoDich.getGhiChuAdmin() != null ? giaoDich.getGhiChuAdmin() : "") %></textarea>
                            </div>
                            <button type="submit" class="btn btn-warning w-100 fw-semibold">
                                <i class="fa-solid fa-floppy-disk me-2"></i>Lưu cập nhật
                            </button>
                        </form>
                        <% } else { %><p class="text-muted">Không có thao tác xử lý phù hợp ở trạng thái này. Lịch sử và xác nhận của các bên được giữ nguyên.</p><% } %>
                    </div>

                    <div class="panel">
                        <h4 class="mb-3">Mốc thời gian</h4>
                        <div class="small text-muted mb-2">Cập nhật gần nhất</div>
                        <div class="fw-semibold mb-3"><%= formatDate(giaoDich.getNgayCapNhat()) %></div>
                        <div class="small text-muted mb-2">Admin xử lý lần gần nhất</div>
                        <div class="fw-semibold mb-3"><%= formatDate(giaoDich.getNgayXuLy()) %></div>
                        <div class="small text-muted mb-2">Mã người xử lý</div>
                        <div class="fw-semibold"><%= giaoDich.getAdminXuLy() != null ? giaoDich.getAdminXuLy() : "Chưa có" %></div>
                    </div>
                </div>
            </div>
            <% } else { %>
            <div class="alert alert-danger">Không tìm thấy giao dịch cần xem.</div>
            <% } %>
        </div>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>
