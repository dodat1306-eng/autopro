<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%!
    private String resolveImageUrl(String rawImage, String contextPath) {
        if (rawImage == null || rawImage.trim().isEmpty()) {
            return contextPath + "/uploads/cars/car-placeholder.svg";
        }
        String image = rawImage.trim();
        if (image.startsWith("http://") || image.startsWith("https://")) {
            return image;
        }
        if (image.startsWith("/")) {
            return contextPath + image;
        }
        return contextPath + "/" + image;
    }
%>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Bài đã lưu - AutoPro</title>
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
                background: #fff;
                border-radius: 18px;
                padding: 28px;
                box-shadow: 0 8px 28px rgba(0, 0, 0, 0.08);
            }

            .saved-card {
                border: 1px solid #eceff3;
                border-radius: 18px;
                overflow: hidden;
                background: #fff;
                height: 100%;
                box-shadow: 0 6px 18px rgba(15, 23, 42, 0.06);
            }

            .saved-card.sold {
                border-color: #d7eadf;
                background: #fbfffd;
            }

            .saved-card.trading {
                border-color: #bbf7d0;
                background: #f7fff9;
            }

            .saved-thumb {
                position: relative;
            }

            .saved-card img {
                width: 100%;
                height: 220px;
                object-fit: cover;
                background: #eef2f7;
            }

            .saved-card .body {
                padding: 18px;
            }

            .saved-title {
                font-size: 18px;
                font-weight: 700;
                margin-bottom: 10px;
                color: #111827;
            }

            .saved-meta {
                color: #64748b;
                font-size: 13px;
                margin-bottom: 8px;
            }

            .saved-price {
                color: #dc2626;
                font-size: 22px;
                font-weight: 700;
            }

            .saved-card.sold .saved-price {
                color: #64748b;
            }

            .sold-badge {
                position: absolute;
                top: 12px;
                left: 12px;
                display: inline-flex;
                align-items: center;
                gap: 6px;
                padding: 7px 12px;
                border-radius: 999px;
                background: #198754;
                color: #fff;
                font-size: 12px;
                font-weight: 800;
            }

            .trading-badge {
                background: #16a34a;
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
                        <span class="badge bg-warning text-dark">Buyer tools</span>
                    </div>
                    <h2 class="mb-1">Bài đăng đã lưu</h2>
                    <p class="text-muted mb-0">Theo dõi nhanh những bài bạn quan tâm để quay lại chat, gửi yêu cầu mua hoặc bỏ lưu khi không còn nhu cầu.</p>
                </div>
                <div class="d-flex align-items-center gap-2">
                            <a href="lich-hen-cua-toi" class="btn btn-outline-primary"><i class="fa-solid fa-calendar-check me-2"></i>Lịch hẹn</a>
                    <a href="home" class="btn btn-outline-dark">Về trang chủ</a>
                    <jsp:include page="/WEB-INF/includes/account-menu.jsp" />
                </div>
            </div>

            

            <c:if test="${not empty savedPostSuccess}">
                <div class="alert alert-success">${savedPostSuccess}</div>
            </c:if>
            <c:if test="${not empty savedPostError}">
                <div class="alert alert-danger">${savedPostError}</div>
            </c:if>

            <c:choose>
                <c:when test="${not empty savedPosts}">
                    <div class="row g-4">
                        <c:forEach items="${savedPosts}" var="post">
                            <%
                                model.BaiDang post = (model.BaiDang) pageContext.getAttribute("post");
                                pageContext.setAttribute("thumbnail", resolveImageUrl(post.getAnhDaiDien(), request.getContextPath()));
                            %>
                            <div class="col-12 col-md-6 col-xl-4">
                                <div class="saved-card ${post.trangThai == 'da_ban' ? 'sold' : ''} ${post.trangThai == 'dang_giao_dich' ? 'trading' : ''}">
                                    <div class="saved-thumb">
                                        <img src="${thumbnail}" alt="${post.tenXe}" data-car-name="${post.tenXe}" data-context-path="${pageContext.request.contextPath}" loading="lazy" onerror="useAutoProCarFallback(this)">
                                        <c:if test="${post.trangThai == 'da_ban'}">
                                            <span class="sold-badge"><i class="fa-solid fa-lock"></i>Đã bán</span>
                                        </c:if>
                                        <c:if test="${post.trangThai == 'dang_giao_dich'}">
                                            <span class="sold-badge trading-badge"><i class="fa-solid fa-handshake"></i>Đang giao dịch</span>
                                        </c:if>
                                    </div>
                                    <div class="body">
                                        <div class="saved-meta">
                                            <i class="fa-solid fa-user me-1"></i>${empty post.tenNguoiDang ? 'Người bán' : post.tenNguoiDang}
                                            <span class="mx-2">•</span>
                                            <i class="fa-solid fa-location-dot me-1"></i>${empty post.tenTinh ? 'Chưa rõ tỉnh' : post.tenTinh}
                                        </div>
                                        <div class="saved-title">${empty post.tieuDe ? post.tenXe : post.tieuDe}</div>
                                        <div class="saved-meta">
                                            <i class="fa-solid fa-car-side me-1"></i>${post.tenXe}
                                            <span class="mx-2">•</span>
                                            <i class="fa-solid fa-map me-1"></i>${empty post.mien ? 'Chưa rõ miền' : post.mien}
                                        </div>
                                        <div class="saved-price mb-3">
                                            <fmt:formatNumber value="${post.giaBan}" type="number" maxFractionDigits="0"/> đ
                                        </div>
                                        <p class="text-muted small mb-3">${empty post.moTa ? 'Người bán chưa bổ sung mô tả cho bài đăng này.' : post.moTa}</p>
                                        <div class="d-flex gap-2">
                                            <a href="detail?id=${post.maXe}" class="btn ${post.trangThai == 'da_ban' || post.trangThai == 'dang_giao_dich' ? 'btn-outline-secondary' : 'btn-primary'} flex-fill">
                                                <c:choose>
                                                    <c:when test="${post.trangThai == 'da_ban'}">Xem xe đã bán</c:when>
                                                    <c:when test="${post.trangThai == 'dang_giao_dich'}">Xem xe đang giao dịch</c:when>
                                                    <c:otherwise>Xem chi tiết</c:otherwise>
                                                </c:choose>
                                            </a>
                                            <form action="saved-posts/action" method="post" class="m-0">
<input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                                                <input type="hidden" name="maBaiDang" value="${post.maBaiDang}">
                                                <input type="hidden" name="action" value="unsave">
                                                <input type="hidden" name="source" value="saved-posts">
                                                <button type="submit" class="btn btn-outline-danger">
                                                    <i class="fa-regular fa-heart-crack"></i>
                                                </button>
                                            </form>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </c:forEach>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="text-center py-5">
                        <i class="fa-regular fa-heart text-secondary mb-3" style="font-size: 56px;"></i>
                        <h4 class="mb-2">Bạn chưa lưu bài đăng nào</h4>
                        <p class="text-muted mb-4">Hãy mở một bài đăng chi tiết rồi bấm “Lưu bài đăng” để gom các xe bạn đang cân nhắc mua.</p>
                        <a href="home" class="btn btn-warning">Khám phá xe đang bán</a>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
        <script src="${pageContext.request.contextPath}/assets/js/autopro-image-fallback.js"></script>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
        <jsp:include page="/WEB-INF/includes/site-footer.jsp" />

    </body>
</html>
