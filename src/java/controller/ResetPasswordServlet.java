package controller;

import dao.NguoiDungDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import util.WebAppUrlUtil;

@WebServlet("/reset-password")
public class ResetPasswordServlet extends HttpServlet {

    private static final String SESSION_RESET_IDENTIFIER = "resetIdentifier";
    private static final String SESSION_RESET_VERIFIED_USER_ID = "resetVerifiedUserId";

    private String trimToEmpty(String value) {
        return value == null ? "" : value.trim();
    }

    private Integer getVerifiedUserId(HttpServletRequest request) {
        Object value = request.getSession().getAttribute(SESSION_RESET_VERIFIED_USER_ID);
        if (value instanceof Integer) {
            return (Integer) value;
        }
        if (value == null) {
            return null;
        }
        try {
            return Integer.parseInt(String.valueOf(value));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String getSessionIdentifier(HttpServletRequest request) {
        Object value = request.getSession().getAttribute(SESSION_RESET_IDENTIFIER);
        return value == null ? "" : String.valueOf(value).trim();
    }

    private void clearResetSession(HttpServletRequest request) {
        request.getSession().removeAttribute(SESSION_RESET_IDENTIFIER);
        request.getSession().removeAttribute(SESSION_RESET_VERIFIED_USER_ID);
    }

    private void forward(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("reset-password.jsp").forward(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Integer verifiedUserId = getVerifiedUserId(request);
        String identifier = getSessionIdentifier(request);
        if (verifiedUserId == null || identifier.isEmpty()) {
            WebAppUrlUtil.redirect(request, response, "forgot-password");
            return;
        }

        request.setAttribute("identifier", identifier);
        forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        Integer verifiedUserId = getVerifiedUserId(request);
        String identifier = getSessionIdentifier(request);
        if (verifiedUserId == null || identifier.isEmpty()) {
            WebAppUrlUtil.redirect(request, response, "forgot-password");
            return;
        }

        String newPassword = trimToEmpty(request.getParameter("newPassword"));
        String confirmPassword = trimToEmpty(request.getParameter("confirmPassword"));

        request.setAttribute("identifier", identifier);

        if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
            request.setAttribute("error", "Vui lòng nhập đầy đủ mật khẩu mới.");
            forward(request, response);
            return;
        }
        if (!newPassword.equals(confirmPassword)) {
            request.setAttribute("error", "Mật khẩu xác nhận không khớp.");
            forward(request, response);
            return;
        }
        if (newPassword.length() < 6) {
            request.setAttribute("error", "Mật khẩu mới phải có ít nhất 6 ký tự.");
            forward(request, response);
            return;
        }

        if (!new NguoiDungDAO().updatePassword(verifiedUserId, newPassword)) {
            request.setAttribute("error", "Không thể cập nhật mật khẩu lúc này.");
            forward(request, response);
            return;
        }

        clearResetSession(request);
        WebAppUrlUtil.redirect(request, response, "login?reset=1");
    }
}
