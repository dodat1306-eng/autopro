<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!doctype html><html lang="vi"><head>
<meta charset="UTF-8"><meta name="viewport" content="width=device-width,initial-scale=1"><title>Lịch xem xe - AutoPro</title>
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
<style>body{background:#f4f6f9;color:#132238}.appointment-shell{max-width:1080px;margin:32px auto;padding:16px}.appointment-panel{background:white;border:1px solid #e1e7ef;border-radius:20px;padding:24px}.form-label{font-weight:600}.form-control{margin-bottom:16px}</style>
</head><body><main class="appointment-shell">
<nav class="d-flex gap-2 flex-wrap mb-4"><a class="btn btn-outline-dark" href="${pageContext.request.contextPath}/home">Trang chủ</a><a class="btn btn-outline-secondary" href="${pageContext.request.contextPath}/lich-hen-cua-toi">Lịch hẹn của tôi</a></nav>
<c:if test="${not empty sessionScope.appointmentError}"><div class="alert alert-danger"><c:out value="${sessionScope.appointmentError}"/></div><c:remove var="appointmentError" scope="session"/></c:if>
<c:if test="${not empty sessionScope.appointmentSuccess}"><div class="alert alert-success"><c:out value="${sessionScope.appointmentSuccess}"/></div><c:remove var="appointmentSuccess" scope="session"/></c:if>
<h1 class="h3 mb-4">Lịch xem xe <c:out value="${appointment.tenXe}"/></h1>
<div class="row g-4"><div class="col-lg-6"><section class="appointment-panel h-100">
<h2 class="h5">Lịch hẹn hiện tại</h2>
<p><strong>Thời gian:</strong> <fmt:formatDate value="${appointment.thoiGianHen}" pattern="dd/MM/yyyy HH:mm"/></p>
<p><strong>Địa điểm:</strong> <c:out value="${appointment.diaDiemHen}"/></p>
<p><strong>Ghi chú:</strong> <c:out value="${appointment.ghiChuHen}"/></p>
<p class="badge bg-warning text-dark p-2"><c:choose>
<c:when test="${appointment.tinhTrangHen eq 'CHO_XAC_NHAN'}">Chờ bên còn lại xác nhận lịch</c:when>
<c:when test="${appointment.tinhTrangHen eq 'DA_XAC_NHAN'}">Hai bên đã xác nhận lịch</c:when>
<c:when test="${appointment.tinhTrangHen eq 'DA_XEM_XE'}">Đã xem xe</c:when>
<c:when test="${appointment.tinhTrangHen eq 'KHONG_DEN'}">Người bán ghi nhận khách không đến</c:when>
<c:otherwise>Lịch đã hủy</c:otherwise></c:choose></p>
<c:if test="${appointment.tinhTrangHen eq 'CHO_XAC_NHAN' and appointment.nguoiDeXuat ne sessionScope.user.maNguoiDung}">
<form method="post" action="${pageContext.request.contextPath}/lich-hen/chi-tiet">
<input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}"><input type="hidden" name="id" value="${appointment.maYeuCau}"><input type="hidden" name="version" value="${appointment.phienBan}"><input type="hidden" name="action" value="confirm"><button class="btn btn-success">Đồng ý lịch hẹn này</button></form>
</c:if>
<c:if test="${appointment.tinhTrangHen eq 'DA_XAC_NHAN'}">
<form method="post" action="${pageContext.request.contextPath}/lich-hen/chi-tiet">
<input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}"><input type="hidden" name="id" value="${appointment.maYeuCau}"><input type="hidden" name="version" value="${appointment.phienBan}"><input type="hidden" name="action" value="${appointment.maNguoiMua eq sessionScope.user.maNguoiDung ? 'visited' : 'no-show'}"><button class="btn btn-outline-primary">${appointment.maNguoiMua eq sessionScope.user.maNguoiDung ? 'Tôi đã xem xe' : 'Khách không đến'}</button><p class="small text-muted mt-2">Chỉ ghi nhận kết quả khi đã tới giờ hẹn.</p></form>
</c:if>
<c:if test="${appointment.tinhTrangHen eq 'DA_XEM_XE'}"><a class="btn btn-success" href="${pageContext.request.contextPath}/lich-hen-cua-toi">Về danh sách để tạo giao dịch</a></c:if>
</section></div><div class="col-lg-6"><section class="appointment-panel">
<c:choose><c:when test="${appointment.tinhTrangHen eq 'CHO_XAC_NHAN' or appointment.tinhTrangHen eq 'DA_XAC_NHAN'}">
<h2 class="h5">Đề xuất đổi lịch</h2><p class="text-muted small">Đổi lịch sẽ yêu cầu bên còn lại xác nhận lại. Bạn sẽ nhận thông báo nhắc lịch trước buổi hẹn trong vòng 24 giờ.</p>
<form method="post" action="${pageContext.request.contextPath}/lich-hen/chi-tiet">
<input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
<input type="hidden" name="id" value="${appointment.maYeuCau}"><input type="hidden" name="version" value="${appointment.phienBan}">
<label class="form-label" for="time">Thời gian mới</label><input id="time" type="datetime-local" class="form-control" name="time">
<label class="form-label" for="place">Địa điểm</label><input id="place" class="form-control" name="place" maxlength="255">
<label class="form-label" for="note">Ghi chú / lý do hủy</label><textarea id="note" class="form-control" name="note" maxlength="1000" rows="3"></textarea>
<button class="btn btn-warning" name="action" value="propose">Gửi đề xuất</button>
<button class="btn btn-outline-danger" name="action" value="cancel" onclick="return confirm('Bạn muốn hủy lịch hẹn này?')">Hủy lịch</button>
</form></c:when><c:otherwise><h2 class="h5">Lịch hẹn đã kết thúc</h2><p>Bạn có thể tra cứu kết quả tại đây và tiếp tục trao đổi trong mục tin nhắn.</p></c:otherwise></c:choose>
</section></div></div></main></body></html>
