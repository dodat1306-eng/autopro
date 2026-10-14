<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Chat - AutoPro</title>
        <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
        <style>
            :root {
                --primary: #ffc400;
                --text: #222222;
            }

            body {
                font-family: 'Inter', sans-serif;
                background: #f0f2f5;
                min-height: 100vh;
            }

            .page-wrap {
                max-width: 1000px;
                margin: 24px auto;
            }

            .chat-wrapper {
                height: calc(100vh - 110px);
                display: flex;
                flex-direction: column;
                background: white;
                box-shadow: 0 0 20px rgba(0, 0, 0, 0.1);
                border-radius: 18px;
                overflow: hidden;
            }

            .chat-header {
                background: white;
                padding: 15px 20px;
                border-bottom: 1px solid #eee;
                display: flex;
                align-items: center;
                gap: 15px;
                box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
            }

            .chat-thumb {
                width: 54px;
                height: 54px;
                border-radius: 14px;
                object-fit: cover;
                border: 2px solid var(--primary);
            }

            .messages-area {
                flex: 1;
                padding: 20px;
                overflow-y: auto;
                background: #f0f2f5;
                display: flex;
                flex-direction: column;
                gap: 12px;
            }

            .message {
                max-width: 75%;
                padding: 12px 18px;
                border-radius: 20px;
                font-size: 15px;
                line-height: 1.5;
            }

            .message-mine {
                align-self: flex-end;
                background: var(--primary);
                color: #000;
                border-bottom-right-radius: 4px;
            }

            .message-other {
                align-self: flex-start;
                background: white;
                border: 1px solid #ddd;
                border-bottom-left-radius: 4px;
            }

            .message-time {
                font-size: 11px;
                margin-top: 6px;
                opacity: 0.7;
                text-align: right;
            }

            .chat-input {
                padding: 15px 20px;
                background: white;
                border-top: 1px solid #eee;
            }

            .input-group {
                border-radius: 50px;
                overflow: hidden;
                box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
            }

            .btn-send {
                background: var(--primary);
                color: #000;
                border: none;
                width: 56px;
            }

            .empty-state {
                flex: 1;
                display: flex;
                align-items: center;
                justify-content: center;
                text-align: center;
                color: #6b7280;
                padding: 40px;
            }
        </style>
    </head>
    <body>
        <div class="container page-wrap">
            <div class="d-flex justify-content-between align-items-center flex-wrap gap-3 mb-3">
                <div>
                    <div class="d-flex align-items-center gap-2 mb-2">
                        <a href="${pageContext.request.contextPath}/home" class="text-decoration-none d-inline-flex align-items-center">
                            <jsp:include page="/WEB-INF/includes/site-logo.jsp">
                                <jsp:param name="logoHeight" value="46px" />
                            </jsp:include>
                        </a>
                        <span class="badge bg-warning text-dark">Chat</span>
                    </div>
                    <div class="text-muted">Trao đổi thông tin, chốt lịch xem xe và xác nhận các bước tiếp theo ngoài thực tế.</div>
                </div>
                <div class="d-flex align-items-center gap-2">
                    <a href="chat-list" class="btn btn-outline-secondary">Danh sách chat</a>
                <a href="lich-hen-cua-toi" class="btn btn-outline-primary">Lịch hẹn</a>
                    <jsp:include page="/WEB-INF/includes/account-menu.jsp" />
                </div>
            </div>

            <div class="chat-wrapper">
                <c:choose>
                    <c:when test="${not empty chatInfo}">
                        <div class="chat-header">
                            <a href="chat-list" class="text-dark me-2"><i class="fa-solid fa-arrow-left fa-xl"></i></a>
                            <img src="${chatInfo.anhXe}" class="chat-thumb" alt="${chatInfo.tenXe}">
                            <div class="flex-grow-1">
                                <h6 class="mb-0 fw-semibold">${chatInfo.doiTacTen}</h6>
                                <small class="text-muted">
                                    <i class="fa-solid fa-car-side me-1"></i>
                                    ${empty chatInfo.tenXe ? 'Xe đang trao đổi' : chatInfo.tenXe}
                                </small>
                            </div>
                            <button type="button" id="refreshChatBtn" class="btn btn-outline-primary btn-sm rounded-pill">
                                <i class="fa-solid fa-rotate me-1"></i>Tải lại
                            </button>
                            <a href="detail?id=${chatInfo.maXe}" class="btn btn-outline-secondary btn-sm rounded-pill">Xem xe</a>
                        </div>

                        <div class="messages-area" id="messagesArea"></div>

                        <div class="chat-input">
                            <form id="chatForm" class="input-group">
                                <input type="hidden" id="maHoiThoai" value="${maHoiThoai}">
                                <input type="text" id="noiDung" class="form-control" placeholder="Nhập tin nhắn..." autocomplete="off" required>
                                <button type="submit" class="btn btn-send"><i class="fa-solid fa-paper-plane"></i></button>
                            </form>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="chat-header">
                            <a href="chat-list" class="text-dark me-2"><i class="fa-solid fa-arrow-left fa-xl"></i></a>
                            <div class="fw-semibold">Chat</div>
                        </div>
                        <div class="empty-state">
                            <div>
                                <i class="fa-solid fa-comments fa-3x mb-3 text-secondary"></i>
                                <h4>Chọn một cuộc trò chuyện để bắt đầu</h4>
                                <p class="mb-4">Danh sách hội thoại của bạn đang ở trang trước. Chọn một hội thoại để xem lịch sử và tiếp tục nhắn tin.</p>
                                <a href="chat-list" class="btn btn-warning">Mở danh sách chat</a>
                            </div>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
        <script>
            const maHoiThoaiElement = document.getElementById('maHoiThoai');
            const messagesArea = document.getElementById('messagesArea');
            const chatForm = document.getElementById('chatForm');
            const input = document.getElementById('noiDung');
            const currentUserId = ${sessionScope.user.maNguoiDung};

            if (maHoiThoaiElement && messagesArea && chatForm && input) {
                const maHoiThoai = maHoiThoaiElement.value;
                const refreshChatBtn = document.getElementById('refreshChatBtn');
                let lastSignature = '';

                function formatTime(timestamp) {
                    if (!timestamp) {
                        return '';
                    }
                    const date = new Date(timestamp);
                    if (Number.isNaN(date.getTime())) {
                        return '';
                    }
                    return date.toLocaleTimeString('vi-VN', {hour: '2-digit', minute: '2-digit'});
                }

                function buildSignature(messages) {
                    if (!messages || messages.length === 0) {
                        return 'empty';
                    }
                    const last = messages[messages.length - 1];
                    return messages.length + ':' + last.maTinNhan + ':' + last.thoiGian;
                }

                function renderMessages(messages) {
                    messagesArea.innerHTML = '';

                    if (!messages || messages.length === 0) {
                        const empty = document.createElement('div');
                        empty.className = 'text-center text-muted py-5';
                        empty.textContent = 'Chưa có tin nhắn nào. Hãy bắt đầu cuộc trò chuyện.';
                        messagesArea.appendChild(empty);
                        return;
                    }

                    messages.forEach((msg) => {
                        const wrapper = document.createElement('div');
                        wrapper.className = 'message ' + (msg.maNguoiGui == currentUserId ? 'message-mine' : 'message-other');

                        const content = document.createElement('div');
                        content.textContent = msg.noiDung;

                        const time = document.createElement('div');
                        time.className = 'message-time';
                        time.textContent = formatTime(msg.thoiGian);

                        wrapper.appendChild(content);
                        wrapper.appendChild(time);
                        messagesArea.appendChild(wrapper);
                    });

                    messagesArea.scrollTop = messagesArea.scrollHeight;
                }

                function loadMessages(force) {
                    fetch('chat-messages?maHoiThoai=' + encodeURIComponent(maHoiThoai))
                            .then((res) => {
                                if (!res.ok) {
                                    throw new Error('Cannot load messages');
                                }
                                return res.json();
                            })
                            .then((messages) => {
                                const signature = buildSignature(messages);
                                if (!force && signature === lastSignature) {
                                    return;
                                }
                                lastSignature = signature;
                                renderMessages(messages);
                            })
                            .catch(() => {
                            });
                }

                chatForm.addEventListener('submit', function (e) {
                    e.preventDefault();
                    const noiDung = input.value.trim();
                    if (!noiDung) {
                        return;
                    }

                    fetch('chat', {
                        method: 'POST',
                        headers: {'X-CSRF-Token': '${sessionScope.csrfToken}', 'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'},
                        body: 'maHoiThoai=' + encodeURIComponent(maHoiThoai) + '&noiDung=' + encodeURIComponent(noiDung)
                    }).then((res) => {
                        if (!res.ok) {
                            throw new Error('Send failed');
                        }
                        input.value = '';
                        loadMessages(true);
                    }).catch(() => {
                        loadMessages(true);
                    });
                });

                if (refreshChatBtn) {
                    refreshChatBtn.addEventListener('click', () => loadMessages(true));
                }

                const poller = window.setInterval(() => loadMessages(false), 3000);
                window.addEventListener('beforeunload', () => window.clearInterval(poller));
                loadMessages(true);
            }
        </script>
        <jsp:include page="/WEB-INF/includes/site-footer.jsp" />

    </body>
</html>
