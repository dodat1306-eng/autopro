<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <%@ include file="/WEB-INF/jspf/favicon.jspf" %>
        <title>AutoPro - Chợ ô tô chuyên nghiệp</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
        <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
        <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@400;500;700&display=swap" rel="stylesheet">
        <style>
            body {
                font-family: 'Roboto', sans-serif;
                background-color: #f0f2f5;
            }

            .navbar {
                min-height: 88px;
                overflow: visible;
                background: #ffffff !important;
                box-shadow: 0 10px 28px rgba(15, 23, 42, 0.08);
            }

            .navbar-brand {
                display: flex !important;
                align-items: center;
                overflow: visible;
                padding-block: 4px;
            }

            .home-navbar-brand {
                padding: 4px 0;
            }

            .home-top-actions .btn-outline-light {
                border-color: #cbd5e1;
                color: #0f172a;
                background: #ffffff;
            }

            .home-top-actions .btn-outline-light:hover {
                border-color: #0f172a;
                background: #0f172a;
                color: #ffffff;
            }

            @media (max-width: 768px) {
                .navbar {
                    min-height: 80px;
                }

                .home-navbar-brand {
                    padding: 2px 0;
                }
            }

            .hero-section {
                background: linear-gradient(rgba(0, 0, 0, 0.62), rgba(0, 0, 0, 0.48)), url('https://images.unsplash.com/photo-1492144534655-ae79c964c9d7?auto=format&fit=crop&w=1500&q=80');
                background-size: cover;
                background-position: center;
                padding: 88px 0 132px;
                color: white;
                text-align: center;
            }

            .hero-section h1 {
                font-weight: 700;
                margin-bottom: 14px;
            }

            .hero-section p {
                max-width: 760px;
                margin: 0 auto;
                opacity: 0.92;
            }

            .filter-box {
                background: #fff;
                padding: 26px;
                border-radius: 20px;
                margin-top: -74px;
                box-shadow: 0 16px 38px rgba(15, 23, 42, 0.12);
            }

            .filter-box .form-label {
                font-weight: 700;
                color: #16213e;
            }

            .brand-strip {
                display: grid;
                grid-template-columns: repeat(auto-fit, minmax(102px, 1fr));
                gap: 12px;
                margin-bottom: 24px;
            }

            .brand-chip {
                border: 1px solid #d7deea;
                background: #fff;
                border-radius: 16px;
                padding: 12px 10px;
                display: flex;
                flex-direction: column;
                align-items: center;
                justify-content: center;
                gap: 8px;
                min-height: 94px;
                transition: all 0.2s ease;
            }

            .brand-chip:hover {
                border-color: #0d6efd;
                box-shadow: 0 10px 18px rgba(13, 110, 253, 0.12);
                transform: translateY(-2px);
            }

            .brand-chip.active {
                border-color: #0d6efd;
                background: #eff6ff;
                box-shadow: 0 10px 18px rgba(13, 110, 253, 0.16);
            }

            .brand-chip img {
                width: 42px;
                height: 42px;
                object-fit: contain;
            }

            .brand-chip span {
                font-size: 13px;
                font-weight: 600;
                color: #25324a;
                text-align: center;
                line-height: 1.2;
            }

            .quick-chip {
                border: 1px dashed #b7c3d9;
                background: #f8fafc;
            }

            .filter-caption {
                display: flex;
                justify-content: space-between;
                align-items: center;
                gap: 12px;
                margin-bottom: 14px;
            }

            .filter-caption h5 {
                margin: 0;
                font-weight: 700;
                color: #16213e;
            }

            .filter-caption span {
                font-size: 13px;
                color: #65758b;
            }

            .car-card {
                border: none;
                border-radius: 16px;
                overflow: hidden;
                transition: transform 0.25s, box-shadow 0.25s;
                background: #fff;
                height: 100%;
            }

            .car-card:hover {
                transform: translateY(-8px);
                box-shadow: 0 14px 28px rgba(0, 0, 0, 0.14);
            }

            .car-card.sold-card {
                border: 1px solid #d7eadf;
                background: #fbfffd;
            }

            .car-card.sold-card:hover {
                transform: none;
            }

            .car-img-container {
                position: relative;
                height: 220px;
                overflow: hidden;
                background: #e9ecef;
            }

            .car-img-container img {
                width: 100%;
                height: 100%;
                object-fit: cover;
            }

            .badge-condition {
                position: absolute;
                top: 12px;
                left: 12px;
                padding: 6px 12px;
                border-radius: 999px;
                font-size: 12px;
                font-weight: 700;
            }

            .badge-sold {
                position: absolute;
                top: 12px;
                left: 12px;
                padding: 7px 13px;
                border-radius: 999px;
                font-size: 12px;
                font-weight: 800;
                background: #198754;
                color: #ffffff;
                box-shadow: 0 10px 20px rgba(25, 135, 84, 0.24);
            }

            .car-title {
                font-size: 17px;
                font-weight: 700;
                color: #2f2f2f;
                margin-bottom: 10px;
                display: -webkit-box;
                -webkit-line-clamp: 2;
                -webkit-box-orient: vertical;
                overflow: hidden;
                min-height: 48px;
            }

            .car-price {
                color: #ee4d2d;
                font-size: 20px;
                font-weight: 700;
            }

            .sold-card .car-price {
                color: #64748b;
            }

            .car-info-small {
                font-size: 13px;
                color: #6c757d;
            }

            .section-head {
                display: flex;
                justify-content: space-between;
                align-items: center;
                gap: 16px;
                margin-bottom: 24px;
            }

            .section-head h3 {
                margin: 0;
                font-weight: 700;
            }

            .summary-wrap {
                display: flex;
                align-items: center;
                gap: 10px;
                flex-wrap: wrap;
            }

            .summary-chip {
                background: #fff3cd;
                color: #8a6200;
                padding: 8px 14px;
                border-radius: 999px;
                font-size: 13px;
                font-weight: 600;
            }

            .active-filter-chip {
                background: #e7f1ff;
                color: #0a58ca;
                padding: 8px 14px;
                border-radius: 999px;
                font-size: 13px;
                font-weight: 600;
            }

            .empty-state {
                background: white;
                border-radius: 16px;
                padding: 48px 24px;
                text-align: center;
                color: #6c757d;
            }

            @media (max-width: 768px) {
                .hero-section {
                    padding: 72px 0 116px;
                }

                .filter-box {
                    margin-top: -58px;
                    padding: 18px;
                }

                .brand-strip {
                    grid-template-columns: repeat(3, minmax(0, 1fr));
                }
            }
        </style>
    </head>
    <body>
        <nav class="navbar navbar-expand-lg sticky-top">
            <div class="container">
                <a class="navbar-brand home-navbar-brand d-inline-flex align-items-center" href="${pageContext.request.contextPath}/home">
                    <jsp:include page="/WEB-INF/includes/site-logo.jsp">
                        <jsp:param name="logoHeight" value="62px" />
                    </jsp:include>
                </a>
                <div class="home-top-actions d-flex align-items-center gap-2">
                    <c:if test="${sessionScope.user != null && !sessionScope.user.admin}">
                        <a href="chat-list" class="btn btn-outline-light btn-sm">Tin nhắn</a>
                        <c:if test="${sessionScope.user.seller}">
                            <a href="my-posts#sold-posts" class="btn btn-outline-warning btn-sm fw-semibold">
                                <i class="fa-solid fa-circle-check me-1"></i>Đã bán
                            </a>
                        </c:if>
                        <a href="lich-hen-cua-toi" class="btn btn-outline-light btn-sm">Lịch hẹn</a>
                    </c:if>
                    <c:choose>
                        <c:when test="${sessionScope.user == null}">
                            <a href="post-car" class="btn btn-warning btn-sm fw-bold">Đăng tin</a>
                        </c:when>
                        <c:when test="${sessionScope.user.admin}">
                        </c:when>
                        <c:when test="${sessionScope.user.seller}">
                            <a href="post-car" class="btn btn-warning btn-sm fw-bold">Đăng tin</a>
                        </c:when>
                        <c:otherwise>
                            <a href="seller-register/status" class="btn btn-warning btn-sm fw-bold">Đăng ký Seller</a>
                        </c:otherwise>
                    </c:choose>
                    <jsp:include page="/WEB-INF/includes/account-menu.jsp" />
                </div>
            </div>
        </nav>

        <header class="hero-section">
            <div class="container">
                <h1 class="display-5">Tìm chiếc xe phù hợp theo hãng, ngân sách và quãng đường di chuyển</h1>
                <p>Danh sách bên dưới chỉ hiển thị xe từ các bài đăng đã được duyệt. Bạn có thể lọc nhanh theo logo hãng hoặc đi sâu theo miền, tỉnh/thành, nhiên liệu, hộp số, số chỗ, số km và xuất xứ.</p>
            </div>
        </header>

        <div class="container mb-5">
            <div class="filter-box mb-5">
                <form id="filterForm" action="filter" method="get">
                    <input type="hidden" name="maHang" id="selectedBrandInput" value="${maHang}">

                    <div class="filter-caption">
                        <h5>Lọc nhanh theo hãng xe</h5>
                        <span>Bấm trực tiếp logo để lọc ngay, bấm lại logo đang chọn để bỏ lọc.</span>
                    </div>

                    <div class="brand-strip">
                        <button type="button" class="brand-chip quick-chip ${empty maHang ? 'active' : ''}" onclick="submitBrandFilter('')">
                            <i class="fa-solid fa-car-side fa-lg text-primary"></i>
                            <span>Tất cả hãng</span>
                        </button>
                        <c:forEach items="${listHangXe}" var="hang">
                            <button type="button" class="brand-chip ${maHang == hang.maHang ? 'active' : ''}" onclick="submitBrandFilter('${hang.maHang}')">
                                <img src="${hang.logoUrl}" alt="${hang.tenHang}" loading="lazy">
                                <span>${hang.tenHang}</span>
                            </button>
                        </c:forEach>
                    </div>

                    <div class="row g-3 align-items-end">
                        <div class="col-xl-3 col-lg-4 col-md-6">
                            <label class="form-label">Từ khóa</label>
                            <input type="text" name="keyword" class="form-control" value="${keyword}" placeholder="Tên xe, dòng xe, mô tả...">
                        </div>
                        <div class="col-xl-2 col-lg-4 col-md-6">
                            <label class="form-label">Mức giá</label>
                            <select class="form-select" name="giaRange">
                                <option value="" ${empty giaRange ? 'selected' : ''}>Tất cả</option>
                                <option value="under-300" ${giaRange == 'under-300' ? 'selected' : ''}>Dưới 300 triệu</option>
                                <option value="300-500" ${giaRange == '300-500' ? 'selected' : ''}>300 - 500 triệu</option>
                                <option value="500-700" ${giaRange == '500-700' ? 'selected' : ''}>500 - 700 triệu</option>
                                <option value="700-1000" ${giaRange == '700-1000' ? 'selected' : ''}>700 triệu - 1 tỷ</option>
                                <option value="1000-2000" ${giaRange == '1000-2000' ? 'selected' : ''}>1 - 2 tỷ</option>
                                <option value="over-2000" ${giaRange == 'over-2000' ? 'selected' : ''}>Trên 2 tỷ</option>
                            </select>
                        </div>
                        <div class="col-xl-2 col-lg-4 col-md-6">
                            <label class="form-label">Miền</label>
                            <select class="form-select" name="mien">
                                <option value="" ${empty mien ? 'selected' : ''}>Tất cả</option>
                                <c:forEach items="${listMien}" var="mienItem">
                                    <option value="${mienItem}" ${mien == mienItem ? 'selected' : ''}>${mienItem}</option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="col-xl-2 col-lg-4 col-md-6">
                            <label class="form-label">Tỉnh/Thành</label>
                            <select class="form-select" name="maTinh">
                                <option value="" ${empty maTinh ? 'selected' : ''}>Tất cả</option>
                                <c:forEach items="${listTinhThanh}" var="t">
                                    <option value="${t.maTinh}" ${maTinh == t.maTinh ? 'selected' : ''}>${t.tenTinh}</option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="col-xl-2 col-lg-4 col-md-6">
                            <label class="form-label">Kiểu dáng</label>
                            <select class="form-select" name="kieuDang">
                                <option value="" ${empty kieuDang ? 'selected' : ''}>Tất cả</option>
                                <option value="Sedan" ${kieuDang == 'Sedan' ? 'selected' : ''}>Sedan</option>
                                <option value="SUV" ${kieuDang == 'SUV' ? 'selected' : ''}>SUV</option>
                                <option value="Hatchback" ${kieuDang == 'Hatchback' ? 'selected' : ''}>Hatchback</option>
                                <option value="Pickup" ${kieuDang == 'Pickup' ? 'selected' : ''}>Pickup</option>
                                <option value="Coupe" ${kieuDang == 'Coupe' ? 'selected' : ''}>Coupe</option>
                            </select>
                        </div>
                        <div class="col-xl-1 col-lg-4 col-md-6">
                            <label class="form-label">Năm SX</label>
                            <select class="form-select" name="namSX">
                                <option value="" ${empty namSX ? 'selected' : ''}>-</option>
                                <c:forEach var="year" begin="2000" end="2026">
                                    <option value="${year}" ${namSX == year ? 'selected' : ''}>${year}</option>
                                </c:forEach>
                            </select>
                        </div>

                        <div class="col-xl-2 col-lg-3 col-md-6">
                            <label class="form-label">Nhiên liệu</label>
                            <select class="form-select" name="nhienLieu">
                                <option value="" ${empty nhienLieu ? 'selected' : ''}>Tất cả</option>
                                <option value="Xăng" ${nhienLieu == 'Xăng' ? 'selected' : ''}>Xăng</option>
                                <option value="Dầu (Diesel)" ${nhienLieu == 'Dầu (Diesel)' ? 'selected' : ''}>Dầu (Diesel)</option>
                                <option value="Điện" ${nhienLieu == 'Điện' ? 'selected' : ''}>Điện</option>
                                <option value="Hybrid" ${nhienLieu == 'Hybrid' ? 'selected' : ''}>Hybrid</option>
                            </select>
                        </div>
                        <div class="col-xl-2 col-lg-3 col-md-6">
                            <label class="form-label">Hộp số</label>
                            <select class="form-select" name="hopSo">
                                <option value="" ${empty hopSo ? 'selected' : ''}>Tất cả</option>
                                <option value="Tự động" ${hopSo == 'Tự động' ? 'selected' : ''}>Tự động</option>
                                <option value="Số sàn" ${hopSo == 'Số sàn' ? 'selected' : ''}>Số sàn</option>
                            </select>
                        </div>
                        <div class="col-xl-2 col-lg-3 col-md-6">
                            <label class="form-label">Tình trạng</label>
                            <select class="form-select" name="tinhTrang">
                                <option value="" ${empty tinhTrang ? 'selected' : ''}>Tất cả</option>
                                <option value="Cũ" ${tinhTrang == 'Cũ' ? 'selected' : ''}>Cũ</option>
                                <option value="Mới" ${tinhTrang == 'Mới' ? 'selected' : ''}>Mới</option>
                                <option value="Lướt" ${tinhTrang == 'Lướt' ? 'selected' : ''}>Lướt</option>
                            </select>
                        </div>
                        <div class="col-xl-2 col-lg-3 col-md-6">
                            <label class="form-label">Số chỗ</label>
                            <select class="form-select" name="soCho">
                                <option value="" ${empty soCho ? 'selected' : ''}>Tất cả</option>
                                <option value="2" ${soCho == '2' ? 'selected' : ''}>2 chỗ</option>
                                <option value="4" ${soCho == '4' ? 'selected' : ''}>4 chỗ</option>
                                <option value="5" ${soCho == '5' ? 'selected' : ''}>5 chỗ</option>
                                <option value="7" ${soCho == '7' ? 'selected' : ''}>7 chỗ</option>
                                <option value="8plus" ${soCho == '8plus' ? 'selected' : ''}>8 chỗ trở lên</option>
                            </select>
                        </div>
                        <div class="col-xl-2 col-lg-3 col-md-6">
                            <label class="form-label">Số km đã đi</label>
                            <select class="form-select" name="kmRange">
                                <option value="" ${empty kmRange ? 'selected' : ''}>Tất cả</option>
                                <option value="under-10000" ${kmRange == 'under-10000' ? 'selected' : ''}>Dưới 10.000 km</option>
                                <option value="10000-50000" ${kmRange == '10000-50000' ? 'selected' : ''}>10.000 - 50.000 km</option>
                                <option value="50000-100000" ${kmRange == '50000-100000' ? 'selected' : ''}>50.000 - 100.000 km</option>
                                <option value="over-100000" ${kmRange == 'over-100000' ? 'selected' : ''}>Trên 100.000 km</option>
                            </select>
                        </div>
                        <div class="col-xl-2 col-lg-3 col-md-6">
                            <label class="form-label">Xuất xứ</label>
                            <select class="form-select" name="xuatXu">
                                <option value="" ${empty xuatXu ? 'selected' : ''}>Tất cả</option>
                                <option value="Việt Nam" ${xuatXu == 'Việt Nam' ? 'selected' : ''}>Việt Nam</option>
                                <option value="Nhật Bản" ${xuatXu == 'Nhật Bản' ? 'selected' : ''}>Nhật Bản</option>
                                <option value="Hàn Quốc" ${xuatXu == 'Hàn Quốc' ? 'selected' : ''}>Hàn Quốc</option>
                                <option value="Đức" ${xuatXu == 'Đức' ? 'selected' : ''}>Đức</option>
                                <option value="Mỹ" ${xuatXu == 'Mỹ' ? 'selected' : ''}>Mỹ</option>
                                <option value="Thái Lan" ${xuatXu == 'Thái Lan' ? 'selected' : ''}>Thái Lan</option>
                            </select>
                        </div>
                        <div class="col-xl-2 col-lg-3 col-md-6">
                            <button type="submit" class="btn btn-primary w-100 fw-bold">
                                <i class="fas fa-magnifying-glass me-2"></i>Tìm kiếm
                            </button>
                        </div>
                        <div class="col-xl-1 col-lg-3 col-md-6">
                            <a href="home" class="btn btn-outline-secondary w-100">Reset</a>
                        </div>
                    </div>
                </form>
            </div>

            <div class="section-head">
                <h3>Danh sách xe</h3>
                <div class="summary-wrap">
                    <span class="summary-chip">${empty totalItems ? listXe.size() : totalItems} kết quả</span>
                    <c:if test="${not empty maHang}">
                        <c:forEach items="${listHangXe}" var="hang">
                            <c:if test="${maHang == hang.maHang}">
                                <span class="active-filter-chip">Đang lọc: ${hang.tenHang}</span>
                            </c:if>
                        </c:forEach>
                    </c:if>
                </div>
            </div>

            <c:choose>
                <c:when test="${not empty listXe}">
                    <div class="row row-cols-1 row-cols-md-2 row-cols-lg-4 g-4">
                        <c:forEach items="${listXe}" var="x">
                            <c:choose>
                                <c:when test="${empty x.anhDaiDien}">
                                    <c:set var="thumbnail" value="${pageContext.request.contextPath}/uploads/cars/car-placeholder.svg" />
                                </c:when>
                                <c:when test="${fn:startsWith(x.anhDaiDien, 'http://') or fn:startsWith(x.anhDaiDien, 'https://')}">
                                    <c:set var="thumbnail" value="${x.anhDaiDien}" />
                                </c:when>
                                <c:when test="${fn:startsWith(x.anhDaiDien, '/')}">
                                    <c:set var="thumbnail" value="${pageContext.request.contextPath}${x.anhDaiDien}" />
                                </c:when>
                                <c:otherwise>
                                    <c:set var="thumbnail" value="${pageContext.request.contextPath}/${x.anhDaiDien}" />
                                </c:otherwise>
                            </c:choose>

                            <div class="col">
                                <div class="car-card shadow-sm ${x.khoaYeuCauMua ? 'sold-card' : ''}">
                                    <div class="car-img-container">
                                        <img src="${thumbnail}" alt="${x.tenXe}" data-car-name="${x.tenXe}" data-context-path="${pageContext.request.contextPath}" loading="lazy" onerror="useAutoProCarFallback(this)">
                                        <c:choose>
                                            <c:when test="${x.daBan}">
                                                <span class="badge-sold"><i class="fa-solid fa-circle-check me-1"></i>Đã bán</span>
                                            </c:when>
                                            <c:when test="${x.dangGiaoDich}">
                                                <span class="badge-sold"><i class="fa-solid fa-handshake me-1"></i>Đang giao dịch</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge-condition ${x.tinhTrang == 'Mới' ? 'bg-success' : 'bg-warning'} text-dark">
                                                    ${empty x.tinhTrang ? 'Đang bán' : x.tinhTrang}
                                                </span>
                                            </c:otherwise>
                                        </c:choose>
                                    </div>
                                    <div class="card-body">
                                        <a href="detail?id=${x.maXe}" class="text-decoration-none">
                                            <h5 class="car-title">${x.tenXe}</h5>
                                        </a>
                                        <div class="car-price mb-2">
                                            <fmt:formatNumber value="${x.getGia()}" type="number" maxFractionDigits="0"/> đ
                                        </div>
                                        <div class="d-flex justify-content-between car-info-small">
                                            <span><i class="far fa-calendar-alt me-1"></i>${x.namSanXuat}</span>
                                            <span><i class="fas fa-gas-pump me-1"></i>${x.nhienLieu}</span>
                                        </div>
                                        <div class="d-flex justify-content-between car-info-small mt-2">
                                            <span><i class="fas fa-car me-1"></i>${x.kieuDang}</span>
                                            <span><i class="fas fa-map-marker-alt me-1"></i>${empty x.tenTinh ? 'Chưa rõ tỉnh' : x.tenTinh}</span>
                                        </div>
                                        <div class="d-flex justify-content-between car-info-small mt-2">
                                            <span><i class="fas fa-compass me-1"></i>${empty x.mien ? 'Chưa rõ miền' : x.mien}</span>
                                            <span><i class="fas fa-road me-1"></i><fmt:formatNumber value="${x.soKmDaDi}" type="number" maxFractionDigits="0"/> km</span>
                                        </div>
                                    </div>
                                    <div class="card-footer bg-white border-top-0 pb-3">
                                        <c:choose>
                                            <c:when test="${x.daBan}">
                                                <a href="detail?id=${x.maXe}" class="btn btn-outline-secondary btn-sm w-100">
                                                    <i class="fa-solid fa-lock me-1"></i> Đã bán
                                                </a>
                                            </c:when>
                                            <c:when test="${x.dangGiaoDich}">
                                                <a href="detail?id=${x.maXe}" class="btn btn-outline-warning btn-sm w-100">
                                                    <i class="fa-solid fa-lock me-1"></i> Đang giao dịch
                                                </a>
                                            </c:when>
                                            <c:otherwise>
                                                <a href="detail?id=${x.maXe}" class="btn btn-outline-primary btn-sm w-100">Xem chi tiết</a>
                                            </c:otherwise>
                                        </c:choose>
                                    </div>
                                </div>
                            </div>
                        </c:forEach>
                    </div>
                    <c:if test="${totalPages gt 1}">
                        <nav class="mt-4" aria-label="Phân trang danh sách xe">
                            <ul class="pagination justify-content-center flex-wrap gap-1">
                                <li class="page-item ${currentPage le 1 ? 'disabled' : ''}">
                                    <a class="page-link" href="${paginationBaseUrl}${currentPage - 1}">Trước</a>
                                </li>
                                <c:forEach var="p" begin="1" end="${totalPages}">
                                    <li class="page-item ${p == currentPage ? 'active' : ''}">
                                        <a class="page-link" href="${paginationBaseUrl}${p}">${p}</a>
                                    </li>
                                </c:forEach>
                                <li class="page-item ${currentPage ge totalPages ? 'disabled' : ''}">
                                    <a class="page-link" href="${paginationBaseUrl}${currentPage + 1}">Sau</a>
                                </li>
                            </ul>
                        </nav>
                    </c:if>
                </c:when>
                <c:otherwise>
                    <div class="empty-state">
                        <i class="fa-solid fa-car-side fa-3x mb-3 text-secondary"></i>
                        <h5 class="mb-2">Không tìm thấy xe phù hợp</h5>
                        <p class="mb-4">Hãy thử thay đổi bộ lọc, bỏ bớt hãng hoặc quay lại trang chủ để xem toàn bộ xe đã được duyệt.</p>
                        <a href="home" class="btn btn-warning">Xem toàn bộ xe</a>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>

        <script>
            function submitBrandFilter(maHang) {
                const brandInput = document.getElementById('selectedBrandInput');
                const nextValue = String(maHang || '');
                brandInput.value = brandInput.value === nextValue ? '' : nextValue;
                document.getElementById('filterForm').submit();
            }
        </script>
        <script src="${pageContext.request.contextPath}/assets/js/autopro-image-fallback.js"></script>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
        <jsp:include page="/WEB-INF/includes/site-footer.jsp" />

    </body>
</html>
