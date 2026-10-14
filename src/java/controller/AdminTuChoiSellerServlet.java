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

@WebServlet("/admin/seller-request-reject")
public class AdminTuChoiSellerServlet extends HttpServlet {

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

    private String trimToEmpty(String value) {
        return value == null ? "" : value.trim();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

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

        String lyDoTuChoi = trimToEmpty(request.getParameter("lyDoTuChoi"));
        if (lyDoTuChoi.isEmpty()) {
            lyDoTuChoi = "Hồ sơ seller chưa đủ điều kiện xác minh. Vui lòng bổ sung thông tin và nộp lại.";
        }

        boolean rejected = new DangKySellerDAO().rejectSeller(maDangKySeller, admin.getMaNguoiDung(), lyDoTuChoi);
        if (!rejected) {
            WebAppUrlUtil.redirect(request, response, "admin/seller-request-detail?id=" + maDangKySeller + "&result=invalid-state");
            return;
        }

        WebAppUrlUtil.redirect(request, response, "admin/seller-request-detail?id=" + maDangKySeller + "&result=rejected");
    }
}
