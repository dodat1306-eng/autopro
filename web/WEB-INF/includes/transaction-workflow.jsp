<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<section class="card border-0 shadow-sm my-4"><div class="card-body p-4">
<c:if test="${giaoDich.trangThai eq 'MOI_TAO'}">
<h2 class="h5">Chờ người bán tiếp nhận</h2><p>Người bán có 48 giờ để chấp nhận hoặc từ chối. Trong thời gian chờ, xe vẫn nhận yêu cầu từ khách khác.</p>
<c:if test="${sessionScope.user.maNguoiDung eq giaoDich.maNguoiBan}"><form method="post" action="${pageContext.request.contextPath}/giao-dich/xu-ly">
<input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}"><input type="hidden" name="maGiaoDich" value="${giaoDich.maGiaoDich}"><input type="hidden" name="action" value="accept"><button class="btn btn-warning fw-bold">Chấp nhận giao dịch</button></form><p class="small text-muted mt-2">Để từ chối, dùng nút Hủy giao dịch và ghi rõ lý do.</p></c:if>
</c:if>
<c:if test="${!sessionScope.user.admin and (giaoDich.trangThai eq 'CHO_XAC_NHAN_HAI_BEN' or giaoDich.trangThai eq 'CHO_XAC_NHAN_CON_LAI' or giaoDich.trangThai eq 'TRANH_CHAP')}">
<details><summary class="fw-bold text-danger">Gửi khiếu nại hoặc bổ sung bằng chứng</summary>
<p class="small mt-3">Nếu có bất đồng, gửi thông tin để admin xem xét. Xác nhận giao xe/nhận xe sẽ được tạm dừng khi đang xử lý khiếu nại.</p>
<form method="post" enctype="multipart/form-data" action="${pageContext.request.contextPath}/giao-dich/xu-ly">
<input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
<input type="hidden" name="maGiaoDich" value="${giaoDich.maGiaoDich}"><input type="hidden" name="action" value="dispute">
<label class="form-label" for="dispute-reason">Nội dung khiếu nại</label><textarea id="dispute-reason" class="form-control mb-3" name="reason" minlength="10" maxlength="2000" rows="4" required></textarea>
<label class="form-label" for="evidence">Ảnh bằng chứng (không bắt buộc, tối đa 5 MB)</label><input id="evidence" class="form-control mb-3" type="file" name="evidence" accept="image/png,image/jpeg">
<button class="btn btn-outline-danger">Gửi tới admin</button></form></details>
</c:if>
<h2 class="h5 mt-4">Lịch sử xử lý</h2>
<c:forEach items="${transactionHistory}" var="entry"><div class="border-top py-3">
<strong><c:out value="${entry.hanhDong}"/></strong><div class="small text-muted"><fmt:formatDate value="${entry.ngayTao}" pattern="dd/MM/yyyy HH:mm"/> · <c:out value="${empty entry.hoTen ? 'Hệ thống' : entry.hoTen}"/></div>
<p class="mb-1" style="white-space:pre-wrap"><c:out value="${entry.ghiChu}"/></p>
<c:if test="${not empty entry.tepDinhKem}"><a target="_blank" rel="noopener" href="${pageContext.request.contextPath}/private-document?type=evidence&amp;id=${entry.maNhatKy}">Xem ảnh bằng chứng</a></c:if>
</div></c:forEach><c:if test="${empty transactionHistory}"><p class="small text-muted mb-0">Chưa có thao tác mới được ghi nhận trong nhật ký.</p></c:if>
</div></section>
