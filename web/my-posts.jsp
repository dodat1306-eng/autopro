<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Tin của tôi - AutoPro</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
        <style>
            body {
                background: #f5f6f8;
                font-family: Arial, sans-serif;
            }

            .wrap {
                max-width: 1080px;
                margin: 32px auto;
                background: white;
                border-radius: 18px;
                padding: 28px;
                box-shadow: 0 8px 28px rgba(0, 0, 0, 0.08);
            }

            .post-card {
                border: 1px solid #eceff3;
                border-radius: 16px;
                padding: 20px;
                margin-bottom: 18px;
                transition: box-shadow 0.2s ease, transform 0.2s ease;
            }

            .post-card:hover {
                box-shadow: 0 10px 24px rgba(0, 0, 0, 0.08);
                transform: translateY(-2px);
            }

            .status-pill {
                display: inline-flex;
                align-items: center;
                padding: 7px 12px;
                border-radius: 999px;
                font-size: 12px;
                font-weight: 700;
            }

            .status-cho_duyet {
                background: #fff3cd;
                color: #8a6200;
            }

            .status-da_duyet {
                background: #d1e7dd;
                color: #146c43;
            }

            .status-tu_choi {
                background: #f8d7da;
                color: #b02a37;
            }

            .status-canh_bao {
                background: #ffe8cc;
                color: #b45309;
            }

            .status-da_ban {
                background: #dbeafe;
                color: #1d4ed8;
            }

            .status-dang_giao_dich {
                background: #dcfce7;
                color: #166534;
            }

            .status-default {
                background: #e2e8f0;
                color: #334155;
            }

            .hint-box {
                background: #f8fafc;
                border: 1px solid #e2e8f0;
                border-radius: 14px;
                padding: 16px 18px;
            }
        </style>
    </head>
    <body>
        <div class="container wrap">
            <div class="d-flex justify-content-between align-items-center flex-wrap gap-3 mb-4">
                <div>
                    <div class="d-flex align-items-center gap-2 mb-2">
                        <a href="${pageContext.request.contextPath}/home" class="text-decoration-none d-inline-flex align-items-center">
                            <jsp:include page="/WEB-INF/includes/site-logo.jsp">
                                <jsp:param name="logoHeight" value="46px" />
                            </jsp:include>
                        </a>
                        <span class="badge bg-warning text-dark">Quản lý tin đăng</span>
                    </div>
                    <h2 class="mb-1">Tin của tôi</h2>
                    <p class="text-muted mb-0">Theo dõi trạng thái duyệt, sửa nội dung, xem ghi chú admin và xóa bài đăng không còn dùng.</p>
                </div>
                <div class="d-flex align-items-center gap-2">
                    <a href="seller/dashboard" class="btn btn-outline-warning fw-semibold"><i class="fa-solid fa-chart-simple me-2"></i>Dashboard</a>
                    <a href="lich-hen-cua-toi" class="btn btn-outline-primary"><i class="fa-solid fa-calendar-check me-2"></i>Lịch hẹn</a>
                    <a href="home" class="btn btn-outline-dark">Về trang chủ</a>
                    <a href="post-car" class="btn btn-warning fw-semibold">Đăng tin mới</a>
                    <jsp:include page="/WEB-INF/includes/account-menu.jsp" />
                </div>
            </div>

            <div class="hint-box mb-4">
                <i class="fa-solid fa-circle-info me-2"></i>
                            Nếu có người bấm “Gửi yêu cầu hẹn xem xe” cho xe của bạn, bạn sẽ thấy họ trong mục <a href="lich-hen-cua-toi" class="text-decoration-none fw-semibold">Lịch hẹn của tôi</a>.
            </div>

            <div class="d-flex flex-wrap gap-2 mb-4">
                <a href="#active-posts" class="btn btn-outline-dark">
                    <i class="fa-solid fa-car-side me-2"></i>Tin đang quản lý
                    <span class="badge text-bg-light ms-2">${activePostCount}</span>
                </a>
                <a href="#sold-posts" class="btn btn-outline-primary">
                    <i class="fa-solid fa-circle-check me-2"></i>Mục đã bán
                    <span class="badge text-bg-light ms-2">${soldPostCount}</span>
                </a>
            </div>

            <c:if test="${param.success == '1'}">
                <div class="alert alert-success">Bài đăng đã được tạo thành công và đang chờ duyệt.</div>
            </c:if>
            <c:if test="${param.updated == '1'}">
                <div class="alert alert-success">Bài đăng đã được cập nhật và quay lại trạng thái chờ duyệt.</div>
            </c:if>
            <c:if test="${param.deleted == '1'}">
                <div class="alert alert-success">Đã xóa bài đăng thành công.</div>
            </c:if>
            <c:if test="${param.deleteError == '1'}">
                <div class="alert alert-danger">Không thể xóa bài đăng lúc này. Hệ thống có thể đang giữ dữ liệu liên quan hoặc bài đăng không còn tồn tại.</div>
            </c:if>
            <c:if test="${param.soldLocked == '1'}">
                <div class="alert alert-warning">Bài đăng đang giao dịch hoặc đã bán nên hệ thống đã khóa thao tác sửa/xóa để giữ lịch sử giao dịch.</div>
            </c:if>

            <c:choose>
                <c:when test="${not empty listBaiDang}">
                    <c:set var="soldSectionStarted" value="false" />
                    <c:if test="${activePostCount > 0}">
                        <div id="active-posts" class="d-flex justify-content-between align-items-center flex-wrap gap-2 mb-3">
                            <div>
                                <h4 class="mb-1">Tin đang quản lý</h4>
                                <div class="text-muted small">Bao gồm tin đang bán, đang giao dịch, chờ duyệt, cảnh báo hoặc bị từ chối.</div>
                            </div>
                        </div>
                    </c:if>
                    <c:forEach items="${listBaiDang}" var="bd">
                        <c:if test="${bd.trangThai == 'da_ban' and soldSectionStarted == false}">
                            <c:set var="soldSectionStarted" value="true" />
                            <div id="sold-posts" class="d-flex justify-content-between align-items-center flex-wrap gap-2 mt-5 mb-3">
                                <div>
                                    <h4 class="mb-1">Mục đã bán</h4>
                                    <div class="text-muted small">Các xe đã hoàn tất giao dịch được lưu tại đây để người bán tra cứu, không còn xuất hiện ở danh sách xe của người mua.</div>
                                </div>
                            </div>
                        </c:if>
                        <div class="post-card">
                            <div class="d-flex justify-content-between align-items-start flex-wrap gap-3">
                                <div class="flex-grow-1">
                                    <div class="d-flex align-items-center gap-2 mb-2 flex-wrap">
                                        <c:choose>
                                            <c:when test="${bd.trangThai == 'cho_duyet'}">
                                                <span class="status-pill status-cho_duyet">Chờ duyệt</span>
                                            </c:when>
                                            <c:when test="${bd.trangThai == 'da_duyet'}">
                                                <span class="status-pill status-da_duyet">Đã duyệt</span>
                                            </c:when>
                                            <c:when test="${bd.trangThai == 'tu_choi'}">
                                                <span class="status-pill status-tu_choi">Từ chối</span>
                                            </c:when>
                                            <c:when test="${bd.trangThai == 'canh_bao'}">
                                                <span class="status-pill status-canh_bao">Cảnh báo</span>
                                            </c:when>
                                            <c:when test="${bd.trangThai == 'da_ban'}">
                                                <span class="status-pill status-da_ban"><i class="fa-solid fa-circle-check me-1"></i>Đã bán</span>
                                            </c:when>
                                            <c:when test="${bd.trangThai == 'dang_giao_dich'}">
                                                <span class="status-pill status-dang_giao_dich"><i class="fa-solid fa-handshake me-1"></i>Đang giao dịch</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="status-pill status-default">${bd.trangThai}</span>
                                            </c:otherwise>
                                        </c:choose>
                                        <span class="text-muted small">Mã bài #${bd.maBaiDang}</span>
                                        <span class="text-muted small">Ngày đăng: <fmt:formatDate value="${bd.ngayDang}" pattern="dd/MM/yyyy HH:mm" /></span>
                                    </div>

                                    <h4 class="mb-2">${bd.tieuDe}</h4>
                                    <div class="text-muted mb-2">
                                        <strong>${empty bd.tenXe ? 'Chưa có tên xe' : bd.tenXe}</strong>
                                        <span class="mx-2">•</span>
                                        <fmt:formatNumber value="${bd.giaBan}" type="number" maxFractionDigits="0"/> đ
                                    </div>
                                    <p class="mb-0 text-secondary">${bd.moTa}</p>

                                    <c:if test="${not empty bd.ghiChuAdmin}">
                                        <div class="alert alert-warning mt-3 mb-0">
                                            <strong>Ghi chú từ admin:</strong> ${bd.ghiChuAdmin}
                                        </div>
                                    </c:if>
                                </div>

                                <div class="d-flex gap-2 flex-wrap justify-content-end">
                                    <c:choose>
                                        <c:when test="${bd.trangThai == 'da_ban'}">
                                            <c:if test="${not empty bd.maGiaoDich}">
                                                <a class="btn btn-outline-primary" href="${pageContext.request.contextPath}/giao-dich/chi-tiet?id=${bd.maGiaoDich}">
                                                    <i class="fa-solid fa-file-lines me-2"></i>Xem giao dịch
                                                </a>
                                            </c:if>
                                            <button type="button" class="btn btn-outline-secondary" disabled>
                                                <i class="fa-solid fa-lock me-2"></i>Đã khóa
                                            </button>
                                        </c:when>
                                        <c:when test="${bd.trangThai == 'dang_giao_dich'}">
                                            <button type="button" class="btn btn-outline-secondary" disabled>
                                                <i class="fa-solid fa-lock me-2"></i>Đã khóa
                                            </button>
                                        </c:when>
                                        <c:otherwise>
                                    <a class="btn btn-outline-primary" href="edit-post?id=${bd.maBaiDang}">
                                        <i class="fa-solid fa-pen-to-square me-2"></i>Sửa tin
                                    </a>
                                    <form action="delete-post" method="post" onsubmit="return confirm('Bạn có chắc muốn xóa tin này?');" class="m-0">
<input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                                        <input type="hidden" name="id" value="${bd.maBaiDang}">
                                        <button type="submit" class="btn btn-outline-danger">
                                            <i class="fa-solid fa-trash me-2"></i>Xóa tin
                                        </button>
                                    </form>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                            </div>
                        </div>
                    </c:forEach>
                </c:when>
                <c:otherwise>
                    <div class="text-center py-5">
                        <i class="fa-solid fa-car-side text-secondary mb-3" style="font-size: 56px;"></i>
                        <h4 class="mb-2">Bạn chưa có tin đăng nào</h4>
                        <p class="text-muted mb-4">Khi bạn đăng xe mới, bài sẽ xuất hiện tại đây để theo dõi trạng thái và chỉnh sửa lại nếu cần.</p>
                    <a href="post-car" class="btn btn-warning">Đăng tin đầu tiên</a>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
        <jsp:include page="/WEB-INF/includes/site-footer.jsp" />

    </body>
</html>
