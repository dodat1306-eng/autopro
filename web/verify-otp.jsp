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
        <title>Xác minh OTP - AutoPro</title>
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

            .otp-box {
                letter-spacing: 0.28em;
                font-size: 20px;
                text-align: center;
                font-weight: 800;
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

            .btn-ghost {
                border: 1px solid #d7deea;
                background: #ffffff;
                color: #243447;
                border-radius: 14px;
                padding: 12px 14px;
                font-weight: 700;
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
                            <i class="fa-solid fa-shield"></i>
                            Verify OTP
                        </span>
                        <h2 class="hero-title">Xác minh đúng mã OTP trước khi tạo mật khẩu mới</h2>
                        <p class="hero-text">
                            Hệ thống đã tạo OTP cho tài khoản của bạn. Chỉ khi xác minh đúng mã, AutoPro mới mở bước tạo mật khẩu mới.
                        </p>

                        <div class="bullet-list">
                            <div class="bullet-item">
                                <div class="bullet-icon"><i class="fa-solid fa-stopwatch"></i></div>
                                <div>
                                    <strong>OTP có thời hạn ngắn</strong>
                                    <span>Mã chỉ có hiệu lực trong thời gian giới hạn để tránh rủi ro bị lộ hoặc dùng lại nhiều lần.</span>
                                </div>
                            </div>
                            <div class="bullet-item">
                                <div class="bullet-icon"><i class="fa-solid fa-rotate"></i></div>
                                <div>
                                    <strong>Có thể gửi lại khi cần</strong>
                                    <span>Nếu bạn chưa nhận được mã, có thể gửi lại OTP ngay trên màn này mà không phải làm lại toàn bộ quy trình.</span>
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
                        <div class="step-chip active">2. Xác minh OTP</div>
                        <div class="step-chip">3. Tạo mật khẩu mới</div>
                    </div>

                    <h1>Xác minh OTP</h1>
                    <div class="auth-subtitle">
                        Nhập mã OTP đã được gửi tới <strong><%= HtmlUtil.escape(identifier) %></strong> để chuyển sang bước tạo mật khẩu mới.
                    </div>

                    <% if (request.getAttribute("error") != null) { %>
                        <div class="alert alert-danger rounded-4 border-0"><%= HtmlUtil.escape(request.getAttribute("error")) %></div>
                    <% } %>
                    <% if (request.getAttribute("info") != null) { %>
                        <div class="alert alert-success rounded-4 border-0"><%= HtmlUtil.escape(request.getAttribute("info")) %></div>
                    <% } %>
                    <% if (request.getAttribute("debugOtp") != null) { %>
                        <div class="alert alert-warning rounded-4 border-0"><%= HtmlUtil.escape(request.getAttribute("debugOtp")) %></div>
                    <% } %>

                    <form action="${pageContext.request.contextPath}/verify-otp" method="post">
<input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                        <div class="mb-4">
                            <label class="form-label">Mã OTP</label>
                            <input type="text" class="form-control otp-box" name="otp" placeholder="000000" maxlength="6" required autofocus>
                        </div>

                        <button type="submit" class="btn-auth">
                            <i class="fa-solid fa-circle-check me-2"></i>Xác minh OTP
                        </button>
                    </form>

                    <div class="d-flex justify-content-between align-items-center flex-wrap gap-2 mt-4">
                        <form action="${pageContext.request.contextPath}/forgot-password" method="post" class="m-0">
<input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                            <input type="hidden" name="action" value="request-otp">
                            <input type="hidden" name="identifier" value="<%= HtmlUtil.escape(identifier) %>">
                            <button type="submit" class="btn-ghost">
                                <i class="fa-solid fa-paper-plane me-2"></i>Gửi lại OTP
                            </button>
                        </form>
                        <a href="${pageContext.request.contextPath}/forgot-password?identifier=<%= HtmlUtil.urlEncode(identifier) %>" class="text-decoration-none text-muted">Đổi tài khoản khác</a>
                    </div>
                </div>
            </main>
        </div>
        <jsp:include page="/WEB-INF/includes/site-footer.jsp" />

    </body>
</html>
