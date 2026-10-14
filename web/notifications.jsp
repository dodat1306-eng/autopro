<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="model.ThongBao" %>
<%@ page import="util.HtmlUtil" %>
<%
    List<ThongBao> notifications = (List<ThongBao>) request.getAttribute("notifications");
    Integer unreadCount = (Integer) request.getAttribute("unreadCount");
    if (unreadCount == null) {
        unreadCount = 0;
    }
%>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Thông báo - AutoPro</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
        <style>
            body {
                background: #f5f6f8;
                font-family: Arial, sans-serif;
            }

            .page-wrap {
                max-width: 980px;
                margin: 32px auto;
                background: white;
                border-radius: 18px;
                padding: 28px;
                box-shadow: 0 8px 28px rgba(0, 0, 0, 0.08);
            }

            .notification-card {
                border: 1px solid #e9edf3;
                border-radius: 16px;
                padding: 18px 20px;
                margin-bottom: 16px;
            }

            .notification-card.unread {
                background: #fffdf2;
                border-color: #ffe08a;
            }

            .notification-title {
                font-weight: 700;
                color: #1f2937;
            }
        </style>
    </head>
    <body>
        <div class="container page-wrap">
            <div class="d-flex justify-content-between align-items-center flex-wrap gap-3 mb-4">
                <div>
                    <div class="d-flex align-items-center gap-2 mb-2">
                        <a href="${pageContext.request.contextPath}/home" class="text-decoration-none d-inline-flex align-items-center">
                            <jsp:include page="/WEB-INF/includes/site-logo.jsp">
                                <jsp:param name="logoHeight" value="46px" />
                            </jsp:include>
                        </a>
                        <span class="badge bg-warning text-dark">Thông báo</span>
                    </div>
                    <h2 class="mb-1">Thông báo của tôi</h2>
                    <p class="text-muted mb-0">Theo dõi tin nhắn mới, lịch hẹn, cập nhật moderation và các hoạt động liên quan tới tài khoản của bạn.</p>
                </div>
                <div class="d-flex align-items-center gap-2">
                    <form action="notifications" method="post" class="m-0">
<input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                        <input type="hidden" name="action" value="mark-all-read">
                        <button type="submit" class="btn btn-outline-primary">Đánh dấu tất cả đã đọc</button>
                    </form>
                    <a href="home" class="btn btn-outline-secondary">Về trang chủ</a>
                    <jsp:include page="/WEB-INF/includes/account-menu.jsp" />
                </div>
            </div>

            <div class="alert alert-light border d-flex justify-content-between align-items-center flex-wrap gap-2 mb-4">
                <div><strong><%= unreadCount %></strong> thông báo chưa đọc</div>
                <div class="text-muted small">Bấm vào từng thông báo để mở đúng trang liên quan.</div>
            </div>

            <% if (notifications != null && !notifications.isEmpty()) { %>
            <% for (ThongBao thongBao : notifications) { %>
            <div class="notification-card <%= thongBao.isDaDoc() ? "" : "unread" %>">
                <div class="d-flex justify-content-between align-items-start flex-wrap gap-2">
                    <div>
                        <div class="notification-title"><%= HtmlUtil.escape(thongBao.getTieuDe()) %></div>
                        <div class="text-muted mt-1"><%= HtmlUtil.escape(thongBao.getNoiDung()) %></div>
                        <div class="small text-muted mt-2"><%= thongBao.getNgayTao() %></div>
                    </div>
                    <div class="d-flex gap-2">
                        <% if (!thongBao.isDaDoc()) { %>
                        <form action="notifications" method="post" class="m-0">
<input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                            <input type="hidden" name="action" value="mark-read">
                            <input type="hidden" name="id" value="<%= thongBao.getMaThongBao() %>">
                            <button type="submit" class="btn btn-sm btn-outline-secondary">Đánh dấu đã đọc</button>
                        </form>
                        <% } %>
                        <a href="notifications?open=<%= thongBao.getMaThongBao() %>" class="btn btn-sm btn-warning">Mở</a>
                    </div>
                </div>
            </div>
            <% } %>
            <% } else { %>
            <div class="text-center py-5 text-muted">
                <i class="fa-regular fa-bell-slash fa-3x mb-3"></i>
                <h4 class="mb-2">Chưa có thông báo nào</h4>
                <p class="mb-0">Khi có tin nhắn mới, lịch hẹn hoặc cập nhật từ admin, chúng sẽ xuất hiện tại đây.</p>
            </div>
            <% } %>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
        <jsp:include page="/WEB-INF/includes/site-footer.jsp" />

    </body>
</html>
