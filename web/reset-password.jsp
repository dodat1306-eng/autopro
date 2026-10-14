<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="util.HtmlUtil"%>
<%
    String identifier = request.getAttribute("identifier") != null
            ? String.valueOf(request.getAttribute("identifier"))
            : "";
%>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <%@ include file="/WEB-INF/jspf/favicon.jspf" %>
        <title>Tạo mật khẩu mới - AutoPro</title>
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

            .step-bar {
                display: grid;
                grid-template-columns: repeat(3, minmax(0, 1fr));
                gap: 10px;
                margin-bottom: 22px;
            }

            .step-chip {
                padding: 12px 14px;
                border-radius: 16px;
                border: 1px solid #dbe3ee;
                background: #fbfcff;
                text-align: center;
                font-size: 12px;
                font-weight: 800;
                color: #64748b;
            }

            .step-chip.active {
                border-color: #0f172a;
                background: #0f172a;
                color: #ffffff;
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

            .tip-box {
                margin-top: 16px;
                border-radius: 20px;
                background: #f8fbff;
                border: 1px solid #dbe7f5;
                padding: 16px;
                color: #4b6480;
                font-size: 13px;
                line-height: 1.65;
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
                            <i class="fa-solid fa-lock"></i>
                            New Password
                        </span>
                        <h2 class="hero-title">Tạo mật khẩu mới để quay lại tài khoản an toàn</h2>
                        <p class="hero-text">
                            OTP đã được xác minh thành công. Đây là bước cuối để hoàn tất khôi phục tài khoản AutoPro của bạn.
                        </p>

                        <div class="bullet-list">
                            <div class="bullet-item">
                                <div class="bullet-icon"><i class="fa-solid fa-key"></i></div>
                                <div>
                                    <strong>Dùng mật khẩu đủ mạnh</strong>
                                    <span>Nên kết hợp chữ hoa, chữ thường, số và ký tự đặc biệt nếu bạn muốn bảo vệ tài khoản tốt hơn.</span>
                                </div>
                            </div>
                            <div class="bullet-item">
                                <div class="bullet-icon"><i class="fa-solid fa-user-shield"></i></div>
                                <div>
                                    <strong>Kết thúc luồng khôi phục</strong>
                                    <span>Sau khi cập nhật thành công, hệ thống sẽ xóa thông tin reset tạm thời và đưa bạn quay lại màn hình đăng nhập.</span>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <a href="${pageContext.request.contextPath}/login" class="text-decoration-none text-muted">
                    <i class="fa-solid fa-arrow-left me-2"></i>Quay lại đăng nhập
                </a>
            </section>

            <main class="auth-main">
                <div class="auth-card">
                    <div class="step-bar">
                        <div class="step-chip">1. Gửi OTP</div>
                        <div class="step-chip">2. Xác minh OTP</div>
                        <div class="step-chip active">3. Tạo mật khẩu mới</div>
                    </div>

                    <h1>Tạo mật khẩu mới</h1>
                    <div class="auth-subtitle">
                        OTP đã được xác minh cho <strong><%= HtmlUtil.escape(identifier) %></strong>. Hãy nhập mật khẩu mới để hoàn tất khôi phục tài khoản.
                    </div>

                    <% if (request.getAttribute("error") != null) { %>
                        <div class="alert alert-danger rounded-4 border-0"><%= HtmlUtil.escape(request.getAttribute("error")) %></div>
                    <% } %>

                    <form action="${pageContext.request.contextPath}/reset-password" method="post">
<input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                        <div class="mb-3">
                            <label class="form-label">Mật khẩu mới</label>
                            <input type="password" class="form-control" name="newPassword" placeholder="Ít nhất 6 ký tự" required autofocus>
                        </div>

                        <div class="mb-4">
                            <label class="form-label">Xác nhận mật khẩu mới</label>
                            <input type="password" class="form-control" name="confirmPassword" placeholder="Nhập lại mật khẩu mới" required>
                        </div>

                        <button type="submit" class="btn-auth">
                            <i class="fa-solid fa-check me-2"></i>Cập nhật mật khẩu
                        </button>
                    </form>

                    <div class="tip-box">
                        Sau khi đổi xong, hãy đăng nhập lại bằng mật khẩu mới. Nếu bạn vẫn chưa yên tâm, có thể đổi mật khẩu thêm một lần trong phần tài khoản sau khi đăng nhập.
                    </div>
                </div>
            </main>
        </div>
        <jsp:include page="/WEB-INF/includes/site-footer.jsp" />

    </body>
</html>
