package controller;

import dao.DangKySellerDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import model.NguoiDung;
import util.WebAppUrlUtil;

@WebServlet("/admin/seller-request-delete")
public class AdminXoaSellerServlet extends HttpServlet {

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

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        NguoiDung admin = (NguoiDung) request.getSession().getAttribute("user");
        if (admin == null || !admin.isAdmin()) {
            WebAppUrlUtil.redirect(request, response, "home");
            return;
        }

        Integer maDangKySeller = parseInteger(request.getParameter("id"));
        if (maDangKySeller == null) {
            WebAppUrlUtil.redirect(request, response, "admin/seller-requests?result=invalid");
            return;
        }

        boolean deleted = new DangKySellerDAO().deleteSellerRequest(maDangKySeller, admin.getMaNguoiDung());
        WebAppUrlUtil.redirect(request, response,
                "admin/seller-requests?result=" + (deleted ? "deleted" : "invalid-state"));
    }
}
