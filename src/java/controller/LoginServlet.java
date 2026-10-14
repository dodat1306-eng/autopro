package controller;

import dao.NguoiDungDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import model.NguoiDung;
import util.WebAppUrlUtil;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private String trimToEmpty(String value) {
        return value == null ? "" : value.trim();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        String identifier = trimToEmpty(request.getParameter("identifier"));
        String matKhau = trimToEmpty(request.getParameter("matKhau"));

        if (identifier.isEmpty() || matKhau.isEmpty()) {
            request.setAttribute("error", "Vui lòng nhập email hoặc số điện thoại và mật khẩu.");
            request.setAttribute("identifier", identifier);
            request.getRequestDispatcher("login.jsp").forward(request, response);
            return;
        }

        NguoiDung user = new NguoiDungDAO().login(identifier, matKhau);

        if (user != null) {
            // Invalidate session cũ trước khi tạo session mới — chống Session Fixation
            jakarta.servlet.http.HttpSession oldSession = request.getSession(false);
            if (oldSession != null) {
                oldSession.invalidate();
            }
            jakarta.servlet.http.HttpSession newSession = request.getSession(true);
            newSession.setAttribute("user", user);
            if (user.isAdmin()) {
                response.sendRedirect(request.getContextPath() + "/admin/dashboard");
            } else {
                WebAppUrlUtil.redirect(request, response, "home");
            }
            return;
        }

        request.setAttribute("error", "Sai email/số điện thoại hoặc mật khẩu.");
        request.setAttribute("identifier", identifier);
        request.getRequestDispatcher("login.jsp").forward(request, response);
    }
}
