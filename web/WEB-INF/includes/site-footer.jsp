<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<footer class="autopro-footer" id="autopro-contact" role="contentinfo">
    <style>
        .autopro-footer,
        .autopro-footer * {
            box-sizing: border-box;
        }

        .autopro-footer {
            margin-top: 64px;
            background: #111827;
            color: #e5e7eb;
            border-top: 1px solid rgba(255, 255, 255, 0.08);
            font-family: inherit;
        }

        .autopro-footer a {
            color: inherit;
            text-decoration: none;
        }

        .autopro-footer a:hover {
            color: #fbbf24;
        }

        .autopro-footer__wrap {
            width: min(1180px, calc(100% - 32px));
            margin: 0 auto;
            padding: 42px 0 22px;
        }

        .autopro-footer__grid {
            display: grid;
            grid-template-columns: minmax(240px, 1.35fr) minmax(170px, 0.8fr) minmax(220px, 0.95fr) minmax(230px, 0.9fr);
            gap: 30px;
            align-items: start;
        }

        .autopro-footer__brand {
            max-width: 360px;
        }

        .autopro-footer__brand-row {
            display: flex;
            align-items: center;
            gap: 12px;
            margin-bottom: 14px;
        }

        .autopro-footer__brand-name {
            color: #ffffff;
            font-size: 28px;
            font-weight: 800;
            line-height: 1;
        }

        .autopro-footer__description {
            margin: 0;
            color: #cbd5e1;
            line-height: 1.7;
            font-size: 15px;
        }

        .autopro-footer__title {
            margin: 0 0 14px;
            color: #ffffff;
            font-size: 15px;
            font-weight: 800;
            letter-spacing: 0.01em;
        }

        .autopro-footer__list {
            display: grid;
            gap: 10px;
            margin: 0;
            padding: 0;
            list-style: none;
            color: #cbd5e1;
            font-size: 14px;
            line-height: 1.5;
        }

        .autopro-footer__list a,
        .autopro-footer__list span {
            display: inline-flex;
            align-items: center;
            min-height: 24px;
        }

        .autopro-footer__contact-item {
            display: grid;
            gap: 4px;
        }

        .autopro-footer__contact-label {
            color: #94a3b8;
            font-size: 12px;
            font-weight: 700;
            text-transform: uppercase;
        }

        .autopro-footer__contact-value {
            color: #f8fafc;
            font-weight: 700;
            overflow-wrap: anywhere;
        }

        .autopro-footer__bottom {
            display: flex;
            justify-content: center;
            align-items: center;
            gap: 16px;
            margin-top: 34px;
            padding-top: 20px;
            border-top: 1px solid rgba(255, 255, 255, 0.08);
            color: #94a3b8;
            font-size: 13px;
        }

        @media (max-width: 991.98px) {
            .autopro-footer__grid {
                grid-template-columns: 1fr 1fr;
            }
        }

        @media (max-width: 575.98px) {
            .autopro-footer {
                margin-top: 44px;
            }

            .autopro-footer__wrap {
                width: min(100% - 24px, 1180px);
                padding-top: 34px;
            }

            .autopro-footer__grid {
                grid-template-columns: 1fr;
                gap: 24px;
            }

            .autopro-footer__bottom {
                align-items: flex-start;
                flex-direction: column;
            }
        }
    </style>

    <div class="autopro-footer__wrap">
        <div class="autopro-footer__grid">
            <section class="autopro-footer__brand" aria-label="AutoPro">
                <div class="autopro-footer__brand-row">
                    <jsp:include page="/WEB-INF/includes/site-logo.jsp">
                        <jsp:param name="logoHeight" value="44px" />
                        <jsp:param name="iconOnly" value="1" />
                        <jsp:param name="onDark" value="1" />
                    </jsp:include>
                    <div class="autopro-footer__brand-name">AutoPro</div>
                </div>
                <p class="autopro-footer__description">Nền tảng kết nối trung gian mua bán ô tô.</p>
            </section>

            <nav aria-label="Liên kết nhanh">
                <h2 class="autopro-footer__title">Liên kết nhanh</h2>
                <ul class="autopro-footer__list">
                    <li><a href="${pageContext.request.contextPath}/home">Trang chủ</a></li>
                    <li><a href="${pageContext.request.contextPath}/search">Tìm kiếm xe</a></li>
                    <li><a href="${pageContext.request.contextPath}/add-post">Đăng bán xe</a></li>
                    <li><a href="#autopro-contact">Liên hệ</a></li>
                </ul>
            </nav>

            <section aria-label="Hỗ trợ">
                <h2 class="autopro-footer__title">Hỗ trợ</h2>
                <ul class="autopro-footer__list">
                    <li><span>Hướng dẫn mua bán</span></li>
                    <li><span>Chính sách đăng tin</span></li>
                    <li><span>Điều khoản sử dụng</span></li>
                </ul>
            </section>

            <section aria-label="Thông tin liên hệ">
                <h2 class="autopro-footer__title">Liên hệ</h2>
                <ul class="autopro-footer__list">
                    <li class="autopro-footer__contact-item">
                        <span class="autopro-footer__contact-label">Email</span>
                        <a class="autopro-footer__contact-value" href="mailto:autopro@gmail.com">autopro@gmail.com</a>
                    </li>
                    <li class="autopro-footer__contact-item">
                        <span class="autopro-footer__contact-label">Số điện thoại</span>
                        <a class="autopro-footer__contact-value" href="tel:0999666999">0999666999</a>
                    </li>
                </ul>
            </section>
        </div>

        <div class="autopro-footer__bottom">
            <span>© 2026 AutoPro. All rights reserved.</span>
        </div>
    </div>
</footer>
