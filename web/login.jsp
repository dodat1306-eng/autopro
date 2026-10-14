<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="util.HtmlUtil"%>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <%@ include file="/WEB-INF/jspf/favicon.jspf" %>
        <title>Đăng nhập - AutoPro</title>
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
                grid-template-columns: 1.05fr 0.95fr;
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
                font-size: clamp(2.3rem, 4vw, 4rem);
                line-height: 1.02;
                font-weight: 800;
                max-width: 560px;
            }

            .hero-text {
                max-width: 560px;
                color: #64748b;
                line-height: 1.75;
                font-size: 15px;
            }

            .feature-grid {
                display: grid;
                gap: 14px;
                margin-top: 26px;
            }

            .feature-card {
                background: rgba(255, 255, 255, 0.9);
                border: 1px solid #e5ebf3;
                border-radius: 24px;
                padding: 18px;
            }

            .feature-card .icon {
                width: 46px;
                height: 46px;
                border-radius: 14px;
                display: inline-flex;
                align-items: center;
                justify-content: center;
                background: #fff4cf;
                color: #8a5b00;
                margin-bottom: 12px;
            }

            .feature-card strong {
                display: block;
                margin-bottom: 6px;
            }

            .feature-card span {
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
                max-width: 470px;
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

            .bottom-link {
                color: #64748b;
                text-decoration: none;
                font-size: 14px;
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
                            <i class="fa-solid fa-arrow-right-to-bracket"></i>
                            Sign In
                        </span>
                        <h2 class="hero-title">Kết nối lại với tài khoản AutoPro của bạn</h2>
                        <p class="hero-text">
                            Đăng nhập để quản lý tin đăng, theo dõi lịch hẹn, xử lý giao dịch, nhận thông báo và tiếp tục các cuộc trò chuyện với người mua hoặc người bán.
                        </p>

                        <div class="feature-grid">
                            <div class="feature-card">
                                <div class="icon"><i class="fa-solid fa-bell"></i></div>
                                <strong>Thông báo tập trung</strong>
                                <span>Nhận ngay thông báo về tin nhắn mới, lịch hẹn, duyệt bài và mọi thay đổi quan trọng trên tài khoản.</span>
                            </div>
                            <div class="feature-card">
                                <div class="icon"><i class="fa-solid fa-shield-halved"></i></div>
                                <strong>Bảo mật và xác minh</strong>
                                <span>Luồng OTP và đổi mật khẩu được đồng bộ cùng hệ thống seller verification để bảo vệ tài khoản tốt hơn.</span>
                            </div>
                        </div>
                    </div>
                </div>

                <a href="${pageContext.request.contextPath}/home" class="bottom-link">
                    <i class="fa-solid fa-house me-2"></i>Quay lại trang chủ
                </a>
            </section>

            <main class="auth-main">
                <div class="auth-card">
                    <h1>Đăng nhập</h1>
                    <div class="auth-subtitle">Nhập email hoặc số điện thoại cùng mật khẩu để truy cập tài khoản của bạn.</div>

                    <% if ("1".equals(request.getParameter("registered"))) { %>
                        <div class="alert alert-success rounded-4 border-0">Đăng ký thành công. Bạn có thể đăng nhập ngay.</div>
                    <% } %>
                    <% if ("1".equals(request.getParameter("reset"))) { %>
                        <div class="alert alert-success rounded-4 border-0">Đặt lại mật khẩu thành công. Hãy đăng nhập bằng mật khẩu mới.</div>
                    <% } %>
                    <% if (request.getAttribute("error") != null) { %>
                        <div class="alert alert-danger rounded-4 border-0">
                            <i class="fa-solid fa-triangle-exclamation me-2"></i><%= HtmlUtil.escape(request.getAttribute("error")) %>
                        </div>
                    <% } %>

                    <form action="${pageContext.request.contextPath}/login" method="post">
<input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                        <div class="mb-3">
                            <label class="form-label">Email hoặc số điện thoại</label>
                            <input type="text" class="form-control" name="identifier"
                                   value="<%= HtmlUtil.escape(request.getAttribute("identifier")) %>"
                                   placeholder="Nhập email hoặc số điện thoại" required autofocus autocomplete="off">
                        </div>

                        <div class="mb-3">
                            <label class="form-label">Mật khẩu</label>
                            <input type="password" class="form-control" name="matKhau" placeholder="Nhập mật khẩu" required>
                        </div>

                        <div class="d-flex justify-content-end mb-4">
                            <a href="${pageContext.request.contextPath}/forgot-password" class="text-decoration-none small">Quên mật khẩu?</a>
                        </div>

                        <button type="submit" class="btn-auth">
                            <i class="fa-solid fa-right-to-bracket me-2"></i>Đăng nhập
                        </button>
                    </form>

                    <div class="text-center mt-4">
                        Chưa có tài khoản?
                        <a href="${pageContext.request.contextPath}/register" class="text-decoration-none fw-bold">Đăng ký ngay</a>
                    </div>
                </div>
            </main>
        </div>
        <jsp:include page="/WEB-INF/includes/site-footer.jsp" />

    </body>
</html>
