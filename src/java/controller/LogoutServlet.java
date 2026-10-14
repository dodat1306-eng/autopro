package controller;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import util.WebAppUrlUtil;

@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // 1. Lấy session hiện tại
        HttpSession session = request.getSession(false);
        
        if (session != null) {
            // 2. Xóa toàn bộ dữ liệu trong session (Xóa đối tượng user)
            session.invalidate();
        }
        
        // 3. Đuổi người dùng về trang login hoặc trang chủ
        WebAppUrlUtil.redirect(request, response, "login");
    }
}
