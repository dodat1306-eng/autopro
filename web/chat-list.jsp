<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Tin nhắn - AutoPro</title>
        <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
        <style>
            :root {
                --primary: #ffc400;
                --text: #222222;
                --bg: #f5f5f5;
            }

            body {
                font-family: 'Inter', sans-serif;
                background: var(--bg);
                padding-top: 24px;
            }

            .chat-container {
                max-width: 1100px;
                margin: 0 auto 32px;
            }

            .chat-header {
                background: white;
                border-radius: 16px 16px 0 0;
                padding: 20px 25px;
                box-shadow: 0 4px 15px rgba(0, 0, 0, 0.06);
            }

            .chat-list {
                background: white;
                border-radius: 0 0 16px 16px;
                box-shadow: 0 4px 15px rgba(0, 0, 0, 0.06);
                max-height: 72vh;
                overflow-y: auto;
            }

            .chat-item {
                padding: 18px 24px;
                border-bottom: 1px solid #f0f0f0;
                transition: all 0.2s;
                cursor: pointer;
            }

            .chat-item:hover {
                background: #fff9e6;
            }

            .chat-item:last-child {
                border-bottom: none;
            }

            .chat-thumb {
                width: 64px;
                height: 64px;
                border-radius: 14px;
                object-fit: cover;
                border: 2px solid #ffe48f;
            }

            .chat-name {
                font-weight: 700;
                font-size: 16px;
                color: var(--text);
            }

            .chat-car {
                font-size: 14px;
                color: #6b7280;
            }

            .chat-preview {
                font-size: 14px;
                color: #4b5563;
                margin-top: 6px;
                display: -webkit-box;
                -webkit-line-clamp: 1;
                -webkit-box-orient: vertical;
                overflow: hidden;
            }

            .chat-time {
                font-size: 13px;
                color: #999;
            }

            .btn-chat {
                background: var(--primary);
                color: #000;
                font-weight: 600;
                border-radius: 50px;
                padding: 8px 18px;
            }

            .btn-chat:hover {
                background: #e6b000;
            }

            .empty-state {
                text-align: center;
                padding: 80px 20px;
                color: #888;
            }

            .empty-state i {
                font-size: 72px;
                margin-bottom: 20px;
                opacity: 0.3;
            }
        </style>
    </head>
    <body>
        <div class="chat-container container">
            <div class="d-flex align-items-center justify-content-between flex-wrap gap-3 mb-3">
                <div>
                    <div class="d-flex align-items-center gap-2 mb-2">
                        <a href="${pageContext.request.contextPath}/home" class="text-decoration-none d-inline-flex align-items-center">
                            <jsp:include page="/WEB-INF/includes/site-logo.jsp">
                                <jsp:param name="logoHeight" value="46px" />
                            </jsp:include>
                        </a>
                        <span class="badge bg-warning text-dark">Tin nhắn</span>
                    </div>
                    <h4 class="mb-0 fw-semibold">Tin nhắn của tôi</h4>
                </div>
                <div class="d-flex align-items-center gap-2">
                <a href="lich-hen-cua-toi" class="btn btn-outline-primary rounded-pill">Lịch hẹn</a>
                    <a href="home" class="btn btn-outline-secondary rounded-pill">Về trang chủ</a>
                    <jsp:include page="/WEB-INF/includes/account-menu.jsp" />
                </div>
            </div>

            <div class="chat-header d-flex align-items-center justify-content-between flex-wrap gap-3">
                <div class="d-flex align-items-center">
                    <i class="fa-solid fa-message text-warning fs-4 me-3"></i>
                    <h5 class="mb-0 fw-semibold">Cuộc trò chuyện</h5>
                </div>
                <span class="badge bg-warning text-dark px-3 py-2">${listChat.size()} cuộc trò chuyện</span>
            </div>

            <div class="chat-list">
                <c:choose>
                    <c:when test="${not empty listChat}">
                        <c:forEach items="${listChat}" var="chat">
                            <div class="chat-item d-flex align-items-center gap-3"
                                 onclick="window.location.href='chat?room=${chat.maHoiThoai}'">
                                <img src="${chat.anhXe}" class="chat-thumb" alt="${chat.tenXe}">

                                <div class="flex-grow-1">
                                    <div class="d-flex justify-content-between align-items-start gap-3">
                                        <div>
                                            <div class="chat-name">${chat.doiTacTen}</div>
                                            <div class="chat-car">
                                                <i class="fa-solid fa-car-side me-1"></i>
                                                ${empty chat.tenXe ? 'Xe đang trao đổi' : chat.tenXe}
                                            </div>
                                        </div>
                                        <div class="text-end">
                                            <div class="chat-time">
                                                <c:choose>
                                                    <c:when test="${not empty chat.thoiGianCuoi}">
                                                        <fmt:formatDate value="${chat.thoiGianCuoi}" pattern="dd/MM HH:mm" />
                                                    </c:when>
                                                    <c:otherwise>Mới tạo</c:otherwise>
                                                </c:choose>
                                            </div>
                                        </div>
                                    </div>
                                    <div class="chat-preview">${chat.tinNhanCuoi}</div>
                                </div>

                                <button class="btn btn-chat">Mở chat</button>
                            </div>
                        </c:forEach>
                    </c:when>

                    <c:otherwise>
                        <div class="empty-state">
                            <i class="fa-solid fa-inbox"></i>
                            <h5>Bạn chưa có cuộc trò chuyện nào</h5>
                            <p class="mb-4">Khi bạn nhắn tin với người bán, cuộc trò chuyện sẽ xuất hiện tại đây.</p>
                            <a href="home" class="btn btn-warning btn-lg rounded-pill px-4">
                                <i class="fa-solid fa-car me-2"></i>Tìm xe ngay
                            </a>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
        <jsp:include page="/WEB-INF/includes/site-footer.jsp" />

    </body>
</html>
