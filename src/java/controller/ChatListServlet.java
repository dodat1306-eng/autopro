package controller;

import dao.HoiThoaiDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import model.NguoiDung;
import util.WebAppUrlUtil;

@WebServlet("/chat-list")
public class ChatListServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        NguoiDung user = (NguoiDung) request.getSession().getAttribute("user");
        if (user == null) {
            WebAppUrlUtil.redirect(request, response, "login");
            return;
        }
        if (user.isAdmin()) {
            WebAppUrlUtil.redirect(request, response, "admin/dashboard");
            return;
        }

        List<Map<String, Object>> listChat = new HoiThoaiDAO().getDanhSachHoiThoai(user.getMaNguoiDung());
        request.setAttribute("listChat", listChat);
        request.getRequestDispatcher("chat-list.jsp").forward(request, response);
    }
}
