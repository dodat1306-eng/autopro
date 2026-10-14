<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Quản lý danh mục - AutoPro</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
        <style>
            body {
                background: #f3f4f6;
                font-family: Arial, sans-serif;
            }

            .wrap {
                max-width: 1320px;
                margin: 28px auto;
                background: #fff;
                border-radius: 18px;
                padding: 28px;
                box-shadow: 0 10px 28px rgba(0, 0, 0, 0.08);
            }

            .section-box {
                border: 1px solid #e5e7eb;
                border-radius: 18px;
                padding: 20px;
                height: 100%;
            }

            .section-box h4 {
                margin-bottom: 16px;
                font-weight: 700;
            }

            table td,
            table th {
                vertical-align: middle;
                font-size: 14px;
            }

            .hint-box {
                background: #eff6ff;
                border: 1px solid #bfdbfe;
                border-radius: 14px;
                padding: 14px 16px;
                color: #1d4ed8;
            }

            .table-form input,
            .table-form select {
                min-width: 110px;
            }
        </style>
    </head>
    <body>
        <div class="container wrap">
            <div class="d-flex justify-content-between align-items-center flex-wrap gap-3 mb-4">
                <div>
                    <div class="d-flex align-items-center gap-2 mb-2">
                        <a href="${pageContext.request.contextPath}/admin/dashboard" class="text-decoration-none d-inline-flex align-items-center">
                            <jsp:include page="/WEB-INF/includes/site-logo.jsp">
                                <jsp:param name="logoHeight" value="42px" />
                            </jsp:include>
                        </a>
                        <span class="badge bg-warning text-dark">Danh mục</span>
                    </div>
                    <h2 class="mb-1">Quản lý hãng xe và tỉnh/thành</h2>
                    <p class="text-muted mb-0">Module này được thêm để khớp với mô tả quyền admin trong báo cáo: quản lý danh mục hãng xe và tỉnh/thành dùng cho form đăng tin và bộ lọc tìm kiếm.</p>
                </div>
                <div class="d-flex gap-2">
                    <a href="${pageContext.request.contextPath}/admin/dashboard" class="btn btn-outline-dark"><i class="fa-solid fa-arrow-left me-2"></i>Về Dashboard</a>
                    <a href="${pageContext.request.contextPath}/home" class="btn btn-outline-secondary">Xem trang chủ</a>
                </div>
            </div>

            <div class="hint-box mb-4">
                <i class="fa-solid fa-circle-info me-2"></i>
                Không nên xóa danh mục đang được xe hoặc bài đăng sử dụng. Hệ thống sẽ chặn thao tác xóa trong các trường hợp đó để tránh làm hỏng dữ liệu.
            </div>

            <c:if test="${not empty adminCategorySuccess}">
                <div class="alert alert-success">${adminCategorySuccess}</div>
            </c:if>
            <c:if test="${not empty adminCategoryError}">
                <div class="alert alert-danger">${adminCategoryError}</div>
            </c:if>

            <div class="row g-4">
                <div class="col-12 col-xl-6">
                    <div class="section-box">
                        <h4><i class="fa-solid fa-car-side me-2 text-warning"></i>Danh mục hãng xe</h4>
                        <form action="${pageContext.request.contextPath}/admin/categories" method="post" class="row g-3 mb-4">
<input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                            <input type="hidden" name="entity" value="hangxe">
                            <input type="hidden" name="action" value="create">
                            <div class="col-md-2">
                                <label class="form-label">Mã</label>
                                <input type="number" name="maHang" class="form-control" required>
                            </div>
                            <div class="col-md-4">
                                <label class="form-label">Tên hãng</label>
                                <input type="text" name="tenHang" class="form-control" required>
                            </div>
                            <div class="col-md-3">
                                <label class="form-label">Slug</label>
                                <input type="text" name="slug" class="form-control" placeholder="Tự sinh nếu bỏ trống">
                            </div>
                            <div class="col-md-3">
                                <label class="form-label">Thứ tự</label>
                                <input type="number" name="thuTuSapXep" class="form-control" value="0">
                            </div>
                            <div class="col-md-6">
                                <label class="form-label">Logo URL</label>
                                <input type="text" name="logoUrl" class="form-control">
                            </div>
                            <div class="col-md-6">
                                <label class="form-label">Từ khóa đồng bộ</label>
                                <input type="text" name="tuKhoaDongBo" class="form-control" placeholder="VD: MERCEDES|MERCEDES-BENZ">
                            </div>
                            <div class="col-12">
                                <button type="submit" class="btn btn-warning fw-semibold"><i class="fa-solid fa-plus me-2"></i>Thêm hãng xe</button>
                            </div>
                        </form>

                        <div class="table-responsive">
                            <table class="table align-middle">
                                <thead>
                                    <tr>
                                        <th>Mã</th>
                                        <th>Tên hãng</th>
                                        <th>Logo</th>
                                        <th>Cập nhật</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach items="${hangXeList}" var="hang">
                                        <tr>
                                            <td>${hang.maHang}</td>
                                            <td colspan="3">
                                                <form action="${pageContext.request.contextPath}/admin/categories" method="post" class="row g-2 table-form">
<input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                                                    <input type="hidden" name="entity" value="hangxe">
                                                    <input type="hidden" name="maHang" value="${hang.maHang}">
                                                    <div class="col-md-3">
                                                        <input type="text" name="tenHang" class="form-control" value="${hang.tenHang}" required>
                                                    </div>
                                                    <div class="col-md-2">
                                                        <input type="text" name="slug" class="form-control" value="${hang.slug}">
                                                    </div>
                                                    <div class="col-md-3">
                                                        <input type="text" name="logoUrl" class="form-control" value="${hang.logoUrl}">
                                                    </div>
                                                    <div class="col-md-2">
                                                        <input type="text" name="tuKhoaDongBo" class="form-control" value="${hang.tuKhoaDongBo}">
                                                    </div>
                                                    <div class="col-md-1">
                                                        <input type="number" name="thuTuSapXep" class="form-control" value="${hang.thuTuSapXep}">
                                                    </div>
                                                    <div class="col-md-1 d-flex gap-2">
                                                        <button type="submit" name="action" value="update" class="btn btn-outline-primary btn-sm"><i class="fa-solid fa-floppy-disk"></i></button>
                                                        <button type="submit" name="action" value="delete" class="btn btn-outline-danger btn-sm" onclick="return confirm('Xóa hãng xe này khỏi danh mục?');"><i class="fa-solid fa-trash"></i></button>
                                                    </div>
                                                </form>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>

                <div class="col-12 col-xl-6">
                    <div class="section-box">
                        <h4><i class="fa-solid fa-location-dot me-2 text-warning"></i>Danh mục tỉnh/thành</h4>
                        <form action="${pageContext.request.contextPath}/admin/categories" method="post" class="row g-3 mb-4">
<input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                            <input type="hidden" name="entity" value="tinhthanh">
                            <input type="hidden" name="action" value="create">
                            <div class="col-md-3">
                                <label class="form-label">Mã tỉnh</label>
                                <input type="number" name="maTinh" class="form-control" required>
                            </div>
                            <div class="col-md-5">
                                <label class="form-label">Tên tỉnh/thành</label>
                                <input type="text" name="tenTinh" class="form-control" required>
                            </div>
                            <div class="col-md-4">
                                <label class="form-label">Miền</label>
                                <select name="mien" class="form-select" required>
                                    <option value="Miền Bắc">Miền Bắc</option>
                                    <option value="Miền Trung">Miền Trung</option>
                                    <option value="Tây Nguyên">Tây Nguyên</option>
                                    <option value="Miền Nam">Miền Nam</option>
                                </select>
                            </div>
                            <div class="col-12">
                                <button type="submit" class="btn btn-warning fw-semibold"><i class="fa-solid fa-plus me-2"></i>Thêm tỉnh/thành</button>
                            </div>
                        </form>

                        <div class="table-responsive">
                            <table class="table align-middle">
                                <thead>
                                    <tr>
                                        <th>Mã</th>
                                        <th>Tỉnh/Thành</th>
                                        <th>Miền</th>
                                        <th>Cập nhật</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach items="${tinhThanhList}" var="tinh">
                                        <tr>
                                            <td>${tinh.maTinh}</td>
                                            <td colspan="3">
                                                <form action="${pageContext.request.contextPath}/admin/categories" method="post" class="row g-2 table-form">
<input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                                                    <input type="hidden" name="entity" value="tinhthanh">
                                                    <input type="hidden" name="maTinh" value="${tinh.maTinh}">
                                                    <div class="col-md-5">
                                                        <input type="text" name="tenTinh" class="form-control" value="${tinh.tenTinh}" required>
                                                    </div>
                                                    <div class="col-md-4">
                                                        <select name="mien" class="form-select" required>
                                                            <option value="Miền Bắc" ${tinh.mien == 'Miền Bắc' ? 'selected' : ''}>Miền Bắc</option>
                                                            <option value="Miền Trung" ${tinh.mien == 'Miền Trung' ? 'selected' : ''}>Miền Trung</option>
                                                            <option value="Tây Nguyên" ${tinh.mien == 'Tây Nguyên' ? 'selected' : ''}>Tây Nguyên</option>
                                                            <option value="Miền Nam" ${tinh.mien == 'Miền Nam' ? 'selected' : ''}>Miền Nam</option>
                                                        </select>
                                                    </div>
                                                    <div class="col-md-3 d-flex gap-2">
                                                        <button type="submit" name="action" value="update" class="btn btn-outline-primary btn-sm"><i class="fa-solid fa-floppy-disk"></i></button>
                                                        <button type="submit" name="action" value="delete" class="btn btn-outline-danger btn-sm" onclick="return confirm('Xóa tỉnh/thành này khỏi danh mục?');"><i class="fa-solid fa-trash"></i></button>
                                                    </div>
                                                </form>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>
