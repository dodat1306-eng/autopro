<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Chi tiết bài đăng - AutoPro Admin</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
        <style>
            body {
                background: #f5f6f8;
                font-family: Arial, sans-serif;
            }

            .page-wrap {
                max-width: 1220px;
                margin: 36px auto;
            }

            .thumb {
                width: 100%;
                height: 180px;
                object-fit: cover;
                border-radius: 12px;
                border: 1px solid #e5e7eb;
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

            .status-approved {
                background: #e8f1ff;
                color: #175cd3;
            }

            .status-sold {
                background: #d1e7dd;
                color: #146c43;
            }

            .status-trading {
                background: #fff3cd;
                color: #946200;
            }
        </style>
    </head>
    <body>
        <c:set var="ctx" value="${pageContext.request.contextPath}" />
        <c:set var="canApprove" value="${baiDang.trangThai eq 'cho_duyet' or baiDang.trangThai eq 'canh_bao'}" />
        <c:set var="canReject" value="${baiDang.trangThai eq 'cho_duyet' or baiDang.trangThai eq 'canh_bao'}" />
        <c:set var="canWarn" value="${baiDang.trangThai eq 'cho_duyet'}" />
        <c:set var="showActionForm" value="${canApprove or canReject or canWarn}" />

        <div class="page-wrap container">
            <div class="d-flex justify-content-between align-items-center flex-wrap gap-2 mb-3">
                <div>
                    <h2 class="mb-1">Chi tiết bài đăng #${baiDang.maBaiDang}</h2>
                    <div class="text-muted">Xem thông tin xe, chủ bài đăng, lịch sử báo cáo và thực hiện kiểm duyệt bài đăng.</div>
                </div>
                <div class="d-flex gap-2 flex-wrap">
                    <a href="${ctx}/admin/dashboard" class="btn btn-outline-secondary">
                        <i class="fa-solid fa-arrow-left me-2"></i>Về Dashboard
                    </a>
                    <a href="${ctx}/admin/posts" class="btn btn-outline-dark">Danh sách bài đăng</a>
                    <c:if test="${baiDang.trangThai eq 'da_ban' and not empty baiDang.maGiaoDich}">
                        <a href="${ctx}/admin/giao-dich?id=${baiDang.maGiaoDich}" class="btn btn-primary">
                            <i class="fa-solid fa-file-invoice-dollar me-2"></i>Chi tiết giao dịch
                        </a>
                    </c:if>
                    <a href="${ctx}/admin/reports" class="btn btn-outline-danger">Quản lý báo cáo</a>
                </div>
            </div>

            <c:if test="${param.result == 'approved'}">
                <div class="alert alert-success">Bài đăng đã được duyệt và hiển thị công khai.</div>
            </c:if>
            <c:if test="${param.result == 'rejected'}">
                <div class="alert alert-danger">Bài đăng đã bị từ chối. Ghi chú của admin đã được lưu cho người bán.</div>
            </c:if>
            <c:if test="${param.result == 'warned'}">
                <div class="alert alert-warning">Admin đã cảnh báo bài đăng này và thông báo tới người bán.</div>
            </c:if>
            <c:if test="${param.result == 'error'}">
                <div class="alert alert-danger">Không thể xử lý bài đăng lúc này. Vui lòng thử lại.</div>
            </c:if>
            <c:if test="${param.result == 'invalid_state'}">
                <div class="alert alert-warning">Bài đăng này đã được xử lý ở trạng thái khác nên thao tác vừa rồi không còn hợp lệ.</div>
            </c:if>

            <div class="row g-4">
                <div class="col-lg-8">
                    <div class="card shadow-sm border-0 mb-4">
                        <div class="card-header bg-primary text-white">Nội dung bài đăng</div>
                        <div class="card-body">
                            <div class="d-flex justify-content-between align-items-start flex-wrap gap-3 mb-3">
                                <div>
                                    <h4 class="mb-2">${baiDang.tieuDe}</h4>
                                    <div class="text-muted">
                                        Ngày đăng:
                                        <fmt:formatDate value="${baiDang.ngayDang}" pattern="dd/MM/yyyy HH:mm" />
                                    </div>
                                </div>
                                <div>
                                    <c:choose>
                                        <c:when test="${baiDang.trangThai eq 'cho_duyet'}">
                                            <span class="status-pill status-pending"><i class="fa-solid fa-hourglass-half"></i>Chờ duyệt</span>
                                        </c:when>
                                        <c:when test="${baiDang.trangThai eq 'canh_bao'}">
                                            <span class="status-pill status-warned"><i class="fa-solid fa-triangle-exclamation"></i>Cảnh báo</span>
                                        </c:when>
                                        <c:when test="${baiDang.trangThai eq 'tu_choi'}">
                                            <span class="status-pill status-removed"><i class="fa-solid fa-ban"></i>Từ chối</span>
                                        </c:when>
                                        <c:when test="${baiDang.trangThai eq 'da_ban'}">
                                            <span class="status-pill status-sold"><i class="fa-solid fa-circle-check"></i>Đã bán</span>
                                        </c:when>
                                        <c:when test="${baiDang.trangThai eq 'dang_giao_dich'}">
                                            <span class="status-pill status-trading"><i class="fa-solid fa-handshake"></i>Đang giao dịch</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="status-pill status-approved"><i class="fa-solid fa-circle-check"></i>Đã duyệt</span>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                            </div>
                            <p class="mb-0">${empty baiDang.moTa ? 'Người bán chưa bổ sung mô tả cho bài đăng này.' : baiDang.moTa}</p>
                        </div>
                    </div>

                    <div class="card shadow-sm border-0 mb-4">
                        <div class="card-header bg-light">Thông tin xe</div>
                        <div class="card-body">
                            <div class="row">
                                <div class="col-md-6 mb-2"><strong>Tên xe:</strong> ${xe.tenXe}</div>
                                <div class="col-md-6 mb-2"><strong>Giá bán:</strong> <fmt:formatNumber value="${xe.gia}" type="number" maxFractionDigits="0"/> VNĐ</div>
                                <div class="col-md-6 mb-2"><strong>Năm sản xuất:</strong> ${xe.namSanXuat}</div>
                                <div class="col-md-6 mb-2"><strong>Tình trạng:</strong> ${empty xe.tinhTrang ? 'Chưa cập nhật' : xe.tinhTrang}</div>
                                <div class="col-md-6 mb-2"><strong>Kiểu dáng:</strong> ${empty xe.kieuDang ? 'Chưa cập nhật' : xe.kieuDang}</div>
                                <div class="col-md-6 mb-2"><strong>Nhiên liệu:</strong> ${empty xe.nhienLieu ? 'Chưa cập nhật' : xe.nhienLieu}</div>
                                <div class="col-md-6 mb-2"><strong>Hộp số:</strong> ${empty xe.hopSo ? 'Chưa cập nhật' : xe.hopSo}</div>
                                <div class="col-md-6 mb-2"><strong>Số km đã đi:</strong> <fmt:formatNumber value="${xe.soKmDaDi}" type="number" maxFractionDigits="0"/></div>
                                <div class="col-md-6 mb-2"><strong>Số chỗ:</strong> ${xe.soCho > 0 ? xe.soCho : 'Chưa cập nhật'}</div>
                                <div class="col-md-6 mb-2"><strong>Tỉnh/Thành:</strong> ${empty xe.tenTinh ? 'Chưa cập nhật' : xe.tenTinh}</div>
                                <div class="col-md-6 mb-2"><strong>Màu xe:</strong> ${empty xe.mauXe ? 'Chưa cập nhật' : xe.mauXe}</div>
                                <div class="col-md-6 mb-2"><strong>Xuất xứ:</strong> ${empty xe.xuatXu ? 'Chưa cập nhật' : xe.xuatXu}</div>
                            </div>
                        </div>
                    </div>

                    <div class="card shadow-sm border-0 mb-4">
                        <div class="card-header bg-light">Ảnh xe</div>
                        <div class="card-body">
                            <div class="row g-3">
                                <c:choose>
                                    <c:when test="${not empty xe.danhSachHinhAnh}">
                                        <c:forEach items="${xe.danhSachHinhAnh}" var="img">
                                            <div class="col-6 col-md-4">
                                                <c:choose>
                                                    <c:when test="${fn:startsWith(img, 'http://') or fn:startsWith(img, 'https://')}">
                                                        <img src="${img}" alt="${xe.tenXe}" class="thumb" data-car-name="${xe.tenXe}" data-context-path="${ctx}" loading="lazy" onerror="useAutoProCarFallback(this)">
                                                    </c:when>
                                                    <c:when test="${fn:startsWith(img, '/')}">
                                                        <img src="${ctx}${img}" alt="${xe.tenXe}" class="thumb" data-car-name="${xe.tenXe}" data-context-path="${ctx}" loading="lazy" onerror="useAutoProCarFallback(this)">
                                                    </c:when>
                                                    <c:otherwise>
                                                        <img src="${ctx}/${img}" alt="${xe.tenXe}" class="thumb" data-car-name="${xe.tenXe}" data-context-path="${ctx}" loading="lazy" onerror="useAutoProCarFallback(this)">
                                                    </c:otherwise>
                                                </c:choose>
                                            </div>
                                        </c:forEach>
                                    </c:when>
                                    <c:otherwise>
                                        <div class="text-muted">Bài đăng này chưa có ảnh.</div>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </div>
                    </div>

                    <div class="card shadow-sm border-0">
                        <div class="card-header bg-light d-flex justify-content-between align-items-center flex-wrap gap-2">
                            <span>Lịch sử báo cáo</span>
                            <span class="badge bg-danger">${empty baoCaoList ? 0 : fn:length(baoCaoList)} báo cáo</span>
                        </div>
                        <div class="card-body">
                            <c:choose>
                                <c:when test="${not empty baoCaoList}">
                                    <div class="table-responsive">
                                        <table class="table table-sm align-middle">
                                            <thead>
                                                <tr>
                                                    <th>Người báo cáo</th>
                                                    <th>Lý do</th>
                                                    <th>Chi tiết</th>
                                                    <th>Trạng thái</th>
                                                </tr>
                                            </thead>
                                            <tbody>
                                                <c:forEach items="${baoCaoList}" var="baoCao">
                                                    <tr>
                                                        <td>
                                                            <div class="fw-semibold">${empty baoCao.tenNguoiBaoCao ? 'Ẩn danh' : baoCao.tenNguoiBaoCao}</div>
                                                            <div class="small text-muted">${baoCao.emailNguoiBaoCao}</div>
                                                            <div class="small text-muted">${baoCao.ngayBaoCao}</div>
                                                        </td>
                                                        <td>${baoCao.lyDo}</td>
                                                        <td>
                                                            <div>${empty baoCao.chiTiet ? '-' : baoCao.chiTiet}</div>
                                                            <c:if test="${not empty baoCao.ghiChuAdmin}">
                                                                <div class="small text-muted mt-1">Ghi chú admin: ${baoCao.ghiChuAdmin}</div>
                                                            </c:if>
                                                        </td>
                                                        <td>
                                                            <c:choose>
                                                                <c:when test="${baoCao.trangThai eq 'CHO_XU_LY'}">
                                                                    <span class="status-pill status-pending">Chờ xử lý</span>
                                                                </c:when>
                                                                <c:when test="${baoCao.trangThai eq 'DA_CANH_BAO'}">
                                                                    <span class="status-pill status-warned">Đã cảnh báo</span>
                                                                </c:when>
                                                                <c:when test="${baoCao.trangThai eq 'DA_XOA_BAI'}">
                                                                    <span class="status-pill status-removed">Đã xóa bài</span>
                                                                </c:when>
                                                                <c:otherwise>
                                                                    <span class="status-pill status-approved">Bỏ qua</span>
                                                                </c:otherwise>
                                                            </c:choose>
                                                        </td>
                                                    </tr>
                                                </c:forEach>
                                            </tbody>
                                        </table>
                                    </div>
                                </c:when>
                                <c:otherwise>
                                    <div class="text-muted">Bài đăng này hiện chưa có báo cáo nào từ người dùng.</div>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>
                </div>

                <div class="col-lg-4">
                    <div class="card shadow-sm border-0 mb-4">
                        <div class="card-header bg-warning text-dark">Thông tin người đăng</div>
                        <div class="card-body">
                            <p><strong>Họ tên:</strong> ${nguoiDang.hoTen}</p>
                            <p><strong>Email:</strong> ${empty nguoiDang.email ? 'Chưa cập nhật' : nguoiDang.email}</p>
                            <p><strong>Số điện thoại:</strong> ${empty nguoiDang.soDienThoai ? 'Chưa cập nhật' : nguoiDang.soDienThoai}</p>
                            <p class="mb-0"><strong>Trạng thái tài khoản:</strong> ${empty nguoiDang.trangThai ? 'active' : nguoiDang.trangThai}</p>
                        </div>
                    </div>

                    <div class="card shadow-sm border-0">
                        <div class="card-header bg-light">Hành động của admin</div>
                        <div class="card-body">
                            <c:choose>
                                <c:when test="${showActionForm}">
                                    <form action="${ctx}/admin/posts" method="post">
<input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                                        <input type="hidden" name="id" value="${baiDang.maBaiDang}">
                                        <input type="hidden" name="returnToDetail" value="1">

                                        <div class="mb-3">
                                            <label class="form-label fw-semibold">Ghi chú gửi người bán</label>
                                            <textarea name="ghiChuAdmin" class="form-control" rows="4" placeholder="Nhập lý do cảnh báo, yêu cầu chỉnh sửa hoặc giải thích quyết định của admin...">${baiDang.ghiChuAdmin}</textarea>
                                        </div>

                                        <div class="d-grid gap-2">
                                            <c:if test="${canApprove}">
                                                <button type="submit" name="action" value="approve" class="btn btn-success">
                                                    <i class="fa-solid fa-circle-check me-2"></i>Phê duyệt bài này
                                                </button>
                                            </c:if>
                                            <c:if test="${canWarn}">
                                                <button type="submit" name="action" value="warn" class="btn btn-warning">
                                                    <i class="fa-solid fa-triangle-exclamation me-2"></i>Cảnh báo người bán
                                                </button>
                                            </c:if>
                                            <c:if test="${canReject}">
                                                <button type="submit" name="action" value="reject" class="btn btn-danger">
                                                    <i class="fa-solid fa-ban me-2"></i>Từ chối bài này
                                                </button>
                                            </c:if>
                                            <button type="submit" name="action" value="delete" class="btn btn-outline-danger"
                                                    onclick="return confirm('Bạn có chắc muốn xóa hẳn bài đăng này khỏi hệ thống không?');">
                                                <i class="fa-solid fa-trash me-2"></i>Xóa hẳn bài đăng
                                            </button>
                                        </div>
                                    </form>
                                </c:when>
                                <c:otherwise>
                                    <div class="alert alert-light border mb-0">
                                        Bài đăng này đã được xử lý xong. Các nút duyệt, cảnh báo và từ chối đã được ẩn để tránh thao tác lặp.
                                    </div>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <script src="${ctx}/assets/js/autopro-image-fallback.js"></script>
    </body>
</html>
