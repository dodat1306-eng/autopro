<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="util.HtmlUtil"%>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <%@ include file="/WEB-INF/jspf/favicon.jspf" %>
        <title>Đăng ký - AutoPro</title>
        <link rel="preconnect" href="https://fonts.googleapis.com">
        <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
        <link href="https://fonts.googleapis.com/css2?family=Manrope:wght@400;500;600;700;800&display=swap" rel="stylesheet">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
        <style>
            body {
                min-height: 100vh;
                font-family: 'Manrope', sans-serif;
                background:
                    radial-gradient(circle at top left, rgba(255, 214, 102, 0.24), transparent 24%),
                    linear-gradient(180deg, #f8fafc 0%, #f1f5f9 100%);
                color: #132238;
            }

            .auth-shell {
                min-height: 100vh;
                display: grid;
                grid-template-columns: 1.02fr 0.98fr;
            }

            .auth-aside {
                padding: 42px;
                background:
                    linear-gradient(135deg, rgba(255, 239, 184, 0.96), rgba(255, 255, 255, 0.98) 48%),
                    #ffffff;
                display: flex;
                flex-direction: column;
                justify-content: space-between;
            }

            .brand-mark {
                display: inline-flex;
                align-items: center;
                gap: 10px;
                text-decoration: none;
                color: #132238;
                font-size: 30px;
                font-weight: 800;
            }

            .brand-mark span {
                color: #f59e0b;
            }

            .hero-badge {
                display: inline-flex;
                align-items: center;
                gap: 8px;
                padding: 8px 14px;
                border-radius: 999px;
                background: #0f172a;
                color: #ffffff;
                font-size: 12px;
                font-weight: 800;
                text-transform: uppercase;
                letter-spacing: 0.04em;
            }

            .hero-title {
                margin: 18px 0 14px;
                font-size: clamp(2.2rem, 4vw, 3.8rem);
                line-height: 1.03;
                font-weight: 800;
                max-width: 580px;
            }

            .hero-text {
                max-width: 580px;
                color: #64748b;
                line-height: 1.75;
                font-size: 15px;
            }

            .bullet-list {
                display: grid;
                gap: 14px;
                margin-top: 24px;
            }

            .bullet-item {
                border-radius: 22px;
                border: 1px solid #e5ebf3;
                background: rgba(255, 255, 255, 0.9);
                padding: 18px;
                display: flex;
                gap: 14px;
                align-items: start;
            }

            .bullet-icon {
                width: 44px;
                height: 44px;
                flex: 0 0 44px;
                border-radius: 14px;
                display: inline-flex;
                align-items: center;
                justify-content: center;
                background: #fff4cf;
                color: #8a5b00;
            }

            .bullet-item strong {
                display: block;
                margin-bottom: 6px;
            }

            .bullet-item span {
                color: #64748b;
                font-size: 13px;
                line-height: 1.65;
            }

            .auth-main {
                display: flex;
                align-items: center;
                justify-content: center;
                padding: 32px 20px;
            }

            .auth-card {
                width: 100%;
                max-width: 520px;
                background: #ffffff;
                border: 1px solid #e6ebf2;
                border-radius: 28px;
                box-shadow: 0 20px 48px rgba(15, 23, 42, 0.08);
                padding: 30px;
            }

            .auth-card h1 {
                font-size: 30px;
                font-weight: 800;
                margin-bottom: 10px;
            }

            .auth-subtitle {
                color: #64748b;
                font-size: 14px;
                line-height: 1.7;
                margin-bottom: 22px;
            }

            .form-label {
                font-weight: 700;
                color: #243447;
            }

            .form-control {
                border-radius: 16px;
                padding: 14px 16px;
                border-color: #dbe3ee;
                box-shadow: none !important;
            }

            .form-control:focus {
                border-color: #f5b700;
            }

            .contact-switch {
                display: grid;
                grid-template-columns: repeat(2, minmax(0, 1fr));
                gap: 10px;
            }

            .switch-option {
                position: relative;
            }

            .switch-option input {
                position: absolute;
                opacity: 0;
                pointer-events: none;
            }

            .switch-option label {
                display: flex;
                align-items: center;
                justify-content: center;
                gap: 8px;
                min-height: 52px;
                border-radius: 16px;
                border: 1px solid #d7deea;
                background: #fbfcff;
                font-weight: 700;
                cursor: pointer;
                transition: 0.2s ease;
            }

            .switch-option input:checked + label {
                border-color: #0f172a;
                background: #0f172a;
                color: #ffffff;
            }

            .btn-auth {
                width: 100%;
                padding: 14px;
                border: none;
                border-radius: 16px;
                background: #f5b700;
                color: #132238;
                font-weight: 800;
                font-size: 15px;
            }

            @media (max-width: 991.98px) {
                .auth-shell {
                    grid-template-columns: 1fr;
                }

                .auth-aside {
                    display: none;
                }
            }
        </style>
    </head>
    <body>
        <div class="auth-shell">
            <section class="auth-aside">
                <div>
                    <a href="${pageContext.request.contextPath}/home" class="brand-mark">
                        <jsp:include page="/WEB-INF/includes/site-logo.jsp">
                            <jsp:param name="logoHeight" value="58px" />
                        </jsp:include>
                    </a>
                    <div class="mt-5">
                        <span class="hero-badge">
                            <i class="fa-solid fa-user-plus"></i>
                            Create Account
                        </span>
                        <h2 class="hero-title">Tạo tài khoản để bắt đầu mua bán xe trên AutoPro</h2>
                        <p class="hero-text">
                            Bạn có thể đăng ký bằng email hoặc số điện thoại. Sau khi tạo tài khoản, bạn sẽ được dùng đầy đủ tính năng chat, lịch hẹn, yêu cầu kết nối và đăng ký seller khi cần bán xe.
                        </p>

                        <div class="bullet-list">
                            <div class="bullet-item">
                                <div class="bullet-icon"><i class="fa-solid fa-envelope"></i></div>
                                <div>
                                    <strong>Linh hoạt phương thức đăng ký</strong>
                                    <span>Dùng email hoặc số điện thoại tùy theo cách bạn muốn đăng nhập và nhận mã OTP.</span>
                                </div>
                            </div>
                            <div class="bullet-item">
                                <div class="bullet-icon"><i class="fa-solid fa-comments"></i></div>
                                <div>
                                    <strong>Mở chat và lịch hẹn ngay</strong>
                                    <span>Tài khoản buyer có thể bắt đầu nhắn tin, gửi yêu cầu kết nối và đặt lịch xem xe ngay sau khi đăng ký.</span>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <a href="${pageContext.request.contextPath}/home" class="text-decoration-none text-muted">
                    <i class="fa-solid fa-house me-2"></i>Quay lại trang chủ
                </a>
            </section>

            <main class="auth-main">
                <div class="auth-card">
                    <h1>Tạo tài khoản mới</h1>
                    <div class="auth-subtitle">Điền thông tin cơ bản và chọn phương thức liên hệ chính để sử dụng tài khoản AutoPro.</div>

                    <% if (request.getAttribute("error") != null) { %>
                        <div class="alert alert-danger rounded-4 border-0">
                            <i class="fa-solid fa-triangle-exclamation me-2"></i><%= HtmlUtil.escape(request.getAttribute("error")) %>
                        </div>
                    <% } %>

                    <form action="${pageContext.request.contextPath}/register" method="post">
<input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                        <div class="mb-3">
                            <label class="form-label">Họ và tên</label>
                            <input type="text" class="form-control" name="hoTen" value="<%= HtmlUtil.escape(request.getParameter("hoTen")) %>" required autofocus>
                        </div>

                        <div class="mb-3">
                            <label class="form-label">Đăng ký bằng</label>
                            <div class="contact-switch mt-2">
                                <div class="switch-option">
                                    <input type="radio" name="contactType" id="contactEmail" value="email"
                                           <%= !"phone".equals(request.getParameter("contactType")) ? "checked" : "" %>>
                                    <label for="contactEmail"><i class="fa-solid fa-envelope"></i>Email</label>
                                </div>
                                <div class="switch-option">
                                    <input type="radio" name="contactType" id="contactPhone" value="phone"
                                           <%= "phone".equals(request.getParameter("contactType")) ? "checked" : "" %>>
                                    <label for="contactPhone"><i class="fa-solid fa-phone"></i>Số điện thoại</label>
                                </div>
                            </div>
                        </div>

                        <div class="mb-3" id="emailField">
                            <label class="form-label">Email</label>
                            <input type="email" class="form-control" name="email" value="<%= HtmlUtil.escape(request.getParameter("email")) %>" placeholder="Nhập email đăng nhập">
                        </div>

                        <div class="mb-3" id="phoneField">
                            <label class="form-label">Số điện thoại</label>
                            <input type="text" class="form-control" name="soDienThoai" value="<%= HtmlUtil.escape(request.getParameter("soDienThoai")) %>" placeholder="Nhập số điện thoại">
                        </div>

                        <div class="mb-3">
                            <label class="form-label">Mật khẩu</label>
                            <input type="password" class="form-control" name="matKhau" placeholder="Ít nhất 6 ký tự" required>
                        </div>

                        <div class="mb-4">
                            <label class="form-label">Xác nhận mật khẩu</label>
                            <input type="password" class="form-control" name="confirmMatKhau" placeholder="Nhập lại mật khẩu" required>
                        </div>

                        <div class="text-muted small mb-4">
                            Bằng việc đăng ký, bạn đồng ý với điều khoản sử dụng và chính sách hoạt động của AutoPro.
                        </div>

                        <button type="submit" class="btn-auth">
                            <i class="fa-solid fa-user-plus me-2"></i>Tạo tài khoản
                        </button>
                    </form>

                    <div class="text-center mt-4">
                        Đã có tài khoản?
                        <a href="${pageContext.request.contextPath}/login" class="text-decoration-none fw-bold">Đăng nhập ngay</a>
                    </div>
                </div>
            </main>
        </div>

        <script>
            const emailField = document.getElementById('emailField');
            const phoneField = document.getElementById('phoneField');
            const contactEmail = document.getElementById('contactEmail');
            const contactPhone = document.getElementById('contactPhone');

            function updateContactFields() {
                const usingPhone = contactPhone.checked;
                emailField.style.display = usingPhone ? 'none' : 'block';
                phoneField.style.display = usingPhone ? 'block' : 'none';
                emailField.querySelector('input').required = !usingPhone;
                phoneField.querySelector('input').required = usingPhone;
            }

            contactEmail.addEventListener('change', updateContactFields);
            contactPhone.addEventListener('change', updateContactFields);
            updateContactFields();
        </script>
        <jsp:include page="/WEB-INF/includes/site-footer.jsp" />

    </body>
</html>
