<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!doctype html>
<html lang="vi"><head>
<meta charset="UTF-8"><meta name="viewport" content="width=device-width,initial-scale=1">
<title>Bảo mật tài khoản - AutoPro</title>
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
<style>body{background:linear-gradient(135deg,#fff8db,#f3f6fa 45%);color:#132238;min-height:100vh}.security-shell{max-width:900px;margin:40px auto;padding:20px}.security-card{background:white;border:1px solid #e3e8ef;border-radius:20px;padding:28px;height:100%}.form-label{font-weight:600}.form-control,.form-select{margin-bottom:16px}.btn-warning{font-weight:600}</style>
</head><body><main class="security-shell">
<nav class="d-flex justify-content-between align-items-center mb-4 flex-wrap gap-2">
<a href="${pageContext.request.contextPath}/home" class="btn btn-outline-dark">Về trang chủ</a>
<c:if test="${not empty sessionScope.user}"><a href="${pageContext.request.contextPath}/profile" class="btn btn-outline-secondary">Hồ sơ của tôi</a></c:if>
</nav>
<h1 class="h3 mb-4">Bảo mật và xác minh tài khoản</h1>
<c:if test="${not empty sessionScope.securityError}"><div class="alert alert-danger"><c:out value="${sessionScope.securityError}"/></div><c:remove var="securityError" scope="session"/></c:if>
<c:if test="${not empty sessionScope.securitySuccess}"><div class="alert alert-success"><c:out value="${sessionScope.securitySuccess}"/></div><c:remove var="securitySuccess" scope="session"/></c:if>
<c:choose><c:when test="${not empty challenge}">
<section class="security-card mx-auto" style="max-width:520px">
<h2 class="h4">Xác nhận mã OTP</h2>
<p>Mã đã gửi tới <strong><c:out value="${challenge.lienHe}"/></strong>. Mã có hiệu lực 10 phút, dùng một lần và cho phép tối đa 5 lần nhập sai.</p>
<form method="post" action="${pageContext.request.contextPath}/verify-contact">
<input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
<input type="hidden" name="action" value="verify"><input type="hidden" name="id" value="${challenge.maXacMinh}">
<label class="form-label" for="otp">Mã gồm 6 chữ số</label>
<input id="otp" class="form-control form-control-lg" name="otp" inputmode="numeric" pattern="[0-9]{6}" maxlength="6" autocomplete="one-time-code" required autofocus>
<button class="btn btn-warning w-100">Xác minh</button>
</form>
<p class="small text-muted mt-3">Nếu mã hết hạn hoặc chưa nhận được, quay lại gửi yêu cầu mới sau ít nhất 60 giây.</p>
<a href="${pageContext.request.contextPath}/${challenge.mucDich eq 'DANG_KY' ? 'register' : 'account-security'}">Quay lại gửi yêu cầu</a>
</section>
</c:when><c:otherwise>
<div class="row g-4"><div class="col-md-6"><section class="security-card">
<h2 class="h4">Xác minh thông tin liên hệ</h2>
<p class="text-muted">Email/số điện thoại đang dùng được giữ nguyên cho tới khi bạn nhập đúng mã gửi tới thông tin mới.</p>
<form method="post" action="${pageContext.request.contextPath}/account-security">
<input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
<input type="hidden" name="action" value="contact">
<label for="contact-type" class="form-label">Loại thông tin</label><select id="contact-type" name="type" class="form-select"><option value="email">Email</option><option value="phone">Số điện thoại</option></select>
<label for="contact" class="form-label">Email hoặc số điện thoại cần xác minh</label><input id="contact" name="contact" class="form-control" maxlength="100" required>
<label for="contact-password" class="form-label">Mật khẩu hiện tại</label><input id="contact-password" name="currentPassword" type="password" class="form-control" autocomplete="current-password" required>
<button class="btn btn-warning w-100">Gửi mã xác minh</button></form>
</section></div><div class="col-md-6"><section class="security-card">
<h2 class="h4">Đổi mật khẩu</h2><p class="text-muted">Mật khẩu mới từ 8 đến 128 ký tự. Sau khi đổi, bạn sẽ đăng nhập lại.</p>
<form method="post" action="${pageContext.request.contextPath}/account-security">
<input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
<input type="hidden" name="action" value="password">
<label for="old-password" class="form-label">Mật khẩu hiện tại</label><input id="old-password" name="currentPassword" class="form-control" type="password" autocomplete="current-password" required>
<label for="new-password" class="form-label">Mật khẩu mới</label><input id="new-password" name="newPassword" class="form-control" type="password" minlength="8" maxlength="128" autocomplete="new-password" required>
<label for="confirm-password" class="form-label">Nhập lại mật khẩu mới</label><input id="confirm-password" name="confirmPassword" class="form-control" type="password" minlength="8" maxlength="128" autocomplete="new-password" required>
<button class="btn btn-dark w-100">Đổi mật khẩu</button></form>
</section></div></div>
</c:otherwise></c:choose></main></body></html>
