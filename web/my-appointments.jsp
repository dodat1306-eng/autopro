<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Map"%>
<%@page import="model.GiaoDichXe"%>
<%@page import="model.YeuCauKetNoi"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%!
    private String getStatusLabel(String status) {
        if (status == null) {
            return "Chưa cập nhật";
        }
        switch (status) {
            case "CHO_LIEN_HE":
                return "Chờ người bán liên hệ";
            case "DA_LIEN_HE":
                return "Đã liên hệ";
            case "DA_XEM_XE":
                return "Đã xem xe";
            case "DA_CHOT":
                return "Đã chốt ngoài hệ thống";
            case "DA_HUY":
                return "Đã hủy";
            default:
                return status;
        }
    }

    private String formatDate(java.sql.Timestamp timestamp) {
        if (timestamp == null) {
            return "-";
        }
        return new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm").format(timestamp);
    }

    private boolean canBuyerConfirm(String status) {
        return "CHO_LIEN_HE".equals(status) || "DA_LIEN_HE".equals(status);
    }

    private boolean canSellerContact(String status) {
        return "CHO_LIEN_HE".equals(status);
    }

    private boolean canCancel(String status) {
        return "CHO_LIEN_HE".equals(status) || "DA_LIEN_HE".equals(status) || "DA_XEM_XE".equals(status);
    }

    private boolean canCreateTransaction(String status) {
        return "DA_XEM_XE".equals(status) || "DA_CHOT".equals(status);
    }

    private String getTransactionStatusLabel(String status) {
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

    private String getTransactionStatusClass(String status) {
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

    private String formatMoney(Double value) {
        if (value == null) {
            return "Chưa cập nhật";
        }
        return String.format("%,.0f", value).replace(",", ".") + " đ";
    }
%>
<%
    List<YeuCauKetNoi> outgoingRequests = (List<YeuCauKetNoi>) request.getAttribute("outgoingRequests");
    List<YeuCauKetNoi> incomingRequests = (List<YeuCauKetNoi>) request.getAttribute("incomingRequests");
    List<GiaoDichXe> outgoingTransactions = (List<GiaoDichXe>) request.getAttribute("outgoingTransactions");
    List<GiaoDichXe> incomingTransactions = (List<GiaoDichXe>) request.getAttribute("incomingTransactions");
    Map<Integer, GiaoDichXe> outgoingTransactionMap = (Map<Integer, GiaoDichXe>) request.getAttribute("outgoingTransactionMap");
    Map<Integer, GiaoDichXe> incomingTransactionMap = (Map<Integer, GiaoDichXe>) request.getAttribute("incomingTransactionMap");
%>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Lịch hẹn và kết nối của tôi - AutoPro</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
        <style>
            body {
                background: #f5f6f8;
                font-family: Arial, sans-serif;
            }

            .wrap {
                max-width: 1180px;
                margin: 32px auto;
            }

            .topbar,
            .panel {
                background: #fff;
                border-radius: 18px;
                padding: 24px;
                box-shadow: 0 8px 28px rgba(0, 0, 0, 0.08);
                margin-bottom: 22px;
            }

            .status {
                display: inline-block;
                padding: 6px 12px;
                border-radius: 999px;
                font-size: 13px;
                font-weight: 700;
            }

            .status-CHO_LIEN_HE {
                background: #fff3cd;
                color: #946200;
            }

            .status-DA_LIEN_HE {
                background: #dbeafe;
                color: #1d4ed8;
            }

            .status-DA_XEM_XE {
                background: #fef3c7;
                color: #b45309;
            }

            .status-DA_CHOT {
                background: #dcfce7;
                color: #15803d;
            }

            .status-DA_HUY {
                background: #fee2e2;
                color: #b91c1c;
            }

            .status-MOI_TAO,
            .status-CHO_XAC_NHAN_HAI_BEN,
            .status-CHO_XAC_NHAN_CON_LAI {
                background: #e0f2fe;
                color: #0369a1;
            }

            .status-HOAN_TAT {
                background: #dcfce7;
                color: #15803d;
            }

            .status-HUY {
                background: #fee2e2;
                color: #b91c1c;
            }

            .status-TRANH_CHAP {
                background: #fef3c7;
                color: #b45309;
            }

            .empty-box {
                padding: 28px;
                border-radius: 14px;
                background: #f8fafc;
                text-align: center;
                color: #64748b;
            }

            .contact-line {
                font-size: 13px;
                color: #64748b;
            }
        </style>
    </head>
    <body>
        <div class="container wrap">
            <div class="topbar">
                <div class="d-flex justify-content-between align-items-center flex-wrap gap-3">
                    <div>
                        <div class="d-flex align-items-center gap-2 mb-2">
                        <a href="${pageContext.request.contextPath}/home" class="text-decoration-none d-inline-flex align-items-center">
                            <jsp:include page="/WEB-INF/includes/site-logo.jsp">
                                <jsp:param name="logoHeight" value="46px" />
                            </jsp:include>
                        </a>
                            <span class="badge bg-warning text-dark">Kết nối & lịch hẹn</span>
                        </div>
                        <h2 class="mb-1">Lịch hẹn và kết nối của tôi</h2>
                        <p class="text-muted mb-0">Người mua theo dõi yêu cầu kết nối đã gửi tại đây; người bán nhận lịch hẹn và nhu cầu trao đổi từ các bài đăng thuộc quyền sở hữu của mình.</p>
                    </div>
                    <div class="d-flex align-items-center gap-2">
                        <a href="chat-list" class="btn btn-outline-primary"><i class="fa-solid fa-comments me-2"></i>Tin nhắn</a>
                        <a href="home" class="btn btn-outline-dark">Về trang chủ</a>
                        <jsp:include page="/WEB-INF/includes/account-menu.jsp" />
                    </div>
                </div>
            </div>

            <c:if test="${not empty requestSuccess}">
                <div class="alert alert-success">${requestSuccess}</div>
            </c:if>
            <c:if test="${not empty requestError}">
                <div class="alert alert-danger">${requestError}</div>
            </c:if>
            <c:if test="${not empty transactionSuccess}">
                <div class="alert alert-success">${transactionSuccess}</div>
            </c:if>
            <c:if test="${not empty transactionError}">
                <div class="alert alert-danger">${transactionError}</div>
            </c:if>

            <div class="panel">
                <div class="d-flex justify-content-between align-items-center mb-3 flex-wrap gap-2">
                    <div>
                        <h4 class="mb-1">Yêu cầu kết nối tôi đã gửi</h4>
                        <div class="text-muted small">Dành cho người mua: gửi nhu cầu quan tâm xe, trao đổi qua chat và xác nhận lịch hẹn xem xe khi đã thống nhất với người bán.</div>
                    </div>
                    <span class="badge bg-light text-dark border px-3 py-2"><%= outgoingRequests != null ? outgoingRequests.size() : 0 %> yêu cầu</span>
                </div>

                <% if (outgoingRequests != null && !outgoingRequests.isEmpty()) { %>
                <div class="table-responsive">
                    <table class="table table-hover align-middle">
                        <thead>
                            <tr>
                                <th>Mã</th>
                                <th>Xe quan tâm</th>
                                <th>Người bán</th>
                                <th>Ngày tạo</th>
                                <th>Giá tham khảo</th>
                                <th>Trạng thái</th>
                                <th class="text-end">Thao tác</th>
                            </tr>
                        </thead>
                        <tbody>
                            <% for (YeuCauKetNoi item : outgoingRequests) { %>
                            <% GiaoDichXe giaoDich = outgoingTransactionMap != null ? outgoingTransactionMap.get(item.getMaYeuCau()) : null; %>
                            <tr>
                                <td>#<%= item.getMaYeuCau() %></td>
                                <td>
                                    <strong><%= item.getTenXe() != null ? item.getTenXe() : "Chưa rõ tên xe" %></strong>
                                    <% if (item.getMaXe() != null) { %>
                                    <div><a href="detail?id=<%= item.getMaXe() %>" class="small text-decoration-none">Xem tin xe</a></div>
                                    <% } %>
                                    <% if (giaoDich != null) { %>
                                    <div class="small mt-2">
                                        <span class="status status-<%= giaoDich.getTrangThai() %>"><%= getTransactionStatusLabel(giaoDich.getTrangThai()) %></span>
                                    </div>
                                    <% } %>
                                    <% if (item.getGhiChuNguoiMua() != null && !item.getGhiChuNguoiMua().isBlank()) { %>
                                    <div class="small text-muted mt-2"><strong>Nhu cầu gửi đi:</strong> <%= item.getGhiChuNguoiMua() %></div>
                                    <% } %>
                                </td>
                                <td>
                                    <div><%= item.getTenNguoiBan() != null ? item.getTenNguoiBan() : "Chưa xác định" %></div>
                                    <% if (item.getEmailNguoiBan() != null && !item.getEmailNguoiBan().isBlank()) { %>
                                    <div class="contact-line"><i class="fa-solid fa-envelope me-1"></i><%= item.getEmailNguoiBan() %></div>
                                    <% } %>
                                    <% if (item.getSoDienThoaiNguoiBan() != null && !item.getSoDienThoaiNguoiBan().isBlank()) { %>
                                    <div class="contact-line"><i class="fa-solid fa-phone me-1"></i><%= item.getSoDienThoaiNguoiBan() %></div>
                                    <% } %>
                                </td>
                                <td><%= formatDate(item.getNgayTao()) %></td>
                                <td><%= String.format("%,.0f", item.getGiaThamKhao()).replace(",", ".") %> đ</td>
                                <td><span class="status status-<%= item.getTrangThai() %>"><%= getStatusLabel(item.getTrangThai()) %></span></td>
                                <td class="text-end">
                                    <div class="d-flex justify-content-end flex-wrap gap-2">
                                        <% if (item.getMaHoiThoai() != null) { %>
                                        <a href="chat?room=<%= item.getMaHoiThoai() %>" class="btn btn-sm btn-outline-primary">Mở chat</a>
                                        <% } %>
                                        <% if (giaoDich != null) { %>
                                        <a href="giao-dich/chi-tiet?id=<%= giaoDich.getMaGiaoDich() %>" class="btn btn-sm btn-dark">Xem giao dịch</a>
                                        <% } else if (canCreateTransaction(item.getTrangThai())) { %>
                                        <form action="giao-dich/tao" method="post" class="m-0">
<input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                                            <input type="hidden" name="maYeuCau" value="<%= item.getMaYeuCau() %>">
                                            <button type="submit" class="btn btn-sm btn-success">Tạo giao dịch</button>
                                        </form>
                                        <% } %>
                                        
                                        <% if (item.getMaLichHen() != null) { %>
                                        <a href="${pageContext.request.contextPath}/lich-hen/chi-tiet?id=<%= item.getMaYeuCau() %>" class="btn btn-sm btn-outline-dark">Chi tiết / đổi lịch</a>
                                        <% } %>
                                    </div>
                                </td>
                            </tr>
                            <% } %>
                        </tbody>
                    </table>
                </div>
                <% } else { %>
                <div class="empty-box">
                    <h5 class="mb-2">Bạn chưa gửi yêu cầu nào.</h5>
                    <p class="mb-3">Khi bạn bấm “Gửi yêu cầu kết nối” ở trang chi tiết xe, lịch hẹn và yêu cầu trao đổi sẽ xuất hiện tại đây.</p>
                    <a href="home" class="btn btn-warning">Xem danh sách xe</a>
                </div>
                <% } %>
            </div>

            <div class="panel">
                <div class="d-flex justify-content-between align-items-center mb-3 flex-wrap gap-2">
                    <div>
                        <h4 class="mb-1">Yêu cầu kết nối gửi đến xe của tôi</h4>
                        <div class="text-muted small">Dành cho người bán: đây là nơi bạn nhận nhu cầu trao đổi, lịch hẹn xem xe và thông tin liên hệ từ khách trên các bài đăng của mình.</div>
                    </div>
                    <span class="badge bg-light text-dark border px-3 py-2"><%= incomingRequests != null ? incomingRequests.size() : 0 %> yêu cầu đến</span>
                </div>

                <% if (incomingRequests != null && !incomingRequests.isEmpty()) { %>
                <div class="table-responsive">
                    <table class="table table-hover align-middle">
                        <thead>
                            <tr>
                                <th>Mã</th>
                                <th>Xe của tôi</th>
                                <th>Khách quan tâm</th>
                                <th>Ngày tạo</th>
                                <th>Giá tham khảo</th>
                                <th>Trạng thái</th>
                                <th class="text-end">Thao tác</th>
                            </tr>
                        </thead>
                        <tbody>
                            <% for (YeuCauKetNoi item : incomingRequests) { %>
                            <% GiaoDichXe giaoDich = incomingTransactionMap != null ? incomingTransactionMap.get(item.getMaYeuCau()) : null; %>
                            <tr>
                                <td>#<%= item.getMaYeuCau() %></td>
                                <td>
                                    <strong><%= item.getTenXe() != null ? item.getTenXe() : "Chưa rõ tên xe" %></strong>
                                    <% if (item.getMaXe() != null) { %>
                                    <div><a href="detail?id=<%= item.getMaXe() %>" class="small text-decoration-none">Xem tin xe</a></div>
                                    <% } %>
                                    <% if (giaoDich != null) { %>
                                    <div class="small mt-2">
                                        <span class="status status-<%= giaoDich.getTrangThai() %>"><%= getTransactionStatusLabel(giaoDich.getTrangThai()) %></span>
                                    </div>
                                    <% } %>
                                    <% if (item.getGhiChuNguoiMua() != null && !item.getGhiChuNguoiMua().isBlank()) { %>
                                    <div class="small text-muted mt-2"><strong>Nhu cầu kết nối:</strong> <%= item.getGhiChuNguoiMua() %></div>
                                    <% } %>
                                </td>
                                <td>
                                    <div><%= item.getTenNguoiMua() != null ? item.getTenNguoiMua() : "Chưa rõ khách" %></div>
                                    <% if (item.getEmailNguoiMua() != null && !item.getEmailNguoiMua().isBlank()) { %>
                                    <div class="contact-line"><i class="fa-solid fa-envelope me-1"></i><%= item.getEmailNguoiMua() %></div>
                                    <% } %>
                                    <% if (item.getSoDienThoaiNguoiMua() != null && !item.getSoDienThoaiNguoiMua().isBlank()) { %>
                                    <div class="contact-line"><i class="fa-solid fa-phone me-1"></i><%= item.getSoDienThoaiNguoiMua() %></div>
                                    <% } %>
                                </td>
                                <td><%= formatDate(item.getNgayTao()) %></td>
                                <td><%= String.format("%,.0f", item.getGiaThamKhao()).replace(",", ".") %> đ</td>
                                <td><span class="status status-<%= item.getTrangThai() %>"><%= getStatusLabel(item.getTrangThai()) %></span></td>
                                <td class="text-end">
                                    <div class="d-flex justify-content-end flex-wrap gap-2">
                                        <% if (item.getMaHoiThoai() != null) { %>
                                        <a href="chat?room=<%= item.getMaHoiThoai() %>" class="btn btn-sm btn-outline-primary">Nhắn khách</a>
                                        <% } %>
                                        <% if (giaoDich != null) { %>
                                        <a href="giao-dich/chi-tiet?id=<%= giaoDich.getMaGiaoDich() %>" class="btn btn-sm btn-dark">Xem giao dịch</a>
                                        <% } else if (canCreateTransaction(item.getTrangThai())) { %>
                                        <form action="giao-dich/tao" method="post" class="m-0">
<input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                                            <input type="hidden" name="maYeuCau" value="<%= item.getMaYeuCau() %>">
                                            <button type="submit" class="btn btn-sm btn-success">Tạo giao dịch</button>
                                        </form>
                                        <% } %>
                                        
                                        <% if (item.getMaLichHen() != null) { %>
                                        <a href="${pageContext.request.contextPath}/lich-hen/chi-tiet?id=<%= item.getMaYeuCau() %>" class="btn btn-sm btn-outline-dark">Chi tiết / đổi lịch</a>
                                        <% } %>
                                    </div>
                                </td>
                            </tr>
                            <% } %>
                        </tbody>
                    </table>
                </div>
                <% } else { %>
                <div class="empty-box">
                    <h5 class="mb-2">Chưa có khách gửi yêu cầu cho xe của bạn.</h5>
                    <p class="mb-0">Khi có người bấm “Gửi yêu cầu kết nối”, lịch hẹn và yêu cầu trao đổi sẽ xuất hiện ở đây, đồng thời bạn có thể mở chat để làm việc trực tiếp.</p>
                </div>
                <% } %>
            </div>

            <div class="panel">
                <div class="d-flex justify-content-between align-items-center mb-3 flex-wrap gap-2">
                    <div>
                        <h4 class="mb-1">Hồ sơ giao dịch tôi là người mua</h4>
                        <div class="text-muted small">Sau khi đã xem xe và hai bên đồng ý giao dịch ngoài thực tế, hồ sơ giao dịch sẽ được tạo để theo dõi xác nhận giao xe, thanh toán và nhận xe.</div>
                    </div>
                    <span class="badge bg-light text-dark border px-3 py-2"><%= outgoingTransactions != null ? outgoingTransactions.size() : 0 %> giao dịch</span>
                </div>

                <% if (outgoingTransactions != null && !outgoingTransactions.isEmpty()) { %>
                <div class="table-responsive">
                    <table class="table table-hover align-middle">
                        <thead>
                            <tr>
                                <th>Mã GD</th>
                                <th>Xe</th>
                                <th>Giá</th>
                                <th>Trạng thái</th>
                                <th>Xác nhận</th>
                                <th class="text-end">Chi tiết</th>
                            </tr>
                        </thead>
                        <tbody>
                            <% for (GiaoDichXe item : outgoingTransactions) { %>
                            <tr>
                                <td>#<%= item.getMaGiaoDich() %></td>
                                <td>
                                    <strong><%= item.getTenXe() != null ? item.getTenXe() : ("Xe #" + item.getMaXe()) %></strong>
                                    <div class="small text-muted mt-1">Yêu cầu #<%= item.getMaYeuCau() %></div>
                                </td>
                                <td><%= formatMoney(item.getGiaChot() != null ? item.getGiaChot() : item.getGiaThamKhao()) %></td>
                                <td><span class="status status-<%= item.getTrangThai() %>"><%= getTransactionStatusLabel(item.getTrangThai()) %></span></td>
                                <td>
                                    <div class="small">
                                        <div><i class="fa-solid <%= item.isXacNhanNguoiMua() ? "fa-circle-check text-success" : "fa-clock text-muted" %> me-2"></i>Tôi</div>
                                        <div><i class="fa-solid <%= item.isXacNhanNguoiBan() ? "fa-circle-check text-success" : "fa-clock text-muted" %> me-2"></i>Người bán</div>
                                    </div>
                                </td>
                                <td class="text-end">
                                    <a href="giao-dich/chi-tiet?id=<%= item.getMaGiaoDich() %>" class="btn btn-sm btn-outline-dark">Xem giao dịch</a>
                                </td>
                            </tr>
                            <% } %>
                        </tbody>
                    </table>
                </div>
                <% } else { %>
                <div class="empty-box">
                    Chưa có hồ sơ giao dịch nào ở vai trò người mua.
                </div>
                <% } %>
            </div>

            <div class="panel">
                <div class="d-flex justify-content-between align-items-center mb-3 flex-wrap gap-2">
                    <div>
                        <h4 class="mb-1">Hồ sơ giao dịch tôi là người bán</h4>
                        <div class="text-muted small">Khi một yêu cầu kết nối đã đi đến bước chốt giao dịch ngoài đời thực, bạn có thể tạo hồ sơ giao dịch để ghi nhận đã giao xe và chờ người mua xác nhận đã thanh toán, đã nhận xe.</div>
                    </div>
                    <span class="badge bg-light text-dark border px-3 py-2"><%= incomingTransactions != null ? incomingTransactions.size() : 0 %> giao dịch</span>
                </div>

                <% if (incomingTransactions != null && !incomingTransactions.isEmpty()) { %>
                <div class="table-responsive">
                    <table class="table table-hover align-middle">
                        <thead>
                            <tr>
                                <th>Mã GD</th>
                                <th>Xe</th>
                                <th>Khách mua</th>
                                <th>Trạng thái</th>
                                <th>Xác nhận</th>
                                <th class="text-end">Chi tiết</th>
                            </tr>
                        </thead>
                        <tbody>
                            <% for (GiaoDichXe item : incomingTransactions) { %>
                            <tr>
                                <td>#<%= item.getMaGiaoDich() %></td>
                                <td>
                                    <strong><%= item.getTenXe() != null ? item.getTenXe() : ("Xe #" + item.getMaXe()) %></strong>
                                    <div class="small text-muted mt-1">Yêu cầu #<%= item.getMaYeuCau() %></div>
                                </td>
                                <td>
                                    <div><%= item.getTenNguoiMua() != null ? item.getTenNguoiMua() : ("ID " + item.getMaNguoiMua()) %></div>
                                    <div class="small text-muted"><%= item.getEmailNguoiMua() != null ? item.getEmailNguoiMua() : "" %></div>
                                </td>
                                <td><span class="status status-<%= item.getTrangThai() %>"><%= getTransactionStatusLabel(item.getTrangThai()) %></span></td>
                                <td>
                                    <div class="small">
                                        <div><i class="fa-solid <%= item.isXacNhanNguoiBan() ? "fa-circle-check text-success" : "fa-clock text-muted" %> me-2"></i>Tôi</div>
                                        <div><i class="fa-solid <%= item.isXacNhanNguoiMua() ? "fa-circle-check text-success" : "fa-clock text-muted" %> me-2"></i>Người mua</div>
                                    </div>
                                </td>
                                <td class="text-end">
                                    <a href="giao-dich/chi-tiet?id=<%= item.getMaGiaoDich() %>" class="btn btn-sm btn-outline-dark">Xem giao dịch</a>
                                </td>
                            </tr>
                            <% } %>
                        </tbody>
                    </table>
                </div>
                <% } else { %>
                <div class="empty-box">
                    Chưa có hồ sơ giao dịch nào ở vai trò người bán.
                </div>
                <% } %>
            </div>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
        <jsp:include page="/WEB-INF/includes/site-footer.jsp" />

    </body>
</html>
