package controller;

import dao.DangKySellerDAO;
import dao.ThongBaoDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import model.NguoiDung;
import util.WebAppUrlUtil;

@WebServlet("/admin/seller-requests")
public class AdminDanhSachSellerServlet extends HttpServlet {

    private String trimToEmpty(String value) {
        return value == null ? "" : value.trim();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        NguoiDung admin = (NguoiDung) request.getSession().getAttribute("user");
        if (admin == null || !admin.isAdmin()) {
            WebAppUrlUtil.redirect(request, response, "home");
            return;
        }

        String status = trimToEmpty(request.getParameter("status"));
        String keyword = trimToEmpty(request.getParameter("keyword"));

        DangKySellerDAO dangKySellerDAO = new DangKySellerDAO();
        request.setAttribute("sellerRequests", dangKySellerDAO.searchRequests(keyword, status));
        request.setAttribute("selectedStatus", status);
        request.setAttribute("keyword", keyword);

        new ThongBaoDAO().markByUrlAsRead(admin.getMaNguoiDung(), "admin/seller-requests");

        request.getRequestDispatcher("/admin-seller-request-list.jsp").forward(request, response);
    }
}
