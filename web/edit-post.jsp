<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Sửa tin đăng - AutoPro</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
        <style>
            body {
                background: #f5f6f8;
                font-family: Arial, sans-serif;
            }

            .editor-wrap {
                max-width: 1100px;
                margin: 24px auto 40px;
                background: white;
                border-radius: 18px;
                padding: 30px;
                box-shadow: 0 10px 30px rgba(0, 0, 0, 0.08);
            }

            .section-title {
                font-size: 1.1rem;
                font-weight: 700;
                margin-bottom: 18px;
                padding-left: 14px;
                border-left: 4px solid #ee4d2d;
            }

            .image-tile {
                position: relative;
                border-radius: 12px;
                overflow: hidden;
                border: 1px solid #dee2e6;
                background: #f8f9fa;
            }

            .image-tile img {
                width: 100%;
                height: 130px;
                object-fit: cover;
            }

            .image-remove {
                position: absolute;
                top: 8px;
                right: 8px;
                border: 0;
                width: 30px;
                height: 30px;
                border-radius: 50%;
                background: rgba(0, 0, 0, 0.75);
                color: white;
            }

            .helper-box {
                background: #fff4d9;
                border: 1px solid #ffe08a;
                color: #7a5a00;
                border-radius: 12px;
                padding: 14px 16px;
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
                    <a href="my-posts" class="btn btn-outline-secondary">
                        <i class="fa-solid fa-arrow-left me-2"></i>Tin của tôi
                    </a>
                    <a href="lich-hen-cua-toi" class="btn btn-outline-primary">
                        <i class="fa-solid fa-calendar-check me-2"></i>Lịch hẹn
                    </a>
                </div>
                <div class="d-flex align-items-center gap-2">
                    <span class="badge bg-warning text-dark px-3 py-2">Sau khi cập nhật, bài đăng sẽ quay lại trạng thái chờ duyệt</span>
                    <jsp:include page="/WEB-INF/includes/account-menu.jsp" />
                </div>
            </div>

            <div class="editor-wrap">
                <div class="d-flex justify-content-between align-items-start flex-wrap gap-3 mb-4">
                    <div>
                        <h2 class="mb-1">Sửa tin đăng</h2>
                        <p class="text-muted mb-0">Cập nhật lại thông tin xe, hình ảnh và mô tả trước khi gửi duyệt lại.</p>
                    </div>
                    <span class="badge bg-light text-dark border px-3 py-2">Mã bài: #${baiDang.maBaiDang}</span>
                </div>

                <c:if test="${not empty error}">
                    <div class="alert alert-danger">${error}</div>
                </c:if>

                <c:if test="${not empty baiDang.ghiChuAdmin}">
                    <div class="alert alert-warning">
                        <strong>Ghi chú từ admin:</strong> ${baiDang.ghiChuAdmin}
                    </div>
                </c:if>

                <div class="helper-box mb-4">
                    <i class="fa-solid fa-circle-info me-2"></i>
                    Bạn có thể giữ ảnh cũ, xóa ảnh không còn dùng và thêm ảnh mới nhiều lần trước khi lưu. Tổng số ảnh tối đa là ${maxImages}.
                </div>

                <form action="edit-post" method="post" enctype="multipart/form-data">
<input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                    <input type="hidden" name="maBaiDang" value="${baiDang.maBaiDang}">
                    <input type="hidden" name="existingImages" id="existingImages">

                    <div class="section-title">Thông tin bài đăng</div>
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Tiêu đề bài đăng</label>
                        <input type="text" class="form-control" name="tieuDe" value="${baiDang.tieuDe}" required>
                    </div>

                    <div class="row">
                        <div class="col-md-3 mb-3">
                            <label class="form-label fw-semibold">Hãng xe</label>
                            <select class="form-select" name="maHang" required>
                                <option value="">Chọn hãng xe</option>
                                <c:forEach items="${listHangXe}" var="hang">
                                    <option value="${hang.maHang}" ${xe.maHang == hang.maHang ? 'selected' : ''}>${hang.tenHang}</option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="col-md-4 mb-3">
                            <label class="form-label fw-semibold">Tên xe</label>
                            <input type="text" class="form-control" name="tenXe" value="${xe.tenXe}" required>
                        </div>
                        <div class="col-md-5 mb-3">
                            <label class="form-label fw-semibold">Giá bán (VNĐ)</label>
                            <input type="number" min="1" class="form-control" name="giaBan" value="${xe.gia}" required>
                        </div>
                    </div>

                    <div class="section-title mt-3">Thông số xe</div>
                    <div class="row">
                        <div class="col-md-3 mb-3">
                            <label class="form-label fw-semibold">Năm sản xuất</label>
                            <input type="number" min="1900" max="2100" class="form-control" name="namSanXuat" value="${xe.namSanXuat}" required>
                        </div>
                        <div class="col-md-3 mb-3">
                            <label class="form-label fw-semibold">Hộp số</label>
                            <select class="form-select" name="hopSo">
                                <option value="Tự động" ${xe.hopSo == 'Tự động' ? 'selected' : ''}>Tự động</option>
                                <option value="Số sàn" ${xe.hopSo == 'Số sàn' ? 'selected' : ''}>Số sàn</option>
                            </select>
                        </div>
                        <div class="col-md-3 mb-3">
                            <label class="form-label fw-semibold">Nhiên liệu</label>
                            <select class="form-select" name="nhienLieu">
                                <option value="Xăng" ${xe.nhienLieu == 'Xăng' ? 'selected' : ''}>Xăng</option>
                                <option value="Dầu (Diesel)" ${xe.nhienLieu == 'Dầu (Diesel)' ? 'selected' : ''}>Dầu (Diesel)</option>
                                <option value="Điện" ${xe.nhienLieu == 'Điện' ? 'selected' : ''}>Điện</option>
                                <option value="Hybrid" ${xe.nhienLieu == 'Hybrid' ? 'selected' : ''}>Hybrid</option>
                            </select>
                        </div>
                        <div class="col-md-3 mb-3">
                            <label class="form-label fw-semibold">Tình trạng</label>
                            <select class="form-select" name="tinhTrang">
                                <option value="Cũ" ${xe.tinhTrang == 'Cũ' ? 'selected' : ''}>Cũ</option>
                                <option value="Mới" ${xe.tinhTrang == 'Mới' ? 'selected' : ''}>Mới</option>
                                <option value="Lướt" ${xe.tinhTrang == 'Lướt' ? 'selected' : ''}>Lướt</option>
                            </select>
                        </div>
                    </div>

                    <div class="row">
                        <div class="col-md-3 mb-3">
                            <label class="form-label fw-semibold">Số km đã đi</label>
                            <input type="number" min="0" class="form-control" name="soKmDaDi" value="${xe.soKmDaDi}">
                        </div>
                        <div class="col-md-3 mb-3">
                            <label class="form-label fw-semibold">Kiểu dáng</label>
                            <select class="form-select" name="kieuDang">
                                <option value="Sedan" ${xe.kieuDang == 'Sedan' ? 'selected' : ''}>Sedan</option>
                                <option value="SUV" ${xe.kieuDang == 'SUV' ? 'selected' : ''}>SUV</option>
                                <option value="Hatchback" ${xe.kieuDang == 'Hatchback' ? 'selected' : ''}>Hatchback</option>
                                <option value="Pickup" ${xe.kieuDang == 'Pickup' ? 'selected' : ''}>Pickup</option>
                                <option value="Coupe" ${xe.kieuDang == 'Coupe' ? 'selected' : ''}>Coupe</option>
                            </select>
                        </div>
                        <div class="col-md-2 mb-3">
                            <label class="form-label fw-semibold">Số chỗ</label>
                            <input type="number" min="1" class="form-control" name="soCho" value="${xe.soCho}">
                        </div>
                        <div class="col-md-4 mb-3">
                            <label class="form-label fw-semibold">Tỉnh/Thành</label>
                            <select class="form-select" name="maTinh" required>
                                <option value="">Chọn tỉnh/thành</option>
                                <c:forEach items="${listTinhThanh}" var="t">
                                    <option value="${t.maTinh}" ${baiDang.maTinh == t.maTinh ? 'selected' : ''}>${t.tenTinh}</option>
                                </c:forEach>
                            </select>
                        </div>
                    </div>

                    <div class="row">
                        <div class="col-md-4 mb-3">
                            <label class="form-label fw-semibold">Màu xe</label>
                            <input type="text" class="form-control" name="mauXe" value="${xe.mauXe}">
                        </div>
                        <div class="col-md-4 mb-3">
                            <label class="form-label fw-semibold">Xuất xứ</label>
                            <input type="text" class="form-control" name="xuatXu" value="${xe.xuatXu}">
                        </div>
                    </div>

                    <div class="section-title mt-3">Mô tả chi tiết</div>
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Mô tả</label>
                        <textarea class="form-control" name="moTa" rows="6" placeholder="Nhập tình trạng xe, lịch sử bảo dưỡng, option nổi bật...">${baiDang.moTa}</textarea>
                    </div>

                    <div class="section-title mt-3">Hình ảnh hiện tại</div>
                    <div class="row g-3 mb-3" id="existingImageGrid">
                        <c:choose>
                            <c:when test="${not empty xe.danhSachHinhAnh}">
                                <c:forEach items="${xe.danhSachHinhAnh}" var="img">
                                    <div class="col-6 col-md-3 existing-image-item" data-existing-image="${img}">
                                        <div class="image-tile">
                                            <c:choose>
                                                <c:when test="${fn:startsWith(img, 'http://') or fn:startsWith(img, 'https://')}">
                                                    <img src="${img}" alt="Ảnh xe">
                                                </c:when>
                                                <c:when test="${fn:startsWith(img, '/')}">
                                                    <img src="${pageContext.request.contextPath}${img}" alt="Ảnh xe">
                                                </c:when>
                                                <c:otherwise>
                                                    <img src="${pageContext.request.contextPath}/${img}" alt="Ảnh xe">
                                                </c:otherwise>
                                            </c:choose>
                                            <button class="image-remove" type="button" onclick="removeExistingImage(this)">
                                                <i class="fa-solid fa-xmark"></i>
                                            </button>
                                        </div>
                                    </div>
                                </c:forEach>
                            </c:when>
                            <c:otherwise>
                                <div class="col-12 text-muted" id="noExistingImages">Chưa có ảnh nào được lưu cho bài đăng này.</div>
                            </c:otherwise>
                        </c:choose>
                    </div>

                    <div class="section-title mt-3">Thêm ảnh mới</div>
                    <div class="mb-3">
                        <input type="file" class="form-control" id="fileInput" name="hinhAnh" accept="image/*" multiple onchange="previewNewImages()">
                        <div class="form-text">Bạn có thể chọn ảnh nhiều lần để cộng dồn. Dùng nút X để bỏ ảnh cũ hoặc ảnh mới trước khi lưu.</div>
                        <div class="mt-2">
                            <span class="badge bg-light text-dark border" id="imageCountBadge">Đang giữ 0/${maxImages} ảnh</span>
                        </div>
                    </div>
                    <div class="row g-3" id="newImagePreview"></div>

                    <div class="d-flex justify-content-end gap-2 mt-4">
                        <a href="my-posts" class="btn btn-outline-secondary">Hủy</a>
                        <button type="submit" class="btn btn-primary">
                            <i class="fa-solid fa-floppy-disk me-2"></i>Cập nhật bài đăng
                        </button>
                    </div>
                </form>
            </div>
        </div>

        <script>
            const maxImages = ${maxImages};
            const existingImagesField = document.getElementById('existingImages');
            const existingImageGrid = document.getElementById('existingImageGrid');
            const newImagePreview = document.getElementById('newImagePreview');
            const fileInput = document.getElementById('fileInput');
            const imageCountBadge = document.getElementById('imageCountBadge');
            const selectedFiles = new DataTransfer();

            function syncExistingImages() {
                const currentImages = Array.from(document.querySelectorAll('[data-existing-image]'))
                        .map((item) => item.dataset.existingImage)
                        .filter(Boolean);
                existingImagesField.value = currentImages.join(',');
                imageCountBadge.textContent = 'Đang giữ ' + (currentImages.length + selectedFiles.files.length) + '/' + maxImages + ' ảnh';
            }

            function removeExistingImage(button) {
                const imageItem = button.closest('.existing-image-item');
                if (imageItem) {
                    imageItem.remove();
                }

                if (!document.querySelector('[data-existing-image]')) {
                    const emptyState = document.createElement('div');
                    emptyState.className = 'col-12 text-muted';
                    emptyState.id = 'noExistingImages';
                    emptyState.textContent = 'Chưa có ảnh nào được lưu cho bài đăng này.';
                    existingImageGrid.appendChild(emptyState);
                }

                syncExistingImages();
                renderNewImages();
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

            function removeNewImage(index) {
                selectedFiles.items.remove(index);
                syncSelectedFiles();
                renderNewImages();
            }

            function renderNewImages() {
                newImagePreview.innerHTML = '';
                syncExistingImages();
                Array.from(selectedFiles.files).forEach((file, index) => {
                    const reader = new FileReader();
                    reader.onload = function (e) {
                        const col = document.createElement('div');
                        col.className = 'col-6 col-md-3';
                        col.innerHTML =
                                '<div class="image-tile">' +
                                '<img src="' + e.target.result + '" alt="Ảnh mới">' +
                                '<button type="button" class="image-remove" onclick="removeNewImage(' + index + ')"><i class="fa-solid fa-xmark"></i></button>' +
                                '</div>';
                        newImagePreview.appendChild(col);
                    };
                    reader.readAsDataURL(file);
                });
            }

            function previewNewImages() {
                const pickedFiles = Array.from(fileInput.files || []);
                if (!pickedFiles.length) {
                    return;
                }

                const existingCount = document.querySelectorAll('[data-existing-image]').length;
                let nextTotal = existingCount + selectedFiles.files.length;

                pickedFiles.forEach((file) => {
                    if (!isDuplicateFile(file)) {
                        nextTotal++;
                    }
                });

                if (nextTotal > maxImages) {
                    alert('Tổng số ảnh không được vượt quá ' + maxImages + '.');
                    syncSelectedFiles();
                    return;
                }

                pickedFiles.forEach((file) => {
                    if (!isDuplicateFile(file)) {
                        selectedFiles.items.add(file);
                    }
                });

                syncSelectedFiles();
                renderNewImages();
            }

            syncExistingImages();
        </script>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
        <jsp:include page="/WEB-INF/includes/site-footer.jsp" />

    </body>
</html>
