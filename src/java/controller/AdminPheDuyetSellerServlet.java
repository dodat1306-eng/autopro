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

@WebServlet("/admin/seller-request-approve")
public class AdminPheDuyetSellerServlet extends HttpServlet {

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

        boolean approved = new DangKySellerDAO().approveSeller(maDangKySeller, admin.getMaNguoiDung());
        if (!approved) {
            WebAppUrlUtil.redirect(request, response, "admin/seller-request-detail?id=" + maDangKySeller + "&result=invalid-state");
            return;
        }

        WebAppUrlUtil.redirect(request, response, "admin/seller-request-detail?id=" + maDangKySeller + "&result=approved");
    }
}
