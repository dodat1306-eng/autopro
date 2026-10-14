<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Đăng tin bán xe - AutoPro</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
        <style>
            body {
                background: #f5f6f8;
                font-family: Arial, sans-serif;
            }

            .form-container {
                max-width: 1100px;
                margin: 24px auto 40px;
                background: #fff;
                border-radius: 20px;
                padding: 32px;
                box-shadow: 0 10px 30px rgba(0, 0, 0, 0.08);
            }

            .section-title {
                border-left: 5px solid #ee4d2d;
                padding-left: 14px;
                margin-bottom: 18px;
                font-weight: 700;
            }

            .hint-box {
                background: #fff8e7;
                border: 1px solid #ffe19b;
                border-radius: 12px;
                padding: 14px 16px;
                color: #7b5a00;
            }

            .image-upload-placeholder {
                border: 2px dashed #cbd5e1;
                border-radius: 16px;
                padding: 36px;
                text-align: center;
                cursor: pointer;
                color: #64748b;
                background: #f8fafc;
            }

            .image-upload-placeholder:hover {
                border-color: #ee4d2d;
                background: #fff7f3;
            }
        </style>
    </head>
    <body>
        <div class="container py-4">
            <div class="d-flex justify-content-between align-items-center mb-3 flex-wrap gap-2">
                <div class="d-flex align-items-center gap-2">
                    <a href="${pageContext.request.contextPath}/home" class="text-decoration-none d-inline-flex align-items-center">
                        <jsp:include page="/WEB-INF/includes/site-logo.jsp">
                            <jsp:param name="logoHeight" value="46px" />
                        </jsp:include>
                    </a>
                    <a href="my-posts" class="btn btn-outline-secondary"><i class="fa-solid fa-layer-group me-2"></i>Tin của tôi</a>
                            <a href="lich-hen-cua-toi" class="btn btn-outline-primary"><i class="fa-solid fa-calendar-check me-2"></i>Lịch hẹn</a>
                </div>
                <div class="d-flex align-items-center gap-2">
                    <span class="badge bg-warning text-dark px-3 py-2">Tối đa ${maxImages} ảnh mỗi bài</span>
                    <jsp:include page="/WEB-INF/includes/account-menu.jsp" />
                </div>
            </div>

            <div class="form-container">
                <div class="d-flex justify-content-between align-items-start flex-wrap gap-3 mb-4">
                    <div>
                        <h2 class="mb-1">Đăng tin bán xe</h2>
                        <p class="text-muted mb-0">Điền đầy đủ thông tin xe để người mua dễ so sánh, hỏi giá và hẹn xem xe trực tiếp.</p>
                    </div>
                    <div class="hint-box">
                        Hệ thống hỗ trợ 34 tỉnh/thành, tự gắn miền theo tỉnh và cho phép chọn ảnh nhiều lần để cộng dồn trước khi đăng.
                    </div>
                </div>

                <c:if test="${not empty error}">
                    <div class="alert alert-danger">${error}</div>
                </c:if>

        <form action="post-car" method="post" enctype="multipart/form-data">
<input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                    <h4 class="section-title">Thông tin cơ bản</h4>
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Tiêu đề bài đăng</label>
                        <input type="text" class="form-control" name="tieuDe" value="${param.tieuDe}" placeholder="VD: BMW 320i 2022 chính chủ, bao check hãng" required>
                    </div>

                    <div class="row">
                        <div class="col-md-3 mb-3">
                            <label class="form-label fw-semibold">Hãng xe</label>
                            <select class="form-select" name="maHang" required>
                                <option value="">Chọn hãng xe</option>
                                <c:forEach items="${listHangXe}" var="hang">
                                    <option value="${hang.maHang}" ${param.maHang == hang.maHang ? 'selected' : ''}>${hang.tenHang}</option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="col-md-4 mb-3">
                            <label class="form-label fw-semibold">Tên xe / dòng xe</label>
                            <input type="text" class="form-control" name="tenXe" value="${param.tenXe}" placeholder="VD: BMW 320i" required>
                        </div>
                        <div class="col-md-5 mb-3">
                            <label class="form-label fw-semibold">Giá bán (VNĐ)</label>
                            <input type="number" min="1" class="form-control" name="giaBan" value="${param.giaBan}" placeholder="VD: 1500000000" required>
                        </div>
                    </div>

                    <h4 class="section-title mt-4">Thông số kỹ thuật</h4>
                    <div class="row">
                        <div class="col-md-3 mb-3">
                            <label class="form-label fw-semibold">Năm sản xuất</label>
                            <input type="number" min="1900" max="2100" class="form-control" name="namSanXuat" value="${empty param.namSanXuat ? '2023' : param.namSanXuat}" required>
                        </div>
                        <div class="col-md-3 mb-3">
                            <label class="form-label fw-semibold">Hộp số</label>
                            <select class="form-select" name="hopSo">
                                <option value="Tự động" ${param.hopSo == 'Tự động' || empty param.hopSo ? 'selected' : ''}>Tự động</option>
                                <option value="Số sàn" ${param.hopSo == 'Số sàn' ? 'selected' : ''}>Số sàn</option>
                            </select>
                        </div>
                        <div class="col-md-3 mb-3">
                            <label class="form-label fw-semibold">Nhiên liệu</label>
                            <select class="form-select" name="nhienLieu">
                                <option value="Xăng" ${param.nhienLieu == 'Xăng' || empty param.nhienLieu ? 'selected' : ''}>Xăng</option>
                                <option value="Dầu (Diesel)" ${param.nhienLieu == 'Dầu (Diesel)' ? 'selected' : ''}>Dầu (Diesel)</option>
                                <option value="Điện" ${param.nhienLieu == 'Điện' ? 'selected' : ''}>Điện</option>
                                <option value="Hybrid" ${param.nhienLieu == 'Hybrid' ? 'selected' : ''}>Hybrid</option>
                            </select>
                        </div>
                        <div class="col-md-3 mb-3">
                            <label class="form-label fw-semibold">Tình trạng</label>
                            <select class="form-select" name="tinhTrang">
                                <option value="Cũ" ${param.tinhTrang == 'Cũ' || empty param.tinhTrang ? 'selected' : ''}>Cũ</option>
                                <option value="Mới" ${param.tinhTrang == 'Mới' ? 'selected' : ''}>Mới</option>
                                <option value="Lướt" ${param.tinhTrang == 'Lướt' ? 'selected' : ''}>Lướt</option>
                            </select>
                        </div>
                    </div>

                    <div class="row">
                        <div class="col-md-3 mb-3">
                            <label class="form-label fw-semibold">Số km đã đi</label>
                            <input type="number" min="0" class="form-control" name="soKmDaDi" value="${param.soKmDaDi}" placeholder="VD: 20000">
                        </div>
                        <div class="col-md-3 mb-3">
                            <label class="form-label fw-semibold">Kiểu dáng</label>
                            <select class="form-select" name="kieuDang">
                                <option value="Sedan" ${param.kieuDang == 'Sedan' || empty param.kieuDang ? 'selected' : ''}>Sedan</option>
                                <option value="SUV" ${param.kieuDang == 'SUV' ? 'selected' : ''}>SUV</option>
                                <option value="Hatchback" ${param.kieuDang == 'Hatchback' ? 'selected' : ''}>Hatchback</option>
                                <option value="Pickup" ${param.kieuDang == 'Pickup' ? 'selected' : ''}>Pickup</option>
                                <option value="Coupe" ${param.kieuDang == 'Coupe' ? 'selected' : ''}>Coupe</option>
                            </select>
                        </div>
                        <div class="col-md-2 mb-3">
                            <label class="form-label fw-semibold">Số chỗ</label>
                            <input type="number" min="1" class="form-control" name="soCho" value="${empty param.soCho ? '5' : param.soCho}">
                        </div>
                        <div class="col-md-4 mb-3">
                            <label class="form-label fw-semibold">Tỉnh/Thành</label>
                            <select class="form-select" name="maTinh" id="maTinh" onchange="updateMienBadge()" required>
                                <option value="">Chọn tỉnh/thành</option>
                                <c:forEach items="${listTinhThanh}" var="t">
                                    <option value="${t.maTinh}" data-mien="${t.mien}" ${param.maTinh == t.maTinh ? 'selected' : ''}>${t.tenTinh}</option>
                                </c:forEach>
                            </select>
                            <div class="mt-2">
                                <span class="badge bg-light text-dark border" id="mienBadge">Chưa xác định</span>
                            </div>
                        </div>
                    </div>

                    <div class="row">
                        <div class="col-md-4 mb-3">
                            <label class="form-label fw-semibold">Màu xe</label>
                            <input type="text" class="form-control" name="mauXe" value="${param.mauXe}" placeholder="VD: Trắng ngọc trai">
                        </div>
                        <div class="col-md-4 mb-3">
                            <label class="form-label fw-semibold">Xuất xứ</label>
                            <input type="text" class="form-control" name="xuatXu" value="${empty param.xuatXu ? 'Việt Nam' : param.xuatXu}" placeholder="VD: Nhật Bản">
                        </div>
                    </div>

                    <h4 class="section-title mt-4">Mô tả chi tiết</h4>
                    <div class="mb-3">
                        <textarea class="form-control" name="moTa" rows="6" placeholder="Mô tả tình trạng xe, lịch sử bảo dưỡng, option, số lượng chìa khóa, hỗ trợ check hãng...">${param.moTa}</textarea>
                    </div>

                    <h4 class="section-title mt-4">Hình ảnh xe</h4>
                    <div class="mb-4">
                        <div class="image-upload-placeholder" onclick="document.getElementById('fileInput').click()">
                            <i class="fa-solid fa-cloud-arrow-up fa-3x mb-2"></i>
                            <p class="mb-1">Nhấn để thêm ảnh xe</p>
                            <small>Có thể bấm nhiều lần để cộng dồn ảnh. Ảnh đầu tiên sẽ là ảnh đại diện.</small>
                            <input type="file" id="fileInput" name="hinhAnh" class="d-none" multiple accept="image/*" onchange="previewImages()">
                        </div>
                        <div class="mt-2">
                            <span class="badge bg-light text-dark border" id="imageCountBadge">Đã chọn 0/${maxImages} ảnh</span>
                        </div>
                        <div id="imagePreview" class="row mt-3"></div>
                    </div>

                    <div class="d-flex justify-content-end gap-2">
                        <a href="my-posts" class="btn btn-light">Hủy</a>
                        <button type="submit" class="btn btn-primary">
                            <i class="fa-solid fa-paper-plane me-2"></i>Gửi admin duyệt
                        </button>
                    </div>
                </form>
            </div>
        </div>

        <script>
            const maxImages = ${maxImages};
            const fileInput = document.getElementById('fileInput');
            const imagePreview = document.getElementById('imagePreview');
            const imageCountBadge = document.getElementById('imageCountBadge');
            const selectedFiles = new DataTransfer();

            function updateMienBadge() {
                const select = document.getElementById('maTinh');
                const selectedOption = select.options[select.selectedIndex];
                const badge = document.getElementById('mienBadge');
                const mien = selectedOption ? selectedOption.getAttribute('data-mien') : '';
                badge.textContent = mien && mien.trim() !== '' ? mien : 'Chưa xác định';
            }

            function isDuplicateFile(file) {
                return Array.from(selectedFiles.files).some((existing) =>
                    existing.name === file.name
                    && existing.size === file.size
                    && existing.lastModified === file.lastModified
                );
            }

            function syncSelectedFiles() {
                fileInput.files = selectedFiles.files;
            }

            function removeSelectedImage(index) {
                selectedFiles.items.remove(index);
                syncSelectedFiles();
                renderSelectedImages();
            }

            function renderSelectedImages() {
                imagePreview.innerHTML = '';
                imageCountBadge.textContent = 'Đã chọn ' + selectedFiles.files.length + '/' + maxImages + ' ảnh';
                Array.from(selectedFiles.files).forEach((file, index) => {
                    const reader = new FileReader();
                    reader.onload = function (e) {
                        const col = document.createElement('div');
                        col.className = 'col-6 col-md-3 mb-3';
                        col.innerHTML =
                                '<div class="card h-100 shadow-sm">' +
                                '<img src="' + e.target.result + '" class="card-img-top" style="height: 150px; object-fit: cover;">' +
                                '<div class="card-body py-2 px-3 d-flex justify-content-between align-items-center gap-2">' +
                                '<small class="text-muted">' + (index === 0 ? 'Ảnh đại diện' : 'Ảnh #' + (index + 1)) + '</small>' +
                                '<button type="button" class="btn btn-sm btn-outline-danger" onclick="removeSelectedImage(' + index + ')"><i class="fa-solid fa-xmark"></i></button>' +
                                '</div>' +
                                '</div>';
                        imagePreview.appendChild(col);
                    };
                    reader.readAsDataURL(file);
                });
            }

            function previewImages() {
                const pickedFiles = Array.from(fileInput.files || []);
                if (!pickedFiles.length) {
                    return;
                }

                let nextTotal = selectedFiles.files.length;
                pickedFiles.forEach((file) => {
                    if (!isDuplicateFile(file)) {
                        nextTotal++;
                    }
                });

                if (nextTotal > maxImages) {
                    alert('Bạn chỉ được tải tối đa ' + maxImages + ' ảnh.');
                    syncSelectedFiles();
                    return;
                }

                pickedFiles.forEach((file) => {
                    if (!isDuplicateFile(file)) {
                        selectedFiles.items.add(file);
                    }
                });

                syncSelectedFiles();
                renderSelectedImages();
            }

            updateMienBadge();
        </script>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
        <jsp:include page="/WEB-INF/includes/site-footer.jsp" />

    </body>
</html>
