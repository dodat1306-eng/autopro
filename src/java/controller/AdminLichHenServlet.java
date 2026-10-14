package controller;

import dao.YeuCauKetNoiDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import model.NguoiDung;
import model.YeuCauKetNoi;

@WebServlet("/admin/lich-hen")
public class AdminLichHenServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        NguoiDung user = (NguoiDung) session.getAttribute("user");

        if (user == null || !"admin".equals(user.getVaiTro())) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        YeuCauKetNoiDAO yeuCauKetNoiDAO = new YeuCauKetNoiDAO();
        List<YeuCauKetNoi> danhSachYeuCau = yeuCauKetNoiDAO.getAllYeuCauKetNoi();

        request.setAttribute("danhSachYeuCau", danhSachYeuCau);
        request.getRequestDispatcher("/admin_appointments.jsp").forward(request, response);
    }
}
