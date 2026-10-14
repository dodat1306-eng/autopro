<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
<%@page import="model.BaoCao"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%
    List<BaoCao> listBaoCao = (List<BaoCao>) request.getAttribute("listBaoCao");
    Integer pendingReportCount = (Integer) request.getAttribute("pendingReportCount");
    if (pendingReportCount == null) {
        pendingReportCount = 0;
    }
    Integer currentPage = (Integer) request.getAttribute("currentPage");
    Integer totalPages = (Integer) request.getAttribute("totalPages");
    Integer totalItems = (Integer) request.getAttribute("totalItems");
    String paginationBaseUrl = (String) request.getAttribute("paginationBaseUrl");
    if (currentPage == null) currentPage = 1;
    if (totalPages == null) totalPages = 1;
    if (totalItems == null) totalItems = listBaoCao == null ? 0 : listBaoCao.size();
%>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Báo cáo bài đăng - AutoPro Admin</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
        <style>
            body {
                margin: 0;
                padding: 30px;
                background: #f1f2f6;
                font-family: Arial, sans-serif;
            }

            .page-card {
                max-width: 1280px;
                margin: 0 auto;
                background: #ffffff;
                border-radius: 18px;
                padding: 28px;
                box-shadow: 0 8px 28px rgba(0, 0, 0, 0.08);
            }

            .report-card {
                border: 1px solid #e5e7eb;
                border-radius: 18px;
                padding: 22px;
                margin-bottom: 18px;
            }

            .report-card.pending {
                border-color: #f59e0b;
                box-shadow: 0 0 0 3px rgba(245, 158, 11, 0.08);
            }

            .status-pill {
                display: inline-flex;
                align-items: center;
                gap: 6px;
                padding: 6px 12px;
                border-radius: 999px;
                font-size: 12px;
                font-weight: 700;
            }

            .status-pending {
                background: #fff3cd;
                color: #946200;
            }

            .status-warned {
                background: #fff1d6;
                color: #b45309;
            }

            .status-removed {
                background: #fde2e2;
                color: #b42318;
            }

            .status-dismissed {
                background: #e8f1ff;
                color: #175cd3;
            }

            .meta-label {
                color: #64748b;
                font-size: 12px;
                text-transform: uppercase;
                letter-spacing: 0.04em;
                margin-bottom: 6px;
            }

            .meta-value {
                color: #111827;
                font-weight: 600;
            }
        </style>
    </head>
    <body>
        <div class="page-card">
            <div class="d-flex justify-content-between align-items-center flex-wrap gap-3 mb-4">
                <div>
                    <h2 class="mb-1"><i class="fa-solid fa-flag text-danger me-2"></i>Báo cáo bài đăng</h2>
                    <p class="text-muted mb-0">Admin xem lý do người dùng tố cáo, cảnh báo chủ bài hoặc xóa bài vi phạm ngay từ đây.</p>
                </div>
                <div class="d-flex gap-2 flex-wrap">
                    <span class="badge text-bg-danger fs-6"><%= pendingReportCount %> báo cáo chờ xử lý</span>
                    <a href="${pageContext.request.contextPath}/admin/dashboard" class="btn btn-outline-secondary">
                        <i class="fa-solid fa-arrow-left me-2"></i>Về Dashboard
                    </a>
                    <a href="${pageContext.request.contextPath}/admin/posts" class="btn btn-outline-dark">Danh sách bài đăng</a>
                </div>
            </div>

            <c:if test="${param.result == 'warned'}">
                <div class="alert alert-warning">Đã cảnh báo chủ bài viết và chuyển bài đăng sang trạng thái cần chỉnh sửa.</div>
            </c:if>
            <c:if test="${param.result == 'deleted'}">
                <div class="alert alert-success">Đã xóa bài đăng vi phạm và cập nhật toàn bộ báo cáo liên quan.</div>
            </c:if>
            <c:if test="${param.result == 'dismissed'}">
                <div class="alert alert-info">Đã bỏ qua báo cáo sau khi admin rà soát.</div>
            </c:if>
            <c:if test="${param.result == 'missing'}">
                <div class="alert alert-danger">Không tìm thấy báo cáo hoặc bài đăng liên quan.</div>
            </c:if>
            <c:if test="${param.result == 'sold_locked'}">
                <div class="alert alert-warning">Bài đăng đang giao dịch hoặc đã bán nên không thể cảnh báo/xóa từ luồng báo cáo. Hãy chỉ bỏ qua báo cáo hoặc xem lịch sử giao dịch.</div>
            </c:if>
            <c:if test="${param.result == 'invalid' || param.result == 'error'}">
                <div class="alert alert-danger">Không thể xử lý yêu cầu. Vui lòng thử lại.</div>
            </c:if>

            <% if (listBaoCao != null && !listBaoCao.isEmpty()) { %>
                <% for (BaoCao baoCao : listBaoCao) { 
                    String trangThai = baoCao.getTrangThai() == null ? "" : baoCao.getTrangThai();
                    boolean pending = "CHO_XU_LY".equalsIgnoreCase(trangThai);
                    boolean lockedPost = baoCao.isBaiDangBiKhoaGiaoDich();
                %>
                <div class="report-card <%= pending ? "pending" : "" %>">
                    <div class="d-flex justify-content-between align-items-start flex-wrap gap-3 mb-3">
                        <div>
                            <div class="d-flex align-items-center gap-2 flex-wrap mb-2">
                                <h5 class="mb-0"><%= baoCao.getTieuDeBaiDang() != null ? baoCao.getTieuDeBaiDang() : ("Bài đăng #" + baoCao.getMaBaiDang()) %></h5>
                                <% if (pending) { %>
                                <span class="status-pill status-pending"><i class="fa-solid fa-hourglass-half"></i>Chờ xử lý</span>
                                <% if (lockedPost) { %>
                                <span class="status-pill status-dismissed"><i class="fa-solid fa-lock"></i>Bài đang khóa giao dịch</span>
                                <% } %>
                                <% } else if ("DA_CANH_BAO".equalsIgnoreCase(trangThai)) { %>
                                <span class="status-pill status-warned"><i class="fa-solid fa-triangle-exclamation"></i>Đã cảnh báo</span>
                                <% } else if ("DA_XOA_BAI".equalsIgnoreCase(trangThai)) { %>
                                <span class="status-pill status-removed"><i class="fa-solid fa-trash"></i>Đã xóa bài</span>
                                <% } else { %>
                                <span class="status-pill status-dismissed"><i class="fa-solid fa-circle-check"></i>Bỏ qua</span>
                                <% } %>
                            </div>
                            <div class="text-muted small">
                                Xe: <strong><%= baoCao.getTenXe() != null ? baoCao.getTenXe() : "Chưa rõ" %></strong>
                                • Mã báo cáo #<%= baoCao.getMaBaoCao() %>
                                • Gửi lúc <%= baoCao.getNgayBaoCao() %>
                            </div>
                        </div>
                        <a href="${pageContext.request.contextPath}/admin/post-detail?id=<%= baoCao.getMaBaiDang() %>" class="btn btn-sm btn-outline-dark">
                            <i class="fa-solid fa-arrow-up-right-from-square me-1"></i>Xem bài đăng
                        </a>
                    </div>

                    <div class="row g-3 mb-3">
                        <div class="col-md-4">
                            <div class="meta-label">Người báo cáo</div>
                            <div class="meta-value"><%= baoCao.getTenNguoiBaoCao() != null ? baoCao.getTenNguoiBaoCao() : "Không xác định" %></div>
                            <div class="small text-muted"><%= baoCao.getEmailNguoiBaoCao() != null ? baoCao.getEmailNguoiBaoCao() : "" %></div>
                        </div>
                        <div class="col-md-4">
                            <div class="meta-label">Chủ bài đăng</div>
                            <div class="meta-value"><%= baoCao.getTenNguoiDang() != null ? baoCao.getTenNguoiDang() : "Không xác định" %></div>
                            <div class="small text-muted"><%= baoCao.getEmailNguoiDang() != null ? baoCao.getEmailNguoiDang() : "" %></div>
                        </div>
                        <div class="col-md-4">
                            <div class="meta-label">Lý do báo cáo</div>
                            <div class="meta-value"><%= baoCao.getLyDo() != null ? baoCao.getLyDo() : "Không có" %></div>
                        </div>
                    </div>

                    <div class="mb-3">
                        <div class="meta-label">Chi tiết người dùng gửi lên</div>
                        <div class="p-3 rounded border bg-light">
                            <%= (baoCao.getChiTiet() == null || baoCao.getChiTiet().isBlank()) ? "Người dùng không bổ sung chi tiết thêm." : baoCao.getChiTiet() %>
                        </div>
                    </div>

                    <% if (baoCao.getGhiChuAdmin() != null && !baoCao.getGhiChuAdmin().isBlank()) { %>
                    <div class="mb-3">
                        <div class="meta-label">Ghi chú admin gần nhất</div>
                        <div class="p-3 rounded border bg-white"><%= baoCao.getGhiChuAdmin() %></div>
                    </div>
                    <% } %>

                    <form action="${pageContext.request.contextPath}/admin/reports" method="post">
<input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                        <input type="hidden" name="id" value="<%= baoCao.getMaBaoCao() %>">
                        <div class="mb-3">
                            <label class="form-label fw-semibold">Ghi chú admin</label>
                            <textarea name="ghiChuAdmin" class="form-control" rows="3" placeholder="Nhập nội dung cảnh báo, lý do xóa hoặc ghi chú sau khi rà soát..."><%= baoCao.getGhiChuAdmin() != null ? baoCao.getGhiChuAdmin() : "" %></textarea>
                        </div>
                        <div class="d-flex gap-2 flex-wrap">
                            <% if (pending) { %>
                            <% if (lockedPost) { %>
                            <button type="button" class="btn btn-outline-secondary" disabled>
                                <i class="fa-solid fa-lock me-2"></i>Bài đang khóa giao dịch
                            </button>
                            <% } else { %>
                            <button type="submit" name="action" value="warn" class="btn btn-warning">
                                <i class="fa-solid fa-triangle-exclamation me-2"></i>Cảnh báo chủ bài
                            </button>
                            <button type="submit" name="action" value="delete" class="btn btn-danger"
                                    onclick="return confirm('Bạn có chắc muốn xóa ngay bài đăng này khỏi hệ thống không?');">
                                <i class="fa-solid fa-trash me-2"></i>Xóa bài ngay
                            </button>
                            <% } %>
                            <button type="submit" name="action" value="dismiss" class="btn btn-outline-secondary">
                                <i class="fa-solid fa-check me-2"></i>Bỏ qua báo cáo
                            </button>
                            <% } else { %>
                            <span class="text-muted small">Báo cáo này đã được xử lý. Bạn vẫn có thể mở chi tiết bài đăng để xem trạng thái hiện tại.</span>
                            <% } %>
                        </div>
                    </form>
                </div>
                <% } %>
            <% } else { %>
            <div class="text-center py-5 text-muted">
                <i class="fa-regular fa-flag fa-3x mb-3"></i>
                <div>Hiện chưa có báo cáo bài đăng nào trong hệ thống.</div>
            </div>
            <% } %>
            <% if (totalPages > 1) { %>
            <div class="d-flex justify-content-between align-items-center flex-wrap gap-2 mt-3">
                <div class="text-muted small">Tổng <%= totalItems %> báo cáo. Trang <%= currentPage %>/<%= totalPages %>.</div>
                <nav aria-label="Phân trang báo cáo">
                    <ul class="pagination mb-0">
                        <li class="page-item <%= currentPage <= 1 ? "disabled" : "" %>">
                            <a class="page-link" href="<%= paginationBaseUrl %><%= currentPage - 1 %>">Trước</a>
                        </li>
                        <% for (int p = 1; p <= totalPages; p++) { %>
                        <li class="page-item <%= p == currentPage ? "active" : "" %>">
                            <a class="page-link" href="<%= paginationBaseUrl %><%= p %>"><%= p %></a>
                        </li>
                        <% } %>
                        <li class="page-item <%= currentPage >= totalPages ? "disabled" : "" %>">
                            <a class="page-link" href="<%= paginationBaseUrl %><%= currentPage + 1 %>">Sau</a>
                        </li>
                    </ul>
                </nav>
            </div>
            <% } %>
        </div>
    </body>
</html>
