package controller;

import dao.ThongBaoDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import model.NguoiDung;
import model.ThongBao;
import util.WebAppUrlUtil;

@WebServlet("/notifications")
public class NotificationServlet extends HttpServlet {

    private Integer parseInteger(String rawValue) {
        if (rawValue == null || rawValue.isBlank()) {
            return null;
        }
        try {
            return Integer.parseInt(rawValue.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private NguoiDung requireUser(HttpServletRequest request, HttpServletResponse response) throws IOException {
        NguoiDung user = (NguoiDung) request.getSession().getAttribute("user");
        if (user == null) {
            WebAppUrlUtil.redirect(request, response, "login");
            return null;
        }
        return user;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        NguoiDung user = requireUser(request, response);
        if (user == null) {
            return;
        }

        ThongBaoDAO thongBaoDAO = new ThongBaoDAO();
        Integer openId = parseInteger(request.getParameter("open"));
        if (openId != null) {
            ThongBao thongBao = thongBaoDAO.getNotificationById(openId, user.getMaNguoiDung());
            if (thongBao != null) {
                thongBaoDAO.markAsRead(openId, user.getMaNguoiDung());
                if (thongBao.getUrlDich() != null && !thongBao.getUrlDich().isBlank()) {
                    response.sendRedirect(WebAppUrlUtil.toInternalAppUrl(request, thongBao.getUrlDich()));
                    return;
                }
            }
            WebAppUrlUtil.redirect(request, response, "notifications");
            return;
        }

        request.setAttribute("notifications", thongBaoDAO.getAllNotifications(user.getMaNguoiDung()));
        request.setAttribute("unreadCount", thongBaoDAO.countUnread(user.getMaNguoiDung()));
        request.getRequestDispatcher("notifications.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        NguoiDung user = requireUser(request, response);
        if (user == null) {
            return;
        }

        ThongBaoDAO thongBaoDAO = new ThongBaoDAO();
        String action = request.getParameter("action");
        if ("mark-all-read".equals(action)) {
            thongBaoDAO.markAllAsRead(user.getMaNguoiDung());
        } else if ("mark-read".equals(action)) {
            Integer notificationId = parseInteger(request.getParameter("id"));
            if (notificationId != null) {
                thongBaoDAO.markAsRead(notificationId, user.getMaNguoiDung());
            }
        }

        WebAppUrlUtil.redirect(request, response, "notifications");
    }
}
